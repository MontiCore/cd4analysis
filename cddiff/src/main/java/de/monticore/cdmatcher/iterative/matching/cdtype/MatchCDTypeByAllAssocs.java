/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

public class MatchCDTypeByAllAssocs extends
    MultipleMatchingStrategy<ASTCDType, ASTCDAssociation> {

  private final MatchingStrategy<ASTCDAssociation> strategy;
  private final StructureCache structureCache;

  public MatchCDTypeByAllAssocs(MatchingStrategy<ASTCDAssociation> associationMatchingStrategy,
                                StructureCache structureCache) {
    this.strategy = associationMatchingStrategy;
    this.structureCache = structureCache;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(srcElem, tgtElem, structureCache::getAssociations, strategy);
  }

}
