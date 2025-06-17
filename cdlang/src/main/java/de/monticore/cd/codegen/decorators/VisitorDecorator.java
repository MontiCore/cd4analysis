/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4analysis._prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._ast.ASTCDQualifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class VisitorDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {

  Stack<ASTCDParameter> parameterOfPojo = new Stack<>();
  ASTCDInterface visitorInterface;
  ASTCDParameter visitorInterfaceParameter;
  boolean isInit = false;

  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return Iterables.concat(super.getMustRunAfter(), Collections.singletonList(
        SetterDecorator.class));
  }

  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);

      String packageName = clazz.getSymbol().getPackageName();

      String visitorInterfaceName = packageName.isEmpty() ? "I" + clazz.getName() + "Visitor"
          : packageName + ".I" + clazz.getName() + "Visitor";
      String pojoClassName = packageName.isEmpty() ? clazz.getName() : packageName + "." + clazz
          .getName();
      ASTMCQualifiedType pojoClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
          pojoClassName);
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceName);
      ASTCDParameter pojoClassParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(pojoClassQualifiedType).build();
      ASTCDParameter pojoInterfaceClassParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(visitorInterfaceQualifiedType).build();
      parameterOfPojo.add(pojoClassParameter);

      //create the methods for the visitor interface
      //visit:
      ASTCDMethod visitMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "visit", parameterOfPojo.peek());
      visitorInterface.addCDMember(visitMethodHeader);
      // endVisit:
      ASTCDMethod endVisitMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "endVisit", parameterOfPojo.peek());
      visitorInterface.addCDMember(endVisitMethodHeader);
      // handle:
      ASTCDMethod handleMethodHeader = CD4CodeMill.cDMethodBuilder().setModifier(CD4CodeMill.modifierBuilder().PUBLIC().setAbstract(false).build())
        .setName("handle")
        .setMCReturnType(CD4CodeMill.mCReturnTypeBuilder().setMCVoidType(CD4CodeMill.mCVoidTypeBuilder().build()).build())
        .setCDParametersList(List.of(parameterOfPojo.peek()))
        .build();
      glexOpt.ifPresent(glex -> glex.addAfterTemplate(ANNOTATIONS, handleMethodHeader,new StringHookPoint("default")));
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, handleMethodHeader,
          new TemplateHookPoint("methods.visitor.handle", pojoClassParameter, pojoInterfaceClassParameter)));


      visitorInterface.addCDMember(handleMethodHeader);


      IndentPrinter printer = new IndentPrinter();
      System.out.println(new CD4AnalysisFullPrettyPrinter(printer).prettyprint(handleMethodHeader));
      // public void ${name}>
      // aber weil es ein interface ist wird es automatisch abstract

      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, visitMethodHeader,
          new TemplateHookPoint("methods.visitor.handle")));
      // traverse:
      ASTCDMethod traverseMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "traverse", parameterOfPojo.peek());
      visitorInterface.addCDMember(traverseMethodHeader);

      // add accept method
      ASTCDMethod acceptMethod = CDMethodFacade.getInstance().createDefaultMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "accept", visitorInterfaceParameter);
      decClazz.addCDMember(acceptMethod);

      String errorCode = "0x01472";
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint("methods.visitor.accept", clazz, errorCode)));
    }
  }

  @Override
  public void endVisit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      parameterOfPojo.pop();
    }
  }

  @Override
  public void visit(ASTCDDefinition definition) {
    init("I" + definition.getName() + "Visitor", definition);
  }

  public void init(String visitorInterfaceName, ASTCDDefinition definition) {
    if (!isInit) {
      isInit = true;
      //create the visitor interface
      visitorInterface = CD4CodeMill.cDInterfaceBuilder().setName(visitorInterfaceName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).build();

      // add the visitor interface to the definition
      ASTCDDefinition decoratedDefinition = this.decoratorData.getAsDecorated(definition);
      decoratedDefinition.addCDElement(visitorInterface);

      // create the visitor interface parameter
      String packageName = definition.getSymbol().getPackageName();
      String visitorInterfaceQualifiedName = packageName.isEmpty() ? visitorInterfaceName
          : packageName + "." + visitorInterfaceName;
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceQualifiedName);
      visitorInterfaceParameter = CD4CodeMill.cDParameterBuilder().setName("visitor").setMCType(
          visitorInterfaceQualifiedType).build();

      // add getTraversedElements Set<Object> attribute
      ASTMCSetType setType = MCTypeFacade.getInstance().createSetTypeOf("Object");
      ASTCDAttribute getTraversedElementsAttribute = CDAttributeFacade.getInstance()
        .createAttribute(CD4CodeMill.modifierBuilder().build(),setType, "getTraversedElements");
      visitorInterface.addCDMember(getTraversedElementsAttribute);
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

}
