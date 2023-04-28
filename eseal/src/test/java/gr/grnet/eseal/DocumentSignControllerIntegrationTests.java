package gr.grnet.eseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.eseal.dto.SignDocumentDetachedRequestDto;
import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.dto.ToSignDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * These tests are written as an example - they should not be a part of normal build process and
 * should be run explicitly on demand - note that specific username and password should get
 * requested from HARICA in order for these tests to run properly
 * HARICA service is designed in a way that locks the account on 5 consecutive wrong passwords
 *
 * @author ZhukovA
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class DocumentSignControllerIntegrationTests {

  private final String _username = "TEST___-9894";
  private final String _password = "test";
  private final String _key = "TTIIQ5CZMJBWVOAVNG35FIWD522I53SQ2ZYEVM47A5LCTPSX5E5Q";

  @Autowired private MockMvc mockMvc;

  private final String signingPath = "/api/v1/signing/remoteSignDocument";
  private final String signingDetachedPath = "/api/v1/signing/remoteSignDocumentDetached";

  private ObjectMapper objectMapper = new ObjectMapper();

  // @Test
  void SignDocumentDetatchedSuccess() throws Exception {

    // Valid request body
    SignDocumentDetachedRequestDto req = new SignDocumentDetachedRequestDto();
    req.setUsername(_username);
    req.setPassword(_password);
    req.setKey(_key);
    ToSignDocument toSignDocument = new ToSignDocument();
    toSignDocument.setBytes(makeSampleFileBase64());
    toSignDocument.setName("random-name.pdf");
    req.setToSignDocument(toSignDocument);

    MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(this.signingDetachedPath)
                    .content(this.objectMapper.writeValueAsBytes(req))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    SignDocumentResponseDto signDocumentResponseDto =
        this.objectMapper.readValue(response.getContentAsString(), SignDocumentResponseDto.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(signDocumentResponseDto.getSignedDocumentBytes().length()).isGreaterThan(0);
  }

  //  @Test
  void SignDocumentSuccess() throws Exception {

    // Valid request body
    SignDocumentRequestDto signDocumentRequestDto = new SignDocumentRequestDto();
    signDocumentRequestDto.setUsername(_username);
    signDocumentRequestDto.setPassword(_password);
    signDocumentRequestDto.setKey(_key);
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
