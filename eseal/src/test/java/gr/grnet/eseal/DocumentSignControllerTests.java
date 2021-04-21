package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.api.v1.DocumentSignController;
import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.dto.ToSignDocument;
import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.service.SignDocumentService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentSignController.class)
class DocumentSignControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private SignDocumentService signDocumentService;

  private final String signingPath = "/api/v1/signing/remoteSignDocument";

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void SignDocumentSuccess() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("random-bytes");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    // mock the service response
    when(this.signDocumentService.signDocument("random-bytes", "u1", "p1", "k1"))
        .thenReturn("random-bytes");

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
    assertThat(signDocumentResponseDto.getSignedDocumentBytes()).isEqualTo("random-bytes");
  }

  @Test
  void SignDocumentInternalError() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("u1");
    signDocumentRequestDto.setPassword("p1");
    signDocumentRequestDto.setKey("k1");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes("random-bytes");
    toSignDocument.setName("random-name");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

    // mock the service response
    when(this.signDocumentService.signDocument("random-bytes", "u1", "p1", "k1"))
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
    toSignDocument.setBytes("random-bytes");
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
    toSignDocument.setBytes("random-bytes");
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
    toSignDocument.setBytes("random-bytes");
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
    toSignDocument.setBytes("random-bytes");
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
