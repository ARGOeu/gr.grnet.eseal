package gr.grnet.eseal.dto;

import eu.europa.esig.dss.model.x509.CertificateToken;
import gr.grnet.eseal.enums.VisibleSignatureText;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignDocumentDto {

  private String username;
  private String password;
  private String key;
  private String bytes;
  private String name;
  private String imageBytes = "";
  private Boolean imageVisibility;
  private Date signingDate;
  private String signerInfo;
  List<CertificateToken> certificateList;
  private VisibleSignatureText visibleSignatureText;
}
