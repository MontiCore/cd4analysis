package de.monticore.conformance;

import java.util.Set;

public interface IncarnationStrategy<T> {
  Set<T> getRefElements(T concrete);

  boolean isInstance(T concrete, T ref);
}
