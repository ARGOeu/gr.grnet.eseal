package gr.grnet.eseal.config;

import gr.grnet.eseal.timestamp.TSASourcePropertiesWithBasicAuth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "eseal.tsa.harica")
public class HaricaTSASourceProperties extends TSASourcePropertiesWithBasicAuth {}
