package liqp.nodes;

import liqp.LValue;

import java.util.Objects;

public class EqNode extends ComparingExpressionNode {

    public EqNode(LNode lhs, LNode rhs) {
        super(lhs, rhs);
    }

    @Override
    Object doCompare(Object a, Object b) {

        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }

        if (a instanceof Boolean && b instanceof Boolean) {
            return Objects.equals(a, b);
        }
        if (a instanceof Boolean) {
            return false;
        }
        if (b instanceof Boolean) {
            return false;
        }
        return LValue.areEqual(a, b);
    }
}
