package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Map;

public interface TypeConvertor<T> {
    void serialize(JsonGenerator gen, T val) throws IOException;
    T deserialize(Map node);
}
