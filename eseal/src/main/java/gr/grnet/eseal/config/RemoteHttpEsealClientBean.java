package gr.grnet.eseal.config;

import gr.grnet.eseal.sign.RemoteHttpEsealClient;
import gr.grnet.eseal.sign.RemoteProviderHttpEsealClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RemoteHttpEsealClientBean is a {@link Bean} responsible for the exposure of the http client
 * that will take care of signing and timestamping documents,
 * using a remote provider
 */
@Configuration
public class RemoteHttpEsealClientBean {

    private final RemoteProviderProperties remoteProviderProperties;

    @Autowired
    public RemoteHttpEsealClientBean(RemoteProviderProperties remoteProviderProperties) {
        this.remoteProviderProperties = remoteProviderProperties;
    }

    @Bean
    public RemoteHttpEsealClient remoteHttpEsealClient() throws Exception {
        return new RemoteProviderHttpEsealClient(this.remoteProviderProperties);
    }

}
