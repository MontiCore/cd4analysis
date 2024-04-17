package de.monticore.cdconformance.conf;

public interface ConformanceStrategy<T> {
  public boolean checkConformance(T concrete);
}
