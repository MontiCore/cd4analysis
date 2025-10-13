/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

/**
 * An object, which can be observed
 *
 * @param <O> the corresponding observer class
 * @param <T> the domain class itself
 */
public interface ICDObservable<O extends ICDObserver<T>, T extends ICDObservable<O, T>> {
  
  void addObserver(O observer);
  
  void removeObserver(O observer);
  
}
