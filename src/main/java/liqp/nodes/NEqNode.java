package liqp.nodes;

import liqp.LValue;

import java.util.Objects;

public class NEqNode extends ComparingExpressionNode {

    public NEqNode(LNode lhs, LNode rhs) {
        super(lhs, rhs);
    }

    @Override
    Object doCompare(Object a, Object b) {
        if (a instanceof Boolean && b instanceof Boolean) {
            return !Objects.equals(a, b);
        }
        if (a instanceof Boolean) {
            return false;
        }
        if (b instanceof Boolean) {
            return false;
        }
        return !LValue.areEqual(a, b);
    }
}
