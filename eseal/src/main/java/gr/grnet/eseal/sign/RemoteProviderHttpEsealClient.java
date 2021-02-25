package gr.grnet.eseal.sign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.utils.TOTP;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * RemoteProviderHttpEsealClient implements a {@link RemoteHttpEsealClient} that allows the usage of a provider's remote http
 * rest api in order to access e-seals and sign documents
 */
public class RemoteProviderHttpEsealClient implements RemoteHttpEsealClient{

    private CloseableHttpClient closeableHttpClient;

    private String signingURL;

    private final String SIGNING_PATH = "dsa/v1/sign";

    private final String PROTOCOL = "https";

    private RemoteProviderProperties remoteProviderProperties;

    public RemoteProviderHttpEsealClient(RemoteProviderProperties remoteProviderProperties) throws
            IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException{
        this.remoteProviderProperties = remoteProviderProperties;
        this.signingURL  = String.format("%s://%s/%s", this.PROTOCOL, remoteProviderProperties.getEndpoint(), this.SIGNING_PATH);
        this.closeableHttpClient = buildHttpClient();
    }

    @Override
    public String sign(String document, String username, String password, String key) {

        // check if retry is enabled
        if (this.remoteProviderProperties.isRetryEnabled()) {
            int retryCount = 0;

            while (retryCount < this.remoteProviderProperties.getRetryCounter()) {

                try {
                    return this.doSign(document, username, password, key);
                } catch (InvalidTOTPException | InternalServerErrorException ie) {
                    retryCount++;
                    System.out.println("Encountered an exception while trying to sign");
                    System.out.println(ie);
                    System.out.println("Retrying for the " + retryCount + " time in " +
                            this.remoteProviderProperties.getRetryInterval() + " seconds");
                    try {
                        Thread.sleep(this.remoteProviderProperties.getRetryInterval() * 1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        }
        // if the retry mechanism has been enabled, this is the last retry
        // otherwise is the one and only call to the remote signing service
        return this.doSign(document, username,password,key);
    }

    /**
     * doSign takes care of the internal business logic for connecting to the provider's's remote http api
     * in order to sign the provided document
     */
    private String doSign(String document, String username, String password, String key) {

        // prepare the document signing request
        RemoteProviderSignDocumentRequest remoteProviderSignDocumentRequest =  new RemoteProviderSignDocumentRequest();
        remoteProviderSignDocumentRequest.fileData = document;
        remoteProviderSignDocumentRequest.username = username;
        remoteProviderSignDocumentRequest.password = password;
        try {
            // generate new TOTP password
            remoteProviderSignDocumentRequest.signPassword = TOTP.generate(key);

            //TODO
            //Revisit this code block as it has been provided as a temporary solution for the TOTP
            //timeout possibility and we need to re-evaluate it.
            long timePeriodRemainingSeconds = TOTP.getTimePeriodRemainingSeconds();
            if (timePeriodRemainingSeconds <= this.remoteProviderProperties.getTotpWaitForRefreshSeconds() ) {
                System.out.println("TOTP remaining time period is below/at 5 seconds, " + timePeriodRemainingSeconds +
                        " seconds.Waiting for expiration.");
                Thread.sleep(timePeriodRemainingSeconds * 1000);
                System.out.println("Generating new TOTP");
                remoteProviderSignDocumentRequest.signPassword = TOTP.generate(key);
            }

            // attempt to sign the document with remote provider
            RemoteProviderSignDocumentResponse remoteProviderSignDocumentResponse = this.doPost(remoteProviderSignDocumentRequest);

            // check if the signing was successful
            if (!remoteProviderSignDocumentResponse.isSuccessful()) {

                // check if the user could not login
                if (remoteProviderSignDocumentResponse.hasFailedToLogin()) {
                    throw new UnprocessableEntityException("Wrong user credentials");
                }

                // check if the totp was wrong or expired
                if (remoteProviderSignDocumentResponse.hasInvalidTOTPKey()) {
                    throw new InvalidTOTPException();
                }

                // if any other error occurs
                throw new InternalServerErrorException("Error with signing backend");
            }

            // returned the signed document
            System.out.println("Successful document signing");
            return remoteProviderSignDocumentResponse.getSignedFileData();
        }
        catch (CodeGenerationException e) {
            System.out.println(e);
            throw new InternalServerErrorException("TOTP generator has encountered an error");
        }
        catch (IOException ioe) {
            System.out.println(ioe);
            throw new InternalServerErrorException("Signing backend unavailable");
        }
        catch (InterruptedException ie) {
            System.out.println(ie);
            throw new InternalServerErrorException("Internal thread error");
        }
    }

    private RemoteProviderSignDocumentResponse doPost(RemoteProviderSignDocumentRequest remoteProviderSignDocumentRequest) throws
    IOException{

        StringEntity postBody = new StringEntity(remoteProviderSignDocumentRequest.toJSON());
        postBody.setContentType("application/json");

        // Set up a post request
        HttpPost postReq = new HttpPost(this.signingURL);
        postReq.setEntity(postBody);
        CloseableHttpResponse response = this.closeableHttpClient.execute(postReq);
        HttpEntity entity = response.getEntity();

        // Read the response
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder current_msg = new StringBuilder();
        while ((line = br.readLine()) != null) {
            current_msg.append(line);
        }

        // Make sure that the interaction with the service has closed
        EntityUtils.consume(entity);
        response.close();
        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.readValue(current_msg.toString(),RemoteProviderSignDocumentResponse.class);
    }


    private CloseableHttpClient buildHttpClient() throws IOException, KeyStoreException, NoSuchAlgorithmException,
            CertificateException, KeyManagementException {

        // socket config
        SocketConfig socketCfg = SocketConfig.custom().
                setSoTimeout(30000).
                build();

        RequestConfig reqCfg = RequestConfig.custom().
                setConnectTimeout(30000).
                setConnectionRequestTimeout(30000).
                build();

        // ssl context
        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        if (!this.remoteProviderProperties.isTlsVerifyEnabled()) {
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            }}, new SecureRandom());
        } else {

            // set up a trust manager with the appropriate client truststore
            // in order to verify the remote provider api

            /* Load client truststore. */
            KeyStore theClientTruststore = KeyStore.getInstance(this.remoteProviderProperties.getTruststoreType());

            InputStream clientTruststoreIS = this.getClass().
                    getResourceAsStream("/".concat(this.remoteProviderProperties.getTruststoreFile()));

            theClientTruststore.load(clientTruststoreIS,
                    this.remoteProviderProperties.getTruststorePassword().toCharArray());


        /* Create a trust manager factory using the client truststore. */
        final TrustManagerFactory theTrustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        theTrustManagerFactory.init(theClientTruststore);

        /*
         * Create a SSL context with a trust manager that uses the
         * client truststore.
         */
        sslContext.init(null, theTrustManagerFactory.getTrustManagers(),new SecureRandom());
        }

        // build the client
        return HttpClients.custom().
                setSSLContext(sslContext).
                setDefaultRequestConfig(reqCfg).
                setDefaultSocketConfig(socketCfg).
                build();
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private class RemoteProviderSignDocumentRequest {
        @JsonProperty("Username")
        private String username;
        @JsonProperty("Password")
        private String password;
        @JsonProperty("SignPassword")
        private String signPassword;
        @JsonProperty("FileData")
        private String fileData;
        @JsonProperty("FileType")
        private String fileType = "pdf";
        @JsonProperty("Page")
        private int page = 0;
        @JsonProperty("Height")
        private int height = 100;
        @JsonProperty("Width")
        private int width = 100;
        @JsonProperty("X")
        private int x = 140;
        @JsonProperty("Y")
        private int y = 230;
        @JsonProperty("Appearance")
        private int appearance = 15;

        private String toJSON() throws JsonProcessingException{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class RemoteProviderSignDocumentResponse {
        @JsonProperty("Success")
        private Boolean success;
        @JsonProperty("Data")
        private DataField data;
        @JsonProperty("ErrData")
        private ErrorData errorData;

        private Boolean isSuccessful() {
            return this.success;
        }

        private String getSignedFileData() {
            return this.data.signedFileData;
        }

        private Boolean hasFailedToLogin() {
            System.out.println(this.errorData.message);
            return this.errorData.message.contains("Failed to Logon");
        }

        private Boolean hasInvalidTOTPKey() {
            System.out.println(this.errorData.message);
            return this.errorData.message.contains("Failed to Sign");
        }
    }
        @Getter
        @Setter
        @NoArgsConstructor
        private static class DataField {
            @JsonProperty("SignedFileData")
            private String signedFileData;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        private static class ErrorData {
        @JsonProperty("Message")
        private String message;
        @JsonProperty("Module")
        private Object module;
        @JsonProperty("Code")
        private int code;
        @JsonProperty("InnerCode")
        private int innerCode;
        }
}

