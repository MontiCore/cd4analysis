/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Optional;

public abstract class AbstractDecorator<D> implements IDecorator<D> {
  protected DecoratorData decoratorData;
  protected Optional<GlobalExtensionManagement> glexOpt;

  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    this.decoratorData = util;
    this.glexOpt = glexOpt;
  }

  protected void addElementToParent(ASTNode decoratedParent, ASTCDElement newElem) {
    if (decoratedParent instanceof ASTCDDefinition)
      ((ASTCDDefinition) decoratedParent).addCDElement(newElem);
    else if (decoratedParent instanceof ASTCDPackage)
      ((ASTCDPackage) decoratedParent).addCDElement(newElem);
    else
      throw new IllegalStateException("Unhandled addElementToParent " + decoratedParent.getClass().getName());
  }

  protected void addToClass(ASTCDClass clazz, ASTCDMember member) {
    // TODO: Only add iff not yet present
    clazz.addCDMember(member);
  }

  public CDGenService getCDGenService() {
    return decoratorData.cdGenService;
  }

  static class NoData {
  }
}
