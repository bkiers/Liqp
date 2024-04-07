package liqp.nodes;

import liqp.LValue;
import liqp.TemplateContext;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class ComparingExpressionNode extends LValue implements LNode {
    protected final LNode lhs;
    protected final LNode rhs;
    private final boolean relative;

    /**
     *
     * @param lhs - left-hand side
     * @param rhs - right-hand side
     * @param relative - expressions are two kinds:
     *                 relative(>, >=, <, <=)
     *                 and equality (==, <>, !=) and rules for comparing them different.
     */
    public ComparingExpressionNode(LNode lhs, LNode rhs, boolean relative) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.relative = relative;
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
            a = asRubyDate(a, context);
        }
        if (isTemporal(b)) {
            b = asRubyDate(b, context);
        }

        if (a instanceof Number) {
            a = asStrictNumber((Number)a);
        }
        if (b instanceof Number) {
            b = asStrictNumber((Number)b);
        }
        boolean strictTypedExpressions;
        if (context != null && context.getParser() != null) {
            // this variable always set except for tests
            strictTypedExpressions = context.getParser().strictTypedExpressions;
        } else {
            strictTypedExpressions = true;
        }
        if (relative) {
            Optional<Object> common = relativeCompareCommonRules(a, b, strictTypedExpressions);
            if (common.isPresent()) {
                return common.get();
            }
            if (!strictTypedExpressions) {
                if (a instanceof Boolean) {
                    a = booleanToNumber((Boolean) a);
                }
                if (b instanceof Boolean) {
                    b = booleanToNumber((Boolean) b);
                }
                if (a == null) {
                    a = BigDecimal.ZERO;
                }
                if (b == null) {
                    b = BigDecimal.ZERO;
                }
            }
        }
        if (!strictTypedExpressions) {
            if ((a instanceof Number && canBeNumber(b)) || (b instanceof Number && canBeNumber(a))) {
                a = asStrictNumber(a);
                b = asStrictNumber(b);
            }
        }
        return doCompare(a, b, strictTypedExpressions);
    }

    private Object booleanToNumber(Boolean a) {
        if  (Boolean.TRUE.equals(a)) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.ZERO;
        }
    }

    abstract Object doCompare(Object a, Object b, boolean strictTypedExpressions);

    protected Optional<Object> relativeCompareCommonRules(Object a, Object b, boolean strictTypedExpressions) {
        if (strictTypedExpressions) {
            if (a instanceof Boolean || b instanceof Boolean) {
                // relative comparing with boolean should be false all the time
                return Optional.of(false);
            }
            if (a == null || b == null) {
                // relative comparing with null should be false all the time
                return Optional.of(false);
            }
        }
        return Optional.empty();
    }
}
