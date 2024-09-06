package de.monticore.cdconcretization;

public interface IncarnationCompleter<Kind> {
  void completeIncarnations() throws CompletionException;
}
