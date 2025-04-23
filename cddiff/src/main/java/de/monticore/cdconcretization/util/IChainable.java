package de.monticore.cdconcretization.util;

public interface IChainable<T extends IChainable<T>> {

  /**
   * Sets the next element in the chain, i.e., the element that should be called after this one.
   *
   * @param next the next element in the chain
   */
  void setNext(T next);

  /**
   * @return true if there is a next element in the chain, false otherwise
   */
  boolean hasNext();
}
