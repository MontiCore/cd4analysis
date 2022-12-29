/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

public abstract class ModelValidatorBase implements ModelValidator {

  public abstract static class ModelValidatorBuilder {

    protected abstract ModelValidator buildModelValidator(MergeBlackBoard blackBoard);

    public final ModelValidator build(MergeBlackBoard blackBoard) {
      ModelValidator validator = buildModelValidator(blackBoard);
      return validator;
    }
  }

  private MergeBlackBoard mergeBlackBoard;

  protected final MergePhase PHASE = MergePhase.VALIDATION;

  protected ModelValidatorBase(MergeBlackBoard blackboard) {
    this.mergeBlackBoard = blackboard;
  }

  protected MergeBlackBoard getBlackBoard() {
    return this.mergeBlackBoard;
  }
}
