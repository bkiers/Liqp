package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;
import liqp.filters.where.PropertyResolverAdapter;
import liqp.filters.where.PropertyResolverHelper;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 *     # Filters an array of objects against an expression
 *     #
 *     # input - the object array
 *     # variable - the variable to assign each item to in the expression
 *     # expression - a Liquid comparison expression passed in as a string
 *     #
 *     # Returns the filtered array of objects
 *     def where_exp(input, variable, expression)
 *       return input unless input.respond_to?(:select)
 *
 *       input = input.values if input.is_a?(Hash)
 *
 *       condition = parse_condition(expression)
 *       @context.stack do
 *         input.select do |object|
 *           @context[variable] = object
 *           condition.evaluate(@context)
 *         end
 *       end || []
 *     end
 */
public class Where_Exp extends Filter {
    public Where_Exp(){
        super("where_exp");
    }

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        Object[] items = null;
        if (isArray(value)) {
            items = asArray(value);
        }

        if (items == null && value instanceof Inspectable) {
            LiquidSupport evaluated = context.renderSettings.evaluate(context, (Inspectable) value);
            value = evaluated.toLiquid();
        }
        if (isMap(value)) {
            items = asMap(value).values().toArray();
        }

        // neither collection or map
        if (items == null) {
            return value;
        }
        String varName = asString(params[0]);
        String strExpression = asString(params[1]);

        Template expression = Template.parse("{{ " + strExpression + " }}", context.parseSettings)
                .withRenderSettings(context.renderSettings);

        List<Object> res = new ArrayList<>();
        for (Object item: items) {
            if (matchCondition(context, item, varName, expression)) {
                res.add(item);
            }
        }
        return res;
    }

    private boolean matchCondition(TemplateContext context, Object item, String varName, Template expression) {
        String res = expression.renderUnguarded(Collections.singletonMap(varName, item), context, false);
        return "true".equals(res);
    }
}
