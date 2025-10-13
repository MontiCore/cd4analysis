/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.*;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;

import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Applies the Observer-Pattern to the CD
 */
public class ObserverDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {

  protected static final String SUFFIX = "Observer";
  protected static final String OBS_LIST_ATTR = "observerList";

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
      // Get the parent (package or CDDef)
      ASTNode origParent = this.decoratorData.getParent(clazz).get();
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);

      String packageName = clazz.getSymbol().getPackageName();

      String observerInterfaceName = packageName.isEmpty() ? "I" + clazz.getName() + SUFFIX
          : packageName + ".I" + clazz.getName() + SUFFIX;
      ASTMCQualifiedType observerInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(observerInterfaceName);
      ASTCDParameter observerParameter = CD4CodeMill.cDParameterBuilder().setName("observer")
          .setMCType(observerInterfaceQualifiedType).build();
      //create a type of the class
      ASTMCType classType = MCTypeFacade.getInstance().createQualifiedType(clazz.getName());
      ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("clazz").setMCType(
          classType).build();

      //make sure an attribute of the type and the name of an observer is not already present
      {
        //create an attribute of the type and the name of an observer
        ASTCDAttribute observerList = CD4CodeMill.cDAttributeBuilder().setName(OBS_LIST_ATTR)
            .setMCType(MCTypeFacade.getInstance().createListTypeOf(observerInterfaceQualifiedType))
            .setModifier(CD4CodeMill.modifierBuilder().PROTECTED().build()).build();
        decClazz.addCDMember(observerList);

        glexOpt.ifPresent(glex -> glex.replaceTemplate(VALUE, observerList, new StringHookPoint(
            " = new ArrayList<>()")));
      }

      //create own interface Observable and Observer for every class
      //      ASTCDInterface interfaceObservableArtifact =
      //        CD4CodeMill.cDInterfaceBuilder().setName("I" + decClazz.getName() + "Observable")
      //          .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      ASTCDInterface interfaceObserverArtifact = CD4CodeMill.cDInterfaceBuilder().setName("I"
          + decClazz.getName() + "Observer").setModifier(CD4CodeMill.modifierBuilder().PUBLIC()
              .build()).build();

      interfaceObserverArtifact.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
          .setSuperclassList(List.of(MCTypeFacade.getInstance().createBasicGenericTypeOf(
              "de.monticore.cd.ICDObserver", clazz.getName()))).build());

      //add the interfaces to the package
      //      addElementToParent(decParent, interfaceObservableArtifact);
      addElementToParent(decParent, interfaceObserverArtifact);

      //build the methods
      ASTCDMethod addObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "addObserver", observerParameter);
      ASTCDMethod removeObserver = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "removeObserver", observerParameter);
      ASTCDMethod notifyObservers = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PROTECTED().build(), "notifyObservers");
      ASTCDMethod update = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder()
          .PUBLIC().build(), "notifyUpdate", classParameter);

      //add the methods to the interface Observable
      //      interfaceObservableArtifact.addCDMember(addObserver.deepClone());
      //      interfaceObservableArtifact.addCDMember(removeObserver.deepClone());
      //      interfaceObservableArtifact.addCDMember(notifyObservers.deepClone());

      // add the methods to the interface Observer
      interfaceObserverArtifact.addCDMember(update.deepClone());

      //add the interface methods to the pojo class
      addToClass(decClazz, addObserver);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, addObserver, new TemplateHookPoint(
          "methods.observer.addObserver", OBS_LIST_ATTR, observerInterfaceQualifiedType
              .getMCQualifiedName().getQName())));
      addToClass(decClazz, removeObserver);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, removeObserver,
          new TemplateHookPoint("methods.observer.removeObserver.ftl", OBS_LIST_ATTR,
              observerInterfaceQualifiedType.getMCQualifiedName().getQName())));
      addToClass(decClazz, notifyObservers);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, notifyObservers,
          new TemplateHookPoint("methods.observer.notifyObserver", OBS_LIST_ATTR, observerParameter
              .getMCType().printType())));

      //add an interface list if not present in the clazz
      if (!decClazz.isPresentCDInterfaceUsage()) {
        decClazz.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
      }
      //add the Observable interfaces to the class
      decClazz.getCDInterfaceUsage().addInterface(MCTypeFacade.getInstance()
          .createBasicGenericTypeOf("de.monticore.cd.ICDObservable", observerInterfaceName, clazz
              .getName()));

      // add an import statement for the Observer interface
      //      CD4C.getInstance().addImport(decClazz, observableInterfaceName);
      CD4C.getInstance().addImport(decClazz, observerInterfaceName);

      //To call a generated method whenever an attribute is changed in the pojo class, we need to transform the setters
      // into additionally calling the attribute specific notifyObserver${attributeName} method

      observerInterfaceStack.push(interfaceObserverArtifact);
      this.decParent.push(decClazz);
    }
  }

  @Override
  public void endVisit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      observerInterfaceStack.pop();
      decParent.pop();
    }
  }

  protected Stack<ASTCDInterface> observerInterfaceStack = new Stack<>();
  protected Stack<ASTCDClass> decParent = new Stack<>();

  @Override
  public void visit(ASTCDAttribute attribute) {
    if (!decoratorData.shouldDecorate(this.getClass(), attribute) || observerInterfaceStack
        .isEmpty()) {
      return;
    }

    if (attribute.getName().equals(OBS_LIST_ATTR)) {
      Log.error("0xA1234 The class " + decParent.peek().getName()
          + " already has an attribute with the name observerList");
      return;
    }

    var attrInfo = decoratorData.getAttrHelper().getFromSymTypeExpr(attribute.getSymbol()
        .getType());

    ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("clazz").setMCType(
        MCTypeFacade.getInstance().createQualifiedType(decParent.peek().getName())).build();

    //To call a generated method whenever an attribute is changed in the pojo class, we need to transform the setters
    // into additionally calling the attribute specific notifyObserver${attributeName} method
    {
      //We expect that the SetterDecorator has added a Setter for this attribute to the pojo class
      List<SetterDecorator.MethodInformation> methods = decoratorData.getDecoratorData(
          SetterDecorator.class) != null ? decoratorData.getDecoratorData(SetterDecorator.class)
              .getMethods(attribute) : null;
      if (!(methods == null || methods.isEmpty())) {
        for (SetterDecorator.MethodInformation mi : methods) {
          switch (mi.getKind()) {
            case SET_MANDATORY_OR_OPT:
            case UNSET_OPTIONAL:
              glexOpt.ifPresent(glex -> glex.addBeforeTemplate("Setter:Before", mi.getSetMethod(),
                  new StringHookPoint("var _oldValue = this." + mi.getParamName() + ";")));
              glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", mi.getSetMethod(),
                  new StringHookPoint("this.notifyObserversSet" + StringTransformations.capitalize(
                      mi.getParamName()) + "(_oldValue );\nthis.notifyObservers();")));
              break;
            case ADD:
              if (attrInfo.isOrdered()) {
                glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", mi.getSetMethod(),
                    new StringHookPoint("this.notifyObserversAdd" + StringTransformations
                        .capitalize(mi.getParamName()) + "(index, " + attribute.getName()
                        + " );\nthis.notifyObservers();\n")));
              }
              else {
                glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", mi.getSetMethod(),
                    new StringHookPoint("if(__ret){\n this.notifyObserversAdd"
                        + StringTransformations.capitalize(mi.getParamName()) + "(" + attribute
                            .getName() + " );\nthis.notifyObservers();\n}")));
              }
              break;
            case REM:
              if (attrInfo.isOrdered()) {
                glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", mi.getSetMethod(),
                    new StringHookPoint("this.notifyObserversRemove" + StringTransformations
                        .capitalize(mi.getParamName())
                        + "(index, __ret );\nthis.notifyObservers();\n")));
              }
              else {
                glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", mi.getSetMethod(),
                    new StringHookPoint("if(__ret){\n this.notifyObserversRemove"
                        + StringTransformations.capitalize(mi.getParamName()) + "(" + attribute
                            .getName() + " );\nthis.notifyObservers();\n}")));
              }
              break;
            default:
              Log.warn("0xTODO: Unexpected method kind " + mi.getKind(), attribute
                  .get_SourcePositionStart());
          }
        }
      }
      else {
        Log.warn("0xTODO: No setter found for attribute " + attribute.getName(), attribute
            .get_SourcePositionStart());

      }
    }

    // Add notify methods to domain class & observer interface
    switch (attrInfo.getMultiplicity()) {
      case MANDATORY:
      case OPTIONAL: {
        createObserverMethod(attribute, "Set", List.of(classParameter, CD4CodeMill
            .cDParameterBuilder().setName("ov").setMCType(attribute.getMCType()).build()));
        createNotifyMethod(attribute, "Set", List.of(CD4CodeMill.cDParameterBuilder().setName("ov")
            .setMCType(attribute.getMCType()).build()),
            "methods.observer.notifyObserverAttributeSpecific");
      }
        break;
      case SET:
        var innerType = getCDGenService().getFirstTypeArgument(attribute.getMCType());
        if (attrInfo.isOrdered()) {
          createObserverMethod(attribute, "Add", List.of(classParameter, CDParameterFacade
              .getInstance().createParameter(MCTypeFacade.getInstance().createIntType(), "index"),
              CD4CodeMill.cDParameterBuilder().setName("newElem").setMCType(innerType).build()));
          createNotifyMethod(attribute, "Add", List.of(CDParameterFacade.getInstance()
              .createParameter(MCTypeFacade.getInstance().createIntType(), "index"), CD4CodeMill
                  .cDParameterBuilder().setName("newElem").setMCType(innerType).build()),
              "methods.observer.notifyObserverAttributeSpecificAssoc", "Add", "index, newElem");

          createObserverMethod(attribute, "Remove", List.of(classParameter, CDParameterFacade
              .getInstance().createParameter(MCTypeFacade.getInstance().createIntType(), "index"),
              CD4CodeMill.cDParameterBuilder().setName("elem").setMCType(innerType).build()));
          createNotifyMethod(attribute, "Remove", List.of(CDParameterFacade.getInstance()
              .createParameter(MCTypeFacade.getInstance().createIntType(), "index"), CD4CodeMill
                  .cDParameterBuilder().setName("elem").setMCType(innerType).build()),
              "methods.observer.notifyObserverAttributeSpecificAssoc", "Remove", "index, elem");

        }
        else {
          createObserverMethod(attribute, "Add", List.of(classParameter, CD4CodeMill
              .cDParameterBuilder().setName("newElem").setMCType(innerType).build()));
          createNotifyMethod(attribute, "Add", List.of(CD4CodeMill.cDParameterBuilder().setName(
              "newElem").setMCType(innerType).build()),
              "methods.observer.notifyObserverAttributeSpecificAssoc", "Add", "newElem");

          createObserverMethod(attribute, "Remove", List.of(classParameter, CD4CodeMill
              .cDParameterBuilder().setName("elem").setMCType(innerType).build()));
          createNotifyMethod(attribute, "Remove", List.of(CD4CodeMill.cDParameterBuilder().setName(
              "elem").setMCType(innerType).build()),
              "methods.observer.notifyObserverAttributeSpecificAssoc", "Remove", "elem");
        }
        break;
    }
  }

  protected void createObserverMethod(ASTCDAttribute attribute, String prefix,
      List<ASTCDParameter> params) {
    var notifyUpdateMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder()
        .PUBLIC().build(), "notifyUpdate" + prefix + StringUtils.capitalize(attribute.getName()),
        params);
    observerInterfaceStack.peek().addCDMember(notifyUpdateMethod);
  }

  protected void createNotifyMethod(ASTCDAttribute attribute, String prefix,
      List<ASTCDParameter> params, String template, Object... templateParams) {
    var mObservable = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder()
        .PROTECTED().build(), "notifyObservers" + prefix + StringUtils.capitalize(attribute
            .getName()), params);
    addToClass(decParent.peek(), mObservable);

    Object[] realTemplateParams = new Object[templateParams.length + 3];
    System.arraycopy(templateParams, 0, realTemplateParams, 3, templateParams.length);
    realTemplateParams[0] = OBS_LIST_ATTR;
    realTemplateParams[1] = observerInterfaceStack.peek().getName();
    realTemplateParams[2] = attribute.getName();
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, mObservable, new TemplateHookPoint(
        template, realTemplateParams)));

  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

}
