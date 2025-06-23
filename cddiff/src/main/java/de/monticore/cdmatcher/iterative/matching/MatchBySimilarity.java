/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching;

import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.cdmatcher.MatchingStrategy;

public class MatchBySimilarity<T> implements MatchingStrategy<T> {
  
  private final CDSimilarity<T> similarity;
  
  public MatchBySimilarity(CDSimilarity<T> similarity) {
    this.similarity = similarity;
  }
  
  @Override
  public double getScore(T srcElem, T tgtElem) {
    return similarity.computeWeight(srcElem, tgtElem);
  }
  
}
