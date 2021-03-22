package liqp.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import liqp.TemplateContext;
import liqp.spi.BasicTypesSupport;
import liqp.spi.SPIHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Special kind of {@link Inspectable} that do not perform
 * object conversion to <code>Map&lt;String, Object&gt;</code> using jackson mapper,
 * but instead uses result of method {@link #toLiquid()}.
 * So the objects of this kind can control with internal properties and under
 * what names should be accessible to parser.
 * For example, this Inspectable:
 * <pre>
 * new Inspectable() {
 *     public String myVar = "val";
 * }
 * </pre>
 * will gave same data tree as this LiquidSupport:
 * <pre>
 *  new LiquidSupport() {
 *      public String willBe = "omitted";
 *      private Thread evenNotSerializedOrMappable = new Thread();
 *      private Map&lt;String, Object&gt; data = new HashMap&lt;&gt;();
 *
 *      public Map&lt;String, Object&gt; toLiquid() {
 *          data.put("myVar", "val");
 *          return data;
 *      }
 * }
 * </pre>
 */
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

    /**
     * The implementation of converter from Inspectable to LiquidSupport.
     */
    class LiquidSupportFromInspectable implements LiquidSupport {
        public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {};
        private final TemplateContext context;
        private final Object variable;

        public LiquidSupportFromInspectable(TemplateContext context, Object variable) {
            this.variable = variable;
            this.context = context;
        }

        public static Map<String, Object> objectToMap(TemplateContext context, Object variables) {
            if (context == null) {
                throw new RuntimeException("variant of caller function with TemplateContext required here");
            }
            ObjectMapper mapper = context.parseSettings.mapper;
            ObjectMapper copy = SPIHelper.applyTypeReferencing(mapper.copy());
            ObjectNode value = copy.convertValue(variables, ObjectNode.class);
            Map<String, Object> convertedValue = copy.convertValue(value, MAP_TYPE_REF);
            visitMap(context, convertedValue);
            return convertedValue;
        }

        @Override
        public Map<String, Object> toLiquid() {
            return objectToMap(context, variable);
        }

        static void visitMap(TemplateContext context, Map<?, ?> map) {
            for (Map.Entry entry: map.entrySet()) {
                Object value = BasicTypesSupport.restoreObject(entry.getValue());
                visit(context, value);
                entry.setValue(value);
            }
        }
        static void visitList(TemplateContext context, List list) {
            for (int i = 0; i< list.size(); i++) {
                Object object = list.get(i);
                Object value = BasicTypesSupport.restoreObject(object);
                visit(context, value);
                //noinspection unchecked
                list.set(i, value);
            }
        }
        static void visit(TemplateContext context, Object object) {
            if (object instanceof Map) {
                visitMap(context, (Map)object);
            } if (object instanceof List) {
                visitList(context, (List)object);
            }
        }

    }
}
