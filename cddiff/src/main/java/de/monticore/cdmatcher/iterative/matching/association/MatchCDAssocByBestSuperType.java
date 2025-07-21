/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.caching.StructureCache;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeFromCache;

import java.util.Set;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAssocByBestSuperType extends
    MultipleMatchingStrategy<ASTCDAssociation, ASTCDType> {
  
  public CachedMatches cachedMatches;
  public StructureCache structureCache;
  
  public MatchCDAssocByBestSuperType(CachedMatches cachedMatches, StructureCache structureCache) {
    this.cachedMatches = cachedMatches;
    this.structureCache = structureCache;
  }
  
  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    
    double nameScore = new MatchCDAssocByName().getScore(srcElem, tgtElem);
    double typeScore = -1;
    
    if (structureCache.getLeftType(srcElem).isPresent() && structureCache.getLeftType(tgtElem)
        .isPresent()) {
      typeScore = getBestMatchingScore(srcElem, tgtElem, (assoc) -> getSuperIncludingSelf(
          structureCache.getLeftType(assoc).get()), new MatchCDTypeFromCache(cachedMatches));
    }
    if (structureCache.getRightType(srcElem).isPresent() && structureCache.getRightType(tgtElem)
        .isPresent()) {
      double rightTypeScore = getBestMatchingScore(srcElem, tgtElem, (
          assoc) -> getSuperIncludingSelf(structureCache.getRightType(assoc).get()),
          new MatchCDTypeFromCache(cachedMatches));
      typeScore = typeScore < 0 ? rightTypeScore : mean(typeScore, rightTypeScore);
    }
    
    double score = typeScore < 0 ? nameScore : nameScore * 0.2 + typeScore * 0.8;
    
    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
  
  private Set<ASTCDType> getSuperIncludingSelf(ASTCDType type) {
    Set<ASTCDType> associations = structureCache.getSuperTypes(type);
    associations.add(type);
    return associations;
  }
  
}
