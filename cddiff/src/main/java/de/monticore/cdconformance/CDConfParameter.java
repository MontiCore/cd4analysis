/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

public enum CDConfParameter {
  
  STEREOTYPE_MAPPING("Enable mapping with stereotypes"), SRC_TARGET_ASSOC_MAPPING(
      "when enable a concrete association implicitly incarnate a reference association when the reference and the roles"
          + "match according to the direction"), NAME_MAPPING(
              "Enable mapping with names, element of the concrete and the reference model with the same name will be map together without explicit mapping "),
  INHERITANCE(
      "Enable Inheritance of associations , methods, and attribute on the side of the concrete model."),
  
  ALLOW_CARD_RESTRICTION(
      "when added, the cardinality of a concrete association can refine the card  of a reference association "),
  
  NO_MULTI_INC("when added a type, assoc or attribute can have many incarnations"),
  
  METHOD_OVERLOADING("when added, methods can be overloaded, i.e., a method can have "
      + "the same name but different parameter types"),
  
  STRICT_PARAMETER_ORDER("when added, the type & order of parameters in a method is "
      + "important. By default, method parameters need to match a reference parameter by type "
      + "& name"),
  
  ALLOW_ADDITIONAL_PARAMETERS("when added, a concrete method can have additional"
      + "parameters compared to the reference method. By default, a method must have the same"
      + "parameters as the reference method. If combined with " + STRICT_PARAMETER_ORDER + " "
      + "the additional parameters must be at the end of the parameter list."),
  
  ADAPTED_NAME_MAPPING("when added, names in the concrete CD that are derived via implicit name"
      + " adaptation (using element type incarnations) are accepted as incarnations of the"
      + " corresponding reference elements without requiring explicit stereotype mappings."
      + " Applies to attributes, methods, and association roles. For example, 'assignedTickets'"
      + " is accepted as an incarnation of 'assignedTasks' when 'Ticket' incarnates 'Task',"
      + " and 'findTicket' as an incarnation of 'findTask'.");
  
  private final String description;
  
  CDConfParameter(String description) {
    this.description = description;
  }
  
  public String getDescription() { return description; }
  
}
