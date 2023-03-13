/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.collapsehierarchy.attribute.tf.CollapseHierarchyAttribute;
import de.monticore.cdlib.refactoring.collapsehierarchy.method.tf.CollapseHierarchyMethod;
import de.monticore.cdlib.refactoring.collapsehierarchy.tf.DeleteInheritance;
import de.monticore.cdlib.refactoring.collapsehierarchy.tf.DeleteSuperclass;

/**
 * Collapse Hierarchy: Move all methods and attributes from a superclass to subclasses
 *
 * @author Philipp Nolte
 * @montitoolbox
 */
public class CollapseHierarchy implements Refactoring {

  public CollapseHierarchy() {}

  /**
   * Move all attributes and methods to all subclasses and delete superclass
   *
   * @param classname the class name of the superclass, which should be collapsed
   * @param ast the AST on where the refactoring should be made
   * @return True if the refactoring was successful
   */
  public boolean collapseHierarchy(String classname, ASTCDCompilationUnit ast) {
    boolean success = false;

    if (collapseHierarchyAttribute(classname, ast)) {
      while (collapseHierarchyAttribute(classname, ast))
        ;
      success = true;
    }

    if (collapseHierarchyMethod(classname, ast)) {
      while (collapseHierarchyMethod(classname, ast))
        ;
      success = true;
    }

    if (deleteInheritance(classname, ast)) {
      while (deleteInheritance(classname, ast))
        ;
      success = true;
    }

    if (deleteSuperclass(classname, ast)) {
      while (deleteSuperclass(classname, ast))
        ;
      success = true;
    }

    return success;
  }

  /**
   * Move down all methods from the superclass to all subclasses
   *
   * @param classname The name of the class from which the methods should be moved
   * @param ast The AST on which the refactoring should be performed
   * @return
   */
  public boolean collapseHierarchyMethod(String classname, ASTCDCompilationUnit ast) {
    boolean success = false;

    CollapseHierarchyMethod collapseMethod = new CollapseHierarchyMethod(ast);
    collapseMethod.set_$superclass1(classname);
    if (collapseMethod.doPatternMatching()) {
      collapseMethod.doReplacement();
      success = true;
    }

    return success;
  }

  /**
   * Move down all attributes from the superclass to all subclasses
   *
   * @param classname The name of the class from which the attributes should be moved
   * @param ast The AST on which the refactoring should be performed
   * @return
   */
  public boolean collapseHierarchyAttribute(String classname, ASTCDCompilationUnit ast) {
    boolean success = false;
    CollapseHierarchyAttribute collapseAttribute = new CollapseHierarchyAttribute(ast);
    collapseAttribute.set_$superclass1(classname);
    if (collapseAttribute.doPatternMatching()) {
      collapseAttribute.get_$A1();
      collapseAttribute.doReplacement();
      success = true;
    }
    return success;
  }

  /**
   * Delete the given superclass
   *
   * @param classname The name of class, which should be deleted
   * @param ast The AST on which the refactoring should be performed
   * @return
   */
  public boolean deleteSuperclass(String classname, ASTCDCompilationUnit ast) {
    boolean success = false;
    DeleteSuperclass delete = new DeleteSuperclass(ast);
    delete.set_$superclass(classname);
    if (delete.doPatternMatching()) {
      delete.doReplacement();
      success = true;
    }
    return success;
  }

  public boolean deleteInheritance(String classname, ASTCDCompilationUnit ast) {
    boolean success = false;
    DeleteInheritance delete = new DeleteInheritance(ast);
    delete.set_$superclass(classname);
    if (delete.doPatternMatching()) {
      delete.doReplacement();
      success = true;
    }
    return success;
  }
}
