package de.monticore.cdmatcher.iterative.matching;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeFromCache;
import de.monticore.cdmatcher.similarity.MCNestedTypeSimilarity;
import de.monticore.cdmatcher.similarity.MCTypeInbuildsSimilarity;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.cddiff.ow2cw.CDAttributeHelper.isNestedType;
import static de.monticore.cddiff.ow2cw.CDAttributeHelper.resolveInnermostClass;

public class MatchMCType implements MatchingStrategy<ASTMCType> {

  private final CachedMatches cachedMatches;
  private final ICD4CodeArtifactScope srcScope;
  private final ICD4CodeArtifactScope tgtScope;

  public MatchMCType(CachedMatches cachedMatches, ICD4CodeArtifactScope srcScope, ICD4CodeArtifactScope tgtScope) {
    this.cachedMatches = cachedMatches;
    this.srcScope = srcScope;
    this.tgtScope = tgtScope;
  }

  /**
   * For nested types the similarity is calculated as the product of the similarities of each nesting level.
   * For example the similarity of a nested type A(B(C)) and a nested type D(E(F)) is the product s(A, D) * s(B, E) * s(C, F), where s is the similarity of two types.
   * If the nesting depth is different, the similarity is 0.
   * @param srcElem The source type to compare
   * @param tgtElem The target type to compare
   * @return A similarity value between 0 and 1, where 1 means the types are identical and 0 means they are completely different.
   */
  @Override
  public double getScore(ASTMCType srcElem, ASTMCType tgtElem) {
    MatchCDTypeFromCache typeMatcher = new MatchCDTypeFromCache(cachedMatches);

    if (isNestedType(srcElem) != isNestedType(tgtElem)) {
      return 0.0;
    } else if (isNestedType(srcElem)) {
      Double nestingSimilarity = new MCNestedTypeSimilarity().computeWeight(srcElem, tgtElem);

      ASTCDType srcType = CDAttributeHelper.resolveInnermostClass(srcElem, srcScope);
      ASTCDType tgtType = CDAttributeHelper.resolveInnermostClass(tgtElem, tgtScope);

      if (srcType != null && tgtType != null) {
        return nestingSimilarity * typeMatcher.getScore(srcType, tgtType);
      } else {
        return nestingSimilarity * new MCTypeInbuildsSimilarity().computeWeight(
          resolveInnermostClass(srcElem),
          resolveInnermostClass(tgtElem)
        );
      }

    } else {
      ASTCDType srcType = CDAttributeHelper.resolveClass(srcElem, srcScope);
      ASTCDType tgtType = CDAttributeHelper.resolveClass(tgtElem, tgtScope);

      if (srcType != null && tgtType != null) {
        return typeMatcher.getScore(srcType, tgtType);
      } else {
        return new MCTypeInbuildsSimilarity().computeWeight(srcElem, tgtElem);
      }
    }
  }
}
