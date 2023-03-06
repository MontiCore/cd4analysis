package de.monticore.conformance;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.conformance.basic.BasicAssocConfStrategy;
import de.monticore.conformance.basic.BasicCDConfStrategy;
import de.monticore.conformance.basic.BasicTypeConfStrategy;
import de.monticore.conformance.inc.STNamedAssocIncStrategy;
import de.monticore.conformance.inc.STTypeIncStrategy;
import java.util.Set;

// todo: needs to be fixed
public class ConformanceChecker {
  public static boolean checkBasicStereotypeConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, Set<String> mappings) {

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
