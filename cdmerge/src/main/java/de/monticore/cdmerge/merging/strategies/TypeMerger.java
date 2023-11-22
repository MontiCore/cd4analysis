/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 * The super class of all global type merging strategies; merges all types, considers precedences,
 * reconfigures references (super class, implemented interfaces)
 */
public abstract class TypeMerger extends MergerBase {

  protected final TypeMergeStrategy typeMergeStrategy;

  public TypeMerger(MergeBlackBoard mergeBlackBoard, TypeMergeStrategy typeMergeStrategy) {
    super(mergeBlackBoard, MergePhase.TYPE_MERGING);
    this.typeMergeStrategy = typeMergeStrategy;
  }

  public abstract void mergeTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult);
}
