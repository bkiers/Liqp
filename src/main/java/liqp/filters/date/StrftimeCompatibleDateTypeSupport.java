package liqp.filters.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import liqp.TemplateContext;
import liqp.spi.BasicTypesSupport;
import liqp.spi.TypeConvertor;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

// self-reflection:
// do not prevent users from use this library internal types as
// date/time type transport.
public class StrftimeCompatibleDateTypeSupport extends BasicTypesSupport {
    @Override
    public void configureTypes(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp internal date type support");

        registerType(module, ZonedDateTime.class, new TypeConvertor<ZonedDateTime>(){
            @Override
            public void serialize(JsonGenerator gen, ZonedDateTime val) throws IOException {
                gen.writeNumberField("val", val.toInstant().toEpochMilli());
                gen.writeStringField("zone", val.getZone().getId());
            }

            @Override
            public ZonedDateTime deserialize(TemplateContext context, Map node) {
                long val = (Long) node.get("val");
                Instant inst = Instant.ofEpochMilli(val);
                ZoneId zone = ZoneId.of((String) node.get("zone"));
                return ZonedDateTime.ofInstant(inst, zone);
            }

        });
        mapper.registerModule(module);

        addCustomDateType(new CustomDateFormatSupport<ZonedDateTime>() {

            @Override
            public ZonedDateTime getValue(ZonedDateTime value) {
                return value;
            }

            @Override
            public boolean support(Object in) {
                return in instanceof ZonedDateTime;
            }
        });
    }
}
