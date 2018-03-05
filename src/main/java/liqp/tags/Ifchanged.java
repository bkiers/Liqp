package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

import java.util.*;

public class Ifchanged extends Tag {

    private static final String TEMP_SET_KEY = "@ifchanged";

    /*
        {% for product in products %}

          {% ifchanged %}<h3>{{ product.created_at | date:"%w" }}</h3>{% endifchanged %}

          <p>{{ product.title }} </p>

           ...

        {% endfor %}
    */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        if (nodes == null || nodes.length == 0) {
            return null;
        }

        Map<String, Object> forLoopContext = new HashMap<String, Object>((Map<String, Object>) context.get(For.FORLOOP));
        Boolean isFirst = (Boolean) forLoopContext.get(For.FIRST);
        Boolean isLast = (Boolean) forLoopContext.get(For.LAST);
        Set<Object> alreadySeen = new HashSet<Object>();

        if (isFirst) {
            // This is the first value in the FOR loop, store the Set that keeps track of all the unique
            // values in this context
            context.put(TEMP_SET_KEY, alreadySeen);
        }
        else {
            // Retrieve the Set that keeps track of all the unique values
            alreadySeen = (Set<Object>) context.get(TEMP_SET_KEY);
        }

        Object rendered = nodes[0].render(context);

        if (!alreadySeen.add(rendered)) {
            // We've already seen this value: change it to null so that it won't be rendered
            rendered = null;
        }

        if (isLast) {
            // We're done iterating, remove the temporary Set that keeps track of all the unique values
            context.remove(TEMP_SET_KEY);
        }

        return rendered;
    }
}
