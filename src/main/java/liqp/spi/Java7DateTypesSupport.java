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

        // todo: check impl from sql package
        registerType(module, Date.class, new TypeConvertor<Date>(){
            @Override
            public void serialize(JsonGenerator gen, Date val) throws IOException {
                gen.writeNumberField("val", val.getTime());
            }

            @Override
            public Date deserialize(TemplateContext context, Map node) {
                long val = (Long) node.get("val");
                return new Date(val);
            }

        });

        registerType(module, Calendar.class, new TypeConvertor<Calendar>() {
            @Override
            public void serialize(JsonGenerator gen, Calendar val) throws IOException {
                gen.writeNumberField("val", val.getTimeInMillis());
                gen.writeStringField("zone", val.getTimeZone().getID());
            }

            @Override
            public Calendar deserialize(TemplateContext context, Map node) {
                long val = (Long) node.get("val");
                String zone = (String) node.get("zone");
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone), context.renderSettings.locale);
                calendar.setTimeInMillis(val);
                return calendar;
            }
        });
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
