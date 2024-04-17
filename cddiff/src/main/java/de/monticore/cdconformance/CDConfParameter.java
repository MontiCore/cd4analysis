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

  NO_MULTI_INC("when added a type, assoc or attribute can have many incarnations");

  private final String description;

  CDConfParameter(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
