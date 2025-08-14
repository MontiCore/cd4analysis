package de.monticore.refmodel;

/**
 * Exception thrown when an existing binding conflicts with a new binding that is being added to
 * a set of bindings.
 */
public class BindingConflictException extends Exception {

  public BindingConflictException() {
  }

  public BindingConflictException(Binding<?> conflictingBinding) {
    super("Binding conflict. Cannot add: " + conflictingBinding);
  }
}
