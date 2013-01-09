package liqp.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LookupNode implements LNode {

    private List<String> ids;

    public LookupNode() {
        ids = new ArrayList<String>();
    }

    public void add(String id) {
        ids.add(id);
    }

    @Override
    public Object render(Map<String, Object> variables) {

        String id = ids.get(0);

        Object value = variables.get(id);

        if(value == null) {
            throw new RuntimeException("unknown variable: " + id);
        }

        return value;
    }
}
