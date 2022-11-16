/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.pushdown.attribute.tf.CopyAttributeToSubclass;
import de.monticore.cdlib.refactoring.pushdown.attribute.tf.DeleteAttributeInSuperclass;
import de.monticore.cdlib.refactoring.pushdown.method.tf.CopyMethodToSubclass;
import de.monticore.cdlib.refactoring.pushdown.method.tf.DeleteMethodInSuperclass;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Push down: Push down methods and/or attributes from a superclass to given subclasses
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class PushDown implements Refactoring {
  public PushDown() {}

  /**
   * Push down all attributes of the super class {@code superClassName} to all given subClasses
   *
   * @param superClassName - name of the super class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDown(String superClassName, ASTCDCompilationUnit ast) {
    boolean success = false;

    if (pushDownAllAttributes(superClassName, ast)) {
      success = true;
    }
    if (pushDownAllMethods(superClassName, ast)) {
      return true;
    }
    return success;
  }

  /**
   * Push down all attributes
   *
   * @param superClassName - name of the super class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownAllAttributes(String superClassName, ASTCDCompilationUnit ast) {
    boolean sucess = false;
    // Copy attribute to all subclasses
    CopyAttributeToSubclass copyAttribute = new CopyAttributeToSubclass(ast);
    copyAttribute.set_$superclass(superClassName);
    while (copyAttribute.doPatternMatching()) {
      copyAttribute.doReplacement();
      copyAttribute = new CopyAttributeToSubclass(ast);
      copyAttribute.set_$superclass(superClassName);
      sucess = true;
    }
    // delete attribute in superclass
    DeleteAttributeInSuperclass deleteAttribute = new DeleteAttributeInSuperclass(ast);
    deleteAttribute.set_$superclass(superClassName);
    if (deleteAttribute.doPatternMatching()) {
      deleteAttribute.doReplacement();
    } else {
      return false;
    }

    return sucess;
  }

  /**
   * Push down given attributes {@code attributes} to subclasses
   *
   * @param superClassName - name of the super class
   * @param attributes - list of attributes
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownAttributes(
      String superClassName, List<String> attributes, ASTCDCompilationUnit ast) {

    for (int i = 0; i < attributes.size(); i++) {
      // Copy attribute to all subclasses
      CopyAttributeToSubclass copyAttribute = new CopyAttributeToSubclass(ast);
      copyAttribute.set_$superclass(superClassName);
      copyAttribute.set_$name(attributes.get(i));
      while (copyAttribute.doPatternMatching()) {
        copyAttribute.doReplacement();
        copyAttribute = new CopyAttributeToSubclass(ast);
        copyAttribute.set_$superclass(superClassName);
        copyAttribute.set_$name(attributes.get(i));
      }

      // delete attribute in superclass
      DeleteAttributeInSuperclass deleteAttribute = new DeleteAttributeInSuperclass(ast);
      deleteAttribute.set_$superclass(superClassName);
      deleteAttribute.set_$name(attributes.get(i));
      if (deleteAttribute.doPatternMatching()) {
        deleteAttribute.doReplacement();
      } else {
        Log.info(
            "0xF4111: Error by deleting attribute " + attributes.get(i), PushDown.class.getName());
        return false;
      }
    }
    return true;
  }

  /**
   * Push down given attributes {@code attributes} of a given class {@code superClassName} to the
   * given subclasses {@code subClasses}
   *
   * @param superClassName - name of the super class
   * @param subClasses - list of sub classes
   * @param attributes - list of attributes
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownAttributes(
      String superClassName,
      List<String> subClasses,
      List<String> attributes,
      ASTCDCompilationUnit ast) {

    for (int j = 0; j < attributes.size(); j++) {
      // Add attribute in subclasses
      for (int i = 0; i < subClasses.size(); i++) {
        CopyAttributeToSubclass copyAttribute = new CopyAttributeToSubclass(ast);
        copyAttribute.set_$superclass(superClassName);
        copyAttribute.set_$className(subClasses.get(i));
        copyAttribute.set_$name(attributes.get(j));
        if (copyAttribute.doPatternMatching()) {
          copyAttribute.doReplacement();
        } else {
          Log.info(
              "0xF4112: Error by adding attribute "
                  + attributes.get(j)
                  + " to class "
                  + subClasses.get(i),
              PushDown.class.getName());
          return false;
        }
      }

      // Remove attribute in Superclass
      DeleteAttributeInSuperclass removeAttribute = new DeleteAttributeInSuperclass(ast);
      removeAttribute.set_$name(attributes.get(j));
      removeAttribute.set_$superclass(superClassName);
      if (removeAttribute.doPatternMatching()) {
        removeAttribute.doReplacement();
      } else {
        Log.info(
            "0xF4113: Error by removing attribute " + attributes.get(j) + " after pushing down.",
            PushDown.class.getName());
        return false;
      }
    }

    return true;
  }

  /**
   * Push down all methods
   *
   * @param superClassName - name of the super class
   * @param ast class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownAllMethods(String superClassName, ASTCDCompilationUnit ast) {
    boolean sucess = false;
    // Copy attribute to all subclasses
    CopyMethodToSubclass copyMethod = new CopyMethodToSubclass(ast);
    copyMethod.set_$superclass(superClassName);
    while (copyMethod.doPatternMatching()) {
      copyMethod.doReplacement();
      copyMethod = new CopyMethodToSubclass(ast);
      copyMethod.set_$superclass(superClassName);
      sucess = true;
    }
    // delete attribute in superclass
    DeleteMethodInSuperclass deleteMethod = new DeleteMethodInSuperclass(ast);
    deleteMethod.set_$superclass(superClassName);
    if (deleteMethod.doPatternMatching()) {
      deleteMethod.doReplacement();
    } else {
      return false;
    }

    return sucess;
  }

  /**
   * Push down given methods {@code methods} to subclasses {@code superClassName}
   *
   * @param superClassName - name of the super class
   * @param methods - list of methods
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownMethods(
      String superClassName, List<String> methods, ASTCDCompilationUnit ast) {

    for (int i = 0; i < methods.size(); i++) {
      // Copy attribute to all subclasses
      CopyMethodToSubclass copyMethod = new CopyMethodToSubclass(ast);
      copyMethod.set_$superclass(superClassName);
      copyMethod.set_$name(methods.get(i));
      while (copyMethod.doPatternMatching()) {
        copyMethod.doReplacement();
        copyMethod = new CopyMethodToSubclass(ast);
        copyMethod.set_$superclass(superClassName);
        copyMethod.set_$name(methods.get(i));
      }

      // delete attribute in superclass
      DeleteMethodInSuperclass deleteMethod = new DeleteMethodInSuperclass(ast);
      deleteMethod.set_$superclass(superClassName);
      deleteMethod.set_$name(methods.get(i));
      if (deleteMethod.doPatternMatching()) {
        deleteMethod.doReplacement();
      } else {
        Log.info(
            "0xF4114: Error by deleting attribute " + methods.get(i), PushDown.class.getName());
        return false;
      }
    }
    return true;
  }

  /**
   * Push down given methods {@code methods} of a given class {@code superClassName} to the given
   * subclasses {@code subClasses}
   *
   * @param superClassName - name of the super class
   * @param subClasses - list of subclasses
   * @param methods - list of methods
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean pushDownMethods(
      String superClassName,
      List<String> subClasses,
      List<String> methods,
      ASTCDCompilationUnit ast) {

    for (int j = 0; j < methods.size(); j++) {
      // Add attribute in subclasses
      for (int i = 0; i < subClasses.size(); i++) {
        CopyMethodToSubclass copyMethod = new CopyMethodToSubclass(ast);
        copyMethod.set_$superclass(superClassName);
        copyMethod.set_$className(subClasses.get(i));
        copyMethod.set_$name(methods.get(j));
        if (copyMethod.doPatternMatching()) {
          copyMethod.doReplacement();
        } else {
          Log.info(
              "0xF4115: Error by adding attribute "
                  + methods.get(j)
                  + " to class "
                  + subClasses.get(i),
              PushDown.class.getName());
          return false;
        }
      }

      // Remove attribute in Superclass
      DeleteMethodInSuperclass removeMethod = new DeleteMethodInSuperclass(ast);
      removeMethod.set_$name(methods.get(j));
      removeMethod.set_$superclass(superClassName);
      if (removeMethod.doPatternMatching()) {
        removeMethod.doReplacement();
      } else {
        Log.info(
            "0xF4116: Error by removing attribute " + methods.get(j) + " after pushing down.",
            PushDown.class.getName());
        return false;
      }
    }

    return true;
  }
}
