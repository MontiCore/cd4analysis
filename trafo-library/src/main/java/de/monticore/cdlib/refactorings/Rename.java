/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.*;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Rename a class or an attribute
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class Rename implements Refactoring {
  public Rename() {}

  /**
   * Rename a class. Changes all references, attributes and associations containing this class.
   *
   * @param oldName - Old name of the class
   * @param newName - New name of the class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean renameClass(String oldName, String newName, ASTCDCompilationUnit ast) {

    // Rename type in attribute
    transformationUtility.replaceTypeInAttribute(oldName, newName, ast);

    // Replace old Name of the class by new Name of the class
    // in association
    transformationUtility.changeRefNameInAllAssociations(oldName, newName, ast);
    // in inheritance
    transformationUtility.changeInheritanceClass(oldName, newName, ast);

    // Rename concrete class
    RenameClass rename = new RenameClass(ast);
    rename.set_$newClassName(newName);
    rename.set_$oldClass(oldName);

    if (rename.doPatternMatching()) {
      rename.doReplacement();
    } else {
      return false;
    }
    return true;
  }

  /**
   * Rename an attribute. Changes the attributes getter and setter methods.
   *
   * @param oldName - Old name of the attribute
   * @param newName - New name of the attribute
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean renameAttribute(String oldName, String newName, ASTCDCompilationUnit ast) {

    // Rename attribute itself
    RenameAttribute rename = new RenameAttribute(ast);
    rename.set_$newName(newName);
    rename.set_$oldName(oldName);

    if (rename.doPatternMatching()) {

      // Rename Getter und Setters
      if (rename
          .get_$C()
          .getMCType()
          .printType()
          .equals("boolean")) {
        // Rename Getter und Setters for a boolean
        RenameGetterAndSetter renameWith = new RenameGetterAndSetter(ast);
        renameWith.set_$name(oldName);
        renameWith.set_$getOld("is" + de.se_rwth.commons.StringTransformations.capitalize(oldName));
        renameWith.set_$setOld(
            "set" + de.se_rwth.commons.StringTransformations.capitalize(oldName));
        renameWith.set_$getNew("is" + de.se_rwth.commons.StringTransformations.capitalize(newName));
        renameWith.set_$setNew(
            "set" + de.se_rwth.commons.StringTransformations.capitalize(newName));
        if (renameWith.doPatternMatching()) {
          renameWith.doReplacement();
        }
      } else {
        // Rename Getter und Setters for all other types than boolean
        RenameGetterAndSetter renameWith = new RenameGetterAndSetter(ast);
        renameWith.set_$name(oldName);
        renameWith.set_$getOld(
            "get" + de.se_rwth.commons.StringTransformations.capitalize(oldName));
        renameWith.set_$setOld(
            "set" + de.se_rwth.commons.StringTransformations.capitalize(oldName));
        renameWith.set_$getNew(
            "get" + de.se_rwth.commons.StringTransformations.capitalize(newName));
        renameWith.set_$setNew(
            "set" + de.se_rwth.commons.StringTransformations.capitalize(newName));

        if (renameWith.doPatternMatching()) {
          renameWith.doReplacement();
        }
      }

      rename.doReplacement();
    } else {
      return false;
    }
    return true;
  }
}
