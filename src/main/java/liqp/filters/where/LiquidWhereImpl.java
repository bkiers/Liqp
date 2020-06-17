package liqp.filters.where;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liqp.parser.Flavor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by vasyl.khrystiuk on 10/09/2019.
 */
public class LiquidWhereImpl extends WhereImpl {

    public LiquidWhereImpl(ObjectMapper mapper, PropertyResolverAdapter.Helper helper) {
        super(mapper, Flavor.LIQUID, helper);
    }

    /*
    # Filter the elements of an array to those with a certain property value.
    # By default the target is any truthy value.
    def where(input, property, target_value = nil)
      ary = InputIterator.new(input)

      if ary.empty?
        []
      elsif ary.first.respond_to?(:[]) && target_value.nil?
        begin
          ary.select { |item| item[property] }
        rescue TypeError
          raise_property_error(property)
        end
      elsif ary.first.respond_to?(:[])
        begin
          ary.select { |item| item[property] == target_value }
        rescue TypeError
          raise_property_error(property)
        end
      end
    end

    class InputIterator
      include Enumerable

      def initialize(input)
        @input = if input.is_a?(Array)
          input.flatten # https://apidock.com/ruby/Array/flatten
        elsif input.is_a?(Hash)
          [input]
        elsif input.is_a?(Enumerable)
          input
        else
          Array(input) # nil will cause empty array
        end
      end
 */

    @Override
    public Object apply(Object input, Object... params) {
        Object[] objects = toArray(input);
        if (objects.length == 0) {
            return objects;
        }
        List<Object> res = new ArrayList<>();

        for (Object el : objects) {
            if (objectHasPropertyValue(el, params)) {
                res.add(el);
            }
        }
        return res.toArray();
    }

    private boolean objectHasPropertyValue(Object el, Object[] params) {
        Object rawProperty = params[0];
        String property = asString(rawProperty);
        PropertyResolverAdapter resolver = resolverHelper.findFor(el);
        Object node;
        if (resolver != null) {
            node = resolver.getItemProperty(mapper, el, rawProperty);
        } else {
            Map map = mapper.convertValue(el, Map.class);
            if (!map.containsKey(property)) {
                return false;
            }
            node = map.get(property);
        }

        if (params.length == 1) {
            return asBoolean(node);
        } else /* params.length == 2 */ {
            Object value = params[1];
            JsonNode jsonNode = mapper.convertValue(node, JsonNode.class);
            JsonNode jsonProperty = mapper.convertValue(value, JsonNode.class);
            return Objects.equals(jsonNode, jsonProperty);
        }
    }


    private static List<Object> flatten(Object object) {
        List<Object> l = new ArrayList<>();
        if (object == null) {
            return l;
        }
        if (object.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(object); i++) {
                l.addAll(flatten(Array.get(object, i)));
            }
        } else if (object instanceof Collection) {
            for (Object element : (List<?>) object) {
                l.addAll(flatten(element));
            }
        } else {
            l.add(object);
        }
        return l;
    }

    private static Object[] toArray(Object object) {
        if (object == null) {
            return new Object[]{};
        }
        if (object.getClass().isArray()) {
            return flatten(object).toArray();
        } else if (object instanceof java.util.Map) {
            // map can be also a collection, but we treat it as hash
            return new Object[]{object};
        } else if (object instanceof Collection) {
            return flatten(object).toArray();
        } else {
            return new Object[]{object};
        }
    }

}
