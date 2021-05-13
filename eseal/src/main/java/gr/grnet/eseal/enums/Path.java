package gr.grnet.eseal.enums;

public enum Path {
  REMOTE_SIGNING("dsa/v1/sign"),
  REMOTE_SIGNING_BUFFER("dsa/v1/SignBuffer"),
  REMOTE_CERTIFICATES("dsa/v1/Certificates");

  public final String path;

  Path(String endpoint) {
    this.path = endpoint;
  }

  @Override
  public String toString() {
    return path;
  }
}
