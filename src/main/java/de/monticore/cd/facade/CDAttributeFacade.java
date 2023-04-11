/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;

/** Class that helps with the creation of ASTCDAttributes */
public class CDAttributeFacade {

  private static CDAttributeFacade cdAttributeFacade;

  private CDAttributeFacade() {}

  public static CDAttributeFacade getInstance() {
    if (cdAttributeFacade == null) {
      cdAttributeFacade = new CDAttributeFacade();
    }
    return cdAttributeFacade;
  }

  /** base method for creation of a attribute via builder */
  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final ASTMCType type, final String name) {
    return CD4CodeBasisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .build();
  }

  /** base method for creation of a attribute via builder */
  public ASTCDAttribute createAttribute(
      final ASTModifier modifier,
      final ASTMCType type,
      final String name,
      final ASTExpression initial) {
    return CD4CodeBasisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .setInitial(initial)
        .build();
  }

  /** delegation methods for a more comfortable usage */
  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type) {
    return createAttribute(modifier, type, StringTransformations.uncapitalize(type.printType()));
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final ASTMCType type, final ASTExpression initial) {
    return createAttribute(
        modifier, type, StringTransformations.uncapitalize(type.printType()), initial);
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final String type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier,
      final String type,
      final String name,
      final ASTExpression initial) {
    return createAttribute(
        modifier, MCTypeFacade.getInstance().createQualifiedType(type), name, initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type) {
    return createAttribute(
        modifier,
        MCTypeFacade.getInstance().createQualifiedType(type),
        StringTransformations.uncapitalize(type));
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final String type, final ASTExpression initial) {
    return createAttribute(
        modifier,
        MCTypeFacade.getInstance().createQualifiedType(type),
        StringTransformations.uncapitalize(type),
        initial);
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier,
      final Class<?> type,
      final String name,
      final ASTExpression initial) {
    return createAttribute(
        modifier, MCTypeFacade.getInstance().createQualifiedType(type), name, initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type) {
    return createAttribute(
        modifier,
        MCTypeFacade.getInstance().createQualifiedType(type),
        StringTransformations.uncapitalize(type.getSimpleName()));
  }

  public ASTCDAttribute createAttribute(
      final ASTModifier modifier, final Class<?> type, final ASTExpression initial) {
    return createAttribute(
        modifier,
        MCTypeFacade.getInstance().createQualifiedType(type),
        StringTransformations.uncapitalize(type.getSimpleName()),
        initial);
  }
}
