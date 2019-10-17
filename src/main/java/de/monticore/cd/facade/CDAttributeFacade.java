/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.cd.cd4code._parser.CD4CodeParser;
import de.monticore.cd.facade.exception.CDFactoryErrorCode;
import de.monticore.cd.facade.exception.CDFactoryException;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
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
    } catch (IOException e) {
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
    return CD4CodeMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setMCType(type.deepClone())
        .setName(name)
        .build();
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTMCType type) {
    return createAttribute(modifier, type, StringTransformations.uncapitalize(MCCollectionTypesHelper.printType(type)));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type) {
    return createAttribute(modifier, MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type.getSimpleName()));
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTMCType type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTMCType type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type) {
    return createAttribute(modifier.build(), type);
  }

  /**
    * @deprecated use method isList from MCCollectionTypesHelper, when it exists
   */
  @Deprecated
  private boolean isListType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }
}
