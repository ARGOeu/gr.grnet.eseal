package gr.grnet.eseal.config;

import gr.grnet.eseal.validation.DocumentValidatorLOTL;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * DocumentValidatorLOTLBean is a {@link Bean} responsible for
 * the exposure of the DocumentValidatorLOTL that will be
 * used by the validation service
 */
@Setter
@Getter
@Configuration
@EnableScheduling
public class DocumentValidatorLOTLBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentValidatorLOTLBean.class);


    private DocumentValidatorLOTL lotlValidator;

    private int refreshCounter = 1;

    /**
     * Enable online refresh on intervals
     */
    @Value("${eseal.validation.lotl.refresh.enable}")
    private Boolean lotlRefreshEnable;

    @Value("${eseal.validation.lotl.refresh.initial.delay}")
    private int refreshInitialDelay;

    @Value("${eseal.validation.lotl.refresh.interval}")
    private int refreshInterval;

    @Autowired
    public DocumentValidatorLOTLBean(ValidationProperties validationProperties) {
        this.lotlValidator = new DocumentValidatorLOTL(validationProperties);
        this.lotlValidator.initialize();
    }

    @Bean
    public DocumentValidatorLOTL lotlValidator() {
        return this.lotlValidator;
    }

    @Scheduled(
            initialDelayString = "${eseal.validation.lotl.refresh.initial.delay}",
            fixedDelayString = "${eseal.validation.lotl.refresh.interval}"
    )
    public void refreshLOTL() {
        if (this.lotlRefreshEnable) {
            LOGGER.info("Running online refresh for the " + refreshCounter + " time...");
            this.lotlValidator().onlineLOTLRefresh();
            refreshCounter++;
        }
    }
}
