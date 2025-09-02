/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDTypeComposite implements MatchingStrategy<ASTCDType> {

  Map<MatchingStrategy<ASTCDType>, BiFunction<ASTCDType, ASTCDType, Boolean>>  strategies;
  MatchingStrategy<ASTCDEnum> enumStrategy;
  public CachedMatches cachedMatches;
  public static final BiFunction<ASTCDType, ASTCDType, Boolean> ALWAYS_APPLY = (a, b) -> true;

  /**
   * @param strategies The strategies to use for matching CDTypes along with a filter function, if the function returns false, the strategy is not applied. For meaningful results, at least one strategy should always return true.
   * @param enumStrategy A strategy for matching enums
   * @param cachedMatches A cache for already computed matches
   */
  public MatchCDTypeComposite(Map<MatchingStrategy<ASTCDType>, BiFunction<ASTCDType, ASTCDType, Boolean>> strategies,
                              MatchingStrategy<ASTCDEnum> enumStrategy,
                              CachedMatches cachedMatches) {
    this.strategies = strategies;
    this.enumStrategy = enumStrategy;
    this.cachedMatches = cachedMatches;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    double score;
    if(srcElem instanceof ASTCDEnum && tgtElem instanceof ASTCDEnum) {
      score = enumStrategy.getScore((ASTCDEnum) srcElem, (ASTCDEnum) tgtElem);
    }
    else if(srcElem instanceof ASTCDEnum || tgtElem instanceof ASTCDEnum) {
      score = 0.0;
    }
    else {
      List<Double> scores = strategies.entrySet().stream().filter(e -> e.getValue().apply(srcElem, tgtElem)).map(e -> e.getKey().getScore(srcElem, tgtElem)).collect(
        Collectors.toList());
      score = scores.isEmpty() ? 0.0 : mean(scores);
    }

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;

  }


  public static <T> BiFunction<ASTCDType, ASTCDType, Boolean> notBothEmpty(Function<ASTCDType, Collection<T>> extractor) {
    return (src, tgt) -> !extractor.apply(src).isEmpty() || !extractor.apply(tgt).isEmpty();
  }

}
