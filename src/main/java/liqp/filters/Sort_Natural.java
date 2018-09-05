package liqp.filters;

import java.util.*;

public class Sort_Natural extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (!super.isArray(value)) {
            return value;
        }

        Object[] array = super.asArray(value);
        List<Object> list = new ArrayList<Object>(Arrays.asList(array));

        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
            }
        });

        return list.toArray();
    }
}
