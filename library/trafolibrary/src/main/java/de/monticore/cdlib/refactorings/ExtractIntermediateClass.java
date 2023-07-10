/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.*;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Extract superclass: Extracts additional superclass for classes with same superclass and same
 * attributes or methods
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ExtractIntermediateClass implements Refactoring {
  public ExtractIntermediateClass() {}

  /**
   * Extracts all super classes
   *
   * @param ast - the class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractAllIntermediateClasses(ASTCDCompilationUnit ast) {
    PullUp pullUp = new PullUp();
    pullUp.pullUp(ast);

    if (extractIntermediateClass(ast)) {
      pullUp.pullUp(ast);
      while (extractIntermediateClass(ast)) {
        pullUp.pullUp(ast);
      }
      return true;
    }
    return false;
  }

  // Extracts additional superclass for classes with same superclass and same
  // attributes or methods
  // to avoid adding useless classes use pullUp before
  private boolean extractIntermediateClass(ASTCDCompilationUnit ast) {

    /* Extract Superclass from six classes (or more) */
    // for attributes
    Class6ClassesAttribute extract6Superclasses =
        new Class6ClassesAttribute(ast);
    if (extract6Superclasses.doPatternMatching()) {
      extract6Superclasses.doReplacement();

      // Adapt all additional Classes with this attribute
      ClassAdaptSubclassAttribute additionalSubclass =
          new ClassAdaptSubclassAttribute(ast);
      additionalSubclass.set_$A(extract6Superclasses.get_$A1());
      additionalSubclass.set_$parent(extract6Superclasses.get_$parent());
      additionalSubclass.set_$newParent(extract6Superclasses.get_$newParent());
      while (additionalSubclass.doPatternMatching()) {
        additionalSubclass = new ClassAdaptSubclassAttribute(ast);
        additionalSubclass.set_$A(extract6Superclasses.get_$A1());
        additionalSubclass.set_$parent(extract6Superclasses.get_$parent());
        additionalSubclass.set_$newParent(extract6Superclasses.get_$newParent());
      }

      return true;
    }

    // for methods
    Class6ClassesMethod extract6SuperclassesMethod =
        new Class6ClassesMethod(ast);
    if (extract6SuperclassesMethod.doPatternMatching()) {
      extract6SuperclassesMethod.doReplacement();

      // Adapt all additional classes with this method
      ClassAdaptSubclassMethod additionalSubclassMethod =
          new ClassAdaptSubclassMethod(ast);
      additionalSubclassMethod.set_$A(extract6SuperclassesMethod.get_$A1());
      additionalSubclassMethod.set_$parent(extract6SuperclassesMethod.get_$parent());
      additionalSubclassMethod.set_$newParent(extract6SuperclassesMethod.get_$newParent());
      while (additionalSubclassMethod.doPatternMatching()) {
        additionalSubclassMethod = new ClassAdaptSubclassMethod(ast);
        additionalSubclassMethod.set_$A(extract6SuperclassesMethod.get_$A1());
        additionalSubclassMethod.set_$parent(extract6SuperclassesMethod.get_$parent());
        additionalSubclassMethod.set_$newParent(extract6SuperclassesMethod.get_$newParent());
      }
      return true;
    }

    /* Extract Superclass from five classes */
    // for attributes
    Class5ClassesAttribute extract5Superclasses =
        new Class5ClassesAttribute(ast);
    if (extract5Superclasses.doPatternMatching()) {
      extract5Superclasses.doReplacement();
      return true;
    }

    // for methods
    Class5ClassesMethod extract5SuperclassesMethods =
        new Class5ClassesMethod(ast);
    if (extract5SuperclassesMethods.doPatternMatching()) {
      extract5SuperclassesMethods.doReplacement();
      return true;
    }

    /* Extract Superclass from four classes */
    // for attributes
    Class4ClassesAttribute extract4Superclasses =
        new Class4ClassesAttribute(ast);
    if (extract4Superclasses.doPatternMatching()) {
      extract4Superclasses.doReplacement();
      return true;
    }

    // for methods
    Class4ClassesMethod extract4SuperclassesMethod =
        new Class4ClassesMethod(ast);
    if (extract4SuperclassesMethod.doPatternMatching()) {
      extract4SuperclassesMethod.doReplacement();
      return true;
    }

    /* Extract Superclass from three classes */
    // for attributes
    Class3ClassesAttribute extract3Superclasses =
        new Class3ClassesAttribute(ast);
    if (extract3Superclasses.doPatternMatching()) {
      extract3Superclasses.doReplacement();
      return true;
    }
    // for methods
    Class3ClassesMethod extract3SuperclassesMethod =
        new Class3ClassesMethod(ast);
    if (extract3SuperclassesMethod.doPatternMatching()) {
      extract3SuperclassesMethod.doReplacement();
      return true;
    }

    /* Extract Superclass from two classes */
    // for attributes
    Class2ClassesAttribute extract2Superclasses =
        new Class2ClassesAttribute(ast);
    if (extract2Superclasses.doPatternMatching()) {
      extract2Superclasses.doReplacement();
      return true;
    }

    // for methods
    Class2ClassesMethod extract2SuperclassesMethod =
        new Class2ClassesMethod(ast);
    if (extract2SuperclassesMethod.doPatternMatching()) {
      extract2SuperclassesMethod.doReplacement();
      return true;
    }

    return false;
  }

  /**
   * Extract all (up to 6) listed subclasses {@code subclasses}.
   *
   * @param newSuperclassName - new super class
   * @param subclasses - list of subclasses
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractIntermediateClass(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {

    boolean success = false;

    switch (subclasses.size()) {
      case 2:
        success = Classes(newSuperclassName, subclasses, ast);
        break;
      case 3:
        success = Class3(newSuperclassName, subclasses, ast);
        break;
      case 4:
        success = Class4(newSuperclassName, subclasses, ast);
        break;
      case 5:
        success = Class5(newSuperclassName, subclasses, ast);
        break;
      case 6:
        success = Class6(newSuperclassName, subclasses, ast);
        break;
      default:
        Log.info(
            "0xF4091: ExtractSuperclass is only applicable for up to six subclasses",
            Class.class.getName());
        return false;
    }

    return success;
  }

  // Create new Superclass for six subclasses with same attribute or method
  private boolean Class6(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
    /* Pull Up attributes from six classes */
    Class6ClassesManualNameAttribute extractSuperclass =
        new Class6ClassesManualNameAttribute(ast);
    extractSuperclass.set_$newParent(newSuperclassName);
    extractSuperclass.set_$subclass1(subclasses.get(0));
    extractSuperclass.set_$subclass2(subclasses.get(1));
    extractSuperclass.set_$subclass3(subclasses.get(2));
    extractSuperclass.set_$subclass4(subclasses.get(3));
    extractSuperclass.set_$subclass5(subclasses.get(4));
    extractSuperclass.set_$subclass6(subclasses.get(5));

    if (extractSuperclass.doPatternMatching()) {
      extractSuperclass.doReplacement();
      return true;
    } else {

      /* Pull Up methods from six classes */
      Class6ClassesManualNameMethod extractSuperclassMethod =
          new Class6ClassesManualNameMethod(ast);
      extractSuperclassMethod.set_$newParent(newSuperclassName);
      extractSuperclassMethod.set_$subclass1(subclasses.get(0));
      extractSuperclassMethod.set_$subclass2(subclasses.get(1));
      extractSuperclassMethod.set_$subclass3(subclasses.get(2));
      extractSuperclassMethod.set_$subclass4(subclasses.get(3));
      extractSuperclassMethod.set_$subclass5(subclasses.get(4));
      extractSuperclassMethod.set_$subclass6(subclasses.get(5));

      if (extractSuperclassMethod.doPatternMatching()) {
        extractSuperclassMethod.doReplacement();
        return true;
      }
    }
    return false;
  }

  // Create new Superclass for five subclasses with same attribute or method
  private boolean Class5(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
    /* Pull Up attributes from five classes */
    Class5ClassesManualNameAttribute extractSuperclass =
        new Class5ClassesManualNameAttribute(ast);
    extractSuperclass.set_$newParent(newSuperclassName);
    extractSuperclass.set_$subclass1(subclasses.get(0));
    extractSuperclass.set_$subclass2(subclasses.get(1));
    extractSuperclass.set_$subclass3(subclasses.get(2));
    extractSuperclass.set_$subclass4(subclasses.get(3));
    extractSuperclass.set_$subclass5(subclasses.get(4));

    if (extractSuperclass.doPatternMatching()) {
      extractSuperclass.doReplacement();
      return true;
    } else {
      /* Pull Up methods from five classes */
      Class5ClassesManualNameMethod extractSuperclassMethod =
          new Class5ClassesManualNameMethod(ast);
      extractSuperclassMethod.set_$newParent(newSuperclassName);
      extractSuperclassMethod.set_$subclass1(subclasses.get(0));
      extractSuperclassMethod.set_$subclass2(subclasses.get(1));
      extractSuperclassMethod.set_$subclass3(subclasses.get(2));
      extractSuperclassMethod.set_$subclass4(subclasses.get(3));
      extractSuperclassMethod.set_$subclass5(subclasses.get(4));

      if (extractSuperclassMethod.doPatternMatching()) {
        extractSuperclassMethod.doReplacement();
        return true;
      }
    }
    return false;
  }

  // Create new superclass for four subclasses with same attributes or methods
  private boolean Class4(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
    /* Pull Up attributes from four classes */
    Class4ClassesManualNameAttribute extractSuperclass =
        new Class4ClassesManualNameAttribute(ast);
    extractSuperclass.set_$newParent(newSuperclassName);
    extractSuperclass.set_$subclass1(subclasses.get(0));
    extractSuperclass.set_$subclass2(subclasses.get(1));
    extractSuperclass.set_$subclass3(subclasses.get(2));
    extractSuperclass.set_$subclass4(subclasses.get(3));
    if (extractSuperclass.doPatternMatching()) {
      extractSuperclass.doReplacement();
      return true;
    } else {
      /* Pull Up methods from four classes */
      Class4ClassesManualNameMethod extractSuperclassMethod =
          new Class4ClassesManualNameMethod(ast);
      extractSuperclassMethod.set_$newParent(newSuperclassName);
      extractSuperclassMethod.set_$subclass1(subclasses.get(0));
      extractSuperclassMethod.set_$subclass2(subclasses.get(1));
      extractSuperclassMethod.set_$subclass3(subclasses.get(2));
      extractSuperclassMethod.set_$subclass4(subclasses.get(3));
      if (extractSuperclassMethod.doPatternMatching()) {
        extractSuperclassMethod.doReplacement();
        return true;
      }
    }
    return false;
  }

  // Create new superclass for three subclasses with same attributes or
  // methods
  private boolean Class3(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
    /* Pull Up attributes from three classes */
    Class3ClassesManualNameAttribute extract3Superclasses =
        new Class3ClassesManualNameAttribute(ast);
    extract3Superclasses.set_$newParent(newSuperclassName);
    extract3Superclasses.set_$subclass1(subclasses.get(0));
    extract3Superclasses.set_$subclass2(subclasses.get(1));
    extract3Superclasses.set_$subclass3(subclasses.get(2));

    if (extract3Superclasses.doPatternMatching()) {
      extract3Superclasses.doReplacement();
      return true;
    } else {
      /* Pull Up methods from three classes */
      Class3ClassesManualNameMethod extract3SuperclassesMethod =
          new Class3ClassesManualNameMethod(ast);
      extract3SuperclassesMethod.set_$newParent(newSuperclassName);
      extract3SuperclassesMethod.set_$subclass1(subclasses.get(0));
      extract3SuperclassesMethod.set_$subclass2(subclasses.get(1));
      extract3SuperclassesMethod.set_$subclass3(subclasses.get(2));

      if (extract3SuperclassesMethod.doPatternMatching()) {
        extract3SuperclassesMethod.doReplacement();
        return true;
      }
    }
    return false;
  }

  // Create new superclass for two subclasses with same attributes or methods
  private boolean Classes(
      String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
    /* Pull Up attribtues from two classes */
    Class2ClassesManualNameAttribute extract2Superclasses =
        new Class2ClassesManualNameAttribute(ast);
    extract2Superclasses.set_$newParent(newSuperclassName);
    extract2Superclasses.set_$subclass1(subclasses.get(0));
    extract2Superclasses.set_$subclass2(subclasses.get(1));

    if (extract2Superclasses.doPatternMatching()) {
      extract2Superclasses.doReplacement();
      return true;
    } else {
      /* Pull Up methods from two classes */
      Class2ClassesManualNameMethod extract2SuperclassesMethod =
          new Class2ClassesManualNameMethod(ast);
      extract2SuperclassesMethod.set_$newParent(newSuperclassName);
      extract2SuperclassesMethod.set_$subclass1(subclasses.get(0));
      extract2SuperclassesMethod.set_$subclass2(subclasses.get(1));

      if (extract2SuperclassesMethod.doPatternMatching()) {
        extract2SuperclassesMethod.doReplacement();
        return true;
      }
    }
    return false;
  }

  private boolean Classes(String newSuperclassName, ASTCDCompilationUnit ast) {
    /* Pull Up attribtues from two classes */
    Class2ClassesManualNameAttribute extract2Superclasses =
        new Class2ClassesManualNameAttribute(ast);
    extract2Superclasses.set_$newParent(newSuperclassName);

    if (extract2Superclasses.doPatternMatching()) {
      extract2Superclasses.doReplacement();
      return true;
    } else {
      /* Pull Up methods from two classes */
      Class2ClassesManualNameMethod extract2SuperclassesMethod =
          new Class2ClassesManualNameMethod(ast);
      extract2SuperclassesMethod.set_$newParent(newSuperclassName);

      if (extract2SuperclassesMethod.doPatternMatching()) {
        extract2SuperclassesMethod.doReplacement();
        return true;
      }
    }
    return false;
  }
}
