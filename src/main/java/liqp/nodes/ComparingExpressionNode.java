package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

public abstract class ComparingExpressionNode extends LValue implements LNode {
    protected final LNode lhs;
    protected final LNode rhs;

    public ComparingExpressionNode(LNode lhs, LNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Object render(TemplateContext context) {

        // in original implementation, expression evaluation is done via sending signals with params to expression
        // i.e. expression evaluating delegated to native ruby
        // and ruby does not allow comparing different types
        // but types in java and ruby are different
        // so let's do such assumptions:
        // we have 5 base group of types here:
        // 1. temporal types (date, time, datetime)
        // 2. numeric types (integer, double)
        // 3. Comparable types (including strings)
        // 5. other types
        //
        // different types cannot be compared unless some request with proven example for exception

        // implementation note:
        // other types are not allowed to be compared (unless requested)
        // and temporal and numeric types are moving to single comparable type (ZonedDateTime and BigDecimal)
        // so in the end only comparing of Comparable types is performed

        Object a = lhs.render(context);
        Object b = rhs.render(context);
        if (isTemporal(a)) {
            a = asTemporal(a, context);
        }
        if (isTemporal(b)) {
            b = asTemporal(b, context);
        }

        if (a instanceof Number) {
            a = asStrictNumber((Number)a);
        }
        if (b instanceof Number) {
            b = asStrictNumber((Number)b);
        }
        return doCompare(a, b);
    }

    abstract Object doCompare(Object a, Object b);
}
