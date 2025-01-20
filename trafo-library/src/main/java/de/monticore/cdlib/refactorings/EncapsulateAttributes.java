/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.EncapsulateAttribute;
import de.monticore.cdlib.Refactoring.EncapsulateAttributeBoolean;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Encapsulate attributes : Make public attributes private and add getter and setter methods
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class EncapsulateAttributes implements Refactoring {
  public EncapsulateAttributes() {}

  /**
   * Makes all public attributes private and adds getter and setter methods
   *
   * @param ast the class diagram to be transformed
   * @return true
   */
  public boolean encapsulateAttributes(ASTCDCompilationUnit ast) {
    EncapsulateAttribute encapsulateAttribute = new EncapsulateAttribute(ast);
    while (encapsulateAttribute.doPatternMatching()) {
      encapsulateAttribute.doReplacement();
      encapsulateAttribute = new EncapsulateAttribute(ast);
    }
    EncapsulateAttributeBoolean encapsulateBoolean = new EncapsulateAttributeBoolean(ast);
    if (encapsulateBoolean.doPatternMatching()) {
      encapsulateBoolean.doReplacement();
    }

    return true;
  }

  /**
   * Makes the given attributes {@code attributes} private and adds getter and setter methods
   *
   * @param attributes - list of attributes to be transformed
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean encapsulateAttributes(List<String> attributes, ASTCDCompilationUnit ast) {
    for (String attribute : attributes) {
      EncapsulateAttribute encapsulateAttribute = new EncapsulateAttribute(ast);
      encapsulateAttribute.set_$attrname(attribute);
      if (encapsulateAttribute.doPatternMatching()) {
        encapsulateAttribute.doReplacement();
      } else {
        EncapsulateAttributeBoolean encapsulateBoolean = new EncapsulateAttributeBoolean(ast);
        encapsulateBoolean.set_$attrname(attribute);
        if (encapsulateBoolean.doPatternMatching()) {
          encapsulateBoolean.doReplacement();
        } else {
          Log.info(
            "0xF4061:Could not find attribute " + attribute,
            EncapsulateAttributes.class.getName());
          return false;
        }
      }
    }
    return true;
  }
}
