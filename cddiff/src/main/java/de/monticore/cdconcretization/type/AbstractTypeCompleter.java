package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.AbstractChainable;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public abstract class AbstractTypeCompleter extends AbstractChainable<AbstractTypeCompleter>
    implements ITypeCompleter {

  @Override
  public void completeType(ASTCDDefinition concreteCD, ASTCDType referenceType) {
    if (referenceType instanceof ASTCDClass) {
      completeType(concreteCD, (ASTCDClass) referenceType);
    } else if (referenceType instanceof ASTCDInterface) {
      completeType(concreteCD, (ASTCDInterface) referenceType);
    } else if (referenceType instanceof ASTCDEnum) {
      completeType(concreteCD, (ASTCDEnum) referenceType);
    } else {
      next(concreteCD, referenceType);
    }
  }

  protected void completeType(ASTCDDefinition concreteCD, ASTCDClass referenceType) {
    next(concreteCD, referenceType);
  }

  protected void completeType(ASTCDDefinition concreteCD, ASTCDInterface referenceType) {
    next(concreteCD, referenceType);
  }

  protected void completeType(ASTCDDefinition concreteCD, ASTCDEnum referenceType) {
    next(concreteCD, referenceType);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteCD
   * @param referenceType
   */
  protected void next(ASTCDDefinition concreteCD, ASTCDType referenceType) {
    if (hasNext()) {
      next.completeType(concreteCD, referenceType);
    }
  }
}
