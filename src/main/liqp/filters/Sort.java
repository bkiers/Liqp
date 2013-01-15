package liqp.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Sort extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if(value == null) {
            return "";
        }

        Object[] array = super.asArray(value);
        List<Comparable> list = asComparableList(array);

        Collections.sort(list);

        return list.toArray(new Object[list.size()]);
    }

    private List<Comparable> asComparableList(Object[] array) {

        List<Comparable> list = new ArrayList<Comparable>();

        for(Object obj : array) {
            list.add((Comparable)obj);
        }

        return list;
    }
}
