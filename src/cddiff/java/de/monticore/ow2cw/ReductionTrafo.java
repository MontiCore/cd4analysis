package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
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
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReductionTrafo {

  protected MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff
   */
  public void transform(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

    transformFirst(first, second);
    transformSecond(first, second);
  }

  /**
   * transform the first CD
   */
  protected void transformFirst(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    // add subclass to each interface and abstract class
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getModifier().isAbstract()) {

        ASTModifier newModifier =
            CD4CodeMill.modifierBuilder().build();

        ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
            .setName(astcdClass.getName() + "4Diff")
            .setCDExtendUsage(CDExtendUsageFacade.getInstance()
                .createCDExtendUsage(astcdClass.getSymbol().getFullName()))
            .setCDInterfaceUsageAbsent()
            .setModifier(newModifier)
            .build();
        addClass2PackageInCD(newClass, determinePackageName(astcdClass), first);
      }
    }
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {

      ASTModifier newModifier =
          CD4CodeMill.modifierBuilder().build();

      ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
          .setName(astcdInterface.getName() + "4Diff")
          .setCDInterfaceUsage(CDInterfaceUsageFacade.getInstance()
              .createCDInterfaceUsage(astcdInterface.getSymbol().getFullName()))
          .setCDExtendUsageAbsent()
          .setModifier(newModifier)
          .build();
      addClass2PackageInCD(newClass, determinePackageName(astcdInterface), first);
    }

    // add missing classes
    for (ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope1.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        // add empty class without extends

        ASTModifier newModifier =
            CD4CodeMill.modifierBuilder().build();

        ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
            .setName(astcdClass.getName())
            .setCDExtendUsageAbsent()
            .setCDInterfaceUsageAbsent()
            .setModifier(newModifier)
            .build();
        addClass2PackageInCD(newClass, determinePackageName(astcdClass), first);
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    //add missing associations
    for (ASTCDAssociation assoc1 : second.getCDDefinition().getCDAssociationsList()) {
      boolean found = false;
      for (ASTCDAssociation assoc2 : first.getCDDefinition().getCDAssociationsList()) {
        if (sameAssociation(assoc1, assoc2)) {
          found = true;
          break;
        }
      }
      if (!found) {
        ASTCDAssociation newAssoc = assoc1.deepClone();
        newAssoc.getRight().setCDCardinalityAbsent();
        newAssoc.getLeft().setCDCardinalityAbsent();
        first.getCDDefinition().getCDElementList().add(newAssoc);
      }
    }
  }

  /**
   * transform the second CD
   */
  protected void transformSecond(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    ICD4CodeArtifactScope scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    // add missing classes and attributes in classes to second
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone2CD(astcdClass, second);
      }
      else {
        addMissingAttribute(opt.get().getAstNode(), astcdClass.getCDAttributeList());
      }
    }

    // add missing interfaces and attributes in interfaces to second
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(
          astcdInterface.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone2CD(astcdInterface, second);
      }
      else {
        addMissingAttribute(opt.get().getAstNode(), astcdInterface.getCDAttributeList());
      }
    }

    // add missing enums to second
    for (ASTCDEnum astcdEnum : first.getCDDefinition().getCDEnumsList()) {
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdEnum.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone2CD(astcdEnum, second);
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
          if (!found) {
            // I wanted to avoid reflection, but I think this is just reflection with extra steps...
            for (ASTCDEnum someEnum : second.getCDDefinition().getCDEnumsList()){
              if (astcdEnum.getSymbol().getFullName().equals(someEnum.getSymbol().getFullName())){
                someEnum.addCDEnumConstant(constant.deepClone());
              }
            }
          }
        }
      }
    }

    completeInheritanceInSecond(first,second);
    removeRedundantAttributes(second);

    // add missing associations to second
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
      if (!found) {
        second.getCDDefinition().getCDElementList().add(assoc1.deepClone());
      }
    }
  }

  protected void addClass2PackageInCD(ASTCDClass astcdClass, String packageName,
      ASTCDCompilationUnit ast) {
    if (packageName.equals(ast.getCDDefinition().getDefaultPackageName())) {
      ast.getCDDefinition().getCDElementList().add(astcdClass);
    }
    else {
      ast.getCDDefinition()
          .addCDElementToPackage(astcdClass, packageName);
    }
  }

  /**
   * I hate the default package!
   */
  private void addClone2CD(ASTCDType cdType, ASTCDCompilationUnit cd) {
    if (determinePackageName(cdType)
        .equals(cd.getCDDefinition().getDefaultPackageName())) {
      cd.getCDDefinition().getCDElementList().add(cdType.deepClone());
    }
    else {
      cd.getCDDefinition()
          .addCDElementToPackage(cdType.deepClone(), determinePackageName(cdType));
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
  protected void completeInheritanceInSecond(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    ICD4CodeArtifactScope scope2;

    for (ASTCDClass srcClass : first.getCDDefinition().getCDClassesList()) {

      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

      ASTCDClass targetClass = null;

      for (ASTCDClass someClass : second.getCDDefinition().getCDClassesList()){
        if (srcClass.getSymbol().getFullName().equals(someClass.getSymbol().getFullName())){
          targetClass = someClass;
        }
      }

      if (targetClass == null) {
        Log.error(
            String.format("0xCDD08: Could not find class %s", srcClass.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetClass.getSuperclassList());
        for (ASTMCObjectType superType : srcClass.getSuperclassList()) {
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            extendsList.add(superType);
          }
          targetClass.setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());

        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetClass.getInterfaceList());
        for (ASTMCObjectType superType : srcClass.getInterfaceList()) {
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            interfaceList.add(superType);
          }
          targetClass.setCDInterfaceUsage(
                CD4CodeMill.cDInterfaceUsageBuilder().addAllInterface(interfaceList).build());
        }
      }
    }
    for (ASTCDInterface srcInterface : first.getCDDefinition().getCDInterfacesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

      ASTCDInterface targetInterface = null;

      for (ASTCDInterface someInterface : second.getCDDefinition().getCDInterfacesList()){
        if (srcInterface.getSymbol().getFullName().equals(someInterface.getSymbol().getFullName())){
          targetInterface = someInterface;
        }
      }

      if (targetInterface == null) {
        Log.error(
            String.format("0xCDD09: Could not find interface %s",
                srcInterface.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetInterface.getInterfaceList());
        for (ASTMCObjectType superType : srcInterface.getInterfaceList()) {
          if (isNewSuper(superType, targetInterface,scope2) && inducesNoInheritanceCycle(superType,
              targetInterface,scope2)) {
            extendsList.add(superType);
          }
          targetInterface.setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
  }

  /**
   * check if newSuper is not already a superclass/interface of targetNode
   */
  protected boolean isNewSuper(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType oldSuper : getAllSuper(targetNode, artifactScope)) {
      if (oldSuper.getSymbol().getFullName().contains(newSuper.printType(pp))) {
        return false;
      }
    }
    return true;
  }

  /**
   * check if newSuper does not cause cyclical inheritance
   */
  protected boolean inducesNoInheritanceCycle(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {
    Optional<CDTypeSymbol> opt = targetNode.getEnclosingScope()
        .resolveCDTypeDown(newSuper.printType(pp));
    if (!opt.isPresent()) {
      opt = artifactScope.resolveCDTypeDown(newSuper.printType(pp));
    }
    if (opt.isPresent()) {
      for (ASTCDType superSuper : getAllSuper(opt.get().getAstNode(), artifactScope)) {
        if (superSuper.getSymbol().getFullName().equals(targetNode.getSymbol().getFullName())) {
          return false;
        }
      }
    }
    else {
      Log.error(String.format("0xCDD10: Could not find superclass/interface %s",
          newSuper.printType(pp)));
    }
    return true;
  }

  /**
   * remove redundant attributes
   */
  protected void removeRedundantAttributes(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuper(attribute, astcdClass, artifactScope)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (findInSuper(attribute, astcdInterface, artifactScope)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
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
  protected boolean findInSuper(ASTCDAttribute attribute1, ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
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
  protected List<ASTCDType> getAllSuper(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType,artifactScope));
    superList.addAll(getDirectInterfaces(cdType,artifactScope));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for (ASTCDType nextSuper : superList) {
      nextSuperSuperList.addAll(getAllSuper(nextSuper, artifactScope));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  /**
   * return all superclasses from SuperClassList
   */
  protected List<ASTCDType> getDirectSuperClasses(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      Optional<CDTypeSymbol> opt = cdType.getEnclosingScope()
          .resolveCDTypeDown(superType.printType(pp));
      if (!opt.isPresent()) {
        opt = artifactScope.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> extendsList.add(cdTypeSymbol.getAstNode()));
    }
    return extendsList;
  }

  /**
   * return all interfaces from InterfaceList
   */
  protected List<ASTCDType> getDirectInterfaces(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      Optional<CDTypeSymbol> opt = cdType.getEnclosingScope()
          .resolveCDTypeDown(superType.printType(pp));
      if (!opt.isPresent()) {
        opt = artifactScope.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> interfaceList.add(cdTypeSymbol.getAstNode()));
    }
    return interfaceList;
  }

  protected String determinePackageName(ASTCDType astcdType){
    int start = astcdType.getSymbol().getFullName().length() - astcdType.getName().length() - 1;

    if (start<0){
      return "";
    }

    StringBuilder packageName = new StringBuilder().append(astcdType.getSymbol().getFullName());
    return packageName.delete(start, packageName.length()).toString();
  }

}
