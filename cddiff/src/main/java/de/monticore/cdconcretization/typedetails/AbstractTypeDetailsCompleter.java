package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.util.AbstractChainable;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public abstract class AbstractTypeDetailsCompleter
    extends AbstractChainable<AbstractTypeDetailsCompleter> implements ITypeDetailsCompleter {

  @Override
  public void completeTypeDetails(ASTCDType concreteType, ASTCDType referenceType)
      throws CompletionException {
    if (concreteType instanceof ASTCDClass) {
      if (referenceType instanceof ASTCDClass) {
        completeClassDetails((ASTCDClass) concreteType, (ASTCDClass) referenceType);
      } else {
        // TODO better error message
        throw new CompletionException("A class got matched to a different type.");
      }
    } else if (concreteType instanceof ASTCDInterface) {
      if (referenceType instanceof ASTCDInterface) {
        completeInterfaceDetails((ASTCDInterface) concreteType, (ASTCDInterface) referenceType);
      } else {
        // TODO better error message
        throw new CompletionException("An interface got matched to a different type.");
      }
    } else if (concreteType instanceof ASTCDEnum) {
      if (referenceType instanceof ASTCDEnum) {
        completeEnumDetails((ASTCDEnum) concreteType, (ASTCDEnum) referenceType);
      } else {
        // TODO better error message
        throw new CompletionException("An enum got matched to a different type.");
      }
    } else {
      next(concreteType, referenceType);
    }
  }

  protected void completeClassDetails(ASTCDClass concreteType, ASTCDClass referenceType)
      throws CompletionException {
    next(concreteType, referenceType);
  }

  protected void completeInterfaceDetails(ASTCDInterface concreteType, ASTCDInterface referenceType)
      throws CompletionException {
    next(concreteType, referenceType);
  }

  protected void completeEnumDetails(ASTCDEnum concreteType, ASTCDEnum referenceType)
      throws CompletionException {
    next(concreteType, referenceType);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteType
   * @param referenceType
   */
  protected void next(ASTCDType concreteType, ASTCDType referenceType) throws CompletionException {
    if (hasNext()) {
      next.completeTypeDetails(concreteType, referenceType);
    }
  }
}
