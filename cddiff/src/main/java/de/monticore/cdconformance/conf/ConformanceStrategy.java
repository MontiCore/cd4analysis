/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf;

public interface ConformanceStrategy<T> {
  
  boolean checkConformance(T concrete);
  
}
