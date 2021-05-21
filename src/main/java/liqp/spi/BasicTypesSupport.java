package liqp.spi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import liqp.filters.date.CustomDateFormatRegistry;
import liqp.filters.date.CustomDateFormatSupport;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasicTypesSupport implements TypesSupport {

    private static ThreadLocal<Map<String, Object>> local = ThreadLocal.withInitial(ConcurrentHashMap::new);

    
    protected<T> void registerType(SimpleModule module, final Class<T> clazz) {
        // we put the ref to object here for restoring it by the ref later
        // so we will preserve the object in case of eager evaluation
        // and will put it back when needed
        module.addSerializer(new StdSerializer<T>(clazz) {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeStartObject();
                gen.writeBooleanField("@supportedTypeMarker", true);
                gen.writeStringField("@ref", createReference(value));
                gen.writeEndObject();
            }
        });
    }

    protected void addCustomDateType(CustomDateFormatSupport typeSupport) {
        if (!CustomDateFormatRegistry.isRegistered(typeSupport)) {
            CustomDateFormatRegistry.add(typeSupport);
        }
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
        Object ref = mapObj.get("@ref");
        if (!(ref instanceof String)) {
            // improperly formatted objects will be returned as is
            return obj;
        }
        return getByReference((String) ref);
    }



    public static String createReference(Object obj) {
        String key = Thread.currentThread().hashCode() + ":" + System.currentTimeMillis() + ":" + obj.hashCode();
        local.get().put(key, obj);
        return key;
    }

    public static <TT> TT getByReference(String key) {
        return (TT)local.get().remove(key);
    }

    public static void clearReferences(){
        local.get().clear();
    }

}
