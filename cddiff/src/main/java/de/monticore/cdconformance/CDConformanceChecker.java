/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.UnderspecifiedPlaceholderType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.conf.association.BasicAssocConfStrategy;
import de.monticore.cdconformance.conf.association.DeepAssocConfStrategy;
import de.monticore.cdconformance.conf.attribute.BasicAttributeConfStrategy;
import de.monticore.cdconformance.conf.cd.BasicCDConfStrategy;
import de.monticore.cdconformance.conf.method.BasicMethodConfStrategy;
import de.monticore.cdconformance.conf.type.BasicTypeConfStrategy;
import de.monticore.cdconformance.conf.type.DeepTypeConfStrategy;
import de.monticore.cdconformance.inc.CompIncStrategy;
import de.monticore.cdconformance.inc.ExternalMatchFromCache;
import de.monticore.cdconformance.inc.association.EqNameAssocIncStrategy;
import de.monticore.cdconformance.inc.association.RolePrefixIfPresentIncStrategy;
import de.monticore.cdconformance.inc.association.RolePrefixInNavDirIncStrategy;
import de.monticore.cdconformance.inc.association.STNamedAssocIncStrategy;
import de.monticore.cdconformance.inc.attribute.CompAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.EqNameAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.STAttributeIncStrategy;
import de.monticore.cdconformance.inc.method.CompMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqNameMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqSignatureMethodIncStrategy;
import de.monticore.cdconformance.inc.method.STMethodIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.BooleanMatchFromCache;
import de.monticore.cdmatcher.booleanMatching.MatchCDTypesToSubType;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.caching.StructureCache;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.monticore.cdconformance.CDConfParameter.ALLOW_CARD_RESTRICTION;
import static de.monticore.cdconformance.CDConfParameter.INHERITANCE;
import static de.monticore.cdconformance.CDConfParameter.METHOD_OVERLOADING;
import static de.monticore.cdconformance.CDConfParameter.NAME_MAPPING;
import static de.monticore.cdconformance.CDConfParameter.NO_MULTI_INC;
import static de.monticore.cdconformance.CDConfParameter.SRC_TARGET_ASSOC_MAPPING;
import static de.monticore.cdconformance.CDConfParameter.STEREOTYPE_MAPPING;
import static de.monticore.cdconformance.CDConfParameter.STRICT_PARAMETER_ORDER;

/**
 * Tool for automatic conformance checking of concrete CDs to reference CDs given a set of mappings.
 */
public class CDConformanceChecker {

  protected Set<CDConfParameter> params;
  protected String underspecifiedTypeName = UnderspecifiedPlaceholderType.DEFAULT_TYPE_NAME;
  protected ExternalCandidatesMatchingStrategy<ASTCDType> typeInc;
  protected MCTypeMatcher typeMatcher;
  protected ExternalCandidatesMatchingStrategy<ASTCDAssociation> assocInc;
  protected CompAttributeIncStrategy attrInc;

  protected CompMethodIncStrategy methInc;

  protected Map<ASTCDType, List<ASTCDType>> typeMap = new HashMap<>();
  protected Map<ASTCDAttribute, List<ASTCDAttribute>> attributeMap = new HashMap<>();

  protected Map<ASTCDAssociation, List<ASTCDAssociation>> assocMap = new HashMap<>();
  protected Map<ASTCDMethod, List<ASTCDMethod>> methodMap = new HashMap<>();

  public CDConformanceChecker(Set<CDConfParameter> params) {
    this.params = params;
  }

  public boolean checkConformance(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD,
      Set<String> mappings) {
    for (String mapping : mappings) {
      System.out.println("===== Check if " + concreteCD.getCDDefinition().getName()
          + " conforms to " + referenceCD.getCDDefinition().getName() + " with respect to "
          + mapping + " =====");
      if (!checkConformance(concreteCD, referenceCD, mapping)) {
        System.out.println("===== NOT CONFORM =====");
        return false;
      }
      else {
        System.out.println("===== CONFORM =====");
      }
    }
    return true;
  }

  public boolean checkConformance(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD,
      String mapping) {

    Set<ASTCDType> concTypes = CDDiffUtil.getAllTypesFromCD(concreteCD);
    Set<ASTCDType> refTypes = CDDiffUtil.getAllTypesFromCD(referenceCD);
    Set<ASTCDAssociation> concAssocs = CDDiffUtil.getAllAssocsFromCD(concreteCD);
    Set<ASTCDAssociation> refAssocs = CDDiffUtil.getAllAssocsFromCD(referenceCD);

    StructureCache structureCache = new StructureCache();
    CDSynDiffMatches.setupStructureCache(concreteCD, structureCache);
    CDSynDiffMatches.setupStructureCache(referenceCD, structureCache);

    // init incarnation checker
    CompIncStrategy<ASTCDType> compTypeInc = new CompIncStrategy<>(refTypes);
    typeMatcher = new MCTypeMatcher(underspecifiedTypeName, compTypeInc);

    CompIncStrategy<ASTCDAssociation> compAssocInc = new CompIncStrategy<>(refAssocs);
    attrInc = new CompAttributeIncStrategy();
    methInc = new CompMethodIncStrategy();

    if (params.contains(STEREOTYPE_MAPPING)) {
      compTypeInc.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
      compAssocInc.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      attrInc.addIncStrategy(new STAttributeIncStrategy(mapping));
      methInc.addIncStrategy(new STMethodIncStrategy(mapping));
    }

    if (params.contains(NAME_MAPPING)) {
      compTypeInc.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));
      compAssocInc.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      attrInc.addIncStrategy(new EqNameAttributeIncStrategy());
      if (params.contains(METHOD_OVERLOADING)) {
        methInc.addIncStrategy(new EqSignatureMethodIncStrategy(typeMatcher, params.contains(
            STRICT_PARAMETER_ORDER)));
      }
      else {
        methInc.addIncStrategy(new EqNameMethodIncStrategy());
      }
    }

    // we compute and cache all type matches to optimize performance
    CachedMatch<ASTCDType> typeCache = new CachedMatch<>();
    CDSynDiffMatches.applyMatchingStrategy(concTypes, refTypes, compTypeInc, typeCache);
    typeInc = new ExternalMatchFromCache<>(typeCache, 1.0);
    typeMatcher.setTypeMatcher(typeInc);

    if (params.contains(SRC_TARGET_ASSOC_MAPPING)) {

      if (params.contains(INHERITANCE)) {
        CompIncStrategy<ASTCDType> subTypeInc = new CompIncStrategy<>(refTypes);
        subTypeInc.addIncStrategy(typeInc);

        CachedMatch<ASTCDType> subTypeCache = new CachedMatch<>();
        CDSynDiffMatches.applyMatchingStrategy(concTypes, refTypes, new MatchCDTypesToSubType(new BooleanMatchFromCache<>(typeCache, 1.0), structureCache), subTypeCache);

        compAssocInc.addIncStrategy(new RolePrefixInNavDirIncStrategy(subTypeInc, structureCache));
        compAssocInc.addIncStrategy(new RolePrefixIfPresentIncStrategy(subTypeInc, structureCache));
      }
      else {
        compAssocInc.addIncStrategy(new RolePrefixInNavDirIncStrategy(typeInc, structureCache));
        compAssocInc.addIncStrategy(new RolePrefixIfPresentIncStrategy(typeInc, structureCache));
      }
    }

    // we compute and cache all association matches to optimize performance
    CachedMatch<ASTCDAssociation> assocCache = new CachedMatch<>();
    CDSynDiffMatches.applyMatchingStrategy(concAssocs, refAssocs, compAssocInc, assocCache);
    assocInc = new ExternalMatchFromCache<>(assocCache, 1.0);

    // init Conformance Checker
    ConformanceStrategy<ASTCDCompilationUnit> cdChecker = getBasicCDConfStrategy(concreteCD,
        referenceCD);

    // check conformance
    boolean multiInc = !params.contains(NO_MULTI_INC);
    assert typeInc != null;
    return cdChecker.checkConformance(concreteCD) && checkIncarnationMap(concreteCD, referenceCD,
        multiInc);
  }

  protected BasicCDConfStrategy getBasicCDConfStrategy(ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD) {
    BasicTypeConfStrategy typeChecker;
    BasicAssocConfStrategy assocChecker;
    boolean cardRestriction = params.contains(ALLOW_CARD_RESTRICTION);

    BasicAttributeConfStrategy attrChecker = new BasicAttributeConfStrategy(attrInc, typeMatcher);
    BasicMethodConfStrategy methodChecker = new BasicMethodConfStrategy(methInc, typeMatcher,
        params);

    if (params.contains(INHERITANCE)) {
      assocChecker = new DeepAssocConfStrategy(concreteCD, referenceCD, typeInc, assocInc,
          cardRestriction);
      typeChecker = new DeepTypeConfStrategy(concreteCD, referenceCD, attrChecker, methodChecker,
          attrInc, methInc, typeInc, assocInc);
    }
    else {
      assocChecker = (new BasicAssocConfStrategy(concreteCD, referenceCD, typeInc, assocInc,
          cardRestriction));
      typeChecker = new BasicTypeConfStrategy(concreteCD, referenceCD, attrChecker, methodChecker,
          attrInc, methInc, typeInc, assocInc);
    }

    return new BasicCDConfStrategy(referenceCD, typeInc, assocInc, typeChecker, assocChecker);
  }

  private boolean checkIncarnationMap(ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD,
      boolean multiInc) {
    boolean typeMapping = CDDiffUtil.getAllCDTypes(refCD).stream().allMatch(ref -> checkTypeMapping(
        ref, conCD, multiInc));
    boolean assocMapping = refCD.getCDDefinition().getCDAssociationsList().stream().allMatch(
        ref -> checkAssocMapping(ref, conCD, multiInc));

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
        Log.info("Type " + refAttribute.getName() + " has multiple incarnations ", this.getClass()
            .getName());
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
    return checkAttributeMapping(ref, multiInc) && checkMethodMapping(ref, multiInc);
  }

  protected boolean checkAssocMapping(ASTCDAssociation ref, ASTCDCompilationUnit conCD,
      boolean multiInc) {

    List<ASTCDAssociation> concretes = new ArrayList<>();
    for (ASTCDAssociation con : conCD.getCDDefinition().getCDAssociationsList()) {
      if (getRefElements(con).contains(ref)) {
        concretes.add(con);
      }
    }

    if (concretes.size() > 1 && !multiInc) {
      Log.info("Assoc " + CD4CodeMill.prettyPrint(ref, false) + " has multiple incarnations ", this
          .getClass().getName());
      return false;
    }

    assocMap.put(ref, concretes);
    return true;
  }

  protected boolean checkMethodMapping(ASTCDType refType, boolean multiInc) {

    for (ASTCDMethod refMethod : refType.getCDMethodList()) {
      List<ASTCDMethod> conMethods = new ArrayList<>();
      for (ASTCDType conType : getConElements(refType)) {
        for (ASTCDMethod conMethod : conType.getCDMethodList()) {
          if (getRefElements(conType, conMethod).contains(refMethod)) {
            conMethods.add(conMethod);
          }
        }
      }
      if (conMethods.size() > 1 && !multiInc) {
        Log.info("Type " + refMethod.getName() + " has multiple incarnations ", this.getClass()
            .getName());
        return false;
      }

      methodMap.put(refMethod, conMethods);
    }

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
    getRefElements(conType).forEach(refType -> {
      attrInc.setReferenceType(refType);
      refElements.addAll(attrInc.getMatchedElements(con));
    });
    return refElements;
  }

  public List<ASTCDMethod> getRefElements(ASTCDType conType, ASTCDMethod con) {
    List<ASTCDMethod> refElements = new ArrayList<>();
    getRefElements(conType).forEach(refType -> {
      methInc.setReferenceType(refType);
      refElements.addAll(methInc.getMatchedElements(con));
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

  public List<ASTCDMethod> getConElements(ASTCDMethod con) {
    return methodMap.containsKey(con) ? methodMap.get(con) : new ArrayList<>();
  }

}
