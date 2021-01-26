package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import liqp.filters.date.CustomDateFormatSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicTypesSupport implements TypesSupport {

    @SuppressWarnings("rawtypes")
    private static final Map<String, TypeConvertor> typeRegistry = new HashMap<>();

    protected<T> void registerType(SimpleModule module, final Class<T> clazz, final TypeConvertor<T> typeGenerator) {
        module.addSerializer(new StdSerializer<T>(clazz) {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeStartObject();
                gen.writeBooleanField("@supportedTypeMarker", true);
                gen.writeStringField("@type", clazz.getName());
                gen.writeFieldName("@data");
                typeGenerator.serialize(gen, value);
                gen.writeEndObject();
            }
        });
        typeRegistry.put(clazz.getName(), typeGenerator);
    }

    protected void addCustomDateType(CustomDateFormatSupport<?> typeSupport) {
        liqp.filters.Date.addCustomDateType(typeSupport);
    }

    public static Object restoreObject(Object obj) {
        if (! (obj instanceof Map)) {
            return obj;
        }
        //noinspection rawtypes
        Map mapObj = (Map) obj;
        if (!Boolean.TRUE.equals(mapObj.get("@supportedTypeMarker"))) {
            return obj;
        }
        Object typeName = mapObj.get("@type");
        if (!(typeName instanceof String)) {
            // improperly formatted objects will be returned as is
            return obj;
        }
        //noinspection rawtypes
        TypeConvertor typeConvertor = typeRegistry.get(typeName);
        if (typeConvertor == null) {
            // missing type converted will be treated as error
            // and cause fallback to returning object as is
            return obj;
        }
        Object dataMapObj = mapObj.get("@data");
        if (!(dataMapObj instanceof Map)) {
            return obj;
        }
        //noinspection rawtypes
        return typeConvertor.deserialize((Map)dataMapObj);
    }


}
