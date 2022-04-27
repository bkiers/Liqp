package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

import java.util.*;

public class Ifchanged extends Block {

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

        Object rendered = nodes[0].render(context);
        Map<String, Object> registryMap = context.getRegistry(TemplateContext.REGISTRY_IFCHANGED);
        if (!Objects.equals(rendered, registryMap.get(TemplateContext.REGISTRY_IFCHANGED))) {
            registryMap.put(TemplateContext.REGISTRY_IFCHANGED, rendered);
            return rendered;
        } else {
            return null;
        }
    }
}
