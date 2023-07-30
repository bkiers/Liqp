package liqp.exceptions;

public class ExceededMaxIterationsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExceededMaxIterationsException(int maxIterations) {
        super("exceeded maxIterations: " + maxIterations);
    }
}
