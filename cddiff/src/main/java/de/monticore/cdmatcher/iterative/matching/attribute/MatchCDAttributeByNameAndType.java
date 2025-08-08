/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.similarity.MCGenericTypeSimilarity;
import de.monticore.cdmatcher.similarity.MCTypeInbuildsSimilarity;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

import static com.google.common.math.DoubleMath.mean;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isNestedType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.resolveInnermostClass;

public class MatchCDAttributeByNameAndType implements MatchingStrategy<ASTCDAttribute> {

  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDAttribute> nameMatcher;


  public MatchCDAttributeByNameAndType(CachedMatches cachedMatches, MatchingStrategy<ASTCDAttribute> nameMatcher) {
    this.cachedMatches = cachedMatches;
    this.nameMatcher = nameMatcher;
  }

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    Double typeMatching;

    if(isNestedType(srcElem) != isNestedType(tgtElem)) {
      typeMatching = 0.0;
    } else if (isNestedType(srcElem)) {
      Double nestingSimilarity = new MCGenericTypeSimilarity().computeWeight((ASTMCGenericType)  srcElem.getMCType(), (ASTMCGenericType) tgtElem.getMCType());

      ASTCDType srcType = CDAttributeHelper.resolveInnermostClass(srcElem);
      ASTCDType tgtType = CDAttributeHelper.resolveInnermostClass(tgtElem);

      if (srcType != null && tgtType != null) {
        typeMatching = nestingSimilarity * cachedMatches.getMatch(srcType, tgtType);
      } else {
        typeMatching = nestingSimilarity * new MCTypeInbuildsSimilarity().computeWeight(
          resolveInnermostClass((ASTMCGenericType)  srcElem.getMCType()),
          resolveInnermostClass((ASTMCGenericType) tgtElem.getMCType())
        );
      }

    } else {
      ASTCDType srcType = CDAttributeHelper.resolveClass(srcElem);
      ASTCDType tgtType = CDAttributeHelper.resolveClass(tgtElem);

      if (srcType != null && tgtType != null) {
        typeMatching = cachedMatches.getMatch(srcType, tgtType);
      } else {
        typeMatching = new MCTypeInbuildsSimilarity().computeWeight(srcElem.getMCType(), tgtElem.getMCType());
      }
    }


    double score = nameMatcher.getScore(srcElem, tgtElem);

    if (typeMatching != null) {
      score = mean(score, typeMatching);
    }

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }

}
