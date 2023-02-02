/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDPackageSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.ArrayList;
import java.util.List;

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

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit) {
    compUnit.getCDDefinition().getCDClassesList().stream()
        .filter(
            cdClass -> existsHandwrittenClass(hwPath, determineQualifiedName(cdClass, compUnit)))
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDInterfacesList().stream()
        .filter(
            cdInterface ->
                existsHandwrittenClass(hwPath, determineQualifiedName(cdInterface, compUnit)))
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, determineQualifiedName(cdEnum, compUnit)))
        .forEach(this::applyTopMechanism);

    return compUnit;
  }

  protected String determineQualifiedName(
      ASTCDType astcdtype, ASTCDCompilationUnit astcdCompilationUnit) {
    List<String> packagesNames = new ArrayList<>();
    CDTypeSymbol typeSymbol = astcdtype.getSymbol();
    ICDBasisScope scope = typeSymbol.getEnclosingScope();
    while (scope != null) {
      if (scope.isPresentSpanningSymbol()) {
        IScopeSpanningSymbol symbol = scope.getSpanningSymbol();
        if (symbol instanceof CDPackageSymbol) {
          packagesNames.add(0, symbol.getName());
        }
      }
      scope = scope.getEnclosingScope();
    }
    if (packagesNames.isEmpty()) {
      return constructQualifiedName(astcdCompilationUnit.getCDPackageList(), astcdtype.getName());
    }
    return constructQualifiedName(packagesNames, astcdtype.getName());
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass
        .getCDConstructorList()
        .forEach(constructor -> constructor.setName(constructor.getName() + TOP_SUFFIX));
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
