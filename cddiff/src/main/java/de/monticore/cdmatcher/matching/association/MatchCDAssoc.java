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

    Double leftTypeScore = CachedMatches.getMatch(srcLeftType, tgtLeftType);
    Double rightTypeScore = CachedMatches.getMatch(srcRightType, tgtRightType);

    double score = new MatchCDAssocByName().getScore(srcElem, tgtElem);

    if(leftTypeScore != null) {
      score = mean(score, leftTypeScore);
    }
    if(rightTypeScore != null) {
      score = mean(score, rightTypeScore);
    }

    CachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
}
