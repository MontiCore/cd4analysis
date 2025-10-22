/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.ForwardingTemplateHookPoint;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Add special handling to the setters of bidirectional associations
 * TODO: This decorator requires testing
 * TODO: The previous values of elements are currently not updated
 */
public class NavigableSetterDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    // We require data of the Setter Decorator
    return Iterables.concat(super.getMustRunAfter(), Collections.singletonList(
        SetterDecorator.class));
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    if (attribute.getModifier().isDerived() || attribute.getModifier().isReadonly() || attribute
        .getModifier().isFinal())
      return;
    
    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      // For every attribute, for which the SetterDecorator has created methods:
      var methods = decoratorData.getDecoratorData(SetterDecorator.class).getMethods(attribute);
      if (methods == null || methods.isEmpty())
        return;
      
      var role = this.decoratorData.fieldToRoles.get(attribute.getSymbol());
      
      // And for which a role symbol was present (before being transformed away) and which is
      // navigable in both directions
      if (role == null || !role.isIsDefinitiveNavigable()) {
        return;
      }
      if (!role.isPresentAssoc()) {
        // happens in case trafos before STC are skipped
        Log.error("Assoc of role " + role.getName() + " not present", role.getSourcePosition());
        return;
      }
      if (!role.getOtherSide().isIsDefinitiveNavigable())
        return;
      
      var otherClassOrig = (ASTCDClass) role.getOtherSide().getEnclosingScope().getAstNode();
      var otherClassDec = decoratorData.getAsDecorated(otherClassOrig);
      
      var thisSideAttrInfo = decoratorData.getAttrHelper().getFromRole(role);
      var otherSideAttrInfo = decoratorData.getAttrHelper().getFromRole(role.getOtherSide());
      
      var otherRole = role.getOtherSide();
      
      if (otherSideAttrInfo.getTypeKind() != AttrHelper.TypeKind.DOMAIN) {
        Log.error("0xCDD60: Unable to have a navigable assoc to a type of " + otherSideAttrInfo
            .getSymTypeExpression().print(), role.getSourcePosition());
        return;
      }
      switch (otherSideAttrInfo.getMultiplicity()) {
        case MANDATORY:
          // Add set${role}Local method
          String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(otherRole
              .getName()) + "Local");
          decorate(otherClassDec, otherRole, SetterDecorator.SetterMethodKind.SET_MANDATORY_OR_OPT,
              "methods.Set", name, List.of(CDParameterFacade.getInstance().createParameter(otherRole
                  .getType().printFullName(), otherRole.getName())), otherRole);
          
          callLocal(thisSideAttrInfo, methods, role, "set");
          
          break;
        case OPTIONAL:
          // Add set${role}Local method for opt
          name = "set" + StringUtils.capitalize(StringTransformations.capitalize(otherRole
              .getName()) + "Local");
          decorate(otherClassDec, otherRole, SetterDecorator.SetterMethodKind.SET_MANDATORY_OR_OPT,
              "methods.opt.Set4Opt", name, List.of(CDParameterFacade.getInstance().createParameter(
                  otherRole.getType().printFullName(), otherRole.getName())), otherRole);
          
          callLocal(thisSideAttrInfo, methods, role, "set");
          break;
        case SET:
          // Add set${role}Local method for *
          if (otherSideAttrInfo.isOrdered()) {
            Log.warn("0xTODO: Ordered navigable setters are still WIP", attribute
                .get_SourcePositionStart());
          }
          else {
            name = "add" + StringUtils.capitalize(StringTransformations.capitalize(otherRole
                .getName()) + "Local");
            var m = decorate(otherClassDec, otherRole, SetterDecorator.SetterMethodKind.ADD,
                "methods.list.AddUnordered", name, List.of(CDParameterFacade.getInstance()
                    .createParameter(otherRole.getType().printFullName(), otherRole.getName())),
                otherRole);
            m.getSetMethod().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(
                MCTypeFacade.getInstance().createBooleanType()).build());
            
            name = "remove" + StringUtils.capitalize(StringTransformations.capitalize(otherRole
                .getName()) + "Local");
            m = decorate(otherClassDec, otherRole, SetterDecorator.SetterMethodKind.REM,
                "methods.list.RemUnordered", name, List.of(CDParameterFacade.getInstance()
                    .createParameter(otherRole.getType().printFullName(), otherRole.getName())),
                otherRole);
            m.getSetMethod().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(
                MCTypeFacade.getInstance().createBooleanType()).build());
            
          }
          callLocal(thisSideAttrInfo, methods, role, "add");
          break;
        default:
          Log.error("0xTODO: Unhandled multiplicty " + otherSideAttrInfo.getMultiplicity(),
              attribute.get_SourcePositionStart());
      }
      
    }
  }
  
  protected void callLocal(AttrHelper.AttrData thisSideAttrInfo,
      List<SetterDecorator.MethodInformation> methods, CDRoleSymbol role, String method) {
    if (thisSideAttrInfo.getMultiplicity() == AttrHelper.Multiplicity.MANDATORY) {
      // Call ${role}.set${otherRole}Local when updating
      methods.stream().filter(m -> m.getKind()
          == SetterDecorator.SetterMethodKind.SET_MANDATORY_OR_OPT).forEach(m -> glexOpt.ifPresent(
              g -> g.addAfterTemplate(m.getTemplateName(), m.getSetMethod(), new TemplateHookPoint(
                  "methods.navsetter.CallLocal", role.getOtherSide().getName(), method))));
    }
    else if (thisSideAttrInfo.getMultiplicity() == AttrHelper.Multiplicity.OPTIONAL) {
      // Call ${role}.set${otherRole}Local when updating
      methods.stream().filter(m -> m.getKind()
          == SetterDecorator.SetterMethodKind.SET_MANDATORY_OR_OPT).forEach(m -> glexOpt.ifPresent(
              g -> g.addAfterTemplate(m.getTemplateName(), m.getSetMethod(), new TemplateHookPoint(
                  "methods.navsetter.CallLocalOpt", role.getOtherSide().getName(), method))));
    }
    else if (thisSideAttrInfo.getMultiplicity() == AttrHelper.Multiplicity.SET) {
      methods.stream().filter(m -> m.getKind() == SetterDecorator.SetterMethodKind.ADD).forEach(
          m -> glexOpt.ifPresent(g -> g.addAfterTemplate("Setter:After", m.getSetMethod(),
              new TemplateHookPoint("methods.navsetter.CallLocalAddSet", role, role.getOtherSide()
                  .getName(), method))));
      // Call remove of previous
      methods.stream().filter(m -> m.getKind() == SetterDecorator.SetterMethodKind.REM).forEach(
          m -> glexOpt.ifPresent(g -> g.addAfterTemplate("Setter:After", m.getSetMethod(),
              new TemplateHookPoint("methods.navsetter.CallLocalRemSet", role, role.getOtherSide()
                  .getName(), method))));
    }
    else {
      Log.warn("0xTODO: Unhandled multiplicty" + thisSideAttrInfo.getMultiplicity(), role
          .getSourcePosition());
    }
  }
  
  protected SetterDecorator.MethodInformation decorate(ASTCDClass decParent, CDRoleSymbol role,
      SetterDecorator.SetterMethodKind kind, String templateName, String methodName,
      List<ASTCDParameter> params, Object... templateParams) {
    
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(role.getAssocSide().getModifier()
        .deepClone(), methodName, params);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method,
        new ForwardingTemplateHookPoint(templateName, glex, templateParams)));
    
    addToClass(decParent, method);
    
    return new SetterDecorator.MethodInformation(kind, method, templateName, role.getName());
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
