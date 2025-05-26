/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

/** Add special handling to the setters of bidirectional associations */
public class NavigableSetterDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2 {

  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    // We require data of the Setter Decorator
    return Iterables.concat(
        super.getMustRunAfter(), Collections.singletonList(SetterDecorator.class));
  }

  @Override
  public void visit(ASTCDAttribute attribute) {
    if (attribute.getModifier().isDerived()
        || attribute.getModifier().isReadonly()
        || attribute.getModifier().isFinal()) return;

    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      // For every attribute, for which the SetterDecorator has created methods:
      var methods = decoratorData.getDecoratorData(SetterDecorator.class).methods.get(attribute);
      if (methods == null || methods.isEmpty()) return;

      var role = this.decoratorData.fieldToRoles.get(attribute.getSymbol());

      // And for which a role symbol was present (before being transformed away) and which is
      // navigable in both directions
      if (role == null || !role.isIsDefinitiveNavigable()) {
        return;
      }
      if (!role.isPresentAssoc()) {
        // happens in case trafos before STC are skipped
        Log.error("Assoc of role " + role.getName() + " not present",
                  role.getSourcePosition());
        return;
      }
      if (!role.getOtherSide().isIsDefinitiveNavigable()) return;

      var otherClassOrig = (ASTCDClass) role.getOtherSide().getEnclosingScope().getAstNode();
      var otherClassDec = decoratorData.getAsDecorated(otherClassOrig);

      if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
        Log.error(
            "0xCDD60: Unable to have a navigable assoc to a boolean", role.getSourcePosition());
      } else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
        Log.warn("0xTODO: WIP List NavSetter ", role.getSourcePosition());
      } else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
        Log.warn("0xTODO: WIP Set NavSetter ", role.getSourcePosition());
      } else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
        Log.warn("0xTODO: WIP Optional NavSetter", role.getSourcePosition());
      } else {
        // Add set${role}Local method
        decorateMandatoryLocal(otherClassDec, role.getOtherSide());
        // Call ${role}.set${otherRole}Local when updating
        methods.forEach(
            m ->
                glexOpt.ifPresent(
                    g ->
                        g.addAfterTemplate(
                            "methods.Set",
                            m,
                            new TemplateHookPoint(
                                "methods.CallLocal", role.getOtherSide().getName()))));
        // TODO: Unset old?
      }
    }
  }

  protected void decorateMandatoryLocal(ASTCDClass clazz, CDRoleSymbol role) {
    String name =
        "set" + StringUtils.capitalize(StringTransformations.capitalize(role.getName())) + "Local";
    ASTCDMethod method =
        CDMethodFacade.getInstance()
            .createMethod(
                CD4CodeMill.modifierBuilder().PUBLIC().build().deepClone(),
                name,
                CDParameterFacade.getInstance()
                    .createParameter(role.getType().printFullName(), role.getName()));
    glexOpt.ifPresent(
        glex ->
            glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Set", role)));

    addToClass(clazz, method);
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
