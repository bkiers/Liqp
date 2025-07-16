package liqp.blocks;

public class ExceptionalResult {
  private final Object interruption;
  private final Object evaluationResult;
  public ExceptionalResult(
      Object interruption, Object evaluationResult
  ) {
    this.interruption = interruption;
    this.evaluationResult = evaluationResult;
  }

  public Object getInterruption() {
    return interruption;
  }

  public Object getEvaluationResult() {
    return evaluationResult;
  }
}
