package liqp.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

@JsonSerialize(using = LiquidSupport.LiquidSerializer.class)
public interface LiquidSupport extends Inspectable {
    Map<String, Object> toLiquid();


    class LiquidSerializer extends StdSerializer<LiquidSupport> {

        public LiquidSerializer() {
            this(null);
        }

        protected LiquidSerializer(Class<LiquidSupport> t) {
            super(t);
        }

        @Override
        public void serialize(LiquidSupport item,
                              JsonGenerator jsgen, SerializerProvider provider) throws IOException {
            Map<String, Object> data = item.toLiquid();
            jsgen.writeObject(data);
        }
    }
}
