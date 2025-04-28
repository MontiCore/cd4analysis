package de.monticore.cdconcretization;

/**
 * Exception that indicates that a concrete CD cannot be completed with the given reference CD.
 * This happens if certain definitions in the concrete CD already conflict with the reference CD,
 * e.g. incompatible attribute types or incompatible associations with the same name.
 */
public class CompletionException extends Exception {
  public CompletionException(String message) {
    super(message);
  }
}
