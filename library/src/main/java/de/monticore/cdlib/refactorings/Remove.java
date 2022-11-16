/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.delete.tf.DeleteAttribute;
import de.monticore.cdlib.refactoring.delete.tf.DeleteClass;
import de.monticore.cdlib.refactoring.delete.tf.DeleteMethod;
import de.se_rwth.commons.logging.Log;

/**
 * Remove a class
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class Remove implements Refactoring {
  public Remove() {}

  /**
   * Removes the class and all references
   *
   * @param className - name of the class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean removeClass(String className, ASTCDCompilationUnit ast) {

    // deletes all associations for the class, which should be removed
    transformationUtility.deleteAllAssociations(className, ast);

    // Push down attributes and methods to subclasses
    PushDown pushDown = new PushDown();
    pushDown.pushDown(className, ast);

    // Deletes the class
    DeleteClass deleteClass = new DeleteClass(ast);
    deleteClass.set_$className(className);
    if (deleteClass.doPatternMatching()) {
      deleteClass.doReplacement();
      return true;
    }

    Log.info("0xF4121: Could not remove class " + className, Remove.class.getName());
    return false;
  }

  /**
   * Removes the first occurrence of the method in the class
   *
   * @param className - name of the class
   * @param methodName - name of the method
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean removeMethod(String className, String methodName, ASTCDCompilationUnit ast) {

    // Deletes the methods
    DeleteMethod deleteMethod = new DeleteMethod(ast);
    deleteMethod.set_$className(className);

    if (deleteMethod.doPatternMatching()) {
      deleteMethod.doReplacement();
      return true;
    }

    System.out.println("Could not remove method " + methodName);
    return false;
  }

  /**
   * Removes the first occurrence of the method in the class
   *
   * @param className - name of the class
   * @param attributeName - name of the method
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean removeAttribute(String className, String attributeName, ASTCDCompilationUnit ast) {

    // Delete all the attributes
    DeleteAttribute deleteAttribute = new DeleteAttribute(ast);
    deleteAttribute.set_$className(className);
    deleteAttribute.set_$name(attributeName);

    if (deleteAttribute.doPatternMatching()) {
      deleteAttribute.doReplacement();
      return true;
    } else {
      Log.info("0xF4122: Could not delete attribute" + attributeName, Remove.class.getName());
    }
    return false;
  }
}
