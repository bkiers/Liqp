package liqp.filters;

import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

public class Size extends Filter {

    /*
     * size(input)
     *
     * Return the size of an array or of an string
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value instanceof Inspectable) {
            LiquidSupport evaluated = context.renderSettings.evaluate(context, (Inspectable) value);
            value = evaluated.toLiquid();
        }

        if (isMap(value)) {
            return asMap(value).size();
        }
        if (super.isArray(value)) {
            return super.asArray(value).length;
        }

        if (super.isString(value)) {
            return super.asString(value).length();
        }

        if (super.isNumber(value)) {
            // we're only using 64 bit longs, no BigIntegers or the like.
            // So just return 8 (the number of bytes in a long).
            return 8;
        }

        // boolean or nil
        return 0;
    }
}
