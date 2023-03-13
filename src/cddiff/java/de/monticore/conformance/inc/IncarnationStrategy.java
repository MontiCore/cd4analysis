package de.monticore.conformance.inc;

import java.util.Set;

public interface IncarnationStrategy<T> {
  Set<T> getRefElements(T concrete);

  boolean isIncarnation(T concrete, T ref);
}
