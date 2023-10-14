package liqp.nodes;

import liqp.LValue;

import java.util.Objects;

public class NEqNode extends ComparingExpressionNode {

    public NEqNode(LNode lhs, LNode rhs) {
        super(lhs, rhs, false);
    }

    @Override
    Object doCompare(Object a, Object b, boolean strictTypedExpressions) {
        if (a instanceof Boolean && b instanceof Boolean) {
            return !Objects.equals(a, b);
        }
        if (a instanceof Boolean) {
            return true;
        }
        if (b instanceof Boolean) {
            return true;
        }
        return !LValue.areEqual(a, b);
    }
}
