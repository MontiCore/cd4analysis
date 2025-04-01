package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.attribute.ITypeAttributeCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

/**
 * Completes the attributes of a concrete type by calling the attribute completer for each
 * attribute in the reference type. This is done for both classes and interfaces as interfaces
 * could hold static attributes as well.
 */
/*
 * NOTE: Although, this is a very fine-grained decomposition of the logic, we kep the architecture
 * is more modular and consistent. That way we could later on even generate it for a given language
 * AST.
 */
public class TypeAttributesCompleter extends AbstractTypeCompleter {

  private final ITypeAttributeCompleter attributeCompleter;

  public TypeAttributesCompleter(ITypeAttributeCompleter attributeCompleter) {
    this.attributeCompleter = attributeCompleter;
  }

  @Override
  protected void completeClassDetails(
      ASTCDClass concreteType, ASTCDClass referenceType, TypeCompletionContext context)
      throws CompletionException {
    completeAttributes(concreteType, referenceType, context);
    next(concreteType, referenceType, context);
  }

  @Override
  protected void completeInterfaceDetails(
      ASTCDInterface concreteType, ASTCDInterface referenceType, TypeCompletionContext context)
      throws CompletionException {
    completeAttributes(concreteType, referenceType, context);
    next(concreteType, referenceType, context);
  }

  protected void completeAttributes(
      ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context)
      throws CompletionException {
    for (ASTCDAttribute rAttribute : referenceType.getCDAttributeList()) {
      attributeCompleter.completeTypeForAttribute(concreteType, rAttribute, context);
    }
  }
}
