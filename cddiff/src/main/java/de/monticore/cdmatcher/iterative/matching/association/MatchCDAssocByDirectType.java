/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.caching.StructureCache;

import java.util.Optional;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAssocByDirectType implements MatchingStrategy<ASTCDAssociation> {

  private final CachedMatches cachedMatches;
  private final StructureCache structureCache;
  private final MatchingStrategy<ASTCDAssociation> nameMatcher;

  public MatchCDAssocByDirectType(CachedMatches cachedMatches, StructureCache structureCache, MatchingStrategy<ASTCDAssociation> nameMatcher) {
    this.cachedMatches = cachedMatches;
    this.structureCache = structureCache;
    this.nameMatcher = nameMatcher;
  }

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    Optional<ASTCDType> srcRightType = structureCache.getRightType(srcElem);
    Optional<ASTCDType> srcLeftType = structureCache.getLeftType(srcElem);
    Optional<ASTCDType> tgtRightType = structureCache.getRightType(tgtElem);
    Optional<ASTCDType> tgtLeftType = structureCache.getLeftType(tgtElem);

    double nameScore = nameMatcher.getScore(srcElem, tgtElem);
    double typeScore = -1;
    Double leftTypeScore = null;
    Double rightTypeScore = null;

    if (srcLeftType.isPresent() && tgtLeftType.isPresent()) {
      leftTypeScore = cachedMatches.getMatch(srcLeftType.get(), tgtLeftType.get());
    }
    if (srcRightType.isPresent() && tgtRightType.isPresent()) {
      rightTypeScore = cachedMatches.getMatch(srcRightType.get(), tgtRightType.get());
    }

    if (leftTypeScore != null) {
      typeScore = leftTypeScore;
    }
    if (rightTypeScore != null) {
      typeScore = typeScore < 0 ? rightTypeScore : mean(typeScore, rightTypeScore);
    }

    double score = typeScore < 0 ? nameScore : nameScore * 0.2 + typeScore * 0.8;

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }

}
