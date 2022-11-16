/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.refactor;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.refactor.ModelRefactoringBase.ModelRefactoringBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class executes refactorings to clean up redundant or syntactically malformed or even
 * semantically conflicting entries.
 */
public class PostMergeRefactoring {

  private MergeBlackBoard mergeBlackBoard;

  private List<ModelRefactoring> refactorings;

  public PostMergeRefactoring(MergeBlackBoard mergeBlackBoard) {
    this.refactorings = new ArrayList<ModelRefactoring>();
    this.mergeBlackBoard = mergeBlackBoard;
  }

  public void removeAllRefactorings() {
    this.refactorings.clear();
  }

  public void addRefactoring(ModelRefactoringBuilder refactoring) {
    this.refactorings.add(refactoring.build(this.mergeBlackBoard));
  }

  public void execute(ASTCDCompilationUnit cd) {
    this.refactorings.forEach(r -> r.apply(cd));
  }
}
