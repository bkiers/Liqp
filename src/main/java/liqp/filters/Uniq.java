package liqp.filters;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Uniq extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Set<Object> set = new LinkedHashSet<Object>(Arrays.asList(super.asArray(value)));

        return set.toArray();
    }
}
