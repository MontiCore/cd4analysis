/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Applies the Visitor-Pattern to the CD
 */
public class VisitorDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {
  
  protected static final String SUFFIX = "Visitor";
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Getter for an attribute,
    // thus the Getter decorator has to run before.
    return Iterables.concat(super.getMustRunAfter(), List.of(GetterDecorator.class));
  }
  
  @Override
  public void visit(ASTCDDefinition clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      // Get the parent (package or CDDef)
      ASTNode origParent = this.decoratorData.getParent(clazz).get();
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);
      
      // create a Visitor interface for the class
      ASTCDInterface interfaceVisitorArtifact = CD4CodeMill.cDInterfaceBuilder().setName("I" + clazz
          .getName() + SUFFIX).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      
      addElementToParent(decParent, interfaceVisitorArtifact);
      
      visitorInterfaceStack.push(interfaceVisitorArtifact);
      
    }
  }
  
  @Override
  public void endVisit(ASTCDDefinition clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      visitorInterfaceStack.pop();
    }
  }
  
  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);
      String packageName = clazz.getSymbol().getPackageName();
      
      String visitorInterfaceName = packageName.isEmpty() ? visitorInterfaceStack.peek().getName()
          : packageName + "." + visitorInterfaceStack.peek().getName();
      
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceName);
      ASTCDParameter visitorParameter = CD4CodeMill.cDParameterBuilder().setName("visitor")
          .setMCType(visitorInterfaceQualifiedType).build();
      //create a type of the class
      ASTMCType classType = MCTypeFacade.getInstance().createQualifiedType(clazz.getName());
      ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("node").setMCType(
          classType).build();
      
      // construct visitor handling methods
      ASTCDMethod acceptMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "accept", visitorParameter);
      
      // add the interface methods to the pojo class
      addToClass(decClazz, acceptMethod);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint("methods.visitor.Accept", clazz.getName())));
      //        new StringHookPoint("visitor.visit ((" + clazz.getName() + ")this);")));
      
      CD4C.getInstance().addImport(decClazz, visitorInterfaceName);
      this.decParent.push(decClazz);
      
      // add visit method
      ASTCDMethod visitMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().ABSTRACT().build(), "visit", classParameter);
      visitorInterfaceStack.peek().addCDMember(visitMethod);
    }
  }
  
  @Override
  public void endVisit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      decParent.pop();
    }
  }
  
  protected Stack<ASTCDInterface> visitorInterfaceStack = new Stack<>();
  protected Stack<ASTCDClass> decParent = new Stack<>();
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
