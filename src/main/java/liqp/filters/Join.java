package liqp.filters;

import liqp.RenderTransformer.ObjectAppender;
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

        Object[] array = super.asArray(value, context);
        if (array.length == 0) {
            return "";
        }

        ObjectAppender.Controller builder = context.newObjectAppender(array.length);
        String glue = params.length == 0 ? " " : super.asString(super.get(0, params), context);

        for (int i = 0; i < array.length; i++) {
            builder.append(super.asAppendableObject(array[i], context));

            if (i < array.length - 1) {
                builder.append(glue);
            }
        }

        return builder.getResult();
    }
}
