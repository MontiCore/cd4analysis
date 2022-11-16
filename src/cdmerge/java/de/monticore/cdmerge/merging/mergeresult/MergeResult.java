package de.monticore.cdmerge.merging.mergeresult;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.ExecutionLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergeResult {

  private List<MergeStepResult> intermediateResults;

  public MergeResult() {
    this.intermediateResults = new ArrayList<MergeStepResult>();
  }

  public void add(MergeStepResult mergeStepResult) {
    this.intermediateResults.add(mergeStepResult);
  }

  public boolean mergeSuccess() {
    if (this.intermediateResults.size() > 0) {
      return this.intermediateResults.get(this.intermediateResults.size() - 1).isSuccessful()
          && getMergedCD().isPresent();
    } else {
      return false;
    }
  }

  public Optional<ASTCDCompilationUnit> getMergedCD() {
    if (this.intermediateResults.size() > 0) {
      return Optional.of(
          this.intermediateResults.get(this.intermediateResults.size() - 1).getMergedCD());
    } else {
      return Optional.empty();
    }
  }

  public ExecutionLog getLog() {
    ExecutionLog log = new ExecutionLog();
    this.intermediateResults.forEach(ir -> log.addLog(ir.getMergeLog()));
    return log;
  }

  public ExecutionLog getLog(ErrorLevel minLogLevel) {
    ExecutionLog log = new ExecutionLog(minLogLevel, false, false, false);
    this.intermediateResults.forEach(ir -> log.addLog(ir.getMergeLog()));
    return log;
  }

  public List<MergeStepResult> getIntermediateResults() {
    return this.intermediateResults;
  }

  public ErrorLevel getMaxErrorLevel() {
    return this.getLog().getMaxErrorLevel();
  }
}
