package liqp.blocks;

public class LoopInterruptionResult {
  private final Object interruption;
  private final Object evaluationResult;
  public LoopInterruptionResult(
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
