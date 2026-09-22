/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlmodifier.UMLModifierMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Provides method bodies of (modeled) methods from templates
 * TODO: ALu - Add CI tests for this decorator (right now it is for playing around)
 */
public class RequiredArgsConstructorDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CD4CodeBasisVisitor2, CDBasisVisitor2 {
  
  protected record StackData(List<ASTCDAttribute> attrs) {}
  
  protected Stack<StackData> stack = new Stack<>();
  
  @Override
  public void visit(ASTCDClass astcdClass) {
    stack.push(new StackData(new ArrayList<>()));
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    if (stack.isEmpty())
      return;
    
    // TODO: required vs all?
    if (!attribute.getModifier().isFinal())
      return;
    
    if (decoratorData.shouldDecorate(this.getClass(), attribute))
      stack.peek().attrs.add(attribute);
  }
  
  @Override
  public void endVisit(ASTCDClass node) {
    if (stack.isEmpty())
      return;
    if (decoratorData.shouldDecorate(this.getClass(), node)) {
      // TODO: Check that method does not already exists!
      ASTCDConstructor constructor = CDConstructorFacade.getInstance().createConstructor(
          copyVisibility(node.getModifier()), node.getName());
      for (ASTCDAttribute attribute : stack.peek().attrs()) {
        constructor.addCDParameter(CDParameterFacade.getInstance().createParameter(attribute));
      }
      
      decoratorData.getAsDecorated(node).addCDMember(constructor);
      
      glexOpt.ifPresent(g -> g.replaceTemplate(CD4C.getInstance().getEmptyBodyTemplate(),
          constructor, new TemplateHookPoint("methods.constructor.Constructor", stack
              .peek().attrs)));
      
      stack.pop();
    }
  }
  
  protected ASTModifier copyVisibility(ASTModifier orig) {
    ASTModifierBuilder vis = UMLModifierMill.modifierBuilder();
    vis.setPublic(orig.isPublic());
    vis.setProtected(orig.isProtected());
    vis.setPrivate(orig.isPrivate());
    return vis.build();
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CD4CodeBasis(this);
    traverser.add4CDBasis(this);
  }
  
}
