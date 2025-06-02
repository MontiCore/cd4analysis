/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.method.IMethodInTypeCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public class TypeMethodsCompleter extends AbstractTypeCompleter {
  
  private final IMethodInTypeCompleter methodCompleter;
  
  public TypeMethodsCompleter(IMethodInTypeCompleter methodCompleter) {
    this.methodCompleter = methodCompleter;
  }
  
  @Override
  protected void completeClassDetails(ASTCDClass concreteType, ASTCDClass referenceType,
      TypeCompletionContext context) throws CompletionException {
    completeMethods(concreteType, referenceType, context);
    next(concreteType, referenceType, context);
  }
  
  @Override
  protected void completeInterfaceDetails(ASTCDInterface concreteType, ASTCDInterface referenceType,
      TypeCompletionContext context) throws CompletionException {
    completeMethods(concreteType, referenceType, context);
    next(concreteType, referenceType, context);
  }
  
  protected void completeMethods(ASTCDType concreteType, ASTCDType referenceType,
      TypeCompletionContext context) throws CompletionException {
    for (ASTCDMethod refMethod : referenceType.getCDMethodList()) {
      methodCompleter.completeMethodInType(concreteType, refMethod, context);
    }
  }
  
}
