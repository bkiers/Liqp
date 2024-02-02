package liqp.filters;

import liqp.TemplateContext;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Uniq extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Set<Object> set = new LinkedHashSet<Object>(Arrays.asList(super.asArray(value, context)));

        return set.toArray();
    }
}
