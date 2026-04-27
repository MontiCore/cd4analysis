/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Applies the Visitor-Pattern to the CD
 */
public class VisitorImplementationDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2 {
  
  protected GetterDecorator.GetterData getterData;
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Getter for an attribute,
    // thus the Getter decorator has to run before.
    return Iterables.concat(super.getMustRunAfter(), List.of(VisitorDecorator.class));
  }
  
  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    super.init(util, glexOpt);
    
    // Pre-fetch the data of the getter decorator
    this.getterData = util.getDecoratorData(GetterDecorator.class);
  }
  
  @Override
  public void visit(ASTCDDefinition clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      // Get the parent (package or CDDef)
      ASTNode origParent = this.decoratorData.getParent(clazz).get();
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);
      
      var visitorImplementationClass = CD4CodeMill.cDClassBuilder().setName(clazz.getName()
          + "VisitorImplementation").setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build())
          .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(MCTypeFacade
              .getInstance().createQualifiedType("I" + clazz.getName() + "Visitor")).build())
          .build();
      
      addElementToParent(decParent, visitorImplementationClass);
      
      visitorImplementationStack.push(visitorImplementationClass);
      
      ASTCDAttribute traversedElements = CDAttributeFacade.getInstance().createAttribute(CD4CodeMill
          .modifierBuilder().PROTECTED().build(), MCTypeFacade.getInstance().createCollectionTypeOf(
              "Object"), "traversedElements");
      glexOpt.ifPresent(glex -> glex.replaceTemplate("cd2java.Value", traversedElements,
          new StringHookPoint("= new java.util.LinkedHashSet<>()")));
      addToClass(visitorImplementationClass, traversedElements);
    }
  }
  
  @Override
  public void endVisit(ASTCDDefinition clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      visitorImplementationStack.pop();
    }
  }
  
  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);
      ASTMCType classType = MCTypeFacade.getInstance().createQualifiedType(clazz.getName());
      ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("node").setMCType(
          classType).build();
      
      @Nullable
      String parentClass;
      if (clazz.isPresentCDExtendUsage()) {
        parentClass = clazz.getCDExtendUsage().getSuperclass(0).printType();
      }
      else {
        parentClass = null;
      }
      
      this.decParent.push(decClazz);
      
      // add visit method
      ASTCDMethod visitMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "visit", classParameter);
      visitorImplementationStack.peek().addCDMember(visitMethod);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
          "methods.visitor.DefaultVisit", parentClass)));
      
      visitMethodStack.push(visitMethod);
    }
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    if (!decoratorData.shouldDecorate(this.getClass(), attribute) || visitorImplementationStack
        .isEmpty()) {
      return;
    }
    
    var attrInfo = decoratorData.getAttrHelper().getFromSymTypeExpr(attribute.getSymbol()
        .getType());
    
    if (attrInfo.getTypeKind() != AttrHelper.TypeKind.DOMAIN) {
      return;
    }
    
    // node.getChild().accept(this);
    
    if (attrInfo.getMultiplicity() == AttrHelper.Multiplicity.MANDATORY) {
      var getter = this.getterData.getMethods(attribute).stream().filter(m -> m.getKind()
          == GetterDecorator.GetterMethodKind.GET_MANDATORY_OR_OPT).findAny();
      if (getter.isEmpty()) {
        warnMissingGetter(attribute);
      }
      else {
        glexOpt.ifPresent(glex -> glex.addAfterTemplate("VisitorImplementation:Traverse",
            visitMethodStack.peek(), new TemplateHookPoint("methods.visitor.DefaultTraverseMan",
                getter.get().getGetMethod().getName(), visitorImplementationStack.peek()
                    .getName())));
      }
    }
    else if (attrInfo.getMultiplicity() == AttrHelper.Multiplicity.OPTIONAL) {
      var getter = this.getterData.getMethods(attribute).stream().filter(m -> m.getKind()
          == GetterDecorator.GetterMethodKind.GET_MANDATORY_OR_OPT).findAny();
      var isPresent = this.getterData.getMethods(attribute).stream().filter(m -> m.getKind()
          == GetterDecorator.GetterMethodKind.IS_PRESENT).findAny();
      if (getter.isEmpty() || isPresent.isEmpty()) {
        warnMissingGetter(attribute);
      }
      else {
        glexOpt.ifPresent(glex -> glex.addAfterTemplate("VisitorImplementation:Traverse",
            visitMethodStack.peek(), new TemplateHookPoint("methods.visitor.DefaultTraverseOpt",
                getter.get().getGetMethod().getName(), isPresent.get().getGetMethod().getName(),
                visitorImplementationStack.peek().getName())));
      }
    }
    else if (attrInfo.getMultiplicity() == AttrHelper.Multiplicity.SET) {
      var getter = this.getterData.getMethods(attribute).stream().filter(m -> m.getKind()
          == GetterDecorator.GetterMethodKind.GET_COLLECTION).findAny();
      if (getter.isEmpty()) {
        warnMissingGetter(attribute);
      }
      else {
        glexOpt.ifPresent(glex -> glex.addAfterTemplate("VisitorImplementation:Traverse",
            visitMethodStack.peek(), new TemplateHookPoint("methods.visitor.DefaultTraverseSet",
                getter.get().getGetMethod().getName(), visitorImplementationStack.peek()
                    .getName())));
      }
    }
    
  }
  
  protected void warnMissingGetter(ASTCDAttribute attribute) {
    String message = "Attribute `" + attribute.getSymbol().getFullName()
        + "` has no getter information provided. Unable to include in " + visitorImplementationStack
            .peek().getName();
    Log.warn("0xTODO: " + message, attribute.get_SourcePositionStart(), attribute
        .get_SourcePositionEnd());
    glexOpt.ifPresent(glex -> glex.addAfterTemplate("VisitorImplementation:Traverse",
        visitMethodStack.peek(), new StringHookPoint("//" + message + "\n")));
  }
  
  @Override
  public void endVisit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      decParent.pop();
      visitMethodStack.pop();
    }
  }
  
  protected Stack<ASTCDClass> visitorImplementationStack = new Stack<>();
  protected Stack<ASTCDMethod> visitMethodStack = new Stack<>();
  protected Stack<ASTCDClass> decParent = new Stack<>();
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
