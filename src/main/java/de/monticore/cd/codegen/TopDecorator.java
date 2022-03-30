/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;

public class TopDecorator {

  /*
  Adds the suffix TOP to hand coded ASTs and makes generated TOP class abstract
  Attention! does not actually create a new CD object, because then the glex has the wrong objects referenced
   */

  public static final String TOP_SUFFIX = "TOP";

  protected final MCPath hwPath;

  public TopDecorator(MCPath hwPath) {
    this.hwPath = hwPath;
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalCD) {
    ASTCDCompilationUnit topCD = originalCD;
    topCD.getCDDefinition().getCDClassesList().stream()
        .filter(cdClass -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getCDPackageList(), cdClass.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(cdInterface -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getCDPackageList(), cdInterface.getName())))
        .forEach(this::applyTopMechanism);

    topCD.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getCDPackageList(), cdEnum.getName())))
        .forEach(this::applyTopMechanism);

    return topCD;
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass.getCDConstructorList().forEach(constructor ->
        constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  protected void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
