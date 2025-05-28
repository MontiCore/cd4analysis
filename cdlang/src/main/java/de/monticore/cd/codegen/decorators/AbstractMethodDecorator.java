/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import java.util.Stack;

/**
 * Turn method signatures into abstract methods. For static methods TODO: We should consider if this
 * decorator would be better suited as a transformation
 */
public class AbstractMethodDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CD4CodeBasisVisitor2 {

  protected Stack<ASTCDClass> classStack = new Stack<>();

  @Override
  public void visit(ASTCDClass node) {
    classStack.push(node);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    classStack.pop();
  }

  @Override
  public void visit(ASTCDMethod method) {
    // Only work on classes, not interfaces, etc.
    if (classStack.isEmpty()) return;
    var originalParent = decoratorData.getParent(method).orElseThrow();
    if (originalParent != classStack.peek()) {
      return;
    }
    // First, check if we should decorate the given object
    if (decoratorData.shouldDecorate(this.getClass(), method)) {

      // TODO: Can we somehow check if a template was replaced
      // Right now we can only tag/stereotype a method to avoid its abstract-ion
      if (!method.getModifier().isStatic()) {
        // non-static methods are turned into abstract methods
        decoratorData.getAsDecorated(method).getModifier().setAbstract(true);

        // And also mark the parent (class) as abstract
        decoratorData.getAsDecorated(classStack.peek()).getModifier().setAbstract(true);
      } else {
        // static methods can not be turned abstract:
        // instead we throw an error
        glexOpt.ifPresent(
            g ->
                g.replaceTemplate(
                    CD4C.getInstance().getEmptyBodyTemplate(),
                    decoratorData.getAsDecorated(method),
                    new TemplateHookPoint("methods.EmptyBodyThrowError")));
      }
      // We could add the TOPTrafo.NEEDS_TOP_IDENTIFIER stereotype for improved error messages,
      // but we have to ensure quickFail is disabled during the TOPTrafo
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CD4CodeBasis(this);
  }
}
