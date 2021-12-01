/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade.exception;

public enum CDFactoryErrorCode {

  COULD_NOT_CREATE_ATTRIBUTE(0, "Could not create CD attribute: '%s'"),
  COULD_NOT_CREATE_METHOD(10, "Could not create CD method: '%s'"),
  COULD_NOT_CREATE_TYPE(20, "Could not create CD type: '%s'");

  final int code;

  final String message;

  CDFactoryErrorCode(final int code, final String message) {
    this.code = code;
    this.message = message;
  }

  public String getError(String definition) {
    return String.format("0xCD3%02X", code) + ": " + String.format(message, definition);
  }
}
