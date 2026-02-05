/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators.data;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.hookpoints.TemplateHookPointWithInfo;

import java.util.Optional;

/**
 * Abstract decorator class, which handles access to shared data structures and provides some
 * utilities
 *
 * @param <D>
 */
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
      throw new IllegalStateException("Unhandled addElementToParent " + decoratedParent.getClass()
          .getName());
  }
  
  protected void addToClass(ASTCDClass clazz, ASTCDMember member) {
    // TODO: Only add iff not yet present (#4310)
    clazz.addCDMember(member);
  }
  
  public CDGenService getCDGenService() { return decoratorData.cdGenService; }
  
  /** For Decorators not specifying any additional data */
  public static class NoData {}
  
  protected TemplateHookPoint templateHookPoint(String templateName, String x,
      Object... templateArguments) {
    var ret = new TemplateHookPointWithInfo(templateName, templateArguments);
    if (withDecoratorStacktrace()) {
      StringBuilder sb = new StringBuilder();
      var st = new Throwable().getStackTrace();
      for (int i = 1; i <= Math.min(st.length, 20); i++) {
        var ste = st[i];
        if (getClass().getName().equals(ste.getClassName())) {
          sb.append(ste).append(", ");
        }
      }
      x += sb.toString();
    }
    ret.setDecorator(getClass().getSimpleName() + "#" + x);
    
    return ret;
  }
  
  protected boolean withDecoratorStacktrace() {
    return true;
  }
  
}
