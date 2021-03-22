package gr.grnet.eseal.sign;

/**
 * Interface that represents clients that can access remote qualified e-seals and use them to sign
 * documents
 */
public interface RemoteHttpEsealClient {

  String sign(String document, String username, String password, String key);
}
