package liqp.tags;

import liqp.nodes.LNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class Cycle extends Tag {

    private static final String PREPEND = "\"'";

    /*
     * Cycle is usually used within a loop to alternate
     * between values, like colors or DOM classes.
     */
    @Override
    public Object render(Map<String, Object> variables, LNode... tokens) {

        // The group-name is either the first token-expression, or if that is
        // null (indicating there is no name), give it the name PREPEND followed
        // by the number of expressions in the cycle-group.
        String groupName = tokens[0] == null ?
                PREPEND + (tokens.length - 1) :
                super.asString(tokens[0].render(variables));

        // Prepend a groupName with a single- and double quote as to not
        // let the groupName conflict with other variable assignments
        groupName = PREPEND + groupName;

        Object obj = variables.remove(groupName);

        List<Object> elements = new ArrayList<Object>();

        for(int i = 1; i < tokens.length; i++) {
            elements.add(tokens[i].render(variables));
        }

        CycleGroup group;

        if(obj == null) {
            group = new CycleGroup(elements.size());
        }
        else {
            group = (CycleGroup)obj;
        }

        variables.put(groupName, group);

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

            if(currentIndex >= elements.size()) {
                obj = "";
            }
            else {
                obj = elements.get(currentIndex);
            }

            currentIndex++;

            if(currentIndex == sizeFirstCycle) {
                currentIndex = 0;
            }

            return obj;
        }
    }
}
