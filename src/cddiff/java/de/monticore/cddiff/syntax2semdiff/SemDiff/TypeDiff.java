package de.monticore.cddiff.syntax2semdiff.SemDiff;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import java.util.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class TypeDiff {
  private static ASTCDClass findClassByName(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass cdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (cdClass.getName().equals(className)) {
        return cdClass;
      }
    }
    return null;
  }

  private static List<ASTCDClass> getClassHierarchy(ASTCDCompilationUnit compilationUnit, ASTCDClass targetClass) {
    List<ASTCDClass> classHierarchy = new ArrayList<>();
    collectSuperclasses(compilationUnit, targetClass, classHierarchy);
    return classHierarchy;
  }

  private static void collectSuperclasses(ASTCDCompilationUnit compilationUnit, ASTCDClass clazz, List<ASTCDClass> classHierarchy) {
    classHierarchy.add(clazz);

    // Check if the class has superclasses
    for (ASTMCType superclass : clazz.getSuperclassList()) {
      String superclassName = superclass.getClass().getName();
      ASTCDClass superClass = findClassByName(compilationUnit, superclassName);
      if (superClass != null) {
        collectSuperclasses(compilationUnit, superClass, classHierarchy);
      }
    }
  }

  public static boolean hasAttribute(List<ASTCDClass> classes, String attributeName) {
    for (ASTCDClass clazz : classes) {
      for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
        if (attribute.getName().equals(attributeName)) {
          return true;
        }
      }
    }
    return false;
  }

  //added classes
  private static List<ASTCDClass> realDiffs(List<ASTCDClass> classList){
    List<ASTCDClass> astcdClassList = new ArrayList<>();
    for (ASTCDClass astcdClass: classList) {
      if (!astcdClass.getModifier().isAbstract()){
        astcdClassList.add(astcdClass);
      }
      else{
        //check if the abstract classes have all missing attributes from subclasses
        //superclasses can have new superclasses
      }
    }
    return astcdClassList;
  }

  private static List<ASTCDClass> getSubclasses(ASTCDCompilationUnit compilationUnit, ASTCDClass baseClass) {
    List<ASTCDClass> subclasses = new ArrayList<>();

    // Traverse the top-level class nodes in the compilation unit
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
        // Check if the class is a subclass of the base class
        if (astcdClass.getCDExtendUsage() != null && astcdClass.getCDExtendUsage().getClass().getName().equals(baseClass.getName())) {
          subclasses.add(astcdClass);
        }
      }

    return subclasses;
  }

  private static List<ASTCDClass> changedTypes(){
    return new ArrayList<>();
  }


}
