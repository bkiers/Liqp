package liqp.nodes;

import liqp.LValue;

import java.util.Map;

public class GtEqNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public GtEqNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        Object a = lhs.render(variables);
        Object b = rhs.render(variables);

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() >= super.asNumber(b).doubleValue();
    }
}
