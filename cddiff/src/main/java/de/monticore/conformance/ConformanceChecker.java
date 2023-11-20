package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
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
import de.monticore.matcher.SrcTgtAssocMatcher;
import de.se_rwth.commons.logging.Log;
import java.util.*;

/**
 * Tool for automatic conformance checking of concrete CDs to reference CDs given a set of mappings.
 */
public class ConformanceChecker {
  protected Set<ConfParameter> params;
  protected CompTypeIncStrategy typeInc;
  protected CompAssocIncStrategy assocInc;
  protected CompAttributeChecker attrInc;

  protected Map<ASTCDType, List<ASTCDType>> typeMap = new HashMap<>();
  protected Map<ASTCDAttribute, List<ASTCDAttribute>> attributeMap = new HashMap<>();

  protected Map<ASTCDAssociation, List<ASTCDAssociation>> assocMap = new HashMap<>();

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
    typeInc = new CompTypeIncStrategy(referenceCD, mapping);
    assocInc = new CompAssocIncStrategy(referenceCD, mapping);
    attrInc = new CompAttributeChecker(mapping);

    if (params.contains(STEREOTYPE_MAPPING)) {
      typeInc.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
      assocInc.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      attrInc.addIncStrategy(new STNamedAttributeChecker(mapping));
    }

    if (params.contains(NAME_MAPPING)) {
      typeInc.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));
      assocInc.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      attrInc.addIncStrategy(new EqNameAttributeChecker(mapping));
    }

    if (params.contains(SRC_TARGET_ASSOC_MAPPING)) {
      assocInc.addIncStrategy(new SrcTgtAssocMatcher(typeInc, concreteCD, referenceCD));
    }

    // init conformance Checker
    BasicTypeConfStrategy typeChecker;
    BasicAssocConfStrategy assocChecker;
    boolean cardRestriction = params.contains(ALLOW_CARD_RESTRICTION);

    if (params.contains(STRICT_INHERITANCE)) {
      assocChecker =
          new StrictDeepAssocConfStrategy(
              concreteCD, referenceCD, typeInc, assocInc, cardRestriction);
      typeChecker = new DeepTypeConfStrategy(concreteCD, referenceCD, attrInc, typeInc, assocInc);

    } else if (params.contains(INHERITANCE)) {
      assocChecker =
          new DeepAssocConfStrategy(concreteCD, referenceCD, typeInc, assocInc, cardRestriction);
      typeChecker = new DeepTypeConfStrategy(concreteCD, referenceCD, attrInc, typeInc, assocInc);
    } else {
      assocChecker =
          (new BasicAssocConfStrategy(concreteCD, referenceCD, typeInc, assocInc, cardRestriction));
      typeChecker = new BasicTypeConfStrategy(concreteCD, referenceCD, attrInc, typeInc, assocInc);
    }

    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    boolean muliInc = !params.contains(NO_MULTI_INC);
    return cdChecker.checkConformance(concreteCD)
        && checkIncarnationMap(concreteCD, referenceCD, muliInc);
  }

  private boolean checkIncarnationMap(
      ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD, boolean multiInc) {
    boolean typeMapping =
        CDDiffUtil.getAllCDTypes(refCD).stream()
            .allMatch(ref -> checkTypeMapping(ref, conCD, multiInc));
    boolean assocMapping =
        refCD.getCDDefinition().getCDAssociationsList().stream()
            .allMatch(ref -> checkAssocMapping(ref, conCD, multiInc));

    return typeMapping && assocMapping;
  }

  private boolean checkAttributeMapping(ASTCDType refType, boolean multiInc) {

    for (ASTCDAttribute refAttribute : refType.getCDAttributeList()) {
      List<ASTCDAttribute> conAttributes = new ArrayList<>();
      for (ASTCDType conType : getConElements(refType)) {
        for (ASTCDAttribute conAttr : conType.getCDAttributeList()) {
          if (getRefElements(conType, conAttr).contains(refAttribute)) {
            conAttributes.add(conAttr);
          }
        }
      }
      if (conAttributes.size() > 1 && !multiInc) {
        Log.info(
            "Type " + refAttribute.getName() + " has multiple incarnations ",
            this.getClass().getName());
        return false;
      }

      attributeMap.put(refAttribute, conAttributes);
    }

    return true;
  }

  protected boolean checkTypeMapping(ASTCDType ref, ASTCDCompilationUnit conCD, boolean multiInc) {

    List<ASTCDType> concretes = new ArrayList<>();
    for (ASTCDType con : CDDiffUtil.getAllCDTypes(conCD)) {
      if (getRefElements(con).contains(ref)) {
        concretes.add(con);
      }
    }

    if (concretes.size() > 1 && !multiInc) {
      Log.info("Type " + ref.getName() + " has multiple incarnations ", this.getClass().getName());
      return false;
    }

    typeMap.put(ref, concretes);
    return checkAttributeMapping(ref, multiInc);
  }

  protected boolean checkAssocMapping(
      ASTCDAssociation ref, ASTCDCompilationUnit conCD, boolean multiInc) {

    List<ASTCDAssociation> concretes = new ArrayList<>();
    for (ASTCDAssociation con : conCD.getCDDefinition().getCDAssociationsList()) {
      if (getRefElements(con).contains(ref)) {
        concretes.add(con);
      }
    }

    if (concretes.size() > 1 && !multiInc) {
      Log.info(
          "Assoc " + CD4CodeMill.prettyPrint(ref, false) + " has multiple incarnations ",
          this.getClass().getName());
      return false;
    }

    assocMap.put(ref, concretes);
    return true;
  }

  public List<ASTCDType> getRefElements(ASTCDType con) {
    return typeInc.getMatchedElements(con);
  }

  public List<ASTCDAssociation> getRefElements(ASTCDAssociation con) {
    return assocInc.getMatchedElements(con);
  }

  public List<ASTCDAttribute> getRefElements(ASTCDType conType, ASTCDAttribute con) {
    List<ASTCDAttribute> refElements = new ArrayList<>();
    getRefElements(conType)
        .forEach(
            refType -> {
              attrInc.setConcreteType(conType);
              attrInc.setReferenceType(refType);
              refElements.addAll(attrInc.getMatchedElements(con));
            });
    return refElements;
  }

  public List<ASTCDType> getConElements(ASTCDType con) {
    return typeMap.containsKey(con) ? typeMap.get(con) : new ArrayList<>();
  }

  public List<ASTCDAssociation> getConElements(ASTCDAssociation con) {
    return assocMap.containsKey(con) ? assocMap.get(con) : new ArrayList<>();
  }

  public List<ASTCDAttribute> getConElements(ASTCDAttribute con) {
    return attributeMap.containsKey(con) ? attributeMap.get(con) : new ArrayList<>();
  }
}
