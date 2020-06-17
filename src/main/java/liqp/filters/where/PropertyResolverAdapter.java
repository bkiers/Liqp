package liqp.filters.where;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for resolving properties by name for specific kind of objects.
 * Native implementation has equivalent ":to_liquid" and ":data" that used
 * for resolving properties by name for objects that support such method.
 * In java we do not stick to special interfaces of Jekyll/Liquid that
 * do not have equivalent/meaning here, but still provide a way to create
 * alternative properties resolver for custom objects.
 *
 * See here sample implementation for ":to_liquid" via this interface here:
 * https://gist.github.com/msangel/74c6cec96ea4a4ecc01187e465fdeb14
 *
 * See sample implementation for ":data" here:
 * https://gist.github.com/msangel/4a9b4404b233a6ff57a4ca54db3bfc1f
 *
 */
public interface PropertyResolverAdapter {
    Object getItemProperty(ObjectMapper mapper,  Object input, Object property);

    boolean support(Object target);

    class Helper {
        private final List<PropertyResolverAdapter> propertyResolverAdapters;

        public Helper() {
            this.propertyResolverAdapters = new ArrayList<>();
        }

        public void add(PropertyResolverAdapter one) {
            this.propertyResolverAdapters.add(one);
        }

        public PropertyResolverAdapter findFor(Object target) {
            for (PropertyResolverAdapter e : propertyResolverAdapters) {
                if (e.support(target)) {
                    return e;
                }
            }
            return null;
        }
    }


}
