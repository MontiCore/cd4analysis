package de.monticore.conformance.conf;

public interface ConformanceStrategy<T> {
  public boolean checkConformance(T concrete);
}
