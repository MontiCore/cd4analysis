package de.monticore.ow2cw.expander;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.Optional;

public class BasicExpander implements CDExpander {

  protected ASTCDCompilationUnit cd;

  /**
   * @param cd Used for checking if adding/etc is allowed
   */
  public BasicExpander(ASTCDCompilationUnit cd) {
    this.cd = cd;
  }

  public ASTCDCompilationUnit getCD() {
    return cd;
  }

  /**
   * add newClass as subclass to superclass
   */
  public void addNewSubClass(String name, ASTCDClass superclass) {

    if (!superclass.getModifier().isFinal()) {

      ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

      ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
          .setName(name)
          .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage(superclass.getName()))
          .setCDInterfaceUsageAbsent()
          .setModifier(newModifier)
          .build();
      addType2Package(newClass, determinePackageName(superclass));
    }

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
    addType2Package(newClass, determinePackageName(astcdInterface));
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addType2Package(ASTCDType astcdType, String packageName) {
    if (packageName.equals(cd.getCDDefinition().getDefaultPackageName())) {
      cd.getCDDefinition().getCDElementList().add(astcdType);
    }
    else {
      cd.getCDDefinition().addCDElementToPackage(astcdType, packageName);
    }
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public Optional<ASTCDType> addClone(ASTCDType srcType) {
    ASTCDType newType = srcType.deepClone();

    if (determinePackageName(srcType).equals(cd.getCDDefinition().getDefaultPackageName())) {
      cd.getCDDefinition().getCDElementList().add(newType);
    }
    else {
      cd.getCDDefinition().addCDElementToPackage(newType, determinePackageName(srcType));
    }
    return Optional.of(newType);
  }

  public Optional<ASTCDClass> addDummyClass(ASTCDType srcType) {

    // construct empty clone

    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(srcType.getName())
        .setCDExtendUsageAbsent()
        .setCDInterfaceUsageAbsent()
        .setModifier(newModifier)
        .build();
    addType2Package(newClass, determinePackageName(srcType));
    return Optional.of(newClass);
  }

  public Optional<ASTCDClass> addDummyClass(String dummyName) {
    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(dummyName)
        .setCDExtendUsageAbsent()
        .setCDInterfaceUsageAbsent()
        .setModifier(newModifier)
        .build();
    getCD().getCDDefinition().getCDElementList().add(newClass);
    return Optional.of(newClass);
  }

  public Optional<ASTCDInterface> addDummyInterface(ASTCDInterface srcInterface) {
    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDInterface newInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(srcInterface.getName())
        .setCDExtendUsageAbsent()
        .setModifier(newModifier)
        .build();
    addType2Package(newInterface, determinePackageName(srcInterface));
    return Optional.of(newInterface);
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

  /**
   * Create left-to-right association without multiplicity constraints
   * @param left qualified name of the referenced class on the left
   * @param roleName role name of the referenced class on the right
   * @param right qualified name of the referenced class on the right
   * @return new ASTCDAssociation without multiplicity constraints
   */
  public Optional<ASTCDAssociation> buildDummyAssociation(String left, String roleName,
      String right) {

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
    return Optional.of(dummy);
  }

  public void updateUnspecifiedDir2Default() {
    for (ASTCDAssociation assoc : getCD().getCDDefinition().getCDAssociationsList()) {
      if (!(assoc.getCDAssocDir().isDefinitiveNavigableRight() || assoc.getCDAssocDir()
          .isDefinitiveNavigableLeft())) {
        assoc.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
      }
    }
  }

  public void mismatchDir(ASTCDAssociation src, ASTCDAssociation target) {
    if (!(src.getCDAssocDir().isDefinitiveNavigableLeft() || src.getCDAssocDir()
        .isDefinitiveNavigableRight())) {
      if (target.getCDAssocDir().isDefinitiveNavigableRight()) {
        src.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
      }
      else {
        if (target.getCDAssocDir().isDefinitiveNavigableLeft()) {
          src.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
        }
      }
    }
  }

  public void mismatchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    if (!(src.getCDAssocDir().isDefinitiveNavigableLeft() || src.getCDAssocDir()
        .isDefinitiveNavigableRight())) {
      if (target.getCDAssocDir().isDefinitiveNavigableRight()) {
        src.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
      }
      else if (target.getCDAssocDir().isDefinitiveNavigableLeft()) {
        src.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
      }
    }
  }

  public void matchDir(ASTCDAssociation src, ASTCDAssociation target) {
    if (!(src.getCDAssocDir().isDefinitiveNavigableLeft() || src.getCDAssocDir()
        .isDefinitiveNavigableRight())) {
      src.setCDAssocDir(target.getCDAssocDir().deepClone());
    }
  }

  public void matchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    if (!(src.getCDAssocDir().isDefinitiveNavigableLeft() || src.getCDAssocDir()
        .isDefinitiveNavigableRight()) && target.getCDAssocDir().isBidirectional()) {
      src.setCDAssocDir(target.getCDAssocDir().deepClone());
    }
    else {
      mismatchDirInReverse(src, target);
    }
  }

}
