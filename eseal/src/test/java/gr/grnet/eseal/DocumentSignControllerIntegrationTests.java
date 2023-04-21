package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.dto.ToSignDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.apache.pdfbox.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class DocumentSignControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;

  private final String signingPath = "/api/v1/signing/remoteSignDocument";

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void SignDocumentSuccess() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername("TESTNOC1-9894");
    signDocumentRequestDto.setPassword("testuser1");
    signDocumentRequestDto.setKey("TTIIQ5CZMJBWVOAVNG35FIWD522I53SQ2ZYEVM47A5LCTPSX5E5Q");
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes(makeSampleFileBase64());
    toSignDocument.setName("random-name.pdf");
    signDocumentRequestDto.setToSignDocument(toSignDocument);

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
    assertThat(signDocumentResponseDto.getSignedDocumentBytes().length()).isGreaterThan(0);
  }

  private String makeSampleFileBase64() throws IOException {
    InputStream isPdf =
        DocumentValidationTests.class.getResourceAsStream(
            "/sample-pdfs/".concat("test_unsigned.pdf"));

    byte[] bytesPdf = IOUtils.toByteArray(isPdf);
    String base64encodedPdf = Base64.getEncoder().encodeToString(bytesPdf);
    return base64encodedPdf;
  }
}
