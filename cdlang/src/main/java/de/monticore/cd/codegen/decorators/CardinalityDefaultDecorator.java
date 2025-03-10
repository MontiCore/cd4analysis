/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Sets an initial value of an empty optional, list or set for suitable elements
 */
public class CardinalityDefaultDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {


  @Override
  public void visit(ASTCDAttribute attribute) {
    // First, check if we should decorate the given object
    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      // Retrieve the parent of the attribute
      var originalClazz = decoratorData.getParent(attribute).get();
      //
      var decClazz = (ASTCDClass) decoratorData.getAsDecorated(originalClazz);
      if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
      } else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
        decorateList(decClazz, attribute);
      } else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
        decorateSet(decClazz, attribute);
      } else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
        decorateOptional(decClazz, attribute);
      }

    }
  }


  protected void decorateOptional(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    getOrCreateDecConstructors(decoratedClazz).forEach(c -> glexOpt.ifPresent(glex ->
      // Note: we only handle EMPTY_BODY here and MIGHT have to add the instantiation to other constructors as well?
      // Idea 1: Default constructor template with hookpoints?
      // Setting the initial value of the attribute fails, as "new"/CreatorExpression is not part of CD4C
      glex.addAfterTemplate(EMPTY_BODY, c,
        new TemplateHookPoint("methods.InstantiationEmptyOptional", attribute.getName()))
    ));
  }

  protected void decorateSet(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    getOrCreateDecConstructors(decoratedClazz).forEach(c -> glexOpt.ifPresent(glex ->
      // Note: we only handle EMPTY_BODY here and MIGHT have to add the instantiation to other constructors as well?
      // Idea 1: Default constructor template with hookpoints?
      // Setting the initial value of the attribute fails, as "new"/CreatorExpression is not part of CD4C
      glex.addAfterTemplate(EMPTY_BODY, c,
        new TemplateHookPoint("methods.Instantiation", attribute.getName(), HashSet.class.getName()))
    ));
  }

  protected void decorateList(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    getOrCreateDecConstructors(decoratedClazz).forEach(c -> glexOpt.ifPresent(glex ->
      // Note: we only handle EMPTY_BODY here and MIGHT have to add the instantiation to other constructors as well?
      // Idea 1: Default constructor template with hookpoints?
      // Setting the initial value of the attribute fails, as "new"/CreatorExpression is not part of CD4C
      glex.addAfterTemplate(EMPTY_BODY, c,
        new TemplateHookPoint("methods.Instantiation", attribute.getName(), ArrayList.class.getName()))
    ));
  }

  protected List<ASTCDConstructor> getOrCreateDecConstructors(ASTCDClass decoratedClazz) {
    List<ASTCDConstructor> constructors = new ArrayList<>(decoratedClazz.getCDConstructorList());
    if (constructors.isEmpty()) {
      var c = CDConstructorFacade.getInstance().createDefaultConstructor(decoratedClazz.getModifier().deepClone(), decoratedClazz);
      addToClass(decoratedClazz, c);
      constructors.add(c);
    }
    return constructors;
  }


  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
