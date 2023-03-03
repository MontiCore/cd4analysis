package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociationTOP;
import de.monticore.cdbasis._ast.ASTCDClassTOP;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumTOP;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceTOP;
import de.monticore.conformance.basic.BasicAssocConfStrategy;
import de.monticore.conformance.basic.BasicCDConfStrategy;
import de.monticore.conformance.basic.BasicTypeConfStrategy;
import de.monticore.conformance.inc.STNamedAssocIncStrategy;
import de.monticore.conformance.inc.STTypeIncStrategy;
import java.util.Set;
import java.util.stream.Collectors;

// todo: needs to be fixed
public class ConformanceChecker {
  public static boolean checkBasicStereotypeConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD) {

    // get the names of all (named) reference elements
    Set<String> mappings =
        referenceCD.getCDDefinition().getCDClassesList().stream()
            .map(ASTCDClassTOP::getName)
            .collect(Collectors.toSet());
    mappings.addAll(
        referenceCD.getCDDefinition().getCDInterfacesList().stream()
            .map(ASTCDInterfaceTOP::getName)
            .collect(Collectors.toSet()));
    mappings.addAll(
        referenceCD.getCDDefinition().getCDEnumsList().stream()
            .map(ASTCDEnumTOP::getName)
            .collect(Collectors.toSet()));
    mappings.addAll(
        referenceCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(ASTCDAssociationTOP::isPresentName)
            .map(ASTCDAssociationTOP::getName)
            .collect(Collectors.toSet()));

    // create Incarnation Strategies

    STTypeIncStrategy typeInc = new STTypeIncStrategy(referenceCD, mappings);
    STNamedAssocIncStrategy assocInc = new STNamedAssocIncStrategy(referenceCD, mappings);

    // create Conformance Strategies
    BasicTypeConfStrategy typeChecker =
        new BasicTypeConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicAssocConfStrategy assocChecker =
        new BasicAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }
}
