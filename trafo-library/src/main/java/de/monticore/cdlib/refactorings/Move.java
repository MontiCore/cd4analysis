/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.*;
import java.util.List;

/**
 * Move attributes or methods from one to another class
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class Move implements Refactoring {
  public Move() {}

  /**
   * Moves all methods and attributes from a source class {@code sourceClass} to a target class
   * {@code targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true
   */
  public boolean moveMethodsAndAttributes(
      String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    while (moveMethod(sourceClass, targetClass, ast))
      ;
    while (moveAttribute(sourceClass, targetClass, ast))
      ;
    return true;
  }

  /**
   * Move all methods and attributes from a source class {@code sourceClass} to a target class
   * {@code targetClass}, if their connected with a one-to-one association
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveMethodsAndAttributesToNeighborClass(
      String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    IsNeighbor isNeighbor = new IsNeighbor(ast);
    isNeighbor.set_$c1(sourceClass);
    isNeighbor.set_$c2(targetClass);
    if (isNeighbor.doPatternMatching()) {
      return moveMethodsAndAttributes(sourceClass, targetClass, ast);
    }
    return false;
  }

  /**
   * Move all methods from a source class {@code sourceClass} to a target class {@code targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveAllMethods(String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    boolean success = false;
    while (moveMethod(sourceClass, targetClass, ast)) {
      success = true;
    }
    return success;
  }

  /**
   * Moves the first method from the source class {@code sourceClass} to a target class {@code
   * targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  private boolean moveMethod(String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    MoveMethod moveMethod = new MoveMethod(ast);
    moveMethod.set_$startClassName(sourceClass);
    moveMethod.set_$destinationClassName(targetClass);
    if (moveMethod.doPatternMatching()) {
      moveMethod.doReplacement();
      return true;
    }

    return false;
  }

  /**
   * Move the methods with the names of {@code methodsToMove} from a source class {@code
   * sourceClass} to a target class {@code targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param methodsToMove - list of methods to move
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveMethods(
      String sourceClass,
      String targetClass,
      List<String> methodsToMove,
      ASTCDCompilationUnit ast) {
    for (int i = 0; i < methodsToMove.size(); i++) {
      MoveConcreteMethod moveMethod = new MoveConcreteMethod(ast);
      moveMethod.set_$startClassName(sourceClass);
      moveMethod.set_$destinationClassName(targetClass);
      moveMethod.set_$name(methodsToMove.get(i));
      if (moveMethod.doPatternMatching()) {
        moveMethod.doReplacement();
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Moves the given methods {@code methodsToMove} from the source class {@code sourceClass} to the
   * target class {@code targetClass}, if their connected with a one-to-one association
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param methodsToMove - list of methods to move
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveMethodsToNeighborClass(
      String sourceClass,
      String targetClass,
      List<String> methodsToMove,
      ASTCDCompilationUnit ast) {
    IsNeighbor isNeighbor = new IsNeighbor(ast);
    isNeighbor.set_$c1(sourceClass);
    isNeighbor.set_$c2(targetClass);
    if (isNeighbor.doPatternMatching()) {
      return moveMethods(sourceClass, targetClass, methodsToMove, ast);
    }
    return false;
  }

  /**
   * Move all attributes from a source class {@code sourceClass} to a target class {@code
   * targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveAllAttributes(
      String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    boolean success = false;
    while (moveAttribute(sourceClass, targetClass, ast)) {
      success = true;
    }
    return success;
  }

  /**
   * Moves the first attribute from the source class {@code sourceClass} to the target class {@code
   * targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  private boolean moveAttribute(String sourceClass, String targetClass, ASTCDCompilationUnit ast) {
    MoveAttribute moveAttribute = new MoveAttribute(ast);
    moveAttribute.set_$startClassName(sourceClass);
    moveAttribute.set_$destinationClassName(targetClass);
    if (moveAttribute.doPatternMatching()) {
      moveAttribute.doReplacement();
      return true;
    }
    return false;
  }

  /**
   * Move the attributes with the names of {@code attributesToMove} from the source class {@code
   * sourceClass} to the target class {@code targetClass}
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param attributesToMove - list of attributes to move
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveAttributes(
      String sourceClass,
      String targetClass,
      List<String> attributesToMove,
      ASTCDCompilationUnit ast) {
    for (int i = 0; i < attributesToMove.size(); i++) {
      MoveConcreteAttribute moveMethod = new MoveConcreteAttribute(ast);
      moveMethod.set_$startClassName(sourceClass);
      moveMethod.set_$destinationClassName(targetClass);
      moveMethod.set_$name(attributesToMove.get(i));
      if (moveMethod.doPatternMatching()) {
        moveMethod.doReplacement();
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Moves the given attributes {@code attributesToMove} from the source class {@code sourceClass}
   * to the target class {@code targetClass}, if their connected with a one-to-one association
   *
   * @param sourceClass - the source class
   * @param targetClass - the target class
   * @param attributesToMove - list of attributes to move
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean moveAttributesToNeighborClass(
      String sourceClass,
      String targetClass,
      List<String> attributesToMove,
      ASTCDCompilationUnit ast) {
    IsNeighbor isNeighbor = new IsNeighbor(ast);
    isNeighbor.set_$c1(sourceClass);
    isNeighbor.set_$c2(targetClass);
    if (isNeighbor.doPatternMatching()) {
      return moveAttributes(sourceClass, targetClass, attributesToMove, ast);
    }
    return false;
  }
}
