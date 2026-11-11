/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import javax.annotation.Nonnull;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.function.*;

public class SetAdapter<T> extends AbstractSet<T> {
  
  protected final Supplier<Iterator<T>> iteratorSupplier;
  protected final IntSupplier sizeSupplier;
  protected final Function<Object, Boolean> remover;
  protected final Consumer<T> notifyRemove;
  protected final Function<T, Boolean> adder;
  
  public SetAdapter(Supplier<Iterator<T>> iteratorSupplier, IntSupplier sizeSupplier,
      Function<Object, Boolean> remover, Consumer<T> notifyRemove, Function<T, Boolean> adder) {
    this.iteratorSupplier = iteratorSupplier;
    this.sizeSupplier = sizeSupplier;
    this.remover = remover;
    this.notifyRemove = notifyRemove;
    this.adder = adder;
  }
  
  @Override
  public int size() {
    return this.sizeSupplier.getAsInt();
  }
  
  @Override
  @Nonnull
  public Iterator<T> iterator() {
    Iterator<T> it = this.iteratorSupplier.get();
    return new Iterator<T>() {
      
      T val;
      
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }
      
      @Override
      public T next() {
        val = it.next();
        return val;
      }
      
      @Override
      public void remove() {
        it.remove();
        notifyRemove.accept(val);
      }
      
    };
  }
  
  @Override
  public boolean remove(Object o) {
    return this.remover.apply(o);
  }
  
  @Override
  public boolean add(T t) {
    return this.adder.apply(t);
  }
  
}
