package de.monticore.cdmatcher.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;
import de.monticore.cdmatcher.matching.caching.CachedMatches;
import de.monticore.cdmatcher.matching.caching.StructureCache;
import de.monticore.cdmatcher.matching.cdtype.MatchCDTypeFromCache;

import java.util.Optional;

import static com.google.common.math.DoubleMath.mean;


public class MatchCDAssocByDirectType implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    Optional<ASTCDType> srcRightType = StructureCache.getRightType(srcElem);
    Optional<ASTCDType> srcLeftType = StructureCache.getLeftType(srcElem);
    Optional<ASTCDType> tgtRightType = StructureCache.getRightType(tgtElem);
    Optional<ASTCDType> tgtLeftType = StructureCache.getLeftType(tgtElem);

    double nameScore = new MatchCDAssocByName().getScore(srcElem, tgtElem);
    double typeScore = -1;
    Double leftTypeScore = null;
    Double rightTypeScore = null;

    if(srcLeftType.isPresent() && tgtLeftType.isPresent()){
      leftTypeScore = CachedMatches.getMatch(srcLeftType.get(), tgtLeftType.get());
    }
    if(srcRightType.isPresent() && tgtRightType.isPresent()){
      rightTypeScore = CachedMatches.getMatch(srcRightType.get(), tgtRightType.get());
    }

    if(leftTypeScore != null) {
      typeScore = leftTypeScore;
    }
    if(rightTypeScore != null) {
      typeScore = typeScore < 0 ? rightTypeScore : mean(typeScore, rightTypeScore);
    }

    double score = typeScore < 0 ? nameScore : nameScore * 0.2 + typeScore * 0.8;

    CachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
}
