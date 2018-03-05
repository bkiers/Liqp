package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

/*
    increment

    Creates a new number variable, and increases its value by 1 every time increment is
    called on the variable. The counter's initial value is 0.

    Here, an increment counter is used to create a unique numbered class for each list
    item:

    Input
        <ul>
          <li class="item-{% increment counter %}">apples</li>
          <li class="item-{% increment counter %}">oranges</li>
          <li class="item-{% increment counter %}">peaches</li>
          <li class="item-{% increment counter %}">plums</li>
        </ul>

    Output
        <ul>
          <li class="item-0">apples</li>
          <li class="item-1">oranges</li>
          <li class="item-2">peaches</li>
          <li class="item-3">plums</li>
        </ul>

    Variables created using increment are separate from variables created using assign
    or capture.
*/
public class Increment extends Tag {

    private static final Long INITIAL = 0L;

    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        Long value = INITIAL;
        String variable = super.asString(nodes[0].render(context));
        String incrementVariable = String.format("@increment_%s", variable);
        String variableExistsFlag = String.format("@variable_%s_exists", variable);

        if (context.containsKey(incrementVariable)) {
            // Retrieve the old 'increment' value
            value = (Long) context.get(incrementVariable);
        }

        Long nextValue = value + 1;

        if (value.equals(INITIAL)) {
            // If this is the first 'increment' tag, check if the variable exists in the outer scope.
            context.put(variableExistsFlag, context.containsKey(variable));
        }

        if (!((Boolean) context.get(variableExistsFlag))) {
            // Set the 'variable' to the next value, only if it was flagged as not being defined in the outer scope
            context.put(variable, nextValue);
        }

        // Store the nextValue
        context.put(incrementVariable, nextValue);

        return value;
    }
}
