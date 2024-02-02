package liqp.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import liqp.TemplateContext;

/**
 * Jekyll-specific filter for array manipulation: Return a new array with the original array's last item
 * removed. If the original value is not an array, the value is returned as-is.
 * 
 * @author Christian Kohlschütter
 * @see <a href="https://jekyllrb.com/docs/liquid/filters/">Jekyll Liquid Filters</a>
 */
public class Pop extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        if (!super.isArray(value)) {
            return value;
        }

        int numPop;
        switch (params.length) {
            case 0:
                numPop = 1;
                break;
            case 1:
                numPop = asNumber(params[0]).intValue();
                if (numPop < 0) {
                    throw new RuntimeException("negative pop value");
                }
                break;
            default:
                throw new RuntimeException("pop supports up to 1 parameter");
        }

        List<?> list = asList(value, context);
        int remainingSize = list.size() - numPop;

        if (remainingSize <= 0) {
            return Collections.emptyList();
        } else {
            list = new ArrayList<>(list.subList(0, remainingSize));
        }

        return list;
    }
}
