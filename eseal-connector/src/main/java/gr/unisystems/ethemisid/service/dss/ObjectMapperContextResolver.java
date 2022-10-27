package gr.unisystems.ethemisid.service.dss;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper mapper;
    
    public ObjectMapperContextResolver() {
        mapper = ObjectMapperContextResolver.createMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
    
    public static ObjectMapper createMapper() {
        ObjectMapper ret = new ObjectMapper();
        ret.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        ret.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        return ret;
    }
}