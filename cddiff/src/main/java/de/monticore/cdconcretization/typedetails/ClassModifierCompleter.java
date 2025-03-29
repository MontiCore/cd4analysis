package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.attribute.TypeCompletionContext;

public class ClassModifierCompleter extends AbstractTypeDetailsCompleter {

  @Override
  protected void completeClassDetails(
      ASTCDClass concreteType, ASTCDClass referenceType, TypeCompletionContext context)
      throws CompletionException {
    // maybe add other modifiers later
    if (referenceType.getModifier().isAbstract()) {
      concreteType.getModifier().setAbstract(true);
    }
    next(concreteType, referenceType, context);
  }
}
