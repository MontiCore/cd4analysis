/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.mergeresult;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.ExecutionLog;
import de.monticore.cdmerge.log.LogEntry;

/**
 * This class wraps the result of a merging processes, namely the merged AST and the Logs created
 * during the merging process. It is used by @see {@link MergeBlackBoard} to store intermediate and
 * final merging results
 */
public class MergeStepResult {

  private final ExecutionLog mergeLog;

  private final ASTCDCompilationUnit inputCD1;

  private final ASTCDCompilationUnit inputCD2;

  private final ASTCDCompilationUnit mergedCD;

  private boolean successful;

  public MergeStepResult(ASTCDCompilationUnit inputCD1, ASTCDCompilationUnit inputCD2,
      ASTCDCompilationUnit mergedCD, ExecutionLog mergeLog, boolean successful) {
    this.inputCD1 = inputCD1;
    this.inputCD2 = inputCD2;
    this.mergedCD = mergedCD;
    this.mergeLog = mergeLog;
    this.successful = successful;
  }

  /**
   * @return mergeLog - the log events recorded during the merging process
   */
  public ExecutionLog getMergeLog() {
    return mergeLog;
  }

  /**
   * @return mergedCD - the merged class diagram
   */
  public ASTCDCompilationUnit getMergedCD() {
    return mergedCD;
  }

  /**
   * @return inputCD1 - the first source diagram
   */
  public ASTCDCompilationUnit getInputCD1() {
    return inputCD1;
  }

  /**
   * @return inputCD2 - the second source diagram
   */
  public ASTCDCompilationUnit getInputCD2() {
    return inputCD2;
  }

  public String getFormattedExecutionLog() {
    StringBuilder sb = new StringBuilder();
    for (LogEntry logEntry : mergeLog.getAllLogs()) {
      sb.append(logEntry + "\n");
    }
    return sb.toString();
  }

  public ErrorLevel getMaxErrorLevel() {
    return this.mergeLog.getMaxErrorLevel();
  }

  public boolean isSuccessful() {
    return this.successful;
  }

}
