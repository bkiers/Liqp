package liqp.filters;

import java.util.ArrayList;
import java.util.List;

import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

public class Map extends Filter {

    /*
     * map(input, property)
     *
     * map/collect on a given property
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value == null) {
            return "";
        }

        List<Object> list = new ArrayList<Object>();

        Object[] array = super.asArray(value, context);

        String key = super.asString(super.get(0, params), context);

        for (Object obj : array) {

            java.util.Map<?,?> map;
            if (value instanceof Inspectable) {
                LiquidSupport evaluated = context.evaluate(value);
                map = evaluated.toLiquid();
            } else {
                map = (java.util.Map<?,?>) obj;
            }

            Object val = map.get(key);

            if (val != null) {
                list.add(val);
            }
        }

        return list.toArray(new Object[list.size()]);
    }
}
