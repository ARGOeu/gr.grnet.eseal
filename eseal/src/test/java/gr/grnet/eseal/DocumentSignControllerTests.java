package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.api.v1.DocumentSignController;
import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.dto.ToSignDocument;
import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.service.RemoteSignDocumentService;
import gr.grnet.eseal.service.SignDocumentServiceFactory;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentSignController.class)
@ContextConfiguration(
    classes = {EsealApplication.class, DocumentSignControllerTests.TestConfig.class})
class DocumentSignControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private RemoteSignDocumentService remoteSignDocumentService;

  @MockBean private SignDocumentServiceFactory signDocumentServiceFactory;

  @MockBean private RemoteProviderProperties remoteProviderProperties;

  @MockBean private RemoteProviderCertificates remoteProviderCertificates;

  private final String signingPath = "/api/v1/signing/remoteSignDocument";

  private ObjectMapper objectMapper = new ObjectMapper();

  @TestConfiguration
  static class TestConfig {
    @Bean
    public Validator validator() {
      ValidatorFactory validatorFactory =
          Validation.byProvider(HibernateValidator.class)
              .configure()
              .failFast(true)
              .buildValidatorFactory();
      return validatorFactory.getValidator();
    }
  }

  @Test
  void SignDocumentSuccess() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    when(this.signDocumentServiceFactory.create(any())).thenReturn(remoteSignDocumentService);

    // mock the service response
    when(this.remoteSignDocumentService.signDocument(any())).thenReturn("cmFuZG9tLWJ5dGVz");

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    SignDocumentResponseDto signDocumentResponseDto =
        this.objectMapper.readValue(response.getContentAsString(), SignDocumentResponseDto.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(signDocumentResponseDto.getSignedDocumentBytes()).isEqualTo("cmFuZG9tLWJ5dGVz");
  }

  @Test
  void SignDocumentInternalError() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    when(this.signDocumentServiceFactory.create(any())).thenReturn(remoteSignDocumentService);

    // mock the service response
    when(this.remoteSignDocumentService.signDocument(any()))
        .thenAnswer(
            invocation -> {
              throw new InternalServerErrorException("Internal error");
            });

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError = this.objectMapper.readValue(response.getContentAsString(), APIError.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage()).isEqualTo("Internal error");
    assertThat(apiError.getApiErrorBody().getCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void SignDocumentEmptyOrMissingUsername() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    // case where the username field is present but empty
    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    // case where the username is not present
    signDocumentRequestDto.setUsername(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
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
          .isEqualTo("Field username cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void SignDocumentEmptyOrMissingPassword() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    // case where the password field is present but empty
    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    // case where the password is not present
    signDocumentRequestDto.setPassword(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
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
          .isEqualTo("Field password cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void SignDocumentEmptyOrMissingKey() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    // case where the key field is present but empty
    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    // case where the key is not present
    signDocumentRequestDto.setKey(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
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
      assertThat(apiError.getApiErrorBody().getMessage()).isEqualTo("Field key cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void SignDocumentEmptyOrMissingBytes() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    // case where the bytes field is present but empty
    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    // case where the bytes is not present
    toSignDocument.setBytes(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
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
          .isEqualTo("Field toSignDocument.bytes cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void SignDocumentEmptyOrMissingName() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("cmFuZG9tLWJ5dGVz");
    toSignDocument.setName("");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    // case where the name field is present but empty
    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();
    errorResponses.add(responseEmptyField);

    // case where the name is not present
    toSignDocument.setName(null);
    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
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
          .isEqualTo("Field toSignDocument.name cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void SignDocumentMissingToSignDocument() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    signDocumentRequestDto.setToSignDocument(null);

    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError =
        this.objectMapper.readValue(responseMissingField.getContentAsString(), APIError.class);
    assertThat(responseMissingField.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Field toSignDocument cannot be empty");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void SignDocumentNotEncodedToSignDocument() throws Exception {

    // case where the bytes field is not base64 encoded
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("random-bytes");
    toSignDocument.setName("notBase64");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content(this.objectMapper.writeValueAsBytes(signDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError =
        this.objectMapper.readValue(responseMissingField.getContentAsString(), APIError.class);
    assertThat(responseMissingField.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Field toSignDocument.bytes should be encoded in base64 format");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void SignDocumentMalformedJSONBody() throws Exception {

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.signingPath)
                    .content("{")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError = this.objectMapper.readValue(response.getContentAsString(), APIError.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage()).isEqualTo("Malformed JSON body");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void SignDocumentNoContentTypeHeader() throws Exception {

    MockHttpServletResponse response =
        this.mockMvc.perform(post(this.signingPath).content("{}")).andReturn().getResponse();

    APIError apiError = this.objectMapper.readValue(response.getContentAsString(), APIError.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo(
            "Content type 'application/octet-stream' "
                + "not supported. Using Content Type 'application/json' instead.");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
