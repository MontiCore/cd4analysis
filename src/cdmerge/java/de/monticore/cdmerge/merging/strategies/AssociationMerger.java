/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

import java.util.List;

/**
 * The super class of all global association merging strategies
 */
public abstract class AssociationMerger extends MergerBase {

  protected final AssociationMergeStrategy mergeStrategy;

  public AssociationMerger(MergeBlackBoard mergeBlackBoard,
      AssociationMergeStrategy mergeStrategy) {
    super(mergeBlackBoard, MergePhase.ASSOCIATION_MERGING);
    this.mergeStrategy = mergeStrategy;
  }

  public abstract void mergeAssociations(ASTCDDefinition cd1, ASTCDDefinition cd2,
      CDMatch matchResult);

  /**
   * Only return association within the types declared in the class diagramm
   */
  protected List<ASTCDAssociation> getLocalAssociations(ASTCDDefinition cd) {
    return getBlackBoard().getASTCDHelperMergedCD().getLocalAssociations();
  }

}
