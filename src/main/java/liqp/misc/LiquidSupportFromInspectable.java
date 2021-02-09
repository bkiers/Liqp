package liqp.misc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;
import liqp.spi.BasicTypesSupport;

import java.util.List;
import java.util.Map;

public class LiquidSupportFromInspectable implements LiquidSupport {
    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {};
    private final TemplateContext context;
    private final Inspectable variable;

    public LiquidSupportFromInspectable(TemplateContext context, Inspectable variable) {
        this.variable = variable;
        this.context = context;
    }

    public static Map<String, Object> objectToMap(TemplateContext context, Object variables) {
        ObjectMapper mapper = context.parseSettings.mapper;
        ObjectNode value = mapper.convertValue(variables, ObjectNode.class);
        Map<String, Object> convertedValue = mapper.convertValue(value, MAP_TYPE_REF);
        visitMap(context, convertedValue);
        return convertedValue;
    }

    @Override
    public Map<String, Object> toLiquid() {
        return objectToMap(context, variable);
    }

    static void visitMap(TemplateContext context, Map<?, ?> map) {
        for (Map.Entry entry: map.entrySet()) {
            Object value = BasicTypesSupport.restoreObject(context, entry.getValue());
            visit(context, value);
            entry.setValue(value);
        }
    }
    static void visitList(TemplateContext context, List list) {
        for (int i = 0; i< list.size(); i++) {
            Object object = list.get(i);
            Object value = BasicTypesSupport.restoreObject(context, object);
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
