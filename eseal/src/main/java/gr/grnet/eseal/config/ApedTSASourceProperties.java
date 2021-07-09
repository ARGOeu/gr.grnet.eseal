package gr.grnet.eseal.config;

import gr.grnet.eseal.timestamp.DefaultTSASourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eseal.tsa.aped")
public class ApedTSASourceProperties extends DefaultTSASourceProperties {}
