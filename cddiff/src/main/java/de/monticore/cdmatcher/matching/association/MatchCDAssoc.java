package de.monticore.cdmatcher.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cdmatcher.matching.CachedMatches;
import de.monticore.cdmatcher.matching.MatchingStrategy;

import static com.google.common.math.DoubleMath.mean;


public class MatchCDAssoc implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    ASTCDType srcRightType = CDAssociationHelper.getCDTypeSymbol(srcElem.getRight());
    ASTCDType srcLeftType = CDAssociationHelper.getCDTypeSymbol(srcElem.getLeft());
    ASTCDType tgtRightType = CDAssociationHelper.getCDTypeSymbol(tgtElem.getRight());
    ASTCDType tgtLeftType = CDAssociationHelper.getCDTypeSymbol(tgtElem.getLeft());

    if(srcRightType == null || srcLeftType == null || tgtRightType == null || tgtLeftType == null) {
      return new MatchCDAssocByName().getScore(srcElem, tgtElem);
    }

    double nameScore = new MatchCDAssocByName().getScore(srcElem, tgtElem);
    double typeScore = -1;

    Double leftTypeScore = CachedMatches.getMatch(srcLeftType, tgtLeftType);
    Double rightTypeScore = CachedMatches.getMatch(srcRightType, tgtRightType);

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
