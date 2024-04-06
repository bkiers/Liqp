package liqp.filters;

import java.util.ArrayList;
import java.util.List;

import liqp.TemplateContext;

/**
 * Jekyll-specific filter for array manipulation: Return a new array with the given item added to the
 * beginning. If the filtered value is not an array, it is returned unmodified.
 * 
 * @author Christian Kohlschütter
 * @see <a href="https://jekyllrb.com/docs/liquid/filters/">Jekyll Liquid Filters</a>
 */
public class Unshift extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (!super.isArray(value)) {
            return value;
        }

        if (params.length == 0) {
            return value;
        }

        List<?> valueList = asList(value, context);
        List<?> paramList = asList(params, context);
        List<? super Object> list = new ArrayList<>(valueList.size() + paramList.size());
        list.addAll(paramList);
        list.addAll(valueList);

        return list;
    }
}
