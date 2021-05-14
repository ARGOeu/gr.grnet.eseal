package gr.grnet.eseal.api.v1;

import gr.grnet.eseal.config.RemoteProviderProperties;
import gr.grnet.eseal.dto.SignDocumentDetachedRequestDto;
import gr.grnet.eseal.dto.SignDocumentDto;
import gr.grnet.eseal.dto.SignDocumentRequestDto;
import gr.grnet.eseal.dto.SignDocumentResponseDto;
import gr.grnet.eseal.enums.Sign;
import gr.grnet.eseal.service.SignDocumentService;
import gr.grnet.eseal.service.SignDocumentServiceFactory;
import gr.grnet.eseal.sign.RemoteProviderCertificates;
import gr.grnet.eseal.sign.response.RemoteProviderCertificatesResponse;
import gr.grnet.eseal.utils.validation.Base64RequestFieldCheckGroup;
import gr.grnet.eseal.utils.validation.NotEmptySignDocumentRequestFieldsCheckGroup;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signing/")
public class DocumentSignController {

  private final SignDocumentServiceFactory signDocumentServiceFactory;
  private final RemoteProviderProperties remoteProviderProperties;
  private final RemoteProviderCertificates remoteProviderCertificates;

  @Autowired
  public DocumentSignController(
      SignDocumentServiceFactory signDocumentServiceFactory,
      RemoteProviderProperties remoteProviderProperties,
      RemoteProviderCertificates remoteProviderCertificates) {
    this.signDocumentServiceFactory = signDocumentServiceFactory;
    this.remoteProviderProperties = remoteProviderProperties;
    this.remoteProviderCertificates = remoteProviderCertificates;
  }

  @PostMapping("/remoteSignDocument")
  public SignDocumentResponseDto signDocument(
      @Validated(
              value = {
                NotEmptySignDocumentRequestFieldsCheckGroup.class,
                Base64RequestFieldCheckGroup.class
              })
          @RequestBody
          SignDocumentRequestDto signDocumentRequest) {

    return new SignDocumentResponseDto(
        this.signDocumentServiceFactory
            .create(Sign.REMOTE_SIGN)
            .signDocument(
                SignDocumentDto.builder()
                    .bytes(signDocumentRequest.getToSignDocument().getBytes())
                    .username(signDocumentRequest.getUsername())
                    .password(signDocumentRequest.getPassword())
                    .key(signDocumentRequest.getKey())
                    .build()));
  }

  @PostMapping("/remoteSignDocumentDetached")
  public SignDocumentResponseDto signDocumentDetached(
      @Validated(
              value = {
                NotEmptySignDocumentRequestFieldsCheckGroup.class,
                Base64RequestFieldCheckGroup.class
              })
          @RequestBody
          SignDocumentDetachedRequestDto signDocumentRequest) {

    RemoteProviderCertificatesResponse userCertificates =
        SignDocumentService.getUserCertificates(
            signDocumentRequest.getUsername(),
            signDocumentRequest.getPassword(),
            this.remoteProviderProperties.getEndpoint(),
            this.remoteProviderCertificates);

    return new SignDocumentResponseDto(
        this.signDocumentServiceFactory
            .create(Sign.PKCS1)
            .signDocument(
                SignDocumentDto.builder()
                    .key(signDocumentRequest.getKey())
                    .username(signDocumentRequest.getUsername())
                    .password(signDocumentRequest.getPassword())
                    .bytes(signDocumentRequest.getToSignDocument().getBytes())
                    .imageBytes(signDocumentRequest.getImageBytes())
                    .signingDate(new Date())
                    .signerInfo(SignDocumentService.getSignerInfo(userCertificates))
                    .certificateList(SignDocumentService.getCertificatesToken(userCertificates))
                    .build()));
  }
}
