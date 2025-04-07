/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDAttributeInInterfaceInitialized;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpressionBuilder;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.*;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ObserverDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return List.of(SetterDecorator.class);
  }

  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);
      // Get the parent (package or CDDef)
      ASTNode origParent = this.decoratorData.getParent(clazz).get();
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);

      String packageName = clazz.getSymbol().getPackageName();

      String observerInterfaceName = packageName.isEmpty()? "I" + clazz.getName() + "Observer": packageName+".I" + clazz.getName() + "Observer";
      String observableInterfaceName = packageName.isEmpty()? "I" + clazz.getName() + "Observable": packageName+".I" + clazz.getName() + "Observable";
      ASTMCQualifiedType observerInterfaceQualifiedType = MCTypeFacade.getInstance().createQualifiedType(observerInterfaceName);
      ASTMCQualifiedType observableInterfaceQualifiedType = MCTypeFacade.getInstance().createQualifiedType(observableInterfaceName);
      ASTCDParameter observerParameter = CD4CodeMill.cDParameterBuilder().setName("observer").setMCType(observerInterfaceQualifiedType).build();
      ASTCDParameter observeParameter = CD4CodeMill.cDParameterBuilder().setName("observable").setMCType(observableInterfaceQualifiedType).build();
      //create a type of the class
      ASTMCType classType = MCTypeFacade.getInstance().createQualifiedType(clazz.getName());
      ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("clazz").setMCType(classType).build();

      //make sure an attribute of the type and the name of an observer is not already present
      if(decClazz.getCDAttributeList().stream().anyMatch(attr -> attr.getMCType().printType().equals(observerInterfaceName) && attr.getName().equals("observerList"))){
        Log.error("0xA1234 The class " + decClazz.getName() + " already has an attribute of type " + observerInterfaceName + " with the name observerList");
      } else {
        //create an attribute of the type and the name of an observer
        ASTCDAttribute observerList = CD4CodeMill.cDAttributeBuilder()
                .setName("observerList")
                .setMCType(MCTypeFacade.getInstance().createListTypeOf(observerInterfaceQualifiedType))
                .setModifier(CD4CodeMill.modifierBuilder().PROTECTED().build())
                .build();
        decClazz.addCDMember(observerList);

        glexOpt.ifPresent(glex -> glex.replaceTemplate(VALUE, observerList ,new StringHookPoint(" = new ArrayList<>()")));
      }

      //create own interface Observableand Observer for every class
      ASTCDInterface interfaceObservableArtifact = CD4CodeMill.cDInterfaceBuilder()
              .setName("I" + decClazz.getName() + "Observable")
              .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build())
              .build();
      ASTCDInterface interfaceObserverArtifact = CD4CodeMill.cDInterfaceBuilder()
        .setName("I" + decClazz.getName() + "Observer")
        .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build())
        .build();

      //add the interfaces to the package
      addElementToParent(decParent, interfaceObservableArtifact);
      addElementToParent(decParent, interfaceObserverArtifact);

      //build the methods
      ASTCDMethod addObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"addObserver",observerParameter);
      ASTCDMethod removeObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"removeObserver",observerParameter);
      ASTCDMethod notifyObservers = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"notifyObservers",classParameter);
      ASTCDMethod update = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),"update", classParameter);
      List<AttributeSpecificMethodStash> attributeSpecificMethodStashes = new ArrayList<>();
      clazz.getCDAttributeList().forEach(attribute ->
        attributeSpecificMethodStashes.add(new AttributeSpecificMethodStash(
          CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "updateObserver" + StringUtils.capitalize(attribute.getName()), List.of(classParameter, CD4CodeMill.cDParameterBuilder().setName("ov").setMCType(attribute.getMCType()).build())),
          CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "notifyObserver" + StringUtils.capitalize(attribute.getName()), List.of(classParameter, CD4CodeMill.cDParameterBuilder().setName("ov").setMCType(attribute.getMCType()).build())),
          attribute.getName())
        )
      );

      //add the methods to the interface Observable
      interfaceObservableArtifact.addCDMember(addObserver.deepClone());
      interfaceObservableArtifact.addCDMember(removeObserver.deepClone());
      interfaceObservableArtifact.addCDMember(notifyObservers.deepClone());
      attributeSpecificMethodStashes.forEach(stash -> interfaceObservableArtifact.addCDMember(stash.getMethodObservable().deepClone()));

      // add the methods to the interface Observer
      interfaceObserverArtifact.addCDMember(update.deepClone());
      attributeSpecificMethodStashes.forEach(stash -> interfaceObserverArtifact.addCDMember(stash.getMethodObserver().deepClone()));

      //add the interface methods to the pojo class
      addToClass(decClazz, addObserver);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, addObserver ,new TemplateHookPoint("methods.observer.addObserver","observerList", observerInterfaceQualifiedType.getMCQualifiedName().getQName())));
      addToClass(decClazz, removeObserver);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, removeObserver ,new TemplateHookPoint("methods.observer.removeObserver.ftl","observerList", observerInterfaceQualifiedType.getMCQualifiedName().getQName())));
      addToClass(decClazz, notifyObservers);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, notifyObservers ,new TemplateHookPoint("methods.observer.notifyObserver", "observerList", observerParameter.getMCType().printType())));
      attributeSpecificMethodStashes.forEach(stash -> {
        addToClass(decClazz, stash.getMethodObservable());
        glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, stash.getMethodObservable() ,new TemplateHookPoint("methods.observer.notifyObserverAttributeSpecific", "observerList", observerParameter.getMCType().printType(), stash.getAttributeName())));
      });




      //add an interface list if not present in the clazz
      if(!decClazz.isPresentCDInterfaceUsage()){
        decClazz.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      //add the Observable interfaces to the class
      decClazz.getCDInterfaceUsage().addInterface(observableInterfaceQualifiedType);

      // add an import statement for the Observer interface
      CD4C.getInstance().addImport(decClazz, observableInterfaceName);
      CD4C.getInstance().addImport(decClazz, observerInterfaceName);
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  static class AttributeSpecificMethodStash {
    private final ASTCDMethod methodObserver;
    private final ASTCDMethod methodObservable;
    private final String attributeName;

    public AttributeSpecificMethodStash(ASTCDMethod methodObserver, ASTCDMethod methodObservable, String attributeName) {
      this.methodObserver = methodObserver;
      this.methodObservable= methodObservable;
      this.attributeName = attributeName;
    }

    public ASTCDMethod getMethodObserver() {
      return methodObserver;
    }

    public ASTCDMethod getMethodObservable() {
      return methodObservable;
    }

    public String getAttributeName() {
      return attributeName;
    }
  }

}

