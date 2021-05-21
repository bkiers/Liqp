package liqp.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface TypesSupport {
    void configureTypesForReferencing(ObjectMapper mapper);
    default void configureCustomDateTypes() {
        
    }
}
