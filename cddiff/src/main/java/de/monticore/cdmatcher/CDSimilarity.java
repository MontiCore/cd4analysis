/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

/** Class used for computing structural matches and best matches of CD-elements. */
public interface CDSimilarity<T> {
  
  /**
   * Computes the similarity of two elements based on sub-elements and associated elements.
   *
   * @param srcElem element to compare
   * @param tgtElem element to compare against
   * @return similarity-score as a double
   */
  Double computeWeight(T srcElem, T tgtElem);
  
}
