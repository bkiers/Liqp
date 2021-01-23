package liqp.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface TypesSupport {
    void configureTypes(ObjectMapper mapper);
}
