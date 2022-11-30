package liqp.filters;

import java.util.ArrayList;
import java.util.List;

import liqp.TemplateContext;

/**
 * Jekyll-specific filter for array manipulation: Return a new array with the given item added to the
 * end.
 * 
 * @author Christian Kohlschütter
 * @see <a href="https://jekyllrb.com/docs/liquid/filters/">Jekyll Liquid Filters</a>
 */
public class Push extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (!super.isArray(value)) {
            throw new RuntimeException("cannot push to non-array: " + value);
        }

        if (params.length == 0) {
            return value;
        }

        List<? super Object> list = new ArrayList<>(asList(value, context));
        List<?> asList = asList(params, context);
        list.addAll(asList);

        return list;
    }
}
