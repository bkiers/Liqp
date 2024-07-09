package liqp.nodes;

import liqp.exceptions.IncompatibleTypeComparisonException;

import java.util.Optional;

public class LtNode extends ComparingExpressionNode {

    public LtNode(LNode lhs, LNode rhs) {
        super(lhs, rhs, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    Object doCompare(Object a, Object b, boolean strictTypedExpressions) {

        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable<Object>) a).compareTo(b) < 0;
        } else if (b instanceof Comparable && b.getClass().isInstance(a)) {
            return ((Comparable<Object>) b).compareTo(a) >= 0;
        }

        if (strictTypedExpressions) {
            throw new IncompatibleTypeComparisonException(a, b);
        }
        return false;
    }
}
