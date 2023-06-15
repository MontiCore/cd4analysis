package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.*;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.AttributeChecker;
import de.monticore.conformance.conf.association.BasicAssocConfStrategy;
import de.monticore.conformance.conf.association.DeepAssocConfStrategy;
import de.monticore.conformance.conf.association.StrictDeepAssocConfStrategy;
import de.monticore.conformance.conf.attribute.CompAttributeChecker;
import de.monticore.conformance.conf.attribute.EqNameAttributeChecker;
import de.monticore.conformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.conformance.conf.cd.BasicCDConfStrategy;
import de.monticore.conformance.conf.type.BasicTypeConfStrategy;
import de.monticore.conformance.conf.type.DeepTypeConfStrategy;
import de.monticore.conformance.inc.association.CompAssocIncStrategy;
import de.monticore.conformance.inc.association.EqNameAssocIncStrategy;
import de.monticore.conformance.inc.association.STNamedAssocIncStrategy;
import de.monticore.conformance.inc.type.CompTypeIncStrategy;
import de.monticore.conformance.inc.type.EqTypeIncStrategy;
import de.monticore.conformance.inc.type.STTypeIncStrategy;
import de.monticore.matcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Set;

// todo: needs to be fixed
public class ConformanceChecker {
  protected Set<ConfParameter> params;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;
  protected AttributeChecker attrInc;

  public ConformanceChecker(Set<ConfParameter> params) {
    this.params = params;
  }

  public boolean checkConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, Set<String> mappings) {
    for (String mapping : mappings) {
      if (!checkConformance(concreteCD, referenceCD, mapping)) {
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

  public boolean checkConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {
    // init incarnation checker

    if (params.contains(STEREOTYPE_MAPPING) && !params.contains(NAME_MAPPING)) {
      typeInc = new STTypeIncStrategy(referenceCD, mapping);
      assocInc = new STNamedAssocIncStrategy(referenceCD, mapping);
      attrInc = new STNamedAttributeChecker(mapping);

    } else if (!params.contains(STEREOTYPE_MAPPING) && params.contains(NAME_MAPPING)) {
      typeInc = new EqTypeIncStrategy(referenceCD, mapping);
      assocInc = new EqNameAssocIncStrategy(referenceCD, mapping);
      attrInc = new EqNameAttributeChecker(mapping);
    } else {
      typeInc = new CompTypeIncStrategy(referenceCD, mapping);
      assocInc = new CompAssocIncStrategy(referenceCD, mapping);
      attrInc = new CompAttributeChecker(mapping);
    }

    // init conformance Checker
    BasicTypeConfStrategy typeChecker;
    BasicAssocConfStrategy assocChecker;
    if (!params.contains(INHERITANCE) && !params.contains(STRICT_INHERITANCE)) {

      typeChecker = new BasicTypeConfStrategy(referenceCD, concreteCD, attrInc, typeInc, assocInc);
      assocChecker = new BasicAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);

    } else {
      typeChecker = new DeepTypeConfStrategy(referenceCD, concreteCD, attrInc, typeInc, assocInc);
      if (params.contains(STRICT_INHERITANCE)) {
        assocChecker = new StrictDeepAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
      } else {
        assocChecker = new DeepAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
      }
    }

    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }

  public List<ASTCDType> getRefElements(ASTCDType con) {
    return typeInc.getMatchedElements(con);
  }

  public List<ASTCDAssociation> getRefElements(ASTCDAssociation con) {
    return assocInc.getMatchedElements(con);
  }

  public List<ASTCDAttribute> getRefElements(ASTCDAttribute type) {
    return attrInc.getMatchedElements(type);
  }

  public static boolean checkStrictDeepComposedConformance(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {

    // create Incarnation Strategies
    CompTypeIncStrategy typeInc = new CompTypeIncStrategy(referenceCD, mapping);
    CompAssocIncStrategy assocInc = new CompAssocIncStrategy(referenceCD, mapping);
    CompAttributeChecker attrChecker = new CompAttributeChecker(mapping);

    // create Conformance Strategies
    DeepTypeConfStrategy typeChecker =
        new DeepTypeConfStrategy(referenceCD, concreteCD, attrChecker, typeInc, assocInc);
    BasicAssocConfStrategy assocChecker =
        new StrictDeepAssocConfStrategy(referenceCD, concreteCD, typeInc, assocInc);
    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    return cdChecker.checkConformance(concreteCD);
  }
}
