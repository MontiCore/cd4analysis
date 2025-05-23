package de.monticore.cdmatcher.similarity;

public interface CDSimilarity<T> {
  public Double computeWeight(T srcElem, T tgtElem);
}
