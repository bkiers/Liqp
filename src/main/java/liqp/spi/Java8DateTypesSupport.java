package liqp.spi;

import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class Java8DateTypesSupport extends BasicTypesSupport {
    @Override
    public void configureTypesForReferencing(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp java 8 date type support");
        registerType(module, Temporal.class);
        mapper.registerModule(module);
    }
}
