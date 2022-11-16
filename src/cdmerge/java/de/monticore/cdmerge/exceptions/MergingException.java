/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.exceptions;

import de.monticore.ast.ASTNode;
import de.monticore.cdmerge.log.ExecutionLog;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeStepResult;
import java.util.Optional;

public class MergingException extends Exception {

  private static final long serialVersionUID = 1L;

  private final Optional<MergeStepResult> mergeStepResult;

  private final Optional<MergePhase> phase;

  private final Optional<ASTNode> astNode1;

  private final Optional<ASTNode> astNode2;

  private final Optional<ExecutionLog> log;

  public MergingException(FailFastException cause) {
    super(cause);
    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.empty();
    this.astNode1 = Optional.empty();
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message, ExecutionLog log) {
    super(message);
    this.log = Optional.of(log);
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.empty();
    this.astNode1 = Optional.empty();
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message) {
    super(message);
    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.empty();
    this.astNode1 = Optional.empty();
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message, MergeStepResult mergeStepResult) {
    super(message);

    this.log = Optional.of(mergeStepResult.getMergeLog());
    this.mergeStepResult = Optional.of(mergeStepResult);
    this.phase = Optional.empty();
    this.astNode1 = Optional.empty();
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message, MergePhase phase, ASTNode astNode1, ASTNode astNode2) {
    super(message);

    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.of(phase);
    this.astNode1 = Optional.of(astNode1);
    this.astNode2 = Optional.of(astNode2);
  }

  public MergingException(String message, MergePhase phase, ASTNode astNode) {
    super(message);

    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.of(phase);
    this.astNode1 = Optional.of(astNode);
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message, MergePhase phase) {
    super(message);
    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.of(phase);
    this.astNode1 = Optional.empty();
    this.astNode2 = Optional.empty();
  }

  public MergingException(String message, ASTNode astNode) {
    super(message);
    this.log = Optional.empty();
    this.mergeStepResult = Optional.empty();
    this.phase = Optional.empty();
    this.astNode1 = Optional.of(astNode);
    this.astNode2 = Optional.empty();
  }

  /** @return report */
  public Optional<MergeStepResult> getReport() {
    return mergeStepResult;
  }

  /** @return astNode2 */
  public Optional<ASTNode> getAstNode2() {
    return astNode2;
  }

  /** @return astNode1 */
  public Optional<ASTNode> getAstNode1() {
    return astNode1;
  }

  /** @return phase */
  public Optional<MergePhase> getPhase() {
    return phase;
  }

  /** @return log */
  public Optional<ExecutionLog> getLog() {
    return this.log;
  }
}
