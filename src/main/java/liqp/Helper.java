package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class Helper {

    /**
     * Prepare values (variables) for rendering the template.
     * Input is transformed to JSON and back to a Java map.
     *
     * Note that double transformation is expensive. Transformation of large structures
     * incurs considerable performance penalty. Use wisely!
     *
     * @param o Source of input variables. Typically a hierarchy of maps or POJO objects.
     * @return a map containing only primitives (String, number, boolean), lists, and other maps
     */
    public static Map transformMap(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode oNode = objectMapper.convertValue(o, ObjectNode.class);
        return objectMapper.convertValue(oNode, Map.class);
    }

}
