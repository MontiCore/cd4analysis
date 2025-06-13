package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.CachedMatches;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDType implements MatchingStrategy<ASTCDType> {
  Set<MatchingStrategy<ASTCDType>> strategies;

  public MatchCDType(Set<MatchingStrategy<ASTCDType>> strategies) {
    this.strategies = strategies;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    List<Double> scores = strategies.stream().map(s -> s.getScore(srcElem, tgtElem)).collect(Collectors.toList());

    double score = scores.isEmpty() ? 0.0 : mean(scores);
    CachedMatches.putMatch(srcElem, tgtElem, score);
    return score;

  }
}
