package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.attribute.IAttributeCompleter;
import de.monticore.cdconcretization.attribute.TypeCompletionContext;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

// TODO Do we really want to decompose the logic THAT much or directly call attribute/method
// completion from the BaseCDCompleter??
public class TypeAttributesCompleter extends AbstractTypeDetailsCompleter {

  private final IAttributeCompleter attributeCompleter;

  public TypeAttributesCompleter(IAttributeCompleter attributeCompleter) {
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
      ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context) {
    for (ASTCDAttribute rAttribute : referenceType.getCDAttributeList()) {
      attributeCompleter.completeAttribute(concreteType, rAttribute, context);
    }
  }
}
