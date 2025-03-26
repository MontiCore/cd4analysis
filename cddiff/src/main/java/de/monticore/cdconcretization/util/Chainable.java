package de.monticore.cdconcretization.util;

public interface Chainable<T extends Chainable<T>> {

  /**
   * Sets the next element in the chain, i.e., the element that should be called after this one.
   *
   * @param next the next element in the chain
   */
  void setNext(T next);
}
