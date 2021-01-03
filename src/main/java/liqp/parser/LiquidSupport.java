package liqp.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
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
}
