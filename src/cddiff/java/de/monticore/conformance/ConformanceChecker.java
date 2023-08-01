package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
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
import java.util.*;

/**
 * Tool for automatic conformance checking of concrete CDs to reference CDs given a set of mappings.
 */
public class ConformanceChecker {
  protected Set<ConfParameter> params;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;
  protected AttributeChecker attrInc;

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
    boolean assocCardRefinement = params.contains(ALLOW_CARD_RESTRICTION);
    if (!params.contains(INHERITANCE) && !params.contains(STRICT_INHERITANCE)) {

      typeChecker = new BasicTypeConfStrategy(referenceCD, concreteCD, attrInc, typeInc, assocInc);

      assocChecker =
          new BasicAssocConfStrategy(
              referenceCD, concreteCD, typeInc, assocInc, assocCardRefinement);
    } else {
      typeChecker = new DeepTypeConfStrategy(referenceCD, concreteCD, attrInc, typeInc, assocInc);
      if (params.contains(STRICT_INHERITANCE)) {
        assocChecker =
            new StrictDeepAssocConfStrategy(
                referenceCD, concreteCD, typeInc, assocInc, assocCardRefinement);
      } else {
        assocChecker =
            new DeepAssocConfStrategy(
                referenceCD, concreteCD, typeInc, assocInc, assocCardRefinement);
      }
    }

    BasicCDConfStrategy cdChecker =
        new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);

    // check conformance
    boolean muliInc = !params.contains(NO_MULTI_INC);
    return cdChecker.checkConformance(concreteCD)
        && checkIncarnationMap(referenceCD, concreteCD, muliInc);
  }

  private boolean checkIncarnationMap(
      ASTCDCompilationUnit refCD, ASTCDCompilationUnit conCD, boolean multiInc) {
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
