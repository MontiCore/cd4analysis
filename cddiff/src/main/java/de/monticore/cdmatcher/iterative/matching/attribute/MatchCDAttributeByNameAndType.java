/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAttributeByNameAndType implements MatchingStrategy<ASTCDAttribute> {

  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDAttribute> nameMatcher;


  public MatchCDAttributeByNameAndType(CachedMatches cachedMatches, MatchingStrategy<ASTCDAttribute> nameMatcher) {
    this.cachedMatches = cachedMatches;
    this.nameMatcher = nameMatcher;
  }

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    ASTCDType srcAttributeClassType = CDAttributeHelper.resolveClass(srcElem);
    ASTCDType tgtAttributeClassType = CDAttributeHelper.resolveClass(tgtElem);

    // ToDo resolve nested types
    if (srcAttributeClassType == null && tgtAttributeClassType == null) {
      return 1.0;
    }
    if (srcAttributeClassType == null || tgtAttributeClassType == null) {
      return 0.0;
    }

    Double attributeClassType = cachedMatches.getMatch(srcAttributeClassType,
        tgtAttributeClassType);

    double score = nameMatcher.getScore(srcElem, tgtElem);

    if (attributeClassType != null) {
      score = mean(score, attributeClassType);
    }

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }

}
