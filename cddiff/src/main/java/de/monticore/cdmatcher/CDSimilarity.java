package de.monticore.cdmatcher;

public interface CDSimilarity<T> {
  public Double computeWeight(T srcElem, T tgtElem);
}
