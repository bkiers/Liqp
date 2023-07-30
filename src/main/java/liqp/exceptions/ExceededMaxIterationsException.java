package liqp.exceptions;

public class ExceededMaxIterationsException extends RuntimeException {
    private static final long serialVersionUID = -2177965025182952056L;

    public ExceededMaxIterationsException(int maxIterations) {
        super("exceeded maxIterations: " + maxIterations);
    }
}
