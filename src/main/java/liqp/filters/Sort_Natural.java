package liqp.filters;

import liqp.TemplateContext;

import java.util.*;

public class Sort_Natural extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Object[] array = super.asArray(value, context);
        List<Object> list = new ArrayList<Object>(Arrays.asList(array));

        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
            }
        });

        return list.toArray();
    }
}
