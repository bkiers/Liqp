package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import liqp.TemplateContext;
import liqp.filters.date.CustomDateFormatSupport;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Java7DateTypesSupport extends BasicTypesSupport {

    @Override
    public void configureTypes(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp java 7 date type support");

        registerType(module, Date.class);
        registerType(module, Calendar.class);
        mapper.registerModule(module);

        addCustomDateType(new CustomDateFormatSupport<Date>() {

            @Override
            public ZonedDateTime getValue(Date value) {
                return ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault());
            }

            @Override
            public boolean support(Object in) {
                return in instanceof Date;
            }
        });
    }
}
