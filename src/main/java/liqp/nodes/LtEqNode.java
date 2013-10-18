package liqp.nodes;

import liqp.LValue;

import java.util.Map;

class LtEqNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public LtEqNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(Map<String, Object> context) {

        Object a = lhs.render(context);
        Object b = rhs.render(context);

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() <= super.asNumber(b).doubleValue();
    }
}
