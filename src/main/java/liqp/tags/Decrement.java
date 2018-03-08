package liqp.tags;

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

    private static final Long INITIAL = -1L;

    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        Long value = INITIAL;
        String variable = super.asString(nodes[0].render(context));
        String decrementVariable = String.format("@decrement_%s", variable);
        String variableExistsFlag = String.format("@variable_%s_exists", variable);

        if (context.containsKey(decrementVariable)) {
            // Retrieve the old 'decrement' value
            value = (Long) context.get(decrementVariable);
        }

        if (value.equals(INITIAL)) {
            // If this is the first 'decrement' tag, check if the variable exists in the outer scope.
            context.put(variableExistsFlag, context.containsKey(variable));
        }

        if (!((Boolean) context.get(variableExistsFlag))) {
            // Set the 'variable' to the current value, only if it was flagged as not being defined in the outer scope
            context.put(variable, value);
        }

        // Store the nextValue
        context.put(decrementVariable, value - 1);

        return value;
    }
}
