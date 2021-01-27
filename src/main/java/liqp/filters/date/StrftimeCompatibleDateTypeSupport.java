package liqp.filters.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import liqp.spi.BasicTypesSupport;
import liqp.spi.TypeConvertor;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

// self-reflection:
// do not prevent users from use this library internal types as
// date/time type transport.
public class StrftimeCompatibleDateTypeSupport extends BasicTypesSupport {
    @Override
    public void configureTypes(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp internal date type support");

        registerType(module, StrftimeCompatibleDate.class, new TypeConvertor<StrftimeCompatibleDate>(){
            @Override
            public void serialize(JsonGenerator gen, StrftimeCompatibleDate val) throws IOException {
                gen.writeStringField("val", String.valueOf(val.getDate()));
                gen.writeStringField("zone", String.valueOf(val.getZoneId()));
            }

            @Override
            public StrftimeCompatibleDate deserialize(Map node) {
                String strVal = (String) node.get("val");
                long val = Long.parseLong(strVal);
                String strZone = (String) node.get("zone");
                TimeZone timeZone = strZone == null ? null : TimeZone.getTimeZone(strZone);
                return new StrftimeCompatibleDate(val, timeZone);
            }

        });
        mapper.registerModule(module);

        addCustomDateType(new CustomDateFormatSupport<StrftimeCompatibleDate>() {

            @Override
            public StrftimeCompatibleDate getValue(StrftimeCompatibleDate value) {
                return value;
            }

            @Override
            public boolean support(Object in) {
                return in instanceof StrftimeCompatibleDate;
            }
        });
    }
}
