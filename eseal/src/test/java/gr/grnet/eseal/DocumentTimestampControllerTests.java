package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.api.v1.DocumentTimestampController;
import gr.grnet.eseal.dto.TimestampDocumentRequestDto;
import gr.grnet.eseal.dto.TimestampDocumentResponseDto;
import gr.grnet.eseal.enums.TSASourceEnum;
import gr.grnet.eseal.exception.APIError;
import gr.grnet.eseal.exception.InternalServerErrorException;
import gr.grnet.eseal.service.TimestampDocumentService;
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

@WebMvcTest(DocumentTimestampController.class)
@ContextConfiguration(
    classes = {EsealApplication.class, DocumentTimestampControllerTests.TestConfig.class})
public class DocumentTimestampControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private TimestampDocumentService timestampDocumentService;

  private final String timestampingPath = "/api/v1/timestamping/remoteTimestampDocument";

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
  void TimestampDocumentSuccess() throws Exception {

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("random-name");
    toTimestampDocument.setBytes("cmFuZG9tLWJ5dGVz");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    // mock the service response
    when(this.timestampDocumentService.timestampDocument(
            "cmFuZG9tLWJ5dGVz", TSASourceEnum.valueOf(timestampDocumentRequestDto.getTsaSource())))
        .thenReturn("cmFuZG9tLWJ5dGVz");

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    TimestampDocumentResponseDto timestampDocumentResponseDto =
        this.objectMapper.readValue(
            response.getContentAsString(), TimestampDocumentResponseDto.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(timestampDocumentResponseDto.getTimestampedDocumentBytes())
        .isEqualTo("cmFuZG9tLWJ5dGVz");
  }

  @Test
  void TimestampDocumentInternalError() throws Exception {

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("random-name");
    toTimestampDocument.setBytes("cmFuZG9tLWJ5dGVz");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    // mock the service response
    when(this.timestampDocumentService.timestampDocument(
            "cmFuZG9tLWJ5dGVz", TSASourceEnum.valueOf(timestampDocumentRequestDto.getTsaSource())))
        .thenAnswer(
            invocation -> {
              throw new InternalServerErrorException("Internal error");
            });

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
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
  void TimestampDocumentEmptyOrMissingBytes() throws Exception {

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("random-name");
    toTimestampDocument.setBytes("");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    errorResponses.add(responseEmptyField);

    toTimestampDocument.setBytes(null);

    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
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
          .isEqualTo("Field toTimestampDocument.bytes cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void TimestampDocumentEmptyOrMissingName() throws Exception {

    List<MockHttpServletResponse> errorResponses = new ArrayList<>();

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("");
    toTimestampDocument.setBytes("cmFuZG9tLWJ5dGVz");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    // mock the service response
    when(this.timestampDocumentService.timestampDocument(
            "cmFuZG9tLWJ5dGVz", TSASourceEnum.valueOf(timestampDocumentRequestDto.getTsaSource())))
        .thenReturn("cmFuZG9tLWJ5dGVz");

    MockHttpServletResponse responseEmptyField =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    errorResponses.add(responseEmptyField);

    toTimestampDocument.setName(null);

    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
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
          .isEqualTo("Field toTimestampDocument.name cannot be empty");
      assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void TimestampDocumentMissingToTimestampDocument() throws Exception {

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    MockHttpServletResponse responseMissingToTimestampDocument =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError =
        this.objectMapper.readValue(
            responseMissingToTimestampDocument.getContentAsString(), APIError.class);
    assertThat(responseMissingToTimestampDocument.getStatus())
        .isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Field toTimestampDocument cannot be empty");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void TimestampDocumentNotEncodedToSignDocument() throws Exception {

    // case where the bytes field is not base64 encoded
    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("random-name");
    toTimestampDocument.setBytes("random-bytes");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    MockHttpServletResponse responseMissingField =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError =
        this.objectMapper.readValue(responseMissingField.getContentAsString(), APIError.class);
    assertThat(responseMissingField.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Field toTimestampDocument.bytes should be encoded in base64 format");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void TimestampDocumentInvalidTSPSource() throws Exception {

    TimestampDocumentRequestDto timestampDocumentRequestDto = new TimestampDocumentRequestDto();

    TimestampDocumentRequestDto.ToTimestampDocument toTimestampDocument =
        new TimestampDocumentRequestDto.ToTimestampDocument();
    toTimestampDocument.setName("name");
    toTimestampDocument.setBytes("cmFuZG9tLWJ5dGVz");

    timestampDocumentRequestDto.setTsaSource("test");

    timestampDocumentRequestDto.setToTimestampDocument(toTimestampDocument);

    MockHttpServletResponse responseInvalidTSPSouce =
        this.mockMvc
            .perform(
                post(this.timestampingPath)
                    .content(this.objectMapper.writeValueAsBytes(timestampDocumentRequestDto))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    APIError apiError =
        this.objectMapper.readValue(responseInvalidTSPSouce.getContentAsString(), APIError.class);
    assertThat(responseInvalidTSPSouce.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody()).isNotNull();
    assertThat(apiError.getApiErrorBody().getMessage())
        .isEqualTo("Possible values of property tsaSource are [APED, HARICA]");
    assertThat(apiError.getApiErrorBody().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(apiError.getApiErrorBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
