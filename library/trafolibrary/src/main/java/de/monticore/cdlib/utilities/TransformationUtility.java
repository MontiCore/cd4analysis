/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilities;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.Refactoring.AddInheritanceToInterface;
import de.monticore.cdlib.Refactoring.ChangeInheritanceClasses;
import de.monticore.cdlib.Refactoring.CreateInheritanceInterface;
import de.monticore.cdlib.Refactoring.CreateInheritanceToClass;
import de.monticore.cdlib.Refactoring.DeleteAssociation;
import de.monticore.cdlib.Refactoring.RenameClassInType;
import de.monticore.cdlib.TransformationUtility.AddMethod;
import de.monticore.cdlib.TransformationUtility.AddMethodInterface;
import de.monticore.cdlib.TransformationUtility.ChangeLeftQualifierBiDir;
import de.monticore.cdlib.TransformationUtility.ChangeLeftQualifierLeftDir;
import de.monticore.cdlib.TransformationUtility.ChangeLeftQualifierRightDir;
import de.monticore.cdlib.TransformationUtility.ChangeLeftQualifierUniDir;
import de.monticore.cdlib.TransformationUtility.ChangeRightQualifierBiDir;
import de.monticore.cdlib.TransformationUtility.ChangeRightQualifierLeftDir;
import de.monticore.cdlib.TransformationUtility.ChangeRightQualifierRightDir;
import de.monticore.cdlib.TransformationUtility.ChangeRightQualifierUniDir;
import de.monticore.cdlib.TransformationUtility.ClassIsPresent;
import de.monticore.cdlib.TransformationUtility.CreateClass;
import de.monticore.cdlib.TransformationUtility.CreateInterface;
import de.monticore.cdlib.TransformationUtility.CreateRightDirAssociation;
import de.monticore.cdlib.TransformationUtility.FindAttribute;
import de.monticore.cdlib.TransformationUtility.FindMethod;
import de.monticore.cdlib.TransformationUtility.GetAttribute;
import de.monticore.cdlib.TransformationUtility.GetMethod;
import de.monticore.cdlib.designpatterns.facade.tf.CreateBiDirAssociation;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.List;

/**
 * Provides methods for primitive transformations of a class diagram The methods are used in
 * refactoring and design pattern classes
 *
 * <p>Created by
 *
 * @author KE
 */
public class TransformationUtility {

  public TransformationUtility() {}

  /**
   * Checks if a class is present
   *
   * @param className
   * @param ast
   * @return
   */
  public boolean classIsPresent(String className, ASTCDCompilationUnit ast) {
    ClassIsPresent classTest = new ClassIsPresent(ast);
    classTest.set_$className(className);
    return classTest.doPatternMatching();
  }

  /**
   * Replaces type in an attribute
   *
   * @param oldType
   * @param newType
   * @param ast
   * @return
   */
  public boolean replaceTypeInAttribute(String oldType, String newType, ASTCDCompilationUnit ast) {
    RenameClassInType renameClassInType = new RenameClassInType(ast);
    renameClassInType.set_$oldType(oldType);
    renameClassInType.set_$newType(newType);

    if (renameClassInType.doPatternMatching()) {
      renameClassInType.doReplacement();
      return true;
    }
    return false;
  }

  // Replace ReferenceName (oldName) by newName in all associations
  public boolean changeRefNameInAllAssociations(
      String oldName, String newName, ASTCDCompilationUnit ast) {

    if (!changeRefNameInOneAssociation(oldName, newName, ast)) {
      return false;
    }
    while (changeRefNameInOneAssociation(oldName, newName, ast))
      ;
    return true;
  }

  // Replace ReferenceName (oldName) by newName in one association
  private boolean changeRefNameInOneAssociation(
      String oldName, String newName, ASTCDCompilationUnit ast) {
    // Replace Left Qualifier

    // Bidirectional associations
    ChangeLeftQualifierBiDir leftBi = new ChangeLeftQualifierBiDir(ast);
    leftBi.set_$leftNew((newName));
    leftBi.set_$oldName(oldName);

    if (leftBi.doPatternMatching()) {
      leftBi.doReplacement();
      return true;
    }

    // Unidirectional associations
    ChangeLeftQualifierUniDir leftUni = new ChangeLeftQualifierUniDir(ast);
    leftUni.set_$leftNew((newName));
    leftUni.set_$oldName(oldName);
    if (leftUni.doPatternMatching()) {
      leftUni.doReplacement();
      return true;
    }

    // Rightdirectional associations
    ChangeLeftQualifierRightDir leftRight = new ChangeLeftQualifierRightDir(ast);
    leftRight.set_$leftNew((newName));
    leftRight.set_$oldName(oldName);
    if (leftRight.doPatternMatching()) {
      leftRight.doReplacement();
      return true;
    }

    // Rightdirectional associations
    ChangeLeftQualifierLeftDir leftLeft = new ChangeLeftQualifierLeftDir(ast);
    leftLeft.set_$leftNew((newName));
    leftLeft.set_$oldName(oldName);
    if (leftLeft.doPatternMatching()) {
      leftLeft.doReplacement();
      return true;
    }

    // Replace Right Qualifier
    // Bidirectional associations
    ChangeRightQualifierBiDir rightBi = new ChangeRightQualifierBiDir(ast);
    rightBi.set_$rightNew((newName));
    rightBi.set_$oldName(oldName);
    if (rightBi.doPatternMatching()) {
      rightBi.doReplacement();
      return true;
    }

    // Unidirectional associations
    ChangeRightQualifierUniDir rightUni = new ChangeRightQualifierUniDir(ast);
    rightUni.set_$rightNew((newName));
    rightUni.set_$oldName(oldName);
    if (rightUni.doPatternMatching()) {
      rightUni.doReplacement();
      return true;
    }

    // Rightdirectional associations
    ChangeRightQualifierRightDir rightRight = new ChangeRightQualifierRightDir(ast);
    rightRight.set_$rightNew((newName));
    rightRight.set_$oldName(oldName);
    if (rightRight.doPatternMatching()) {
      rightRight.doReplacement();
      return true;
    }

    // Rightdirectional associations
    ChangeRightQualifierLeftDir rightLeft = new ChangeRightQualifierLeftDir(ast);
    rightLeft.set_$rightNew((newName));
    rightLeft.set_$oldName(oldName);
    if (rightLeft.doPatternMatching()) {
      rightLeft.doReplacement();
      return true;
    }
    return false;
  }

  // Create Associations

  // Creates a Association with right direction for classes
  public boolean createRightDirAssociation(
      String leftReferenceName, String rightReferenceName, ASTCDCompilationUnit ast) {

    CreateRightDirAssociation associate = new CreateRightDirAssociation(ast);

    // Set rightReferenceName and leftReferenceName for transformation
    associate.set_$RightReferenceList(rightReferenceName);
    associate.set_$LeftReferenceList(leftReferenceName);

    // create association between rightReferenceName and leftReferenceName
    if (associate.doPatternMatching()) {
      associate.doReplacement();
      return true;
    }
    return false;
  }

  // Associates a class to all classes in the list rightClasses with a
  // bi-directional association
  public boolean createBiDirAssociations(
      String leftClass, List<String> rightClasses, ASTCDCompilationUnit ast) throws IOException {

    for (String rightClass : rightClasses) {
      // Create Transformation
      CreateBiDirAssociation createAssociation = new CreateBiDirAssociation(ast);

      // Set Variables
      createAssociation.set_$nameFassade(leftClass);
      createAssociation.set_$nameClass(rightClass);

      // Apply Transformation and add association from classes to facade class
      if (createAssociation.doPatternMatching()) {
        createAssociation.doReplacement();
      }
    }
    return true;
  }

  /**
   * Deletes an association
   *
   * @param className
   * @param ast
   * @return
   */
  private boolean deleteAssociation(String className, ASTCDCompilationUnit ast) {
    DeleteAssociation association = new DeleteAssociation(ast);

    // Set className for delete the association
    association.set_$className(className);

    // create association between rightReferenceName and leftReferenceName
    if (association.doPatternMatching()) {
      association.doReplacement();
      return true;
    }
    return false;
  }

  /**
   * Deletes all associations
   *
   * @param className
   * @param ast
   * @return
   */
  public boolean deleteAllAssociations(String className, ASTCDCompilationUnit ast) {
    if (!deleteAssociation(className, ast)) {
      return false;
    }
    while (deleteAssociation(className, ast))
      ;
    return true;
  }

  /**
   * Creates a new class
   *
   * @param className
   * @param ast
   * @return
   */
  public boolean createSimpleClass(String className, ASTCDCompilationUnit ast) {
    CreateClass createClass = new CreateClass(ast);
    createClass.set_$className(className);
    if (createClass.doPatternMatching()) {
      createClass.doReplacement();
      return true;
    }
    return false;
  }

  /**
   * Creates a new interface
   *
   * @param interfaceName
   * @param ast
   * @return
   */
  public boolean createInterface(String interfaceName, ASTCDCompilationUnit ast) {
    CreateInterface createInterface = new CreateInterface(ast);
    createInterface.set_$interfaceName(interfaceName);
    if (createInterface.doPatternMatching()) {
      createInterface.doReplacement();
      return true;
    }
    return false;
  }

  // Adds to a class an inheritance to an interface, if no inheritance to an
  // interface is still there
  public boolean createInheritanceToInterface(
      String subclass, String interfaceName, ASTCDCompilationUnit ast) {
    CreateInheritanceInterface createInheritance = new CreateInheritanceInterface(ast);

    createInheritance.set_$subclass(subclass);
    createInheritance.set_$interfaceName(interfaceName);
    // Do Transformation
    if (createInheritance.doPatternMatching()) {
      createInheritance.doReplacement();
      return true;
    }
    return false;
  }

  // Creates an inheritance from a class to an interface
  public boolean addInheritanceToInterface(
      String className, String newInterface, ASTCDCompilationUnit ast) {

    AddInheritanceToInterface interfaceI = new AddInheritanceToInterface(ast);
    // Set the Class Name
    interfaceI.set_$subclass(className);

    // if the class has still interfaces to implement, newInterface is just
    // added to the interfaceList
    if (interfaceI.doPatternMatching()) {
      // Set the interface list
      if (interfaceI.get_$Sub().printInterfaces().equals("")) {
        interfaceI.set_$newInterfaces(newInterface);
      } else {
        interfaceI.set_$newInterfaces(interfaceI.get_$Sub().printInterfaces() + "," + newInterface);
      }
      // Replace interface list
      interfaceI.doReplacement();
      return true;

      // if the class has no interfaces, to implement yet, create new
      // interfaceList for the class to inherit the interface
    } else {
      if (createInheritanceToInterface(className, newInterface, ast)) {
        return true;
      }
    }
    return false;
  }

  // Introduces an inheritance between subclass and superclass
  public boolean createInheritanceToClass(
      String subclass, String superclass, ASTCDCompilationUnit ast) {

    CreateInheritanceToClass addSuperclass = new CreateInheritanceToClass(ast);
    addSuperclass.set_$subclass(subclass);
    addSuperclass.set_$superclass(superclass);
    if (addSuperclass.doPatternMatching()) {
      addSuperclass.doReplacement();
      return true;
    }
    return false;
  }

  // Replaces all extend to oldClass by newClass
  public boolean changeInheritanceClass(
      String oldSuperclass, String newSuperclass, ASTCDCompilationUnit ast) {

    ChangeInheritanceClasses changeInheritance = new ChangeInheritanceClasses(ast);
    changeInheritance.set_$oldSuperclass(oldSuperclass);
    changeInheritance.set_$newSuperclass(newSuperclass);
    if (changeInheritance.doPatternMatching()) {
      changeInheritance.doReplacement();
      return true;
    }
    return false;
  }

  /**
   * Gets a method
   *
   * @param methodName
   * @param className
   * @param ast
   * @return
   */
  public ASTCDMethod getMethod(String methodName, String className, ASTCDCompilationUnit ast) {

    FindMethod findMethod = new FindMethod(ast);
    findMethod.set_$name(methodName);
    findMethod.set_$className(className);
    if (findMethod.doPatternMatching()) {
      return findMethod.get_$Method().deepClone();
    }
    Log.info("0xF4131: Not found method", TransformationUtility.class.getName());
    return null;
  }

  /**
   * Gets the first method
   *
   * @param className
   * @param ast
   * @return
   */
  public ASTCDMethod getFirstMethod(String className, ASTCDCompilationUnit ast) {

    GetMethod getMethod = new GetMethod(ast);
    getMethod.set_$className(className);
    if (getMethod.doPatternMatching() && getMethod.get_$Method() != null) {
      return getMethod.get_$Method().deepClone();
    }
    return null;
  }

  /**
   * Gets the first attribute
   *
   * @param className
   * @param ast
   * @return
   */
  public ASTCDAttribute getFirstAttribute(String className, ASTCDCompilationUnit ast) {

    GetAttribute getAttribute = new GetAttribute(ast);
    getAttribute.set_$className(className);

    if (getAttribute.doPatternMatching()) {
      return getAttribute.get_$Attribute().deepClone();
    }
    return null;
  }

  /**
   * Gets an attribute
   *
   * @param attributeName
   * @param className
   * @param ast
   * @return
   */
  public ASTCDAttribute getAttribute(
      String attributeName, String className, ASTCDCompilationUnit ast) {

    FindAttribute findAttribute = new FindAttribute(ast);
    findAttribute.set_$name(attributeName);
    findAttribute.set_$className(className);
    if (findAttribute.doPatternMatching()) {
      return findAttribute.get_$Attribute().deepClone();
    }
    Log.info("0xF4132: Not found attribute", TransformationUtility.class.getName());
    return null;
  }

  /**
   * Adds a method to a class
   *
   * @param method
   * @param className
   * @param ast
   * @return
   */
  public boolean addMethod(ASTCDMethod method, String className, ASTCDCompilationUnit ast) {

    AddMethod addMethod = new AddMethod(ast);
    addMethod.set_$className(className);
    addMethod.set_$Method(method.deepClone());
    if (addMethod.doPatternMatching()) {
      addMethod.doReplacement();
      return true;
    }
    Log.info("0xF4133: Could not add method", TransformationUtility.class.getName());
    return false;
  }

  /**
   * Adds a method to an interface
   *
   * @param method
   * @param interfaceName
   * @param ast
   * @return
   */
  public boolean addMethodToInterface(
      ASTCDMethod method, String interfaceName, ASTCDCompilationUnit ast) {

    AddMethodInterface addMethod = new AddMethodInterface(ast);
    addMethod.set_$interfaceName(interfaceName);
    addMethod.set_$Method(method.deepClone());
    if (addMethod.doPatternMatching()) {
      addMethod.doReplacement();
      return true;
    }
    Log.info("0xF4134: Could not add method", TransformationUtility.class.getName());
    return false;
  }
}
