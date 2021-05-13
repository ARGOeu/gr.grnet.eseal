package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.config.VisibleSignatureProperties;
import gr.grnet.eseal.config.VisibleSignaturePropertiesBean;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.exception.InvalidTOTPException;
import gr.grnet.eseal.exception.UnprocessableEntityException;
import gr.grnet.eseal.service.SignDocumentService;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import gr.grnet.eseal.sign.RemoteProviderSignBuffer;
import gr.grnet.eseal.sign.RemoteProviderSignDocument;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class RemoteProviderSignDocumentTests {

  @Mock private CloseableHttpClient httpClient;

  private SignDocumentService signDocumentService;
  private ObjectMapper objectMapper;
  private RemoteProviderSignDocument remoteProviderSignDocument;
  private RemoteProviderSignBuffer remoteProviderSignBuffer;
  private RemoteProviderCertificates remoteProviderCertificates;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.objectMapper = new ObjectMapper();
    this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    RemoteProviderProperties remoteProviderProperties = new RemoteProviderProperties();

    remoteProviderSignDocument =
        new RemoteProviderSignDocument(remoteProviderProperties, httpClient);

    remoteProviderSignBuffer = new RemoteProviderSignBuffer(remoteProviderProperties, httpClient);

    remoteProviderCertificates =
        new RemoteProviderCertificates(remoteProviderProperties, httpClient);

    VisibleSignatureProperties visibleSignatureProperties =
        new VisibleSignaturePropertiesBean().visibleSignatureProperties();

    remoteProviderProperties.setRetryEnabled(false);
    signDocumentService =
        new SignDocumentService(
            remoteProviderSignDocument,
            remoteProviderSignBuffer,
            remoteProviderCertificates,
            remoteProviderProperties,
            visibleSignatureProperties);
  }

  @Test
  void TestSignerCertificateSuccessful() throws Exception {

    String subject = "CN=test.example.com, SERIALNUMBER=879877987, OU=unit-1";

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulCertificatesResponse(subject, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    assertThat(this.signDocumentService.getSignerInfo("u", "p"))
        .isEqualTo("test.example.com/unit-1");
  }

  @Test
  void TestSignerCertificateInvalidSubject() throws Exception {

    String subject = "invalid dn";

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulCertificatesResponse(subject, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    InternalServerErrorException e =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.getSignerInfo("u", "p"));

    assertThat(e.getMessage()).isEqualTo("Error with Signer's Certificate Subject");
  }

  @Test
  void TestDocumentSignSuccessful() throws Exception {

    String documentData = "document-data-bytes";

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulResponse(documentData, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    assertThat("document-data-bytes")
        .isEqualTo(this.signDocumentService.signDocument(documentData, "u", "p", "k"));
  }

  @TestOnlyIfTimezoneUTC
  void TestDocumentSignDetachedSuccessfulUTCWithSignerInfoAndImage() throws Exception {

    InputStream isSignature =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("detached-signature-utc-si-img-b64.txt"));
    InputStream isOriginalPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("original-b64-pdf.txt"));
    InputStream isSignedPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("signed-detached-utc-si-img-b64-pdf.txt"));

    DSSDocument imageDocument =
        new InMemoryDocument(
            RemoteProviderSignDocumentTests.class.getResourceAsStream(
                "/visible-signature/".concat("ste2.jpeg")));

    String signatureB64 =
        new BufferedReader(new InputStreamReader(isSignature, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String originalPDFB64 =
        new BufferedReader(new InputStreamReader(isOriginalPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String signedPDFB64 =
        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulSignatureResponse(signatureB64, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(Long.parseLong("1617901835690"));

    // we have prepared a detached signature for the signing time of 1617901835690
    assertThat(
            this.signDocumentService.signDocumentDetached(
                originalPDFB64,
                "u",
                "p",
                "k",
                calendar.getTime(),
                "test.example.com/unit-1",
                imageDocument))
        .isEqualTo(signedPDFB64);
  }

  @TestOnlyIfTimezoneUTC
  void TestDocumentSignDetachedSuccessfulUTC() throws Exception {

    InputStream isSignature =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("detached-signature-utc-b64.txt"));
    InputStream isOriginalPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("original-b64-pdf.txt"));
    InputStream isSignedPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("signed-detached-utc-b64-pdf.txt"));

    String signatureB64 =
        new BufferedReader(new InputStreamReader(isSignature, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String originalPDFB64 =
        new BufferedReader(new InputStreamReader(isOriginalPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String signedPDFB64 =
        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulSignatureResponse(signatureB64, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(Long.parseLong("1617901835690"));

    // we have prepared a detached signature for the signing time of 1617901835690
    assertThat(
            this.signDocumentService.signDocumentDetached(
                originalPDFB64, "u", "p", "k", calendar.getTime(), "", null))
        .isEqualTo(signedPDFB64);
  }

  @TestOnlyIfTimezoneEuropeAthens
  void TestDocumentSignDetachedSuccessfulAthens() throws Exception {

    InputStream isSignature =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("detached-signature-athens-b64.txt"));
    InputStream isOriginalPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("original-b64-pdf.txt"));
    InputStream isSignedPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("signed-detached-athens-b64-pdf.txt"));

    String signatureB64 =
        new BufferedReader(new InputStreamReader(isSignature, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String originalPDFB64 =
        new BufferedReader(new InputStreamReader(isOriginalPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String signedPDFB64 =
        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulSignatureResponse(signatureB64, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(Long.parseLong("1617901835690"));

    // we have prepared a detached signature for the signing time of 1617901835690
    assertThat(
            this.signDocumentService.signDocumentDetached(
                originalPDFB64, "u", "p", "k", calendar.getTime(), "", null))
        .isEqualTo(signedPDFB64);
  }

  @TestOnlyIfTimezoneEuropeAthens
  void TestDocumentSignDetachedSuccessfulAthensWithSignerInfoAndImage() throws Exception {

    InputStream isSignature =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("detached-signature-athens-si-img-b64.txt"));
    InputStream isOriginalPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("original-b64-pdf.txt"));
    InputStream isSignedPDF =
        RemoteProviderSignDocumentTests.class.getResourceAsStream(
            "/detached-sign-case/".concat("signed-detached-athens-si-img-b64-pdf.txt"));

    DSSDocument imageDocument =
        new InMemoryDocument(
            RemoteProviderSignDocumentTests.class.getResourceAsStream(
                "/visible-signature/".concat("ste2.jpeg")));

    String signatureB64 =
        new BufferedReader(new InputStreamReader(isSignature, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String originalPDFB64 =
        new BufferedReader(new InputStreamReader(isOriginalPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    String signedPDFB64 =
        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulSignatureResponse(signatureB64, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(Long.parseLong("1617901835690"));

    // we have prepared a detached signature for the signing time of 1617901835690
    assertThat(
            this.signDocumentService.signDocumentDetached(
                originalPDFB64,
                "u",
                "p",
                "k",
                calendar.getTime(),
                "test.example.com/unit-1",
                imageDocument))
        .isEqualTo(signedPDFB64);
  }

  @Test
  void TestDocumentSignDetachedDSSDigestError() throws Exception {

    InternalServerErrorException de =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () ->
                this.signDocumentService.signDocumentDetached(
                    "invalid", "u", "p", "k", new Date(), "", null));

    assertThat("Could not compute document digest").isEqualTo(de.getMessage());
  }

  @Test
  void TestDocumentSignDetachedDSSSignError() throws Exception {

    CloseableHttpResponse mockResponse =
        buildMockSuccessfulSignatureResponse("sig", HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    PDFSignatureService pdfSignatureService = mock(PDFSignatureService.class);
    when(pdfSignatureService.digest(any(), any())).thenReturn("random-bytes".getBytes());
    when(pdfSignatureService.sign(any(), any(), any())).thenThrow(DSSException.class);

    ReflectionTestUtils.setField(
        this.signDocumentService, "pdfSignatureService", pdfSignatureService);

    InternalServerErrorException de =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () ->
                this.signDocumentService.signDocumentDetached(
                    "doc", "u", "p", "k", new Date(), "", null));

    assertThat("Could not combine signature to original document").isEqualTo(de.getMessage());
  }

  @Test
  void TestDocumentSignFailToLogon() throws Exception {

    String errMessage = "Failed to Logon";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    UnprocessableEntityException exc =
        Assertions.assertThrows(
            UnprocessableEntityException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Wrong user credentials").isEqualTo(exc.getMessage());
  }

  @Test
  void TestDocumentSignInvalidExpiredTOTP() throws Exception {

    String errMessage =
        "Failed to Sign, Error (0X900201E0)-Failed to verify the user password. "
            + "Passwords should be in wide character representation. "
            + "Password length in bytes includes the null terminator (two bytes in wide char representation).";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    UnprocessableEntityException exc =
        Assertions.assertThrows(
            InvalidTOTPException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Invalid key or expired TOTP").isEqualTo(exc.getMessage());
  }

  @Test
  void TestDocumentSignOCSPError() throws Exception {

    String errMessage =
        "Failed to Sign, Error (0X90030233)-Failed to get the URL of the OCSP server.";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Failed to get the URL of the OCSP server").isEqualTo(exc.getMessage());
  }

  @Test
  void TestDocumentSignUserIsLocked() throws Exception {

    String errMessage = "Failed to Logon, Error (0X900201E2)-The user is locked and cannot logon.";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("The user is locked and cannot logon").isEqualTo(exc.getMessage());
  }

  @Test
  void TestDocumentSignTimeStampingServiceProblem() throws Exception {

    String errMessage =
        "Failed to Sign, Error (0X90030373)-Connection to Time Stamping service problem.";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_OK);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Connection to Time Stamping service problem").isEqualTo(exc.getMessage());
  }

  @Test
  void TestDocumentSignGenericProviderError() throws Exception {

    String errMessage = "generic error";

    CloseableHttpResponse mockResponse =
        buildMockUnSuccessfulResponse(errMessage, HttpStatus.SC_INTERNAL_SERVER_ERROR);

    when(httpClient.execute(any())).thenReturn(mockResponse);

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Error with signing backend").isEqualTo(exc.getMessage());
  }

  // TODO Revisit this code test to add maybe a better way to test that the retry interval and count
  // are honored
  @Test
  void TestDocumentSignIOExceptionRetryEnabled() throws Exception {

    // override the test case default properties
    // enable retry for 3 times and 5 seconds interval
    // that means that the invocation should take at least 15 seconds to complete when the IO
    // exception occurs
    RemoteProviderProperties remoteProviderProperties = new RemoteProviderProperties();
    remoteProviderProperties.setRetryEnabled(true);
    remoteProviderProperties.setRetryCounter(3);
    remoteProviderProperties.setRetryInterval(5);

    remoteProviderSignDocument =
        new RemoteProviderSignDocument(remoteProviderProperties, httpClient);

    signDocumentService.setRemoteProviderSignDocument(remoteProviderSignDocument);

    when(httpClient.execute(any())).thenThrow(IOException.class);

    long start = Instant.now().getEpochSecond();

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    long end = Instant.now().getEpochSecond();

    assertThat("Signing backend unavailable").isEqualTo(exc.getMessage());
    assertThat((end - start) >= 15).isTrue();
  }

  @Test
  void TestDocumentSignIOException() throws Exception {

    when(httpClient.execute(any())).thenThrow(IOException.class);

    InternalServerErrorException exc =
        Assertions.assertThrows(
            InternalServerErrorException.class,
            () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));

    assertThat("Signing backend unavailable").isEqualTo(exc.getMessage());
  }

  //    @Test
  //    void TestDocumentSignCodeGenerationException() throws Exception {
  //
  //      when(httpClient.execute(any()))
  //          .thenAnswer(
  //              invocation -> {
  //                throw new CodeGenerationException("Code generation exception", null);
  //              });
  //
  //      InternalServerErrorException exc =
  //          Assertions.assertThrows(
  //              InternalServerErrorException.class,
  //              () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));
  //
  //      assertThat("TOTP generator has encountered an error").isEqualTo(exc.getMessage());
  //    }
  //
  //    @Test
  //    void TestDocumentSignInterruptedException() throws Exception {
  //
  //      when(httpClient.execute(any()))
  //          .thenAnswer(
  //              invocation -> {
  //                throw new InterruptedException("Interruption exception");
  //              });
  //      InternalServerErrorException exc =
  //          Assertions.assertThrows(
  //              InternalServerErrorException.class,
  //              () -> this.signDocumentService.signDocument("doc", "u", "p", "k"));
  //
  //      assertThat("Internal thread error").isEqualTo(exc.getMessage());
  //    }

  private CloseableHttpResponse buildMockSuccessfulSignatureResponse(
      String signature, int httpStatus) throws IOException {

    // init mock response
    MockDataField mockDataField = new MockDataField();
    mockDataField.setSignature(signature);
    MockRemoteProviderSignDocumentResponse mockResp = new MockRemoteProviderSignDocumentResponse();
    mockResp.setSuccess(true);
    mockResp.setData(mockDataField);

    return buildMockClosableHttpResponse(this.objectMapper.writeValueAsBytes(mockResp), httpStatus);
  }

  private CloseableHttpResponse buildMockSuccessfulCertificatesResponse(
      String subject, int httpStatus) throws IOException {

    // init mock response
    MockRemoteProviderCertificatesResponse mockCerts = new MockRemoteProviderCertificatesResponse();
    mockCerts.setSubject(subject);

    return buildMockClosableHttpResponse(
        this.objectMapper.writeValueAsBytes(mockCerts), httpStatus);
  }

  private CloseableHttpResponse buildMockSuccessfulResponse(String dataField, int httpStatus)
      throws IOException {

    // init mock response
    MockDataField mockDataField = new MockDataField();
    mockDataField.setSignedFileData(dataField);
    MockRemoteProviderSignDocumentResponse mockResp = new MockRemoteProviderSignDocumentResponse();
    mockResp.setSuccess(true);
    mockResp.setData(mockDataField);

    return buildMockClosableHttpResponse(this.objectMapper.writeValueAsBytes(mockResp), httpStatus);
  }

  private CloseableHttpResponse buildMockUnSuccessfulResponse(String dataField, int httpStatus)
      throws IOException {

    // init mock response
    MockErrorData errorData = new MockErrorData();
    errorData.setMessage(dataField);
    MockRemoteProviderSignDocumentResponse mockResp = new MockRemoteProviderSignDocumentResponse();
    mockResp.setSuccess(false);
    mockResp.setErrorData(errorData);

    return buildMockClosableHttpResponse(this.objectMapper.writeValueAsBytes(mockResp), httpStatus);
  }

  private CloseableHttpResponse buildMockClosableHttpResponse(byte[] responseBody, int httpStatus)
      throws IOException {

    CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    HttpEntity entity = mock(HttpEntity.class);

    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, ""));
    when(entity.getContent()).thenReturn(new ByteArrayInputStream(responseBody));
    when(response.getEntity()).thenReturn(entity);

    return response;
  }

  @Setter
  @NoArgsConstructor
  private static class MockRemoteProviderCertificatesResponse {
    @JsonProperty("Subject")
    private String subject;
  }

  @Setter
  @NoArgsConstructor
  private static class MockRemoteProviderSignDocumentResponse {
    @JsonProperty("Success")
    private Boolean success;

    @JsonProperty("Data")
    private MockDataField data;

    @JsonProperty("ErrData")
    private MockErrorData errorData;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class MockDataField {
    @JsonProperty("SignedFileData")
    private String signedFileData;

    @JsonProperty("Signature")
    private String signature;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class MockErrorData {
    @JsonProperty("Message")
    private String message;
  }
}
