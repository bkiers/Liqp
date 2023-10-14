package liqp.nodes;

import java.util.Optional;

public class GtNode extends ComparingExpressionNode {

    public GtNode(LNode lhs, LNode rhs) {
        super(lhs, rhs, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    Object doCompare(Object a, Object b, boolean strictTypedExpressions) {

        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable<Object>) a).compareTo(b) > 0;
        } else if (b instanceof Comparable && b.getClass().isInstance(a)) {
            return ((Comparable<Object>) b).compareTo(a) <= 0;
        }

        String aType = a == null ? "null" : a.getClass().getName();
        String bType = b == null ? "null" : b.getClass().getName();
        throw new RuntimeException("Cannot compare " + a + " with " + b + " because they are not the same type: " + aType + " vs " + bType);
    }

}
