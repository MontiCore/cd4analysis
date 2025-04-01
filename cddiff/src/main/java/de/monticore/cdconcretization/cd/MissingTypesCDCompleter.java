package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.cd.type.ITypeInCDCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public class MissingTypesCDCompleter extends AbstractCDCompleter {

  private final ITypeInCDCompleter typeCompleter;

  public MissingTypesCDCompleter(ITypeInCDCompleter typeCompleter) {
    this.typeCompleter = typeCompleter;
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException {
    for (ASTCDClass referenceClass : referenceCD.getCDDefinition().getCDClassesList()) {
      typeCompleter.completeTypeInCD(concreteCD.getCDDefinition(), referenceClass, context);
    }
    for (ASTCDInterface referenceInterface : referenceCD.getCDDefinition().getCDInterfacesList()) {
      typeCompleter.completeTypeInCD(concreteCD.getCDDefinition(), referenceInterface, context);
    }
    for (ASTCDEnum referenceEnum : referenceCD.getCDDefinition().getCDEnumsList()) {
      typeCompleter.completeTypeInCD(concreteCD.getCDDefinition(), referenceEnum, context);
    }
    super.complete(concreteCD, referenceCD, context);
  }
}
