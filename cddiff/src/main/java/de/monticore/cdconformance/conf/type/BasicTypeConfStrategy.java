/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.type;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.conf.attribute.CDAttributeChecker;
import de.monticore.cdconformance.conf.method.CDMethodChecker;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class BasicTypeConfStrategy implements ConformanceStrategy<ASTCDType> {
  
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  
  protected final CDAttributeChecker attributeChecker;
  protected final CDMethodChecker methodChecker;
  protected final CDIncarnationMapping incMapping;
  
  public BasicTypeConfStrategy(ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD,
      CDAttributeChecker attributeChecker, CDMethodChecker methodChecker,
      CDIncarnationMapping incMapping) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.attributeChecker = attributeChecker;
    this.methodChecker = methodChecker;
    this.incMapping = incMapping;
  }
  
  @Override
  public boolean checkConformance(ASTCDType concrete) {
    Set<ASTCDType> nonConformingTo = incMapping.getReferenceElements(concrete).stream().filter(
        ref -> !checkConformance(concrete, ref)).collect(Collectors.toSet());
    return nonConformingTo.isEmpty();
  }
  
  public boolean checkConformance(ASTCDType concrete, ASTCDType ref) {
    
    // an enum must be incarnated as an enum
    if (ref instanceof ASTCDEnum) {
      if (concrete instanceof ASTCDEnum) {
        return checkConformance((ASTCDEnum) concrete, (ASTCDEnum) ref);
      }
      Log.println(concrete.getSymbol().getInternalQualifiedName() + " (l." + concrete
          .get_SourcePositionStart() + ") must be an enumeration-type!");
      return false;
    }
    else {
      // if ref is not an enum, then concrete should not be an enum, either
      if (concrete instanceof ASTCDEnum) {
        Log.println(concrete.getSymbol().getInternalQualifiedName() + " (l." + concrete
            .get_SourcePositionStart() + ") must not be an enumeration-type!");
        return false;
      }
    }
    
    // a class must be incarnated as a class
    if (ref instanceof ASTCDClass) {
      if (!(concrete instanceof ASTCDClass)) {
        Log.println(concrete.getSymbol().getInternalQualifiedName() + " (l." + concrete
            .get_SourcePositionStart() + ") must be a class and not an interface!");
        return false;
      }
      // abstract classes must be incarnated as abstract classes
      if (ref.getModifier().isAbstract() && !concrete.getModifier().isAbstract()) {
        Log.println(concrete.getSymbol().getInternalQualifiedName() + " (l." + concrete
            .get_SourcePositionStart() + ") must be abstract!");
        return false;
      }
    }
    
    // check if all necessary attributes are present
    boolean attributes = checkAttributeIncarnation(concrete, ref) && checkAttributeConformance(
        concrete, ref);
    
    // check if all necessary methods are present
    boolean methods = checkMethodIncarnation(concrete, ref) && checkMethodConformance(concrete,
        ref);
    
    // check if reference associations are incarnated
    boolean associations = checkAssocIncarnation(concrete, ref);
    
    // check if all reference super-types are incarnated
    boolean superTypes = CDDiffUtil.getAllSuperTypes(ref, refCD.getCDDefinition()).stream()
        .allMatch(refSuper -> CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition())
            .stream().anyMatch(conSuper -> incMapping.getReferenceElements(conSuper).contains(
                refSuper)));
    if (attributes && methods && associations && superTypes) {
      return true;
    }
    System.out.println(concrete.getSymbol().getInternalQualifiedName()
        + " is not a valid incarnation of " + ref.getSymbol().getInternalQualifiedName());
    if (!attributes) {
      System.out.println("Incarnations of attributes are missing or incorrect!");
    }
    if (!methods) {
      System.out.println("Incarnations of methods are missing or incorrect!");
    }
    if (!associations) {
      System.out.println("Incarnations of associations are missing or incorrect!");
    }
    if (!superTypes) {
      System.out.println("Incarnations of super-types are missing or incorrect!");
    }
    return false;
  }
  
  /** We check that all reference enum constants are preserved in relative order. */
  public boolean checkConformance(ASTCDEnum concrete, ASTCDEnum ref) {
    
    List<ASTCDEnumConstant> cConstants = new ArrayList<>(concrete.getCDEnumConstantList());
    
    for (ASTCDEnumConstant rConstant : ref.getCDEnumConstantList()) {
      Optional<ASTCDEnumConstant> cConstant = cConstants.stream().filter(r -> r.getName().equals(
          rConstant.getName())).findFirst();
      if (cConstant.isEmpty()) {
        System.out.println(concrete.getSymbol().getInternalQualifiedName()
            + " is not a valid incarnation of " + ref.getSymbol().getInternalQualifiedName());
        System.out.println("Enumeration constant: " + rConstant.getName()
            + "is missing or at the wrong position!");
        return false;
      }
      cConstants = cConstants.subList(cConstants.indexOf(cConstant.get()), cConstants.size());
    }
    
    return true;
  }
  
  /** check if all attributes of the reference type are incarnated */
  protected boolean checkAttributeIncarnation(ASTCDType con, ASTCDType ref) {
    return checkAttributeIncarnation((new HashSet<>(con.getCDAttributeList())), new HashSet<>(ref
        .getCDAttributeList()));
  }
  
  /** check if all methods of the reference type are incarnated */
  protected boolean checkMethodIncarnation(ASTCDType con, ASTCDType ref) {
    return checkMethodIncarnation((new HashSet<>(con.getCDMethodList())), new HashSet<>(ref
        .getCDMethodList()));
  }
  
  /** check if all attributes that are incarnations are conformed to the references */
  protected boolean checkAttributeConformance(ASTCDType con, ASTCDType refType) {
    return checkAttributeConformance(new HashSet<>(con.getCDAttributeList()), refType);
  }
  
  /** check if all methods that are incarnations are conformed to the references */
  protected boolean checkMethodConformance(ASTCDType con, ASTCDType refType) {
    return checkMethodConformance(new HashSet<>(con.getCDMethodList()), refType);
  }
  
  /** check if all associations of the reference type are incarnated */
  protected boolean checkAssocIncarnation(ASTCDType conType, ASTCDType refType) {
    return checkAssocIncarnation(CDDiffUtil.getReferencingAssociations(conType, conCD), CDDiffUtil
        .getReferencingAssociations(refType, refCD));
  }
  
  /**
   * Checks if all reference associations are incarnated by one of the given concrete associations
   * at least once.<br>
   * If the reference association is annotated with the stereotype <code>optional</code>, it is not
   * required to be incarnated.
   *
   * @param con the concrete associations
   * @param ref the reference associations
   * @return true if all required reference associations are incarnated, false otherwise
   */
  protected boolean checkAssocIncarnation(Set<ASTCDAssociation> con, Set<ASTCDAssociation> ref) {
    return ref.stream().allMatch(refAssoc -> checkAssocIncarnation(refAssoc, con));
  }
  
  /**
   * Checks if the given reference association is incarnated by at least one of the concrete
   * associations.
   *
   * @param ref the reference association to check
   * @param concrete the concrete associations to check against
   * @return true if the reference association is incarnated, false otherwise
   */
  protected boolean checkAssocIncarnation(ASTCDAssociation ref, Set<ASTCDAssociation> concrete) {
    if (StereotypeUtil.isOptional(ref.getModifier())) {
      return true; // optional methods do not need to be incarnated
    }
    if (concrete.stream().anyMatch(conMethod -> incMapping.isIncarnation(conMethod, ref))) {
      return true; // at least one incarnation is present
    }
    Log.info("Missing incarnation for association in '" + ref.getEnclosingScope().getName() + "' ("
        + ref.get_SourcePositionStart() + ")", getClass().getName());
    return false;
  }
  
  /**
   * Checks if all the reference attributes are incarnated by at least one of the concrete
   * attributes (unless they are optional).
   *
   * @param con the concrete attributes to check
   * @param ref the reference attributes to check against
   * @return true if all reference attributes are incarnated, false otherwise
   */
  protected boolean checkAttributeIncarnation(Set<ASTCDAttribute> con, Set<ASTCDAttribute> ref) {
    return ref.stream().allMatch(refAttr -> checkAttributeIncarnation(refAttr, con));
  }
  
  /**
   * Checks if the given reference attribute is incarnated by at least one of the concrete
   * attributes.
   *
   * @param ref the reference attribute to check
   * @param concrete the concrete attributes to check against
   * @return true if the reference attribute is incarnated, false otherwise
   */
  protected boolean checkAttributeIncarnation(ASTCDAttribute ref, Set<ASTCDAttribute> concrete) {
    if (StereotypeUtil.isOptional(ref.getModifier())) {
      return true; // optional methods do not need to be incarnated
    }
    if (concrete.stream().anyMatch(conMethod -> incMapping.isIncarnation(conMethod, ref))) {
      return true; // at least one incarnation is present
    }
    Log.info("Missing incarnation for attribute '" + ref.getName() + "' in '" + ref
        .getEnclosingScope().getName() + "' (" + ref.get_SourcePositionStart() + ")", getClass()
            .getName());
    return false;
  }
  
  /**
   * Checks if all the reference methods are incarnated by at least one of the concrete methods
   * (unless they are optional).
   *
   * @param con the concrete methods to check
   * @param ref the reference methods to check against
   * @return true if all reference methods are incarnated, false otherwise
   */
  protected boolean checkMethodIncarnation(Set<ASTCDMethod> con, Set<ASTCDMethod> ref) {
    return ref.stream().allMatch(refMethod -> checkMethodIncarnation(con, refMethod));
  }
  
  /**
   * Checks if the given reference method is incarnated by at least one of the concrete methods.
   *
   * @param concrete the concrete methods to check against
   * @param ref the reference method to check
   * @return true if the reference method is incarnated, false otherwise
   */
  protected boolean checkMethodIncarnation(Set<ASTCDMethod> concrete, ASTCDMethod ref) {
    if (StereotypeUtil.isOptional(ref.getModifier())) {
      return true; // optional methods do not need to be incarnated
    }
    if (concrete.stream().anyMatch(conMethod -> incMapping.isIncarnation(conMethod, ref))) {
      return true; // at least one incarnation is present
    }
    Log.info("Missing incarnation for method '" + ref.getName() + "' in '" + ref.getEnclosingScope()
        .getName() + "' (" + ref.get_SourcePositionStart() + ")", getClass().getName());
    return false;
  }
  
  protected boolean checkAttributeConformance(Set<ASTCDAttribute> concrete, ASTCDType refType) {
    return concrete.stream().allMatch(conAttr -> incMapping.getReferenceElements(conAttr, refType)
        .isEmpty() || attributeChecker.checkConformance(conAttr));
  }
  
  protected boolean checkMethodConformance(Set<ASTCDMethod> concrete, ASTCDType refType) {
    return concrete.stream().allMatch(conMethod -> incMapping.getReferenceElements(conMethod,
        refType).isEmpty() || methodChecker.checkConformance(conMethod));
  }
  
}
