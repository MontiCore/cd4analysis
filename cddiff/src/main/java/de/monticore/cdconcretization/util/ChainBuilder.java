package de.monticore.cdconcretization.util;

public class ChainBuilder<T extends Chainable<T>> {

  private T head;
  private T tail;

  /**
   * Adds an element to the chain
   *
   * @param completer the element to add
   * @return
   */
  public ChainBuilder<T> add(T completer) {
    if (head == null) {
      head = completer;
    } else {
      tail.setNext(completer);
    }
    tail = completer;
    return this;
  }

  /**
   * Returns the head of the chain, i.e. the element that can be passed to client code to call the
   * whole chain.
   *
   * @return
   */
  public T build() {
    return head;
  }
}
