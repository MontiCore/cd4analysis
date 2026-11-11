/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import java.util.AbstractList;
import java.util.function.*;

public class ListAdapter<T> extends AbstractList<T> {
  
  protected final IntFunction<T> getter;
  protected final IntSupplier sizeSupplier;
  protected final ObjIntConsumer<T> adder;
  protected final IntFunction<T> remover;
  
  public ListAdapter(IntFunction<T> getter, IntSupplier sizeSupplier, ObjIntConsumer<T> adder,
      IntFunction<T> remover) {
    this.getter = getter;
    this.sizeSupplier = sizeSupplier;
    this.adder = adder;
    this.remover = remover;
  }
  
  @Override
  public T get(int index) {
    return this.getter.apply(index);
  }
  
  @Override
  public int size() {
    return this.sizeSupplier.getAsInt();
  }
  
  @Override
  public void add(int index, T element) {
    this.adder.accept(element, index);
  }
  
  @Override
  public T remove(int index) {
    return this.remover.apply(index);
  }
  
}
