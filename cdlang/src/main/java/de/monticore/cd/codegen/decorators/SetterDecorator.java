/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class SetterDecorator extends AbstractDecorator<SetterDecorator.SetterData> implements
    CDBasisVisitor2 {
  
  public static final String AFTER_SETTER_BODY = "Setter:After";
  
  protected SetterData setterData;
  
  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    super.init(util, glexOpt);
    this.setterData = this.decoratorData.createDataIfAbsent(this.getClass(), SetterData::new);
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    // Skip derived, readonly or final attributes
    if (attribute.getModifier().isDerived() || attribute.getModifier().isReadonly() || attribute
        .getModifier().isFinal()) {
      return;
    }
    
    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      var originalClazz = decoratorData.getParent(attribute);
      var decClazz = (ASTCDClass) decoratorData.getAsDecorated(originalClazz.get());
      
      var info = decoratorData.getAttrHelper().getFromSymTypeExpr(attribute.getSymbol().getType());
      
      switch (info.getMultiplicity()) {
        case OPTIONAL:
          this.setterData.getOrCreateMethods(attribute).add(decorateOptionalAbsent(decClazz,
              attribute));
          this.setterData.getOrCreateMethods(attribute).add(decorateOptSet(decClazz, attribute));
          break;
        case MANDATORY:
          this.setterData.getOrCreateMethods(attribute).add(decorateMandatory(decClazz, attribute));
          break;
        case SET:
          if (info.isOrdered()) {
            this.setterData.getOrCreateMethods(attribute).add(decorateAddWithIndex(decClazz,
                attribute));
            this.setterData.getOrCreateMethods(attribute).add(decorateRemoveWithIndex(decClazz,
                attribute));
          }
          else {
            this.setterData.getOrCreateMethods(attribute).add(decorateAddUnordered(decClazz,
                attribute));
            this.setterData.getOrCreateMethods(attribute).add(decorateRemoveUnordered(decClazz,
                attribute));
          }
          
      }
      updateModifier(attribute);
    }
  }
  
  protected MethodInformation decorateMandatory(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    return decorate(clazz, attribute, SetterMethodKind.SET_MANDATORY_OR_OPT, "methods.Set", name,
        CDParameterFacade.getInstance().createParameters(attribute), attribute);
  }
  
  protected MethodInformation decorateOptSet(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    return decorate(clazz, attribute, SetterMethodKind.SET_MANDATORY_OR_OPT, "methods.opt.Set4Opt",
        name, List.of(CDParameterFacade.getInstance().createParameter(type, attribute.getName())),
        attribute, "--unused--");
  }
  
  protected MethodInformation decorateOptionalAbsent(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName())) + "Absent";
    return decorate(clazz, attribute, SetterMethodKind.UNSET_OPTIONAL, "methods.opt.SetAbsent",
        name, List.of(), attribute);
  }
  
  protected MethodInformation decorateAddWithIndex(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "add" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    return decorate(clazz, attribute, SetterMethodKind.ADD, "methods.list.Add", name, List.of(
        CDParameterFacade.getInstance().createParameter(int.class, "index"), CDParameterFacade
            .getInstance().createParameter(type, attribute.getName())), attribute);
  }
  
  protected MethodInformation decorateRemoveWithIndex(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "remove" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    var m = decorate(clazz, attribute, SetterMethodKind.REM, "methods.list.Rem", name, List.of(
        CDParameterFacade.getInstance().createParameter(int.class, "index")), attribute);
    m.getSetMethod().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type)
        .build());
    return m;
  }
  
  protected MethodInformation decorateAddUnordered(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "add" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    var m = decorate(clazz, attribute, SetterMethodKind.ADD, "methods.list.AddUnordered", name, List
        .of(CDParameterFacade.getInstance().createParameter(type, attribute.getName())), attribute);
    m.getSetMethod().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(MCTypeFacade
        .getInstance().createBooleanType()).build());
    return m;
  }
  
  protected MethodInformation decorateRemoveUnordered(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "remove" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    var m = decorate(clazz, attribute, SetterMethodKind.REM, "methods.list.RemUnordered", name, List
        .of(CDParameterFacade.getInstance().createParameter(type, attribute.getName())), attribute);
    m.getSetMethod().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(MCTypeFacade
        .getInstance().createBooleanType()).build());
    return m;
  }
  
  protected MethodInformation decorate(ASTCDClass decParent, ASTCDAttribute attribute,
      SetterMethodKind kind, String templateName, String methodName, List<ASTCDParameter> params,
      Object... templateParams) {
    
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), methodName, params);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        templateName, templateParams)));
    
    addToClass(decParent, method);
    
    return new MethodInformation(kind, method, templateName, attribute.getName());
    
  }
  
  public SetterData getData() {
    return (SetterData) decoratorData.decoratorDataMap.computeIfAbsent(SetterDecorator.class,
        aClass -> new SetterData());
  }
  
  protected void updateModifier(ASTCDAttribute attribute) {
    var decoratedModifier = decoratorData.getAsDecorated(attribute).getModifier();
    decoratedModifier.setProtected(true);
    decoratedModifier.setPublic(false);
    decoratedModifier.setPrivate(false);
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
  public static class SetterData {
    
    @Deprecated
    Map<ASTCDAttribute, List<ASTCDMethod>> methods = new HashMap<>();
    protected final Map<ASTCDAttribute, List<MethodInformation>> attributesData = new HashMap<>();
    
    public List<MethodInformation> getMethods(ASTCDAttribute node) {
      var ret = attributesData.get(node);
      if (ret == null) {
        Log.warn("Requested setter of " + node.getSymbol().getFullName()
            + ", which has no setters!", node.get_SourcePositionStart());
      }
      return ret == null ? List.of() : ret;
    }
    
    protected List<MethodInformation> getOrCreateMethods(ASTCDAttribute node) {
      return this.attributesData.computeIfAbsent(node, a -> new ArrayList<>());
    }
    
  }
  
  public static class MethodInformation {
    
    private final SetterMethodKind kind;
    private final ASTCDMethod setMethod;
    private final String templateName;
    private final String paramName;
    
    public MethodInformation(SetterMethodKind kind, ASTCDMethod setMethod, String templateName,
        String paramName) {
      this.kind = kind;
      this.setMethod = setMethod;
      this.templateName = templateName;
      this.paramName = paramName;
    }
    
    public SetterMethodKind getKind() { return kind; }
    
    public ASTCDMethod getSetMethod() { return setMethod; }
    
    public String getTemplateName() { return templateName; }
    
    public String getParamName() { return paramName; }
    
  }
  
  public enum SetterMethodKind {
    SET_MANDATORY_OR_OPT, UNSET_OPTIONAL, ADD, REM
  }
  
}
