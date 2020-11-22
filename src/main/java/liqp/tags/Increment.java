package liqp.tags;

import java.util.Map;

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

        Map<String, Object> environmentMap = context.getEnvironmentMap();
        if (environmentMap.containsKey(variable)) {
            // Retrieve the old 'decrement' value
            value = (Long) environmentMap.get(variable);
        }

        Long nextValue = value + 1;

        // Store the nextValue
        environmentMap.put(variable, nextValue);

        return value;
    }
}
