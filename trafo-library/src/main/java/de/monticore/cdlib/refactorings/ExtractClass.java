/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Extract Class: Create a new (to a given class) associated class and move methods from given class
 * to it
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ExtractClass implements Refactoring {
  public ExtractClass() {}

  /**
   * Moves the given methods {@code methods} and attributes {@code attributes} from an old class
   * {@code oldClass} to a new class {@code newClass} an creates an association
   *
   * @param oldClass - the old class
   * @param newClass - the new class
   * @param attributes - list of attributes to move
   * @param methods list of methods to move
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractClass(
      String oldClass,
      String newClass,
      List<String> attributes,
      List<String> methods,
      ASTCDCompilationUnit ast) {
    Move move = new Move();
    if (transformationUtility.classIsPresent(oldClass, ast)) {
      if (transformationUtility.createSimpleClass(newClass, ast)) {
        if (transformationUtility.createRightDirAssociation(oldClass, newClass, ast)) {
          if (move.moveAttributes(oldClass, newClass, attributes, ast)) {
            if (move.moveMethods(oldClass, newClass, methods, ast)) {
              return true;
            } else {
              Log.info(
                  "0xF4071: Extract Superclass: Didn't find Methods " + methods.toString(),
                  ExtractClass.class.getName());
            }
          } else {
            Log.info(
                "0xF4072: Extract Superclass: Didn't find Attributes " + attributes.toString(),
                ExtractClass.class.getName());
          }
        }
      }
    }
    return false;
  }
}
