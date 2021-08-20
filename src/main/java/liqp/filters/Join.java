package liqp.filters;

import liqp.TemplateContext;

public class Join extends Filter {

    /*
     * join(input, glue = ' ')
     *
     * Join elements of the array with certain character between them
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Object[] array = super.asArray(value, context);
        String glue = params.length == 0 ? " " : super.asString(super.get(0, params), context);

        for (int i = 0; i < array.length; i++) {

            builder.append(super.asString(array[i], context));

            if (i < array.length - 1) {
                builder.append(glue);
            }
        }

        return builder.toString();
    }
}
