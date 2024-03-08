/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.*;

/**
 * Replace an association by an attribute
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ReplaceDelegationByAttribute implements Refactoring {
  public ReplaceDelegationByAttribute() {}

  /**
   * Replace an association (delegation) by an attribute
   *
   * @param className - name of the class
   * @param classToAttribute - name of the class which should be replaced by an attribute
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean replaceAssociationByAttribute(
      String className, String classToAttribute, ASTCDCompilationUnit ast) {

    ReplaceDelegationByAttributeTransformation replace =
        new ReplaceDelegationByAttributeTransformation(ast);
    replace.set_$attrName(de.se_rwth.commons.StringTransformations.uncapitalize(classToAttribute));
    replace.set_$attrTyp(classToAttribute);
    replace.set_$classToAttrName(classToAttribute);
    replace.set_$superclassName(className);
    if (replace.doPatternMatching()) {
      replace.doReplacement();
      return true;
    }
    return false;
  }
}
