package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.cd.type.ICDTypeCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public class MissingTypesCDCompleter extends AbstractCDCompleter {

  private final ICDTypeCompleter typeCompleter;

  public MissingTypesCDCompleter(ICDTypeCompleter typeCompleter) {
    this.typeCompleter = typeCompleter;
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException {
    for (ASTCDClass referenceClass : referenceCD.getCDDefinition().getCDClassesList()) {
      typeCompleter.completeCDForType(concreteCD.getCDDefinition(), referenceClass, context);
    }
    for (ASTCDInterface referenceInterface : referenceCD.getCDDefinition().getCDInterfacesList()) {
      typeCompleter.completeCDForType(concreteCD.getCDDefinition(), referenceInterface, context);
    }
    for (ASTCDEnum referenceEnum : referenceCD.getCDDefinition().getCDEnumsList()) {
      typeCompleter.completeCDForType(concreteCD.getCDDefinition(), referenceEnum, context);
    }
    super.complete(concreteCD, referenceCD, context);
  }
}
