package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import net.sourceforge.plantuml.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReductionTrafo {

  private ASTCDCompilationUnit first;

  private ASTCDCompilationUnit second;

  private ICD4CodeArtifactScope scope1;

  private ICD4CodeArtifactScope scope2;

  private final static MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  /**
   * Pre-process 2 CDs for OW-CDDiff
   */
  public void transform(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2) {

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    first = ast1;
    second = ast2;

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

    scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    //todo: complete trafo
    completeSecond();
  }

  /**
   * helper method that completes the second CD
   * todo: add missing associations
   */
  protected void completeSecond() {
    for (ASTCDPackage astcdPackage : first.getCDDefinition().getCDPackagesList()) {
      if (!scope2.resolveCDTypeDown(astcdPackage.getSymbol().getFullName()).isPresent()) {
        ASTCDPackage newPackage = astcdPackage.deepClone();
        second.getCDDefinition().getCDElementList().add(newPackage);
      }
    }

    // add missing classes and attributes
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdClass);
      }
      else {
        addMissingAttribute(opt.get().getAstNode(), astcdClass.getCDAttributeList());
      }
    }

    // missing interfaces and attributes
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(
          astcdInterface.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdInterface);
      }
      else {
        addMissingAttribute(opt.get().getAstNode(), astcdInterface.getCDAttributeList());
      }
    }

    // add missing enums
    for (ASTCDEnum astcdEnum : first.getCDDefinition().getCDEnumsList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdEnum.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdEnum);
      }
      else {
        for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
          boolean found = false;
          for (FieldSymbol field : opt.get().getFieldList()) {
            if (field.getName().equals(constant.getName())) {
              found = true;
              break;
            }
          }
          if (!found && (opt.get().getAstNode() instanceof ASTCDEnum)) {
            ((ASTCDEnum) opt.get().getAstNode()).addCDEnumConstant(constant.deepClone());
          }
        }
      }
    }
    completeInheritance();
    removeRedundancies();

    // add missing associations
    for (ASTCDAssociation assoc1 : first.getCDDefinition().getCDAssociationsList()) {
      boolean found = false;
      for (ASTCDAssociation assoc2 : second.getCDDefinition().getCDAssociationsList()) {
        if (sameAssociation(assoc1, assoc2)) {
          if (!(assoc2.getCDAssocDir().isDefinitiveNavigableLeft() || assoc2.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            assoc2.setCDAssocDir(assoc1.getCDAssocDir().deepClone());
          }
          found = true;
          break;
        }
      }
      if (!found){
        second.getCDDefinition().getCDElementList().add(assoc1.deepClone());
      }
    }
  }

  /**
   * I hate the default package!
   */
  private void addClone(ASTCDType cdType) {
    if (cdType.getSymbol()
        .getPackageName()
        .equals(second.getCDDefinition().getDefaultPackageName())) {
      second.getCDDefinition().getCDElementList().add(cdType.deepClone());
    }
    else {
      second.getCDDefinition()
          .addCDElementToPackage(cdType.deepClone(), cdType.getSymbol().getPackageName());
    }
  }

  /**
   * check if assoc1 and assoc2 are the same association
   */
  protected boolean sameAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {

    // check left reference
    if (!assoc1.getLeftQualifiedName()
        .getQName()
        .equals(assoc2.getLeftQualifiedName().getQName())) {
      return false;
    }

    // check right reference
    if (!assoc1.getRightQualifiedName()
        .getQName()
        .equals(assoc2.getRightQualifiedName().getQName())) {
      return false;
    }

    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName();
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName();
    }

    if (!roleName1.equals(roleName2)) {
      return false;
    }

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName();
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName();
    }

    return roleName1.equals(roleName2);
  }

  /**
   * add all inheritance-relations exclusive to first to second
   */
  protected void completeInheritance() {
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        Log.error(
            String.format("0xCDD08: Could not find %s", astcdClass.getSymbol().getFullName()));
      }
      else {
        ASTCDType targetNode = opt.get().getAstNode();
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetNode.getSuperclassList());
        for (ASTMCObjectType superType : astcdClass.getSuperclassList()) {
          if (isNewSuper(superType, targetNode) && noCycle(superType, targetNode)) {
            extendsList.add(superType);
          }
          if (targetNode instanceof ASTCDClass) {
            ((ASTCDClass) targetNode).setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
          }
        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetNode.getInterfaceList());
        for (ASTMCObjectType superType : astcdClass.getInterfaceList()) {
          if (isNewSuper(superType, targetNode) && noCycle(superType, targetNode)) {
            interfaceList.add(superType);
          }
          if (targetNode instanceof ASTCDClass) {
            ((ASTCDClass) targetNode).setCDInterfaceUsage(
                CD4CodeMill.cDInterfaceUsageBuilder().addAllInterface(interfaceList).build());
          }
        }
      }
    }
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(
          astcdInterface.getSymbol().getFullName());
      if (!opt.isPresent()) {
        Log.error(
            String.format("0xCDD08: Could not find %s", astcdInterface.getSymbol().getFullName()));
      }
      else {
        ASTCDType targetNode = opt.get().getAstNode();
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetNode.getInterfaceList());
        for (ASTMCObjectType superType : astcdInterface.getInterfaceList()) {
          if (isNewSuper(superType, targetNode) && noCycle(superType, targetNode)) {
            extendsList.add(superType);
          }
          if (targetNode instanceof ASTCDInterface) {
            ((ASTCDInterface) targetNode).setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
          }
        }
      }
    }
  }

  /**
   * check if newSuper is not already a superclass/interface of targetNode
   */
  protected boolean isNewSuper(ASTMCObjectType newSuper, ASTCDType targetNode) {
    for (ASTCDType oldSuper : getAllSuper(targetNode)) {
      if (oldSuper.getSymbol().getFullName().contains(newSuper.printType(pp))) {
        return false;
      }
    }
    return true;
  }

  /**
   * check if newSuper does not cause cyclical inheritance
   */
  protected boolean noCycle(ASTMCObjectType newSuper, ASTCDType targetNode) {
    Optional<CDTypeSymbol> opt = targetNode.getEnclosingScope()
        .resolveCDTypeDown(newSuper.printType(pp));
    if (!opt.isPresent()) {
      opt = scope2.resolveCDTypeDown(newSuper.printType(pp));
    }
    if (opt.isPresent()) {
      for (ASTCDType superSuper : getAllSuper(opt.get().getAstNode())) {
        if (superSuper.getSymbol().getFullName().equals(targetNode.getSymbol().getFullName())) {
          return false;
        }
      }
    }
    else {
      Log.error(String.format("0xCDD08: Could not find %s", newSuper.printType(pp)));
    }
    return true;
  }

  /**
   * remove redundant attributes
   */
  protected void removeRedundancies() {
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
    for (ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuper(attribute, astcdClass)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : second.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (findInSuper(attribute, astcdInterface)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
  }

  /**
   * add missing attributes to cdType
   */
  protected void addMissingAttribute(ASTCDType cdType, List<ASTCDAttribute> cdAttributeList) {
    for (ASTCDAttribute attribute1 : cdAttributeList) {
      boolean found = false;
      for (ASTCDAttribute attribute2 : cdType.getCDAttributeList()) {
        if (attribute1.getName().equals(attribute2.getName())) {
          found = true;
          break;
        }
      }
      if (!found) {
        ASTCDAttribute newAttribute = attribute1.deepClone();
        cdType.addCDMember(newAttribute);
      }
    }
  }

  /**
   * check if attribute is in superclass/interface
   */
  protected boolean findInSuper(ASTCDAttribute attribute1, ASTCDType cdType) {
    for (ASTCDType supertype : getAllSuper(cdType)) {
      if (supertype != cdType) {
        for (ASTCDAttribute attribute2 : supertype.getCDAttributeList()) {
          if (attribute1.getName().equals(attribute2.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * return all superclasses and interfaces of cdType
   */
  protected List<ASTCDType> getAllSuper(ASTCDType cdType) {
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType));
    superList.addAll(getDirectInterfaces(cdType));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for (ASTCDType nextSuper : superList) {
      nextSuperSuperList.addAll(getAllSuper(nextSuper));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  /**
   * return all superclasses from SuperClassList
   */
  protected List<ASTCDType> getDirectSuperClasses(ASTCDType cdType) {
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      Optional<CDTypeSymbol> opt = cdType.getEnclosingScope()
          .resolveCDTypeDown(superType.printType(pp));
      if (!opt.isPresent()) {
        opt = scope2.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> extendsList.add(cdTypeSymbol.getAstNode()));
    }
    return extendsList;
  }

  /**
   * return all interfaces from InterfaceList
   */
  protected List<ASTCDType> getDirectInterfaces(ASTCDType cdType) {
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      Optional<CDTypeSymbol> opt = cdType.getEnclosingScope()
          .resolveCDTypeDown(superType.printType(pp));
      if (!opt.isPresent()) {
        opt = scope2.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> interfaceList.add(cdTypeSymbol.getAstNode()));
    }
    return interfaceList;
  }

}
