package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.typedetails.ITypeDetailsCompleter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;

public class TypeDetailsCDCompleter extends AbstractCDCompleter {

  private final MatchingStrategy<ASTCDType> typeIncStrategy;
  private final ITypeDetailsCompleter typeDetailsCompleter;

  public TypeDetailsCDCompleter(
      MatchingStrategy<ASTCDType> typeIncStrategy, ITypeDetailsCompleter typeDetailsCompleter) {
    this.typeIncStrategy = typeIncStrategy;
    this.typeDetailsCompleter = typeDetailsCompleter;
  }

  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {
    // complete member incarnations
    for (ASTCDClass cClass : concreteCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cClass)) {
        typeDetailsCompleter.completeTypeDetails(cClass, rType);
      }
    }
    for (ASTCDInterface cInterface : concreteCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cInterface)) {
        typeDetailsCompleter.completeTypeDetails(cInterface, rType);
      }
    }
    for (ASTCDEnum cEnum : concreteCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cEnum)) {
        typeDetailsCompleter.completeTypeDetails(cEnum, rType);
      }
    }
    super.complete(concreteCD, referenceCD);
  }
}
