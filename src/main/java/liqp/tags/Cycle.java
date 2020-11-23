package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Cycle extends Tag {

    /*
     * Cycle is usually used within a loop to alternate
     * between values, like colors or DOM classes.
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {


        // collect all the variants to the list first
        List<Object> elements = new ArrayList<>();

        for (int i = 1; i < nodes.length; i++) {
            elements.add(nodes[i].render(context));
        }


        // The group-name is either the first token-expression, or if that is
        // null (indicating there is no name), give it the name as stringified parameters
        String groupName = nodes[0] == null ?
                super.asString(elements) :
                super.asString(nodes[0].render(context));

        Map<String, Object> cycleRegistry = context.getRegistry(TemplateContext.REGISTRY_CYCLE);

        Object obj = cycleRegistry.remove(groupName);

        CycleGroup group;

        if (obj == null) {
            group = new CycleGroup(elements.size());
        }
        else {
            group = (CycleGroup) obj;
        }

        cycleRegistry.put(groupName, group);

        return group.next(elements);
    }

    private static class CycleGroup {

        private final int sizeFirstCycle;
        private int currentIndex;

        CycleGroup(int sizeFirstCycle) {
            this.sizeFirstCycle = sizeFirstCycle;
            this.currentIndex = 0;
        }

        Object next(List<Object> elements) {

            Object obj;

            if (currentIndex >= elements.size()) {
                obj = "";
            }
            else {
                obj = elements.get(currentIndex);
            }

            currentIndex++;

            if (currentIndex == sizeFirstCycle) {
                currentIndex = 0;
            }

            return obj;
        }
    }
}
