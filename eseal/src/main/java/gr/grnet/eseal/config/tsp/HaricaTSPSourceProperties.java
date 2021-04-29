package gr.grnet.eseal.config.tsp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eseal.tsp.harica")
public class HaricaTSPSourceProperties extends TSPSourceProperties {}
