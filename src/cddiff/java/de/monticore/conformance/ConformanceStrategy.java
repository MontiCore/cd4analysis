package de.monticore.conformance;

public interface ConformanceStrategy<T> {
  public boolean checkConformance(T concrete);
}
