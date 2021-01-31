package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import liqp.TemplateContext;

import java.io.IOException;
import java.util.Map;

public interface TypeConvertor<T> {
    void serialize(JsonGenerator gen, T val) throws IOException;
    T deserialize(TemplateContext context, Map node);
}
