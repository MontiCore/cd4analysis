package de.monticore.conformance;

import java.util.Set;

public interface IncarnationStrategy<T> {
  public Set<T> getRefElements(T concrete);
}
