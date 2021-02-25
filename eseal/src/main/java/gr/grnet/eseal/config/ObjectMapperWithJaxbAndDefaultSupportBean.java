package gr.grnet.eseal.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Exposes an {@link ObjectMapper} {@link Bean} in order to override Spring's default.
 * The new mapper supports both, {@link JacksonAnnotationIntrospector} as a primary
 * and {@link JaxbAnnotationIntrospector} as a secondary.
 */
@Configuration
public class ObjectMapperWithJaxbAndDefaultSupportBean {

    @Bean
    @Primary
    public ObjectMapper objectMapperWithJaxbAndDefaultSupport() {

        ObjectMapper objectMapper = new ObjectMapper();

        // initialize the jaxb annotation introspector
        JaxbAnnotationIntrospector jaxbAnnotationIntrospector =
                new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());

        // initialize the default jackson annotation introspector
        JacksonAnnotationIntrospector  jacksonAnnotationIntrospector =  new JacksonAnnotationIntrospector();

        // set up the annotation introspector pair with primary being the default jackson introspector
        AnnotationIntrospectorPair annotationIntrospectorPair =
                new AnnotationIntrospectorPair(jacksonAnnotationIntrospector, jaxbAnnotationIntrospector);

        objectMapper.setAnnotationIntrospector(annotationIntrospectorPair);

        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

}
