package de.monticore.cdconcretization;

public interface IIncarnationCompleter<Kind> {
  void completeIncarnations() throws CompletionException;
}
