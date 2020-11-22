package liqp.tags;

import java.util.Map;

import liqp.TemplateContext;
import liqp.nodes.LNode;

/*
    decrement

    Creates a new number variable, and decreases its value by 1 every time decrement
    is called on the variable. The counter's initial value is -1.

    Input
        {% decrement variable %}
        {% decrement variable %}
        {% decrement variable %}

    Output
        -1
        -2
        -3

    Like increment, variables declared using decrement are independent from variables
    created using assign or capture.
*/
public class Decrement extends Tag {

    private static final Long INITIAL = 0L;

    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        Long value = INITIAL;
        String variable = super.asString(nodes[0].render(context));

        Map<String, Object> environmentMap = context.getEnvironmentMap();
        if (environmentMap.containsKey(variable)) {
            // Retrieve the old 'decrement' value
            value = (Long) environmentMap.get(variable);
        }

        value = value - 1;
        environmentMap.put(variable, value);

        return value;
    }
}
