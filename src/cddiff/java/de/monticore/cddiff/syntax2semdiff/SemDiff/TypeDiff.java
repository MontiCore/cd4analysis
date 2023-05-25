package de.monticore.cddiff.syntax2semdiff.SemDiff;
import de.monticore.cdbasis._ast.*;
import java.util.*;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.antlr.v4.runtime.misc.NotNull;

public class TypeDiff {
  private static ASTCDClass findClassByName(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass cdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (cdClass.getName().equals(className)) {
        return cdClass;
      }
    }
    return null;
  }

  //Get whole inheritance hierarchy(tree-like-structure) and return all classes in that hierarchy
  private static List<ASTCDClass> getClassHierarchy(ASTCDCompilationUnit compilationUnit, ASTCDClass targetClass) {
    List<ASTCDClass> classHierarchy = new ArrayList<>();
    collectSuperclasses(compilationUnit, targetClass, classHierarchy);
    return classHierarchy;
  }

  //Going through each branch with recursion
  //classes are contained only once
  private static void collectSuperclasses(ASTCDCompilationUnit compilationUnit, ASTCDClass clazz, List<ASTCDClass> classHierarchy) {
    classHierarchy.add(clazz);

    // Check if the class has superclasses
    for (ASTMCObjectType superclass : clazz.getCDExtendUsage().getSuperclassList()) {
      String superclassName = superclass.getClass().getName();
      ASTCDClass superClass = findClassByName(compilationUnit, superclassName);
      if (!classHierarchy.contains(superClass)) {
        classHierarchy.add(superClass);
      }
      if (superClass != null) {
        collectSuperclasses(compilationUnit, superClass, classHierarchy);
      }
    }
  }

  //Check if in an inheritance hierarchy(list) an atrribute @attributeName exists
  public static boolean hasAttribute(List<ASTCDClass> classes, ASTCDAttribute attribute) {
    for (ASTCDClass clazz : classes) {
      if (clazz.getCDAttributeList().contains(attribute)) {
        return true;
      }
    }
    return false;
  }

  //added classes
  //idea - added classes are semantic differences
  //if they are abstract and combine attributes from classes - they don't bring a semDiff
  private static List<ASTCDClass> realDiffs(List<ASTCDClass> addedClasses, ASTCDCompilationUnit astcdCompilationUnit, List<DataStructure.Pair<ASTCDClass>> dataStructureList){
    List<ASTCDClass> astcdClassList = new ArrayList<>();
    for (ASTCDClass astcdClass: addedClasses) {
      if (!astcdClass.getModifier().isAbstract()){
        astcdClassList.add(astcdClass);
      }
      else{
        //check if the abstract classes have all missing attributes from subclasses and if all attributes from cd2 are also in cd1
        //superclasses can have new superclasses
        List<ASTCDClass> absClasses = getAllExtendingNonAbsClasses(astcdCompilationUnit, astcdClass);
        if (!hasMoreAtt(astcdClass, absClasses, dataStructureList)){
          for (DataStructure.Pair<ASTCDClass> pair : dataStructureList){
            for (ASTCDAttribute attribute : pair.getSecond().getCDAttributeList())
            if (!hasAttribute(getClassHierarchy(astcdCompilationUnit, pair.getFirst()), attribute)){
              astcdClassList.add(astcdClass);
            }
          }
        }
        else {
          astcdClassList.add(astcdClass);
        }
      }
    }
    return astcdClassList;
  }

  //Get all attributes
  public static List<ASTCDAttribute> extractAttributes(List<ASTCDClass> cdClasses) {
    Set<String> attributeNames = new HashSet<>();
    List<ASTCDAttribute> attributes = new ArrayList<>();

    for (ASTCDClass cdClass : cdClasses) {
      for (ASTCDAttribute attribute : cdClass.getCDAttributeList()) {
        // Check if attribute name has already been processed
        if (!attributeNames.contains(attribute.getName())) {
          // Add the attribute to the result list
          attributes.add(attribute);
          // Add the attribute name to the set of processed names
          attributeNames.add(attribute.getName());
        }
      }
    }

    return attributes;
  }

  //2&3
  //Check if all attributes of an added superclass are in the nodes in the other class diagram
  private static boolean hasMoreAtt(ASTCDClass astcdClass, List<ASTCDClass> astcdClassList, List<DataStructure.Pair<ASTCDClass>> dataStructureList){
    int i = 0;
    for (ASTCDClass astcdClass1 : astcdClassList){
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()){
        ASTCDClass class2 = searchPairByName(dataStructureList, astcdClass1.getName()).getSecond();
        if (!class2.getCDAttributeList().contains(attribute)){
          i = 1;
          break;
        }
      }
    }
    return i == 0;
  }

  public static DataStructure.Pair<ASTCDClass> searchPairByName(List<DataStructure.Pair<ASTCDClass>> pairs, String name) {
    for (DataStructure.Pair pair : pairs) {
      if (pair.getFirst().getClass().getName().equals(name) || pair.getSecond().getClass().getName().equals(name)) {
        return pair;
      }
    }
    return null;
  }

  //1
  //Get all nodes that are not abstract(representation of the classes with missing attributes)
  private static List<ASTCDClass> getAllExtendingNonAbsClasses(ASTCDCompilationUnit astcdCompilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> subclassesNonAbs = new ArrayList<>();
    List<ASTCDClass> classesToCheck = new ArrayList<>();
    classesToCheck.add(astcdClass);

    // Traverse the top-level class nodes in the compilation unit
    while(!classesToCheck.isEmpty()) {
      ASTCDClass currentClass = classesToCheck.get(0);
      for (ASTCDClass astcdClass1 : getSubclasses(astcdCompilationUnit, currentClass)){
        if (!astcdClass1.getModifier().isAbstract()){
          subclassesNonAbs.add(astcdClass1);
        }
        else{
          classesToCheck.add(astcdClass1);
        }
      }
    }
    return subclassesNonAbs;
  }

  //Get direct subclasses of a class
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
