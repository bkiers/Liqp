package liqp.filters;

import liqp.TemplateContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reverse extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Object[] values = super.asArray(value, context);
        List<Object> list = new ArrayList<Object>(Arrays.asList(values));

        Collections.reverse(list);

        return list.toArray();
    }
}
