package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.util.IChainable;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public abstract class AbstractTypeCompleter implements ITypeCompleter,
        IChainable<AbstractTypeCompleter>  {

  private AbstractTypeCompleter next;

  @Override
  public void completeType(
      ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context)
      throws CompletionException {
    if (concreteType instanceof ASTCDClass) {
      if (referenceType instanceof ASTCDClass) {
        completeClassDetails((ASTCDClass) concreteType, (ASTCDClass) referenceType, context);
      } else {
        // TODO better error message
        throw new CompletionException("A class got matched to a different type.");
      }
    } else if (concreteType instanceof ASTCDInterface) {
      if (referenceType instanceof ASTCDInterface) {
        completeInterfaceDetails(
            (ASTCDInterface) concreteType, (ASTCDInterface) referenceType, context);
      } else {
        // TODO better error message
        throw new CompletionException("An interface got matched to a different type.");
      }
    } else if (concreteType instanceof ASTCDEnum) {
      if (referenceType instanceof ASTCDEnum) {
        completeEnumDetails((ASTCDEnum) concreteType, (ASTCDEnum) referenceType, context);
      } else {
        // TODO better error message
        throw new CompletionException("An enum got matched to a different type.");
      }
    } else {
      next(concreteType, referenceType, context);
    }
  }

  protected void completeClassDetails(
      ASTCDClass concreteType, ASTCDClass referenceType, TypeCompletionContext context)
      throws CompletionException {
    next(concreteType, referenceType, context);
  }

  protected void completeInterfaceDetails(
      ASTCDInterface concreteType, ASTCDInterface referenceType, TypeCompletionContext context)
      throws CompletionException {
    next(concreteType, referenceType, context);
  }

  protected void completeEnumDetails(
      ASTCDEnum concreteType, ASTCDEnum referenceType, TypeCompletionContext context)
      throws CompletionException {
    next(concreteType, referenceType, context);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteType
   * @param referenceType
   */
  protected void next(
      ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context)
      throws CompletionException {
    if (hasNext()) {
      next.completeType(concreteType, referenceType, context);
    }
  }

  @Override
  public void setNext(AbstractTypeCompleter next) {
    this.next = next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }
}
