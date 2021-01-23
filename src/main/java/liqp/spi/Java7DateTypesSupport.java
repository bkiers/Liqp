package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class Java7DateTypesSupport extends BasicTypesSupport {

    @Override
    public void configureTypes(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("liqp java 7 date type support");

        // todo: check impl from sql package
        registerType(module, Date.class, new TypeConvertor<Date>(){
            @Override
            public void serialize(JsonGenerator gen, Date val) throws IOException {
                gen.writeStringField("val", String.valueOf(val.getTime()));
            }

            @Override
            public Date deserialize(Map node) {
                String strVal = (String) node.get("val");
                long val = Long.parseLong(strVal);
                return new Date(val);
            }

        });
        mapper.registerModule(module);

        addCustomDateType(new liqp.filters.Date.CustomDateFormatSupport<Date>() {
            @Override
            public Long getAsSeconds(Date value) {
                return value.getTime() / 1000;
            }

            @Override
            public boolean support(Object in) {
                return in instanceof Date;
            }
        });
    }
}
