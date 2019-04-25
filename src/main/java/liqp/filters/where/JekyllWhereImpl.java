package liqp.filters.where;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.nodes.AtomNode;
import liqp.parser.Flavor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by vasyl.khrystiuk on 10/09/2019.
 * Based on:
 * https://github.com/jekyll/jekyll/blob/master/lib/jekyll/filters.rb
 * https://github.com/jekyll/jekyll/blob/master/test/test_filters.rb
 */
public class JekyllWhereImpl extends WhereImpl {

    public JekyllWhereImpl(ObjectMapper mapper, PropertyResolverAdapter.Helper helper) {
        super(mapper, Flavor.JEKYLL, helper);
    }

    /*
        # Filter an array of objects
    #
    # input    - the object array.
    # property - the property within each object to filter by.
    # value    - the desired value.
    #            Cannot be an instance of Array nor Hash since calling #to_s on them returns
    #            their `#inspect` string object.
    #
    # Returns the filtered array of objects
    def where(input, property, value)
      return input if !property || value.is_a?(Array) || value.is_a?(Hash)
      return input unless input.respond_to?(:select)

      input    = input.values if input.is_a?(Hash)
      input_id = input.hash

      # implement a hash based on method parameters to cache the end-result
      # for given parameters.
      @where_filter_cache ||= {}
      @where_filter_cache[input_id] ||= {}
      @where_filter_cache[input_id][property] ||= {}

      # stash or retrive results to return
      @where_filter_cache[input_id][property][value] ||= begin
        input.select do |object|
          compare_property_vs_target(item_property(object, property), value)
        end.to_a
      end
    end
    */

    // compare_property_vs_target(property, target)
    /*
    # `where` filter helper
    #
    def compare_property_vs_target(property, target)
      case target
      when NilClass
        return true if property.nil?
      when Liquid::Expression::MethodLiteral # `empty` or `blank`
        target = target.to_s
        return true if property == target || Array(property).join == target
      else
        target = target.to_s
        if property.is_a? String
          return true if property == target
        else
          Array(property).each do |prop|
            return true if prop.to_s == target
          end
        end
      end

      false
    end
    */

    // item_property(item, property)
    /*
    def item_property(item, property)
      @item_property_cache ||= {}
      @item_property_cache[property] ||= {}
      @item_property_cache[property][item] ||= begin
        if item.respond_to?(:to_liquid)
          property.to_s.split(".").reduce(item.to_liquid) do |subvalue, attribute|
            parse_sort_input(subvalue[attribute])
          end
        elsif item.respond_to?(:data)
          parse_sort_input(item.data[property.to_s])
        else
          parse_sort_input(item[property.to_s])
        end
      end
    end

    # return numeric values as numbers for proper sorting
    def parse_sort_input(property)
      number_like = %r!\A\s*-?(?:\d+\.?\d*|\.\d+)\s*\Z!
      return property.to_f if property.to_s =~ number_like

      property
    end

     */
    @Override
    public Object apply(Object input, Object... params) {
        if (params.length < 1) {
            return input;
        }
        Object property = params[0];
        if (isFalsy(property)) {
            return input;
        }

        Object value = null;
        if (params.length > 1) {
            value = params[1];
        }
        if (value != null) {
            if (value.getClass().isArray()) {
                return input;
            }
            if (value instanceof java.util.Map) {
                return input;
            }
        }

        if (input == null) {
            return "";
        }

        if (!(input instanceof Collection) && !(input instanceof java.util.Map) && !isArray(input)) {
            return input;
        }

        if (input instanceof java.util.Map) {
            input = ((Map) input).values();
        }

        if (input.getClass().isArray()) {
            input = arrayToArrayList((Object[]) input);
        }
        Collection inputColl = (Collection) input;

        List res = new ArrayList();
        for (Object item : inputColl) {
            Object itemProperty = itemProperty(item, property);
            if (comparePropertyVsTarget(itemProperty, value)) {
                res.add(item);
            }
        }
        return res.toArray(new Object[res.size()]);
    }

    private boolean comparePropertyVsTarget(Object itemProperty, Object target) {
        if (target == null) {
            return itemProperty == null;
        }
        if (AtomNode.isEmpty(target) || AtomNode.isBlank(target)) {
            return "".equals(itemProperty) || "".equals(joinedArray(itemProperty));
        }

        String strTarget = asString(target);
        if (isString(itemProperty)) {
            return strTarget.equals(itemProperty);
        } else {
            Object[] objects = asArray(itemProperty);
            for (Object prop : objects) {
                if (asString(prop).equals(strTarget)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String joinedArray(Object itemProperty) {
        // version of Array(property).join
        Object[] objects;
        if (itemProperty instanceof Map) {
            objects = mapAsArray((Map) itemProperty);
        } else {
            objects = asArray(itemProperty);
        }
        return asString(objects);
    }


    private Object itemProperty(Object e, Object property) {
        PropertyResolverAdapter adapter = resolverHelper.findFor(e);
        if (adapter != null) {
            return parseSortInput(adapter.getItemProperty(mapper, e, property));
        }
        return parseSortInput(((Map) e).get(property));
    }

    private Object parseSortInput(Object property) {
        if (property instanceof String) {
            try {
                return Double.parseDouble((String) property);
            } catch (Exception e) {
            }
        }
        return property;
    }

    private static <T> ArrayList<T> arrayToArrayList(T[] array) {
        ArrayList<T> list = new ArrayList<T>();
        for (T elmt : array) list.add(elmt);
        return list;
    }
}
