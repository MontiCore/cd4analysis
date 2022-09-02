/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.refactor;

import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 *
 */
public abstract class ModelRefactoringBase implements ModelRefactoring {

  public abstract static class ModelRefactoringBuilder {

    protected abstract ModelRefactoring buildModelRefactoring(MergeBlackBoard blackBoard);

    public final ModelRefactoring build(MergeBlackBoard blackBoard) {
      ModelRefactoring validator = buildModelRefactoring(blackBoard);
      return validator;
    }

  }

  private final MergeBlackBoard mergeBlackBoard;

  protected final MergePhase PHASE = MergePhase.MODEL_REFACTORING;

  public ModelRefactoringBase(MergeBlackBoard mergeBlackBoard) {
    this.mergeBlackBoard = mergeBlackBoard;
  }

  /**
   * @return mergeBlackBoard
   */
  protected MergeBlackBoard getMergeBlackBoard() {
    return mergeBlackBoard;
  }

}
