/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/** The super class of all attribute merging strategies */
public abstract class AttributeMerger extends MergerBase {

  public AttributeMerger(MergeBlackBoard mergeBlackBoard) {
    super(mergeBlackBoard, MergePhase.ATTRIBUTE_MERGING);
  }

  public abstract void mergeAttributes(
      ASTCDClass input1,
      ASTCDClass input2,
      ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult,
      ASTCDClass mergedClass);
}
