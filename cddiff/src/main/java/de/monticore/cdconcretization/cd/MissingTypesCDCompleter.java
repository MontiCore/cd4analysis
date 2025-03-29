package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.ITypeCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public class MissingTypesCDCompleter extends AbstractCDCompleter {

  private final ITypeCompleter typeCompleter;

  public MissingTypesCDCompleter(ITypeCompleter typeCompleter) {
    this.typeCompleter = typeCompleter;
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    for (ASTCDClass referenceClass : referenceCD.getCDDefinition().getCDClassesList()) {
      typeCompleter.completeType(concreteCD.getCDDefinition(), referenceClass, context);
    }
    for (ASTCDInterface referenceInterface : referenceCD.getCDDefinition().getCDInterfacesList()) {
      typeCompleter.completeType(concreteCD.getCDDefinition(), referenceInterface, context);
    }
    for (ASTCDEnum referenceEnum : referenceCD.getCDDefinition().getCDEnumsList()) {
      typeCompleter.completeType(concreteCD.getCDDefinition(), referenceEnum, context);
    }
    super.complete(concreteCD, referenceCD, context);
  }
}
