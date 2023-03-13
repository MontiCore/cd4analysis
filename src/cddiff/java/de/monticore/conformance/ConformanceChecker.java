package de.monticore.conformance;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.conformance.basic.BasicAssocConfStrategy;
import de.monticore.conformance.basic.BasicCDConfStrategy;
import de.monticore.conformance.basic.BasicTypeConfStrategy;
import de.monticore.conformance.inc.STNamedAssocIncStrategy;
import de.monticore.conformance.inc.STTypeIncStrategy;
import de.se_rwth.commons.logging.Log;

import java.util.Set;

// todo: needs to be fixed
public class ConformanceChecker {
  public static boolean checkBasicStereotypeConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, Set<String> mappings) {
    for (String mapping : mappings){
      if (!checkBasicStereotypeConformance(concreteCD,referenceCD,mapping)){
        Log.println(concreteCD.getCDDefinition().getName() + " is not conform to " + referenceCD.getCDDefinition().getName() + " with respect to " + mapping);
        return false;
      }
    }
    return true;
  }
  public static boolean checkBasicStereotypeConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {

    // create Incarnation Strategies
    STTypeIncStrategy typeInc = new STTypeIncStrategy(referenceCD, mapping);
    STNamedAssocIncStrategy assocInc = new STNamedAssocIncStrategy(referenceCD, mapping);

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
