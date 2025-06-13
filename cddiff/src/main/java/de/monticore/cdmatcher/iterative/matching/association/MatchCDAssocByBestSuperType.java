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
  
  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    
    double nameScore = new MatchCDAssocByName().getScore(srcElem, tgtElem);
    double typeScore = -1;
    
    if (StructureCache.getLeftType(srcElem).isPresent() && StructureCache.getLeftType(tgtElem)
        .isPresent()) {
      typeScore = getBestMatchingScore(srcElem, tgtElem, (assoc) -> getSuperIncludingSelf(
          StructureCache.getLeftType(assoc).get()), new MatchCDTypeFromCache());
    }
    if (StructureCache.getRightType(srcElem).isPresent() && StructureCache.getRightType(tgtElem)
        .isPresent()) {
      double rightTypeScore = getBestMatchingScore(srcElem, tgtElem, (
          assoc) -> getSuperIncludingSelf(StructureCache.getRightType(assoc).get()),
          new MatchCDTypeFromCache());
      typeScore = typeScore < 0 ? rightTypeScore : mean(typeScore, rightTypeScore);
    }
    
    double score = typeScore < 0 ? nameScore : nameScore * 0.2 + typeScore * 0.8;
    
    CachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
  
  private Set<ASTCDType> getSuperIncludingSelf(ASTCDType type) {
    Set<ASTCDType> associations = StructureCache.getSuperTypes(type);
    associations.add(type);
    return associations;
  }
  
}
