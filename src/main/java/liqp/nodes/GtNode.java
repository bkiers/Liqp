package liqp.nodes;

public class GtNode extends ComparingExpressionNode {

    public GtNode(LNode lhs, LNode rhs) {
        super(lhs, rhs);
    }

    @Override
    Object doCompare(Object a, Object b) {
        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable) a).compareTo(b) > 0;
        } else if (b instanceof Comparable && b.getClass().isInstance(a)) {
            return ((Comparable) b).compareTo(a) <= 0;
        }

        return (a instanceof Number) && (b instanceof Number) &&
                super.asNumber(a).doubleValue() > super.asNumber(b).doubleValue();
    }

}
