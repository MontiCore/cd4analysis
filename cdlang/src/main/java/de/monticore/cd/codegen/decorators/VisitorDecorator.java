/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgumentBuilder;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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
  public void visit(ASTCDDefinition node) {
    _definition = node;
    if (decoratorData.shouldDecorate(this.getClass(), node)) {
      // The definition is explicitly marked as should-add the visitor
      getVisitorInterface();
    }
  }
  
  @Nullable
  ASTCDInterface _interfaceVisitorArtifact;
  @Nullable
  protected ASTCDDefinition _definition;
  
  protected ASTCDInterface getVisitorInterface() {
    if (this._interfaceVisitorArtifact != null) {
      return this._interfaceVisitorArtifact;
    }
    // Get the parent (package or CDDef)
    ASTNode origParent = this.decoratorData.getParent(Objects.requireNonNull(_definition)).get();
    ASTNode decParent = this.decoratorData.getAsDecorated(origParent);
    
    // create a Visitor interface for the class
    _interfaceVisitorArtifact = CD4CodeMill.cDInterfaceBuilder().setName("I" + _definition.getName()
        + SUFFIX).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    
    addElementToParent(decParent, _interfaceVisitorArtifact);
    
    return this._interfaceVisitorArtifact;
  }
  
  @Override
  public void endVisit(ASTCDDefinition clazz) {
    this._definition = null;
    this._interfaceVisitorArtifact = null;
  }
  
  static ASTMCTypeArgument typeParamToArg(ASTTypeParameter parameter) {
    ASTMCWildcardTypeArgumentBuilder wildcardTypeArgumentBuilder = CD4CodeMill
        .mCWildcardTypeArgumentBuilder();
    if (!parameter.getMCTypeList().isEmpty()) {
      // without extends
      Log.error("0xTODO: NYI multiple extends of ASTTypeParameter " + parameter, parameter
          .get_SourcePositionStart(), parameter.get_SourcePositionEnd());
    }
    return wildcardTypeArgumentBuilder.build();
  }
  
  @Override
  public void visit(ASTCDType type) {
    if (decoratorData.shouldDecorate(this.getClass(), type)) {
      ASTCDType decClazz = decoratorData.getAsDecorated(type);
      String packageName = type.getSymbol().getPackageName();
      
      ASTCDInterface visitorInterface = getVisitorInterface();
      
      String visitorInterfaceName = packageName.isEmpty() ? visitorInterface.getName() : packageName
          + "." + visitorInterface.getName();
      
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceName);
      ASTCDParameter visitorParameter = CD4CodeMill.cDParameterBuilder().setName("visitor")
          .setMCType(visitorInterfaceQualifiedType).build();
      //create a type of the class
      ASTMCType classType = MCTypeFacade.getInstance().createQualifiedType(type.getName());
      
      // Workaround to add <?> type paras
      if (type instanceof ASTCDClass typeOfClass) {
        if (typeOfClass.isPresentTypeParameters() && !typeOfClass.getTypeParameters()
            .getTypeParameterList().isEmpty()) {
          classType = CD4CodeMill.mCBasicGenericTypeBuilder().addName(type.getName())
              .addAllMCTypeArguments(typeOfClass.getTypeParameters().getTypeParameterList().stream()
                  .map(VisitorDecorator::typeParamToArg).collect(Collectors.toList())).build();
        }
      }
      else if (type instanceof ASTCDInterface typeOfInterface) {
        if (typeOfInterface.isPresentTypeParameters() && !typeOfInterface.getTypeParameters()
            .getTypeParameterList().isEmpty()) {
          classType = CD4CodeMill.mCBasicGenericTypeBuilder().addName(type.getName())
              .addAllMCTypeArguments(typeOfInterface.getTypeParameters().getTypeParameterList()
                  .stream().map(VisitorDecorator::typeParamToArg).collect(Collectors.toList()))
              .build();
        }
      }
      
      ASTCDParameter classParameter = CD4CodeMill.cDParameterBuilder().setName("node").setMCType(
          classType).build();
      
      // construct visitor handling methods
      ASTCDMethod acceptMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "accept", visitorParameter);
      
      // add the interface methods to the pojo class
      addToClass(decClazz, acceptMethod);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint("methods.visitor.Accept", type.getName())));
      
      CD4C.getInstance().addImport(decClazz, visitorInterfaceName);
      this.decParent.push(decClazz);
      
      // add visit method
      ASTCDMethod visitMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().ABSTRACT().build(), "visit", classParameter);
      visitorInterface.addCDMember(visitMethod);
    }
  }
  
  @Override
  public void endVisit(ASTCDType clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      decParent.pop();
    }
  }
  
  protected Stack<ASTCDType> decParent = new Stack<>();
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
