package liqp.filters;

import java.util.Arrays;

class Join extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if(value == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Object[] array = super.asArray(value);
        String glue = params.length == 0 ? " " : super.asString(super.get(0, params));

        for(int i = 0; i < array.length; i++) {

            builder.append(super.asString(array[i]));

            if(i < array.length - 1) {
                builder.append(glue);
            }
        }

        return builder.toString();
    }
}
