/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw.expander;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.Optional;
import java.util.Set;

public class VariableExpander extends BasicExpander {

  public static final String VAR_TAG = "complete";

  public VariableExpander(ASTCDCompilationUnit cd) {
    super(cd);
  }

  @Override
  public Optional<ASTCDClass> addDummyClass(String dummyName) {
    if (checkCD(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addDummyClass(dummyName);
  }

  @Override
  public Optional<ASTCDClass> addDummyClass(ASTCDType srcType) {
    if (checkCD(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addDummyClass(srcType);
  }

  @Override
  public Optional<ASTCDInterface> addDummyInterface(String dummyName) {
    if (checkCD(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addDummyInterface(dummyName);
  }

  @Override
  public Optional<ASTCDInterface> addDummyInterface(ASTCDInterface srcInterface) {
    if (checkCD(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addDummyInterface(srcInterface);
  }

  @Override
  public Optional<ASTCDType> addClone(ASTCDType srcType) {
    if (checkCD(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addClone(srcType);
  }

  @Override
  public void addNewSubClass(String name, ASTCDClass superclass) {
    if (!checkCD(VAR_TAG)) {
      super.addNewSubClass(name, superclass);
    }
  }

  @Override
  public void addNewSubClass(String name, ASTCDInterface astcdInterface) {
    if (!checkCD(VAR_TAG)) {
      super.addNewSubClass(name, astcdInterface);
    }
  }

  @Override
  public void updateExtends(ASTCDClass targetClass, Set<String> extendsSet) {
    if (!checkCD(VAR_TAG) && !checkType(targetClass, VAR_TAG)) {
      super.updateExtends(targetClass, extendsSet);
    }
  }

  @Override
  public void updateImplements(ASTCDClass targetClass, Set<String> implementsSet) {
    if (!checkCD(VAR_TAG) && !checkType(targetClass, VAR_TAG)) {
      super.updateImplements(targetClass, implementsSet);
    }
  }

  @Override
  public void updateExtends(ASTCDInterface targetInterface, Set<String> extendsSet) {
    if (!checkCD(VAR_TAG) && !checkType(targetInterface, VAR_TAG)) {
      super.updateExtends(targetInterface, extendsSet);
    }
  }

  @Override
  public void addAssociation(ASTCDAssociation assoc) {
    if (checkCD(VAR_TAG)) {
      return;
    }

    ICD4CodeArtifactScope artifactScope =
        (ICD4CodeArtifactScope) getCD().getEnclosingScope();

    if (assoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      Optional<CDTypeSymbol> symbol =
          artifactScope.resolveCDTypeDown(assoc.getLeftQualifiedName().getQName());
      if (symbol.isPresent() && checkType(symbol.get().getAstNode(), VAR_TAG)) {
        return;
      }
    }

    if (assoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      Optional<CDTypeSymbol> symbol =
          artifactScope.resolveCDTypeDown(assoc.getRightQualifiedName().getQName());
      if (symbol.isPresent() && checkType(symbol.get().getAstNode(), VAR_TAG)) {
        return;
      }
    }

    super.addAssociation(assoc);
  }

  @Override
  public void addAttribute(ASTCDType type, ASTCDAttribute attribute) {
    if (!checkCD(VAR_TAG) && !checkType(type, VAR_TAG)) {
      super.addAttribute(type, attribute);
    }
  }

  @Override
  public void addEnumConstant(ASTCDEnum targetEnum, ASTCDEnumConstant constant) {
    if (!checkCD(VAR_TAG) && !checkType(targetEnum, VAR_TAG)) {
      super.addEnumConstant(targetEnum, constant);
    }
  }

  protected boolean checkCD(String stereotype) {
    return getCD().getCDDefinition().getModifier().isPresentStereotype()
        && getCD().getCDDefinition().getModifier().getStereotype().contains(stereotype);
  }

  protected boolean checkType(ASTCDType type, String stereotype) {
    return type.getModifier().isPresentStereotype()
        && type.getModifier().getStereotype().contains(stereotype);
  }
}
