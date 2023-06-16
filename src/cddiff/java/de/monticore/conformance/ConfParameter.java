package de.monticore.conformance;

public enum ConfParameter {
  STEREOTYPE_MAPPING("Enable mapping with stereotypes"),
  NAME_MAPPING(
      "Enable mapping with names, element of the concrete and the reference model with the same name will be map together without explicit mapping "),
  INHERITANCE(
      "Enable Inheritance of associations , methods, and attribute on the side of the concrete model."),
  STRICT_INHERITANCE("Enable Strict Inheritance for associations");

  private final String description;

  ConfParameter(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
