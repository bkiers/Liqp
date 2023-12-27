package liqp;

import java.math.BigDecimal;

/**
 * A {@link BigDecimal} with a {@link #toString()} method that is equivalent to calling
 * {@link #toPlainString()}.
 * 
 * @author Christian Kohlschütter
 */
public final class PlainBigDecimal extends BigDecimal {
    private static final long serialVersionUID = 1L;

    public PlainBigDecimal(String val) {
        super(val);
    }

    @Override
    public String toString() {
        return toPlainString();
    }
}
