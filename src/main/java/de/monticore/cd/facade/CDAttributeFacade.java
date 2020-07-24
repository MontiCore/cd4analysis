/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cd.facade;

import de.monticore.cd.facade.exception.CDFactoryErrorCode;
import de.monticore.cd.facade.exception.CDFactoryException;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

/**
 * Class that helps with the creation of ASTCDAttributes
 */

public class CDAttributeFacade {

  private static CDAttributeFacade cdAttributeFacade;

  private final CD4CodeParser parser;

  private CDAttributeFacade() {
    this.parser = new CD4CodeParser();
  }

  public static CDAttributeFacade getInstance() {
    if (cdAttributeFacade == null) {
      cdAttributeFacade = new CDAttributeFacade();
    }
    return cdAttributeFacade;
  }

  /**
   * creates a attribute by a string definition with the help of the parser
   * only use this method if no of the other methods fit your context !
   */

  public ASTCDAttribute createAttributeByDefinition(final String signature) {
    Optional<ASTCDAttribute> attribute;
    try {
      attribute = parser.parseCDAttribute(new StringReader(signature));
    }
    catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature, e);
    }
    if (!attribute.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature);
    }
    return attribute.get();
  }

  /**
   * base method for creation of a attribute via builder
   */

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type, final String name) {
    return CD4CodeBasisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .build();
  }

  /**
   * base method for creation of a attribute via builder
   */

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type, final String name, final ASTExpression initial) {
    return CD4CodeBasisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .setInitial(initial)
        .build();
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type) {
    return createAttribute(modifier, type, StringTransformations.uncapitalize(type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()))));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type, final ASTExpression initial) {
    return createAttribute(modifier, type, StringTransformations.uncapitalize(type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()))), initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final String name, final ASTExpression initial) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name, initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final ASTExpression initial) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type), initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final String name, final ASTExpression initial) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name, initial);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type.getSimpleName()));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final ASTExpression initial) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type.getSimpleName()), initial);
  }
}
