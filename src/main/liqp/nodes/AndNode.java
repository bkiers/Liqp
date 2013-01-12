package liqp.nodes;

import liqp.LValue;

import java.util.Map;

public class AndNode extends LValue implements LNode {

    private LNode lhs;
    private LNode rhs;

    public AndNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(Map<String, Object> variables) {

        Object a = lhs.render(variables);
        Object b = rhs.render(variables);

        return super.asBoolean(a) && super.asBoolean(b);

    }
}
