/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

/**
 * The generic observer interface
 *
 * @param <T> the domain class which is observed
 */
public interface ICDObserver<T extends ICDObservable<?, T>> {
  
  /**
   * The observed object has been changed.
   * Use the concrete observers to get notified about concrete changes
   *
   * @param clazz the obj which has changed
   */
  void notifyUpdate(T clazz);
  
}
