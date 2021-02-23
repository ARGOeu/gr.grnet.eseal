package gr.grnet.eseal.sign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import gr.grnet.eseal.exception.InternalServerErrorException;
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
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

    public RemoteProviderHttpEsealClient(String endpoint) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException{
        this.signingURL  = String.format("%s://%s/%s", this.PROTOCOL, endpoint, this.SIGNING_PATH);
        this.closeableHttpClient = buildHttpClient();
    }

    @Override
    public String sign(String document, String username, String password, String key) {

        // prepare the document signing request
        RemoteProviderSignDocumentRequest remoteProviderSignDocumentRequest =  new RemoteProviderSignDocumentRequest();
        remoteProviderSignDocumentRequest.fileData = document;
        remoteProviderSignDocumentRequest.username = username;
        remoteProviderSignDocumentRequest.password = password;
        try {
            // generate new TOTP password
            remoteProviderSignDocumentRequest.signPassword = TOTP.generate(key);

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
                    throw new UnprocessableEntityException("Invalid key or expired TOTP");
                }

                // if any other error occurs
                throw new InternalServerErrorException("Error with signing backend");
            }

            // returned the signed document
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
        System.out.println(current_msg.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.readValue(current_msg.toString(),RemoteProviderSignDocumentResponse.class);
    }


    private CloseableHttpClient buildHttpClient() throws NoSuchAlgorithmException, KeyManagementException {

        // socket config
        SocketConfig socketCfg = SocketConfig.custom().
                setSoTimeout(30000).
                build();

        RequestConfig reqCfg = RequestConfig.custom().
                setConnectTimeout(30000).
                setConnectionRequestTimeout(30000).
                build();

        // ssl
        SSLContext sslContext = SSLContext.getInstance("SSL");

// set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
            }
        } }, new SecureRandom());


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

