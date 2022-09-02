package de.monticore.cdmerge.validation;

import de.monticore.cdbasis._ast.ASTCDDefinition;

public interface ModelValidator {

  void apply(ASTCDDefinition classDiagram);

}
