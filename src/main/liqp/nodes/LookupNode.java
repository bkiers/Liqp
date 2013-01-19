package liqp.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class LookupNode implements LNode {

    private List<String> ids;

    public LookupNode() {
        ids = new ArrayList<String>();
    }

    public void add(String id) {
        ids.add(id);
    }

    @Override
    public Object render(Map<String, Object> context) {

        Object value = context.get(ids.get(0));

        for (int i = 1; i < ids.size(); i++) {

            if (value == null) {
                return null;
            }

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                value = map.get(ids.get(i));
            }
            catch (Exception e) {
                return null;
            }
        }

        return value;
    }
}
