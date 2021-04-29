package gr.grnet.eseal.config.tsp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eseal.tsp.aped")
public class ApedTSPSourceProperties extends TSPSourceProperties {}
