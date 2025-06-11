package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.matching.association.MatchCDAssocByBestSuperType;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;
import de.monticore.cdmatcher.matching.caching.StructureCache;

public class MatchCDTypeByDirectAssocs extends MultipleMatchingStrategy<ASTCDType, ASTCDAssociation> {

  private final MatchingStrategy<ASTCDAssociation> strategy;

  public MatchCDTypeByDirectAssocs(MatchingStrategy<ASTCDAssociation> associationMatchingStrategy) {
    this.strategy = associationMatchingStrategy;
  }


  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      StructureCache::getAssociations,
      strategy
    );
  }
}
