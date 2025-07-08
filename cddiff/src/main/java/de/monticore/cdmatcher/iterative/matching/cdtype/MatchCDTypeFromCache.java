/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchBySimilarity;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;

public class MatchCDTypeFromCache implements MatchingStrategy<ASTCDType> {

  private static MatchingStrategy<ASTCDType> defaultFallbackStrategy = new MatchBySimilarity<>(
      new CDTypeSimilarity());
  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDType> fallbackStrategy;

  public MatchCDTypeFromCache(CachedMatches cachedMatches,
      MatchingStrategy<ASTCDType> fallbackStrategy) {
    this.cachedMatches = cachedMatches;
    this.fallbackStrategy = fallbackStrategy;
  }

  public MatchCDTypeFromCache(CachedMatches cachedMatches) {
    this(cachedMatches, defaultFallbackStrategy);
  }

  public static MatchingStrategy<ASTCDType> getDefaultFallbackStrategy() {
    return defaultFallbackStrategy;
  }

  public static void setDefaultFallbackStrategy(
      MatchingStrategy<ASTCDType> defaultFallbackStrategy) {
    MatchCDTypeFromCache.defaultFallbackStrategy = defaultFallbackStrategy;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Double cachedScore = cachedMatches.getMatch(srcElem, tgtElem);

    return cachedScore != null ? cachedScore : fallbackStrategy.getScore(srcElem, tgtElem);
  }

}
