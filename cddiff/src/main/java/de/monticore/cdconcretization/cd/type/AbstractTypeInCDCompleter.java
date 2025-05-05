package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdconcretization.util.IChainable;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public abstract class AbstractTypeInCDCompleter implements ITypeInCDCompleter,
        IChainable<AbstractTypeInCDCompleter> {

  private AbstractTypeInCDCompleter next;

  @Override
  public void completeTypeInCD(
      ASTCDDefinition concreteCD, ASTCDType referenceType, CDCompletionContext context) {
    if (referenceType instanceof ASTCDClass) {
      completeType(concreteCD, (ASTCDClass) referenceType, context);
    } else if (referenceType instanceof ASTCDInterface) {
      completeType(concreteCD, (ASTCDInterface) referenceType, context);
    } else if (referenceType instanceof ASTCDEnum) {
      completeType(concreteCD, (ASTCDEnum) referenceType, context);
    } else {
      next(concreteCD, referenceType, context);
    }
  }

  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDClass referenceType, CDCompletionContext context) {
    next(concreteCD, referenceType, context);
  }

  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDInterface referenceType, CDCompletionContext context) {
    next(concreteCD, referenceType, context);
  }

  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDEnum referenceType, CDCompletionContext context) {
    next(concreteCD, referenceType, context);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteCD
   * @param referenceType
   */
  protected void next(
      ASTCDDefinition concreteCD, ASTCDType referenceType, CDCompletionContext context) {
    if (hasNext()) {
      next.completeTypeInCD(concreteCD, referenceType, context);
    }
  }

  @Override
  public void setNext(AbstractTypeInCDCompleter next) {
    this.next = next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }
}
