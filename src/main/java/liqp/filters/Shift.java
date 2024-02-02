package liqp.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import liqp.TemplateContext;

/**
 * Jekyll-specific filter for array manipulation: Return a new array with the original array's first item
 * removed. If the original value is not an array, the value is returned as-is.
 * 
 * @author Christian Kohlschütter
 * @see <a href="https://jekyllrb.com/docs/liquid/filters/">Jekyll Liquid Filters</a>
 */
public class Shift extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        if (!super.isArray(value)) {
            return value;
        }

        int shiftIndex;
        switch (params.length) {
            case 0:
                shiftIndex = 1;
                break;
            case 1:
                shiftIndex = asNumber(params[0]).intValue();
                if (shiftIndex < 0) {
                    throw new RuntimeException("negative shift value");
                }
                break;
            default:
                throw new RuntimeException("shift supports up to 1 parameter");
        }

        List<?> list = asList(value, context);
        int size = list.size();
        if (shiftIndex >= size) {
            return Collections.emptyList();
        }
        list = new ArrayList<>(list.subList(shiftIndex, size));

        return list;
    }
}
