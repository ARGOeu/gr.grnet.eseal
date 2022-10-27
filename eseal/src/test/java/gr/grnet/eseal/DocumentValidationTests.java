package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.dto.SignedDocument;
import gr.grnet.eseal.dto.ValidateDocumentRequestDto;
import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.service.ValidateDocumentService;
import gr.grnet.eseal.validation.DocumentValidatorLOTL;
import java.util.ArrayList;
import java.util.List;
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

  //  @Test
  //  void ValidateDocumentSuccess() throws Exception {
  //
  //    InputStream isSignedPDF =
  //        DocumentValidationTests.class.getResourceAsStream(
  //            "/validation/".concat("signed-lta-b64-pdf.txt"));
  //
  //    String signedLTAPDF =
  //        new BufferedReader(new InputStreamReader(isSignedPDF, StandardCharsets.UTF_8))
  //            .lines()
  //            .collect(Collectors.joining("\n"));
  //
  //    // Valid request body but with empty bytes field
  //    ValidateDocumentRequestDto validateDocumentRequestDto = new ValidateDocumentRequestDto();
  //    SignedDocument signedDocument = new SignedDocument();
  //    signedDocument.setBytes(signedLTAPDF);
  //    signedDocument.setName("random-name");
  //    validateDocumentRequestDto.setSignedDocument(signedDocument);
  //
  //    MockHttpServletResponse resp =
  //        this.mockMvc
  //            .perform(
  //                post(this.validationPath)
  //                    .content(this.objectMapper.writeValueAsBytes(validateDocumentRequestDto))
  //                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
  //                    .accept(MediaType.APPLICATION_JSON))
  //            .andReturn()
  //            .getResponse();
  //
  //    assertThat(resp.getStatus()).isEqualTo(HttpStatus.OK.value());
  //
  //    WSReportsDTO wsReportsDTO =
  //        this.validateDocumentService.validateDocument(
  //            validateDocumentRequestDto.getSignedDocument().getBytes(),
  //            validateDocumentRequestDto.getSignedDocument().getName());
  //
  //    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().size()).isEqualTo(1);
  //    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getIndication())
  //        .isEqualTo(Indication.INDETERMINATE);
  //
  // assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getSubIndication())
  //        .isEqualTo(SubIndication.NO_CERTIFICATE_CHAIN_FOUND);
  //    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getErrors())
  //        .isEqualTo(
  //            Arrays.asList(
  //                "Unable to build a certificate chain until a trusted list!",
  //                "The result of the LTV validation process is not acceptable to continue the
  // process!",
  //                "The certificate chain for signature is not trusted, it does not contain a trust
  // anchor."));
  //    assertThat(wsReportsDTO.getSimpleReport().getSignatureOrTimestamp().get(0).getWarnings())
  //        .isEqualTo(Arrays.asList("The signature/seal is an INDETERMINATE AdES digital
  // signature!"));
  //  }

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
    this.documentValidatorLOTL
        .onlineLOTLDataLoader()
        .get("https://www.ssi.gouv.fr/eidas/TL-FR.xml");
    //
    // NOT ACCESSIBLE ANYMORE
    //    this.documentValidatorLOTL
    //        .onlineLOTLDataLoader()
    //        .get("https://sede.minetur.gob.es/Prestadores/TSL/TSL.xml");
    this.documentValidatorLOTL
        .onlineLOTLDataLoader()
        .get(
            "https://www.agentschaptelecom.nl/binaries/agentschap-telecom/documenten/publicaties/2018/januari/01/digitale-statuslijst-van-vertrouwensdiensten/current-tsl.xml");
  }
}
