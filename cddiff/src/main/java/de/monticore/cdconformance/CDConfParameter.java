package de.monticore.cdconformance;

public enum CDConfParameter {
  STEREOTYPE_MAPPING("Enable mapping with stereotypes"),
  SRC_TARGET_ASSOC_MAPPING(
      "when enable a concrete association implicitly incarnate a reference association when the reference and the roles"
          + "match according to the direction"),
  NAME_MAPPING(
      "Enable mapping with names, element of the concrete and the reference model with the same name will be map together without explicit mapping "),
  INHERITANCE(
      "Enable Inheritance of associations , methods, and attribute on the side of the concrete model."),

  ALLOW_CARD_RESTRICTION(
      "when added, the cardinality of a concrete association can refine the card  of a reference association "),

  NO_MULTI_INC("when added a type, assoc or attribute can have many incarnations"),

  /**
   * If not added, the order of parameters and the type is considered when checking conformance.
   */
  // TODO what about additional parameters in concrete model?
  IGNORE_PARAMETER_ORDER("when added the order of parameters is ignored when checking conformance. Instead for each reference parameter there has to be one concrete parameter with the same name"),;

  private final String description;

  CDConfParameter(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
