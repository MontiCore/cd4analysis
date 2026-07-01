/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;

/**
 * Provides method bodies of (modeled) methods from templates
 * TODO: ALu - Add CI tests for this decorator (right now it is for playing around)
 */
public class MethodImplementationDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CD4CodeBasisVisitor2 {
  
  @Override
  public void visit(ASTCDMethod method) {
    // First, check if we should decorate the given object
    if (decoratorData.shouldDecorate(this.getClass(), method)) {
      // Then ensure a stereotype is present (as we do not support other configuration values so far)
      if (!method.getModifier().isPresentStereotype()) {
        Log.error("0xTODO: The method " + method.getName()
            + " is decorated with a method implementation, but does not present a stereotype.",
            method.get_SourcePositionStart(), method.get_SourcePositionEnd());
        return;
      }
      
      // TODO: ALu: Create shared get-marked value source (not only for stereotypes)
      String template = method.getModifier().getStereotype().getValue("impl");
      
      // And finally. replace the empty body template with the given template
      glexOpt.ifPresent(g -> g.replaceTemplate(CD4C.getInstance().getEmptyBodyTemplate(),
          decoratorData.getAsDecorated(method), new TemplateHookPoint(template)));
    }
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CD4CodeBasis(this);
  }
  
}
