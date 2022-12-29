/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.validation.ModelValidatorBase.ModelValidatorBuilder;
import java.util.ArrayList;
import java.util.List;

public class PostMergeValidation {

  private final MergeBlackBoard mergeBlackBoard;

  protected final MergePhase PHASE = MergePhase.MODEL_REFACTORING;

  public PostMergeValidation(MergeBlackBoard mergeBlackBoard) {
    this.validators = new ArrayList<ModelValidator>();
    this.mergeBlackBoard = mergeBlackBoard;
  }

  /** @return mergeBlackBoard */
  protected MergeBlackBoard getMergeBlackBoard() {
    return mergeBlackBoard;
  }

  private List<ModelValidator> validators;

  public void addValidator(ModelValidatorBuilder validatorBuilder) {
    this.validators.add(validatorBuilder.build(this.mergeBlackBoard));
  }

  public void removeAllValidators() {
    this.validators.clear();
  }

  public void execute(ASTCDDefinition cd) {
    this.validators.forEach(v -> v.apply(cd));
  }
}
