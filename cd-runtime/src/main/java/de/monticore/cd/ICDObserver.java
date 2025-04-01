/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

public interface ICDObserver<T extends ICDObservable> {

  default void update(T subject) {
  }

}
