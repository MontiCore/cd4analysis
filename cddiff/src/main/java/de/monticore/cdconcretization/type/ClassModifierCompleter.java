package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdconcretization.CompletionException;

public class ClassModifierCompleter extends AbstractTypeDetailsCompleter {

  @Override
  protected void completeClassDetails(ASTCDClass concreteType, ASTCDClass referenceType)
      throws CompletionException {
    // maybe add other modifiers later
    if (referenceType.getModifier().isAbstract()) {
      concreteType.getModifier().setAbstract(true);
    }
    next(concreteType, referenceType);
  }
}
