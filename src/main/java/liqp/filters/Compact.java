package liqp.filters;

import liqp.TemplateContext;

import java.util.ArrayList;
import java.util.List;

public class Compact extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Object[] values = super.asArray(value, context);
        List<Object> compacted = new ArrayList<Object>();

        for (Object obj : values) {
            if (obj != null) {
                compacted.add(obj);
            }
        }

        return compacted.toArray();
    }
}
