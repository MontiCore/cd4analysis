/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDTypeComposite implements MatchingStrategy<ASTCDType> {

  Set<MatchingStrategy<ASTCDType>> strategies;
  MatchingStrategy<ASTCDEnum> enumStrategy;
  public CachedMatches cachedMatches;

  public MatchCDTypeComposite(Set<MatchingStrategy<ASTCDType>> strategies,
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
      List<Double> scores = strategies.stream().map(s -> s.getScore(srcElem, tgtElem)).collect(
        Collectors.toList());
      score = scores.isEmpty() ? 0.0 : mean(scores);
    }

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;

  }

}
