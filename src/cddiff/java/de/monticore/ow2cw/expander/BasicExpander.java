package de.monticore.ow2cw.expander;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.umlmodifier._ast.ASTModifier;

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
    if (packageName.equals(cd.getCDDefinition().getDefaultPackageName())) {
      cd.getCDDefinition().getCDElementList().add(astcdClass);
    }
    else {
      cd.getCDDefinition().addCDElementToPackage(astcdClass, packageName);
    }
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addClone(ASTCDType cdType) {
    if (determinePackageName(cdType).equals(cd.getCDDefinition().getDefaultPackageName())) {
      cd.getCDDefinition().getCDElementList().add(cdType.deepClone());
    }
    else {
      cd.getCDDefinition().addCDElementToPackage(cdType.deepClone(), determinePackageName(cdType));
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

  public void addDummyClass(String dummyName) {
    ASTModifier newModifier = CD4CodeMill.modifierBuilder().build();

    ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
        .setName(dummyName)
        .setCDExtendUsageAbsent()
        .setCDInterfaceUsageAbsent()
        .setModifier(newModifier)
        .build();
    getCD().getCDDefinition().getCDElementList().add(newClass);
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

}
