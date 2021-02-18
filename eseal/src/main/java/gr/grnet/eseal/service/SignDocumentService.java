package gr.grnet.eseal.service;

import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignDocumentService {

    private RemoteHttpEsealClient remoteHttpEsealClient;

    @Autowired
    public SignDocumentService(RemoteHttpEsealClient remoteHttpEsealClient) {
        this.remoteHttpEsealClient = remoteHttpEsealClient;
    }

    public String signDocument(String document, String username, String password, String key) {
        return this.remoteHttpEsealClient.sign(document, username, password, key);
    }

}
