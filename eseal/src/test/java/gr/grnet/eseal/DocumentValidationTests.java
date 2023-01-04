package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import gr.grnet.eseal.dto.SignedDocument;
import gr.grnet.eseal.dto.ValidateDocumentRequestDto;
import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.service.ValidateDocumentService;
import gr.grnet.eseal.validation.DocumentValidatorLOTL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.pdfbox.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentValidationTests {

  @Autowired private MockMvc mockMvc;

  private final String validationPath = "/api/v1/validation/validateDocument";

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired ValidateDocumentService validateDocumentService;

  @Autowired DocumentValidatorLOTL documentValidatorLOTL;

  @Test
  void ValidateDocumentSuccess() throws Exception {

    InputStream isSignedPDF =
        DocumentValidationTests.class.getResourceAsStream(
            "/validation/".concat("signed-lta-b64-pdf.txt"));

    String signedLTAPDF =
        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes(signedLTAPDF);
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    MockHttpServletResponse resp =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK.value());

    WSReportsDTO wsReportsDTO =
        this.validateDocumentService.validateDocument(
            validateDocumentRequestDto.getSignedDocument().getBytes(),
            validateDocumentRequestDto.getSignedDocument().getName());

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().size()).isEqualTo(1);
    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getIndication())
        .isEqualTo(Indication.INDETERMINATE);

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getSubIndication())
        .isEqualTo(SubIndication.NO_CERTIFICATE_CHAIN_FOUND);
    List<XmlMessage> errors =
        wsReportsDTO
            .getSimpleReport()
            .getSignatureOrTimestamp()
            .get(0)
            .getQualificationDetails()
            .getError();

    assertThat(
        errors.size() == 1
            && "Unable to build a certificate chain up to a trusted list!"
                .equals(errors.get(0).getValue()));

    List<XmlMessage> warnings =
        wsReportsDTO
            .getSimpleReport()
            .getSignatureOrTimestamp()
            .get(0)
            .getQualificationDetails()
            .getWarning();

    assertThat(
        warnings.size() == 1
            && "The signature/seal is an INDETERMINATE AdES digital signature!"
                .equals(warnings.get(0).getValue()));
  }

  @Test
  void ValidateDocumentEmptyOrMissingBytes() throws Exception {

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes("");
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    //         case where the bytes field is not present
    signedDocument.setBytes(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseMissingField);

    for (MockHttpServletResponse response : errorResponses) {
      APIError apiError =
          this.objectMapper.readValue(response.getContentAsString(), APIError.class);
      assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody()).isNotNull();
      assertThat(apiError.getApiErrorBody().getMessage())
          .isEqualTo("Field signedDocument.bytes cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void ValidateDocumentEmptyOrMissingName() throws Exception {

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes("b");
    signedDocument.setName("");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    //         case where the bytes field is not present
    signedDocument.setName(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseMissingField);

    for (MockHttpServletResponse response : errorResponses) {
      APIError apiError =
          this.objectMapper.readValue(response.getContentAsString(), APIError.class);
      assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody()).isNotNull();
      assertThat(apiError.getApiErrorBody().getMessage())
          .isEqualTo("Field signedDocument.name cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void ValidateDocumentInvalidBASE64Bytes() throws Exception {

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes("b");
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    MockHttpServletResponse resp =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError = this.objectMapper.readValue(resp.getContentAsString(), APIError.class);
    assertThat(resp.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Field toSignDocument.bytes should be encoded in base64 format");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void LOTLOnlineDataLoaderAccessSuccess() throws Exception {

    // Make sure the data loader can at least access all the following urls
    this.documentValidatorLOTL
        .onlineLOTLDataLoader()
        .get("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
    CommonsDataLoader r = this.documentValidatorLOTL.onlineLOTLDataLoader();

    // note that this will fail if run with Java < 1.8.0_261 e.g. this test passes with AdoptOpenJDK
    // jdk8u362-b09
    r.setSslProtocol("TLSv1.3");
    r.get("https://ssi.gouv.fr/uploads/tl-fr.xml");
    this.documentValidatorLOTL
        .onlineLOTLDataLoader()
        .get(
            "https://www.agentschaptelecom.nl/binaries/agentschap-telecom/documenten/publicaties/2018/januari/01/digitale-statuslijst-van-vertrouwensdiensten/current-tsl.xml");
  }

  @Test
  void ValidateDocumentEIDASUnsigned() throws Exception {

    InputStream isPdf =
        DocumentValidationTests.class.getResourceAsStream(
            "/sample-pdfs/".concat("test_unsigned.pdf"));

    byte[] bytesPdf = IOUtils.toByteArray(isPdf);
    String base64encodedPdf = Base64.getEncoder().encodeToString(bytesPdf);

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes(base64encodedPdf);
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    MockHttpServletResponse resp =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK.value());

    WSReportsDTO wsReportsDTO =
        this.validateDocumentService.validateDocument(
            validateDocumentRequestDto.getSignedDocument().getBytes(),
            validateDocumentRequestDto.getSignedDocument().getName());

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().size()).isEqualTo(0);
  }

  @Test
  void ValidateDocumentEIDASSignedSHA1() throws Exception {

    InputStream isPdf =
        DocumentValidationTests.class.getResourceAsStream(
            "/sample-pdfs/".concat("test_signed_SHA1.pdf"));

    byte[] bytesPdf = IOUtils.toByteArray(isPdf);
    String base64encodedPdf = Base64.getEncoder().encodeToString(bytesPdf);

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes(base64encodedPdf);
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    MockHttpServletResponse resp =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK.value());

    WSReportsDTO wsReportsDTO =
        this.validateDocumentService.validateDocument(
            validateDocumentRequestDto.getSignedDocument().getBytes(),
            validateDocumentRequestDto.getSignedDocument().getName());

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().size()).isEqualTo(1);

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getIndication())
        .isEqualTo(Indication.INDETERMINATE);

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getSubIndication())
        .isEqualTo(SubIndication.CRYPTO_CONSTRAINTS_FAILURE_NO_POE);

    List<XmlMessage> warnings =
        wsReportsDTO
            .getSimpleReport()
            .getSignatureOrTimestamp()
            .get(0)
            .getQualificationDetails()
            .getWarning();

    assertTrue(
        warnings.size() == 1
            && "The signature/seal is an INDETERMINATE AdES digital signature!"
                .equals(warnings.get(0).getValue()));
  }

  @Test
  void ValidateDocumentESealed() throws Exception {

    InputStream isPdf =
        DocumentValidationTests.class.getResourceAsStream(
            "/sample-pdfs/".concat("test_receipt_esealed.pdf"));

    byte[] bytesPdf = IOUtils.toByteArray(isPdf);
    String base64encodedPdf = Base64.getEncoder().encodeToString(bytesPdf);

    // Valid request body but with empty bytes field
    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
    SignedDocument signedDocument = new SignedDocument();
    signedDocument.setBytes(base64encodedPdf);
    signedDocument.setName("random-name");
    validateDocumentRequestDto.setSignedDocument(signedDocument);

    MockHttpServletResponse resp =
        this.mockMvc
            .perform(
                post(this.validationPath)
                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK.value());

    WSReportsDTO wsReportsDTO =
        this.validateDocumentService.validateDocument(
            validateDocumentRequestDto.getSignedDocument().getBytes(),
            validateDocumentRequestDto.getSignedDocument().getName());

    // DSS version 5.9: adESValidationDetails will contain errors including :
    // The algorithm RSA with key size 2048 is no longer considered reliable for revocation data
    // signature (ASCCM_AR_ANS_AKSNR)
    // while DSS version 5.11 will not
    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().size()).isEqualTo(1);

    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getIndication())
        .isEqualTo(Indication.TOTAL_PASSED);

    assertTrue(
        wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getAdESValidationDetails()
            == null);

    assertTrue(wsReportsDTO.getSimpleReport().getValidSignaturesCount() == 1);
  }
}
