package de.monticore.conformance;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.conformance.conf.association.BasicAssocConfStrategy;
import de.monticore.conformance.conf.association.DeepAssocConfStrategy;
import de.monticore.conformance.conf.attribute.CompAttributeChecker;
import de.monticore.conformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.conformance.conf.cd.BasicCDConfStrategy;
import de.monticore.conformance.conf.type.BasicTypeConfStrategy;
import de.monticore.conformance.conf.type.DeepTypeConfStrategy;
import de.monticore.conformance.inc.association.CompAssocIncStrategy;
import de.monticore.conformance.inc.association.STNamedAssocIncStrategy;
import de.monticore.conformance.inc.type.CompTypeIncStrategy;
import de.monticore.conformance.inc.type.STTypeIncStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Set;

// todo: needs to be fixed
public class ConformanceChecker {
  public static boolean checkBasicStereotypeConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, Set<String> mappings) {
    for (String mapping : mappings) {
      if (!checkBasicStereotypeConformance(concreteCD, referenceCD, mapping)) {
        Log.println(
            concreteCD.getCDDefinition().getName()
                + " is not conform to "
                + referenceCD.getCDDefinition().getName()
                + " with respect to "
                + mapping);
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
    STNamedAttributeChecker attrChecker = new STNamedAttributeChecker(mapping);

    BasicTypeConfStrategy typeChecker =
        new BasicTypeConfStrategy(referenceCD, concreteCD, attrChecker, typeInc, assocInc);
    BasicAssocConfStrategy assocChecker =
        new BasicAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }

  public static boolean checkBasicComposedConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {

    // create Incarnation Strategies
    CompTypeIncStrategy typeInc = new CompTypeIncStrategy(referenceCD, mapping);
    CompAssocIncStrategy assocInc = new CompAssocIncStrategy(referenceCD, mapping);
    CompAttributeChecker attrChecker = new CompAttributeChecker(mapping);

    // create Conformance Strategies
    BasicTypeConfStrategy typeChecker =
        new BasicTypeConfStrategy(referenceCD, concreteCD, attrChecker, typeInc, assocInc);
    BasicAssocConfStrategy assocChecker =
        new BasicAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }

  public static boolean checkDeepComposedConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {

    // create Incarnation Strategies
    CompTypeIncStrategy typeInc = new CompTypeIncStrategy(referenceCD, mapping);
    CompAssocIncStrategy assocInc = new CompAssocIncStrategy(referenceCD, mapping);
    CompAttributeChecker attrChecker = new CompAttributeChecker(mapping);

    // create Conformance Strategies
    DeepTypeConfStrategy typeChecker =
        new DeepTypeConfStrategy(referenceCD, concreteCD, attrChecker, typeInc, assocInc);
    BasicAssocConfStrategy assocChecker =
        new DeepAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }
}
