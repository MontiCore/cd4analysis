package de.monticore.cdconcretization.util;

public abstract class AbstractChainable<T extends AbstractChainable<T>> implements IChainable<T> {
  protected T next;

  @Override
  public void setNext(T next) {
    this.next = next;
  }

  // TODO maybe move to interface. Then we can check in builder of the passed object already has a
  // next and move the tail throw an error
  public boolean hasNext() {
    return next != null;
  }
}
