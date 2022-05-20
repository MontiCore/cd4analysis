package de.monticore.ow2cw;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2alloy.generator.CD2AlloyQNameHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.umlmodifier._ast.ASTModifier;
import net.sourceforge.plantuml.Log;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CDExpander {
  protected ASTCDCompilationUnit originalCD;  // Genutzt, um stereotype zu lesen

  protected ASTCDDefinitionBuilder builder = CD4AnalysisMill.cDDefinitionBuilder();  // TODO:
  // nutz einen existierenden
  // Builder

  /**
   * @param originalCD Used for checking if adding/etc is allowed
   */
  public CDExpander(ASTCDCompilationUnit originalCD) {
    this.originalCD = originalCD;
  }

  /**
   * add newClass as subclass to superclass
   */
  public void addNewSubClass(String name, ASTCDClass superclass) {

    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(name)
        .setCDExtendUsage(
            CDExtendUsageFacade.getInstance().createCDExtendUsage(superclass.getName()))
        .setCDInterfaceUsageAbsent()
        .setModifier(newModifier)
        .build();
    addClass2Package(newClass, determinePackageName(superclass));

  }

  /**
   * add newClass as sub-class to astcdInterface
   */
  public void addNewSubClass(String name, ASTCDInterface astcdInterface) {

    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(name)
        .setCDInterfaceUsage(
            CDInterfaceUsageFacade.getInstance().createCDInterfaceUsage(astcdInterface.getName()))
        .setCDExtendUsageAbsent()
        .setModifier(newModifier)
        .build();
    addClass2Package(newClass, determinePackageName(astcdInterface));
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addClass2Package(ASTCDClass astcdClass, String packageName) {
    if (packageName.equals(originalCD.getCDDefinition().getDefaultPackageName())) {
      originalCD.getCDDefinition().getCDElementList().add(astcdClass);
    }
    else {
      originalCD.getCDDefinition().addCDElementToPackage(astcdClass, packageName);
    }
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addClone(ASTCDType cdType) {
    if (determinePackageName(cdType).equals(originalCD.getCDDefinition().getDefaultPackageName())) {
      originalCD.getCDDefinition().getCDElementList().add(cdType.deepClone());
    }
    else {
      originalCD.getCDDefinition()
          .addCDElementToPackage(cdType.deepClone(), determinePackageName(cdType));
    }
  }

  public void addDummyClass(ASTCDClass srcClass) {

    // construct empty clone

    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(srcClass.getName())
        .setCDExtendUsageAbsent()
        .setCDInterfaceUsageAbsent()
        .setModifier(newModifier)
        .build();
    addClass2Package(newClass, determinePackageName(srcClass));
  }

  public <T extends ASTCDType> void addMissingTypesAndAttributes(Collection<T> typeList) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(originalCD);
    for (ASTCDType astcdType : typeList) {
      Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(astcdType.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdType);
      }
      else {
        addMissingAttributes(opt.get().getAstNode(), astcdType.getCDAttributeList());
      }
    }
  }

  public void addMissingEnumsAndConstants(Collection<ASTCDEnum> enumList) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(originalCD);
    // add enums and enum constants exclusive to first
    for (ASTCDEnum astcdEnum : enumList) {
      Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(astcdEnum.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdEnum);
      }
      else {
        for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
          boolean found = opt.get()
              .getFieldList()
              .stream()
              .anyMatch(field -> field.getName().equals(constant.getName()));
          if (!found) {
            // I wanted to avoid reflection, but I think this is just reflection with extra steps...
            for (ASTCDEnum someEnum : originalCD.getCDDefinition().getCDEnumsList()) {
              if (astcdEnum.getSymbol().getFullName().equals(someEnum.getSymbol().getFullName())) {
                someEnum.addCDEnumConstant(constant.deepClone());
              }
            }
          }
        }
      }
    }
  }

  public void addMissingAttributes(ASTCDType cdType, Collection<ASTCDAttribute> missingAttributes) {
    /*if (check stereotype if allowed to add attribute to class ){
      // add Attribute
    }
     */
    for (ASTCDAttribute attribute1 : missingAttributes) {
      boolean found = cdType.getCDAttributeList()
          .stream()
          .anyMatch(attribute2 -> attribute1.getName().equals(attribute2.getName()));
      if (!found) {
        ASTCDAttribute newAttribute = attribute1.deepClone();
        cdType.addCDMember(newAttribute);
      }
    }
  }

  public void addMissingAssociations(Collection<ASTCDAssociation> assocs,
      boolean withCardinalities) {
    for (ASTCDAssociation srcAssoc : assocs) {
      boolean found = originalCD.getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .anyMatch(targetAssoc -> CDAssociationHelper.sameAssociation(targetAssoc, srcAssoc));
      if (!found) {
        ASTCDAssociation newAssoc = srcAssoc.deepClone();
        if (!withCardinalities) {
          newAssoc.getRight().setCDCardinalityAbsent();
          newAssoc.getLeft().setCDCardinalityAbsent();
        }
        //todo: check if class/interface has stereotype ""
        originalCD.getCDDefinition().getCDElementList().add(newAssoc);
      }
    }
  }

  /**
   * update direction of underspecified associations to match those of assocs
   */
  public void updateDir2Match(Collection<ASTCDAssociation> assocs) {
    CDAssociationHelper.updateDir2Match(assocs,
        originalCD.getCDDefinition().getCDAssociationsList());
  }

  public void updateDir4Diff(Collection<ASTCDAssociation> assocs) {
    CDAssociationHelper.updateDir4Diff(assocs,
        originalCD.getCDDefinition().getCDAssociationsList());
  }

  /**
   * helper-method to determine the package name of an ASTCDType since getSymbol().getPackageName()
   * is always an empty String
   */
  public String determinePackageName(ASTCDType astcdType) {
    int start = astcdType.getSymbol().getFullName().length() - astcdType.getName().length() - 1;

    if (start < 0) {
      return "";
    }

    StringBuilder packageName = new StringBuilder().append(astcdType.getSymbol().getFullName());
    return packageName.delete(start, packageName.length()).toString();
  }

  public ASTCDCompilationUnit build() {
    builder.build();
    // todo ... bauen
    // todo removeDuplicateAttributes
    throw new NotImplementedException();
  }

  public Collection<ASTCDAssociation> addDummyAssociations(Collection<ASTCDAssociation> isolated) {

    List<ASTCDAssociation> dummies = new ArrayList<>();
    String roleName;
    int i = 0;

    for (ASTCDAssociation src : isolated) {
      i++;

      if (src.getCDAssocDir().isDefinitiveNavigableRight()) {

        if (src.getRight().isPresentCDRole()) {
          roleName = src.getRight().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              src.getRightQualifiedName().getQName());
        }

        dummies.add(buildDummyClassAndAssociation(src.getLeftQualifiedName().getQName(), roleName,
            src.getRightQualifiedName().getQName(), i));

      }
      else {

        if (src.getLeft().isPresentCDRole()) {
          roleName = src.getLeft().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              src.getLeftQualifiedName().getQName());
        }

        dummies.add(buildDummyClassAndAssociation(src.getRightQualifiedName().getQName(), roleName,
            src.getLeftQualifiedName().getQName(), i));
      }
    }
    return dummies;
  }

  public ASTCDAssociation buildDummyClassAndAssociation(String left, String roleName, String right,
      int id) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(originalCD);
    Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(right);
    if (opt.isPresent()) {

      ASTCDType rightType = opt.get().getAstNode();

      // construct dummy class

      ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

      ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
          .setName(rightType.getName() + "Dummy" + id)
          .setCDExtendUsageAbsent()
          .setCDInterfaceUsageAbsent()
          .setModifier(newModifier)
          .build();
      addClass2Package(newClass, determinePackageName(rightType));

      CD4CodeMill.scopesGenitorDelegator().createFromAST(originalCD);

      return buildDummyAssociation(left, roleName, newClass.getSymbol().getFullName());

    }
    else {
      Log.error(String.format("0xCDD12: Could not resolve %s.", right));
    }
    return null;
  }

  public ASTCDAssociation buildDummyAssociation(String left, String roleName, String right) {

    ASTCDAssociation dummy = CDAssociationMill.cDAssociationBuilder()
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .setCDAssocType(CDAssociationMill.cDAssocTypeAssocBuilder().build())
        .setLeft(CDAssociationMill.cDAssocLeftSideBuilder()
            .setCDCardinalityAbsent()
            .setCDRoleAbsent()
            .setCDOrderedAbsent()
            .setCDQualifierAbsent()
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .setMCQualifiedType(MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
                .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(left))
                .build())
            .build())
        .setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build())
        .setRight(CDAssociationMill.cDAssocRightSideBuilder()
            .setCDCardinalityAbsent()
            .setCDRole(CDAssociationMill.cDRoleBuilder().setName(roleName).build())
            .setCDOrderedAbsent()
            .setCDQualifierAbsent()
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .setMCQualifiedType(MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
                .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(right))
                .build())
            .build())
        .build();
    originalCD.getCDDefinition().getCDElementList().add(dummy);
    return dummy;
  }

  public void updateUnspecifiedDir2Default() {
    for (ASTCDAssociation assoc : originalCD.getCDDefinition().getCDAssociationsList()){
      if (!(assoc.getCDAssocDir().isDefinitiveNavigableRight() || assoc.getCDAssocDir().isDefinitiveNavigableLeft())){
        assoc.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
      }
    }
  }

}
