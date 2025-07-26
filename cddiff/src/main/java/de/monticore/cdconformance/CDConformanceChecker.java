/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import static de.monticore.cdconformance.CDConfParameter.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdconcretization.UnderspecifiedPlaceholderType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.conf.association.BasicAssocConfStrategy;
import de.monticore.cdconformance.conf.association.DeepAssocConfStrategy;
import de.monticore.cdconformance.conf.attribute.BasicAttributeConfStrategy;
import de.monticore.cdconformance.conf.cd.BasicCDConfStrategy;
import de.monticore.cdconformance.conf.method.BasicMethodConfStrategy;
import de.monticore.cdconformance.conf.type.BasicTypeConfStrategy;
import de.monticore.cdconformance.conf.type.DeepTypeConfStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.ForEachBindingDerivingVisitor;
import de.monticore.cdconformance.inc.STBindingDerivingVisitor;
import de.monticore.cddiff.CDDiffUtil;
import de.se_rwth.commons.logging.Log;
import java.util.*;

/**
 * Tool for automatic conformance checking of concrete CDs to reference CDs given a set of mappings.
 */
public class CDConformanceChecker {
  
  private static final String LOG_NAME = CDConformanceChecker.class.getName();
  
  protected Set<CDConfParameter> params;
  protected String underspecifiedTypeName = UnderspecifiedPlaceholderType.DEFAULT_TYPE_NAME;
  protected CDIncarnationMapping incMapping;
  
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
    
    // TODO Context is not used in the same way as in concretization tool
    //  here it is rather used as a factory for the strategies and incarnation mapping
    CDConformanceContext context = DefaultCDConformanceContext.createCached(concreteCD, referenceCD,
        mapping, underspecifiedTypeName, params);
    incMapping = context.getIncarnationMapping();
    
    // 1. introduce incarnation bindings for each incarnation
    CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    STBindingDerivingVisitor stBindingDerivingVisitor = new STBindingDerivingVisitor(incMapping);
    stBindingDerivingVisitor.addToTraverser(traverser);
    ForEachBindingDerivingVisitor nameBindingDerivingVisitor = new ForEachBindingDerivingVisitor(
        incMapping);
    nameBindingDerivingVisitor.addToTraverser(traverser);
    concreteCD.accept(traverser);
    
    if (nameBindingDerivingVisitor.hasMissingBinding()) {
      Log.info("The concrete CD has missing incarnation bindings for reference elements"
          + " using 'forEach'", LOG_NAME);
      // TODO Should we continue conformance check anyway?
      return false;
    }
    if (nameBindingDerivingVisitor.hasFoundInvalidBinding() || stBindingDerivingVisitor
        .hasFoundInvalidBinding()) {
      Log.info("The concrete CD has invalid incarnation bindings", LOG_NAME);
      // TODO Should we continue conformance check anyway?
      return false;
    }
    
    // init Conformance Checker
    ConformanceStrategy<ASTCDCompilationUnit> cdChecker = getBasicCDConfStrategy(context,
        concreteCD, referenceCD);
    
    // check conformance
    boolean multiInc = !params.contains(NO_MULTI_INC);
    return cdChecker.checkConformance(concreteCD) && checkIncarnationMap(concreteCD, referenceCD,
        multiInc);
  }
  
  protected BasicCDConfStrategy getBasicCDConfStrategy(CDConformanceContext context,
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD) {
    BasicTypeConfStrategy typeChecker;
    BasicAssocConfStrategy assocChecker;
    boolean cardRestriction = params.contains(ALLOW_CARD_RESTRICTION);
    
    BasicAttributeConfStrategy attrChecker = new BasicAttributeConfStrategy(incMapping);
    BasicMethodConfStrategy methodChecker = new BasicMethodConfStrategy(context, params);
    
    if (params.contains(INHERITANCE)) {
      assocChecker = new DeepAssocConfStrategy(concreteCD, referenceCD, incMapping,
          cardRestriction);
      typeChecker = new DeepTypeConfStrategy(concreteCD, referenceCD, attrChecker, methodChecker,
          incMapping);
    }
    else {
      assocChecker = (new BasicAssocConfStrategy(concreteCD, referenceCD, incMapping,
          cardRestriction));
      typeChecker = new BasicTypeConfStrategy(concreteCD, referenceCD, attrChecker, methodChecker,
          incMapping);
    }
    
    return new BasicCDConfStrategy(referenceCD, incMapping, typeChecker, assocChecker);
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
      for (ASTCDType conType : incMapping.getIncarnations(refType)) {
        for (ASTCDAttribute conAttr : conType.getCDAttributeList()) {
          if (incMapping.getReferenceElements(conAttr).contains(refAttribute)) {
            conAttributes.add(conAttr);
          }
        }
      }
      if (conAttributes.size() > 1 && !multiInc) {
        Log.info("Type " + refAttribute.getName() + " has multiple incarnations ", this.getClass()
            .getName());
        return false;
      }
    }
    return true;
  }
  
  protected boolean checkTypeMapping(ASTCDType ref, ASTCDCompilationUnit conCD, boolean multiInc) {
    
    List<ASTCDType> concretes = new ArrayList<>();
    for (ASTCDType con : CDDiffUtil.getAllCDTypes(conCD)) {
      if (incMapping.getReferenceElements(con).contains(ref)) {
        concretes.add(con);
      }
    }
    
    if (concretes.size() > 1 && !multiInc) {
      Log.info("Type " + ref.getName() + " has multiple incarnations ", this.getClass().getName());
      return false;
    }
    return checkAttributeMapping(ref, multiInc) && checkMethodMapping(ref, multiInc);
  }
  
  protected boolean checkAssocMapping(ASTCDAssociation ref, ASTCDCompilationUnit conCD,
      boolean multiInc) {
    
    List<ASTCDAssociation> concretes = new ArrayList<>();
    for (ASTCDAssociation con : conCD.getCDDefinition().getCDAssociationsList()) {
      if (incMapping.getReferenceElements(con).contains(ref)) {
        concretes.add(con);
      }
    }
    
    if (concretes.size() > 1 && !multiInc) {
      Log.info("Assoc " + CD4CodeMill.prettyPrint(ref, false) + " has multiple incarnations ", this
          .getClass().getName());
      return false;
    }
    return true;
  }
  
  protected boolean checkMethodMapping(ASTCDType refType, boolean multiInc) {
    
    for (ASTCDMethod refMethod : refType.getCDMethodList()) {
      List<ASTCDMethod> conMethods = new ArrayList<>();
      for (ASTCDType conType : incMapping.getIncarnations(refType)) {
        for (ASTCDMethod conMethod : conType.getCDMethodList()) {
          if (incMapping.getReferenceElements(conMethod).contains(refMethod)) {
            conMethods.add(conMethod);
          }
        }
      }
      if (conMethods.size() > 1 && !multiInc) {
        Log.info("Type " + refMethod.getName() + " has multiple incarnations ", this.getClass()
            .getName());
        return false;
      }
    }
    
    return true;
  }
  
  public CDIncarnationMapping getIncarnationMapping() { return incMapping; }
  
  /**
   * Sets the name of the underspecified placeholder type.
   *
   * @param underspecifiedTypeName the name of the underspecified placeholder type
   */
  public void setUnderspecifiedTypeName(String underspecifiedTypeName) {
    this.underspecifiedTypeName = underspecifiedTypeName;
  }
  
}
