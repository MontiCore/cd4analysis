/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cd4analysis._parser.CD4AnalysisAntlrParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdgen.decorators.data.AbstractDecorator;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Applies the Builder-Pattern to the CD
 */
public class BuilderDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return List.of(SetterDecorator.class);
  }

  Stack<ASTCDClass> decoratedBuilderClasses = new Stack<>();
  Stack<ASTCDMethod> decoratorBuildMethod = new Stack<>();
  Stack<ASTCDMethod> decoratorUnsafeBuildMethod = new Stack<>();
  Stack<ASTCDMethod> decoratorIsValidMethod = new Stack<>();
  Stack<Boolean> enabled = new Stack<>();

  @Override
  public void visit(ASTCDClass node) {
    // Only act if we should decorate the class
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      // Get the parent (package or CDDef)
      ASTNode origParent = this.decoratorData.getParent(node).get();
      // and the parent, but now the element of the target CD
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);

      // Create a new class with the "Builder" suffix
      ASTCDClassBuilder builderClassB = CD4CodeMill.cDClassBuilder();
      builderClassB.setName(node.getName() + "Builder");
      builderClassB.setModifier(node.getModifier().deepClone());
      ASTCDClass builderClass = builderClassB.build();
      // Add the builder class to the decorated CD
      addElementToParent(decParent, builderClass);

      // Add Log import to the builder class
      CD4C.getInstance().addImport(builderClass, "de.se_rwth.commons.logging.Log");

      // Add attributes to the builder class
      for(ASTCDAttribute attribute : node.getCDAttributeList()) {
        builderClass.addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill.modifierBuilder().PROTECTED().build(), attribute.getMCType(), attribute.getName()));
        attribute.getSymbol().getType();
      }

      // Add a build() method to the builder class
      ASTCDMethod buildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "build");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("methods.builder.build", node.getName(), new ArrayList<>(node.getCDAttributeList()))));
      addToClass(builderClass, buildMethod);
      decoratorBuildMethod.push(buildMethod);

      // Add the unsafeBuild() method to the builder class
      ASTCDMethod unsafeBuildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "unsafeBuild");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, unsafeBuildMethod, new TemplateHookPoint("methods.builder.unsafeBuild", node.getName(), new ArrayList<>(node.getCDAttributeList()))));
      addToClass(builderClass, unsafeBuildMethod);
      decoratorUnsafeBuildMethod.push(unsafeBuildMethod);

      // Add a isValid() method to the builder class
      String staticErrorCode = "0x16725";
      ASTCDMethod isValidMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PRIVATE().build(), MCTypeFacade.getInstance().createBooleanType(), "isValid");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("methods.builder.isValid", new ArrayList<>(node.getCDAttributeList()),staticErrorCode)));
      addToClass(builderClass,isValidMethod);
      decoratorIsValidMethod.push(isValidMethod);

      // Add Setter methods for all attributes to the builder class
      for(ASTCDAttribute attribute : node.getCDAttributeList()) {
        ASTCDParameter param = CD4CodeMill.cDParameterBuilder().setName(attribute.getName()).setMCType(attribute.getMCType()).build();
        ASTCDMethod setMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(),builderClass.getName(), "set" + StringTransformations.capitalize(attribute.getName()), param);
        glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setMethod, new TemplateHookPoint("methods.builder.set", attribute)));
        addToClass(builderClass, setMethod);
      }

      // Add isAbsent methods for all attributes with cardinality != 1
      for(ASTCDAttribute attribute : node.getCDAttributeList()) {
        if(MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()) ||
          MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())||
          MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
          ASTCDMethod setAbsentMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "set" + StringTransformations.capitalize(attribute.getName()) + "Absent");
          glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setAbsentMethod, new TemplateHookPoint("methods.builder.setAbsent", attribute)));
          addToClass(builderClass, setAbsentMethod);
        }
      }


      // Add the builder class to the stack c
      decoratedBuilderClasses.add(builderClass);
      enabled.push(true);
    } else
      enabled.push(false);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      decoratedBuilderClasses.pop();
      decoratorBuildMethod.pop();
      decoratorUnsafeBuildMethod.pop();
      decoratorIsValidMethod.pop();
    }
    enabled.pop();
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
