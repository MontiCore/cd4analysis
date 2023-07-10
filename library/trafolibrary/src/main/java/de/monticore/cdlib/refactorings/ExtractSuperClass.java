/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import com.google.common.collect.Lists;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.ExtractSuperClassAttribute;
import de.monticore.cdlib.Refactoring.ExtractSuperClassMethod;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Splitters;
import java.util.ArrayList;

/**
 * Extract class: Introduce a new superclass for classes with no superclass and same methods or
 * attributes
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ExtractSuperClass implements Refactoring {
  public ExtractSuperClass() {}

  /**
   * Introduce a new superclass for classes with no superclass and same methods or attributes
   *
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractSuperClass(ASTCDCompilationUnit ast) {
    if (extractSuperClassAttribute(ast) || extractSuperClassMethod(ast)) {
      return true;
    }
    return false;
  }

  /**
   * Introduce a new superclass for classes with no superclass and same attributes
   *
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractSuperClassAttribute(ASTCDCompilationUnit ast) {
    ExtractSuperClassAttribute extractClass = new ExtractSuperClassAttribute(ast);
    if (extractClass.doPatternMatching()) {
      extractClass.doReplacement();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Introduce a new superclass for classes with no superclass and same methods, where the
   * superclass is called by <code>classname</code>
   *
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractSuperClassMethod(ASTCDCompilationUnit ast) {
    ExtractSuperClassMethod extractClass = new ExtractSuperClassMethod(ast);
    if (extractClass.doPatternMatching()) {
      extractClass.doReplacement();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Introduce a new superclass for classes with no superclass and same methods or attributes, where
   * the superclass is called by <code>classname</code>
   *
   * @param ast - class diagram to be transformed
   * @param classname is the classname of the new superclass
   * @return true, if applied successfully
   */
  public boolean extractSuperClassWithName(ASTCDCompilationUnit ast, String classname) {
    if (extractSuperClassAttributeWithName(ast, classname)
        || extractSuperClassMethodWithName(ast, classname)) {
      return true;
    }
    return false;
  }

  /**
   * Introduce a new superclass for classes with no superclass and same attributes, where the
   * superclass is called by <code>classname</code>
   *
   * @param ast - class diagram to be transformed
   * @param classname is the classname of the new superclass
   * @return true, if applied successfully
   */
  public boolean extractSuperClassAttributeWithName(ASTCDCompilationUnit ast, String classname) {
    ExtractSuperClassAttribute extractClass = new ExtractSuperClassAttribute(ast);
    if (extractClass.doPatternMatching()) {
      extractClass.set_$newParent(classname);
      extractClass.doReplacement();
      extractClass.get_$S().setName(classname);
      ArrayList<String> nameList = new ArrayList<String>();
      nameList.add(classname);
      ASTMCQualifiedType reference =
          CDBasisMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(
                  CDBasisMill.mCQualifiedNameBuilder()
                      .addAllParts(Lists.newArrayList(Splitters.DOT.split(classname)))
                      .build())
              .build();
      extractClass.get_$b().getSuperclassList().clear();
      extractClass.get_$b().getSuperclassList().add(reference);
      for (int i = 0; i < extractClass.get_$c().size(); i++) {
        extractClass.get_$c().get(i).getSuperclassList().clear();
        extractClass.get_$c().get(i).getSuperclassList().add(reference);
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Introduce a new superclass for classes with no superclass and same methods
   *
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean extractSuperClassMethodWithName(ASTCDCompilationUnit ast, String classname) {
    ExtractSuperClassMethod extractClass = new ExtractSuperClassMethod(ast);
    if (extractClass.doPatternMatching()) {
      extractClass.set_$newParent(classname);
      extractClass.doReplacement();
      extractClass.get_$S().setName(classname);
      ArrayList<String> nameList = new ArrayList<String>();
      nameList.add(classname);
      ASTMCQualifiedType reference =
          CDBasisMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(
                  CDBasisMill.mCQualifiedNameBuilder().addAllParts(nameList).build())
              .build();
      extractClass.get_$b().getSuperclassList().clear();
      extractClass.get_$b().getSuperclassList().add(reference);
      for (int i = 0; i < extractClass.get_$c().size(); i++) {
        extractClass.get_$c().get(i).getSuperclassList().clear();
        extractClass.get_$c().get(i).getSuperclassList().add(reference);
      }
      return true;
    } else {
      return false;
    }
  }
}
