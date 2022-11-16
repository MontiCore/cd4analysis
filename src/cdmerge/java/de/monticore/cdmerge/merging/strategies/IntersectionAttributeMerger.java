/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 * Implements an attribute merging strategy, where only types are selected for the result diagram
 * which have been defined in both source diagrams
 */
public class IntersectionAttributeMerger extends AttributeMerger {

  public IntersectionAttributeMerger(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard);
  }

  public void mergeAttributes(
      ASTCDClass left,
      ASTCDClass right,
      ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult,
      ASTCDClass mergedClass) {
    for (ASTCDAttribute leftattr : left.getCDAttributeList()) {
      for (ASTCDAttribute rightattr : right.getCDAttributeList()) {
        if (leftattr.deepEquals(rightattr)) {
          mergedClass.getCDAttributeList().add(leftattr);
        }
      }
    }
  }
}
