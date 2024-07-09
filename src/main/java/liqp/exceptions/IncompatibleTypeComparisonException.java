package liqp.exceptions;

public class IncompatibleTypeComparisonException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Object a;
    private final Object b;

    public IncompatibleTypeComparisonException(Object a, Object b) {
        super();
        this.a = a;
        this.b = b;
    }

    @Override
    public String getMessage() {
        String aType = a == null ? "null" : a.getClass().getName();
        String bType = b == null ? "null" : b.getClass().getName();
        return "Cannot compare " + a + " with " + b + " because they are not the same type: " + aType + " vs " + bType;
    }
}
