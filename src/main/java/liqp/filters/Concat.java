package liqp.filters;

import liqp.TemplateContext;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Concat extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        super.checkParams(params, 1);

        if (!super.isArray(params[0])) {
            throw new RuntimeException("Liquid error: concat filter requires an array argument");
        }

        List<Object> allValues = new ArrayList<Object>();

        if (super.isArray(value)) {
            allValues.addAll(Arrays.asList(super.asArray(value, context)));
        }

        allValues.addAll(Arrays.asList(super.asArray(params[0], context)));

        return allValues.toArray();
    }
}
