package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatSupport;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Map;


public class Java8DateTypesSupport extends BasicTypesSupport {
    @Override
    public void configureTypesForReferencing(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp java 8 date type support");
        registerType(module, Temporal.class);
        mapper.registerModule(module);
    }
}
