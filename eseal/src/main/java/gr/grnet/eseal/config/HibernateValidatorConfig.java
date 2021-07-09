package gr.grnet.eseal.config;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateValidatorConfig {

  @Bean
  public Validator validator() {
    ValidatorFactory validatorFactory =
        Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
    return validatorFactory.getValidator();
  }
}
