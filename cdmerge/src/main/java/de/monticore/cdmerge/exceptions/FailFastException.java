/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.exceptions;

/** A Runtime Exception which forces immediate shutdown of tool */
public class FailFastException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FailFastException(String message) {
    super(message);
  }
}
