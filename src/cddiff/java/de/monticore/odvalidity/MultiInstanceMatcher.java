/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class MultiInstanceMatcher {

  private final OD2CDMatcher matcher;

  private static final String INSTANCE_OF_STEREOTYPE = "instanceof";

  public MultiInstanceMatcher(OD2CDMatcher matcher) {
    this.matcher = matcher;
  }

  /**
   * Returns true if the od is within the baseCD semantics but not within the compareCD semantics.
   * Also checks inheritance of instanced classes in both CDs Returns false otherwise.
   */
  public boolean isDiffWitness(
      CDSemantics semantic,
      ASTCDCompilationUnit baseCD,
      ASTCDCompilationUnit compCD,
      ASTODArtifact odAll) {

    ASTObjectDiagram od = odAll.getObjectDiagram();
    List<ASTODObject> objectList = ODHelper.getAllObjects(od);

    // generate scopes
    ICD4CodeArtifactScope baseScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(baseCD);
    ICD4CodeArtifactScope compScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(compCD);

    Log.print(
        System.lineSeparator()
            + String.format(
                "[CHECK] Check if %s in sem(%s)\\sem(%s)",
                od.getName(),
                baseCD.getCDDefinition().getName(),
                compCD.getCDDefinition().getName())
            + System.lineSeparator());

    Log.print(
        System.lineSeparator()
            + "BASE_CD: "
            + baseCD.getCDDefinition().getName()
            + System.lineSeparator());

    // OD has to be in the semantics of the base CD
    if (!matcher.checkODValidity(semantic, odAll, baseCD)) {
      Log.println(System.lineSeparator() + "[RESULT] " + od.getName() + " is not a diff-witness.");
      return false;
    }

    Log.print(
        System.lineSeparator()
            + "COMPARE_CD: "
            + compCD.getCDDefinition().getName()
            + System.lineSeparator());
    if (!matcher.checkODValidity(semantic, odAll, compCD)) {
      // OD is a diffWitness for OD in semantics of base CD and in semantics of comp CD
      Log.println(System.lineSeparator() + "[RESULT] " + od.getName() + " is a diff-witness.");
      return true;
    }

    // boolean and list for collecting all objects which are witnesses
    boolean isWitness = false;
    List<String> witnessingObjects = new ArrayList<>();

    // iterate over all objects
    for (ASTODObject obj : objectList) {
      // for closed world semantics call hasObjectSameInheritance()
      if (Semantic.isClosedWorld(semantic)) {
        if (!hasObjectSameInheritance(obj, baseScope, compScope)) {
          // object has not the same inheritance in both CDs
          // -> DiffWitness
          isWitness = true;
          witnessingObjects.add(obj.getMCObjectType().printType());
        }
      }
      // for open world semantics call checkInheritanceOpenWorld()
      else if (Semantic.isOpenWorld(semantic)) {
        if (!checkInheritanceOpenWorld(obj, baseScope, compScope, compCD)) {
          // inheritance contradicts that both CDs have the same open world semantics
          // -> DiffWitness
          isWitness = true;
          witnessingObjects.add(obj.getMCObjectType().printType());
        }
      }
    }

    // if DiffWitness -> print all witnessing objects
    if (isWitness) {
      Log.print("OD is a diffWitness in " + semantic + "\n");
      Log.print("Following object types are witnesses:\n");
      for (String objType : witnessingObjects) {
        Log.print(objType + "\n");
      }
      return true;
    }

    Log.print("OD is no DiffWitness\n");
    return false;
  }

  /** Returns if an object has the same inheritance in two CDs */
  private boolean hasObjectSameInheritance(
      ASTODObject obj, ICD4CodeArtifactScope baseScope, ICD4CodeArtifactScope compScope) {

    // get object type
    String objType = obj.getMCObjectType().printType();

    // get super set of base CD
    Set<String> baseSuperSet = getSuperSet(objType, baseScope);
    // get super set of compare CD
    Set<String> compSuperSet = getSuperSet(objType, compScope);

    // compare Super Sets
    if (!baseSuperSet.equals(compSuperSet)) {
      Log.print("object of type " + objType + " has not the same inheritance in both CDs\n");
      return false;
    }

    // repeat equality check for superset in stereotype of the object instead of the baseSuperSet
    // if stereotype "instanceOf" is present
    Optional<Set<String>> stereotypeSuperSet = getSuperSetFromStereotype(obj);
    if (stereotypeSuperSet.isPresent()) {
      if (!stereotypeSuperSet.get().equals(compSuperSet)) {
        Log.print(
            "object of type "
                + objType
                + " has not the same inheritance in both CDs for "
                + "super set in stereotype\n");
        return false;
      }
    }

    // no diff found
    return true;
  }

  /**
   * Returns if the inheritance of an object contradicts that both CDs have the same open world
   * semantics
   */
  private boolean checkInheritanceOpenWorld(
      ASTODObject obj,
      ICD4CodeArtifactScope baseScope,
      ICD4CodeArtifactScope compScope,
      ASTCDCompilationUnit compCD) {

    // get object type
    String objType = obj.getMCObjectType().printType();

    // get super set of base CD
    Set<String> baseSuperSet = getSuperSet(objType, baseScope);
    // get super set of compare CD
    Set<String> compSuperSet = getSuperSet(objType, compScope);
    // get sub classes of objType in compCD
    Set<String> compSubClasses = getSubClasses(compCD, objType, compScope);

    // compare Super Sets
    if (!baseSuperSet.containsAll(compSuperSet)) {
      // baseSuperSet is no superset of compSuperSet
      Log.print("Superset of object with type " + objType + " of compCD is no subset of baseCD\n");
      return false;
    }

    // check for cyclic inheritance
    compSubClasses.retainAll(baseSuperSet);
    if (!compSubClasses.isEmpty()) {
      // intersection of baseSuperSet and subclasses in compCD is not empty
      Log.print(
          "Superset of object with type "
              + objType
              + " of baseCD contains a subclass from "
              + "the object in compCD\n");
      return false;
    }

    // repeat checks for superset in stereotype of the object instead of the baseSuperSet
    // if stereotype "instanceof" is present
    Optional<Set<String>> stereotypeSuperSet = getSuperSetFromStereotype(obj);
    if (stereotypeSuperSet.isPresent()) {
      // compare Super Sets
      if (!stereotypeSuperSet.get().containsAll(compSuperSet)) {
        Log.print(
            "Superset of object with type "
                + objType
                + " of compCD is no subset of baseCD "
                + "for superset in stereotype\n");
        return false;
      }
      // check for cyclic inheritance
      compSubClasses = getSubClasses(compCD, objType, compScope);
      compSubClasses.retainAll(stereotypeSuperSet.get());
      if (!compSubClasses.isEmpty()) {
        Log.print(
            "Superset of object with type "
                + objType
                + " of baseCD contains a subclass from "
                + "the object in compCD for superset in stereotype\n");
        return false;
      }
    }

    // no diff found
    return true;
  }

  /**
   * Returns set of superclasses and implemented interfaces of the class "objType" Returns the full
   * qualified names Returns the empty set if class symbol is not found
   */
  public static Set<String> getSuperSet(String objType, ICD4CodeArtifactScope scope) {

    // get class symbol
    Optional<CDTypeSymbol> classSymbol = scope.resolveCDTypeDown(objType);

    // initialize output variable
    Set<String> superSet = new HashSet<>();

    // get all superclasses and implemented interfaces
    if (classSymbol.isPresent()) {
      Set<ASTCDType> astSuperSet =
          CDInheritanceHelper.getAllSuper(classSymbol.get().getAstNode(), scope);

      // transform set of AST nodes to a set of their full qualified names
      for (ASTCDType cdType : astSuperSet) {
        superSet.add(cdType.getSymbol().getInternalQualifiedName());
      }
    } else {
      // print warning if symbol does not exist
      // does not affect the correctness of the overall output
      ASTCDCompilationUnit cd = (ASTCDCompilationUnit) scope.getAstNode();
      String cdName = cd.getCDDefinition().getName();
      Log.warn("class symbol of " + objType + " not found in CD \"" + cdName + "\"");
    }

    return superSet;
  }

  /** Return all subclasses from class "objType" Returns the full qualified names */
  private Set<String> getSubClasses(
      ASTCDCompilationUnit astcd, String objType, ICD4CodeArtifactScope scope) {

    // get class symbol
    Optional<CDTypeSymbol> classSymbol = scope.resolveCDTypeDown(objType);
    // get all classes of astcd
    List<ASTCDClass> astClasses = astcd.getCDDefinition().getCDClassesList();

    // collect all classes which have the class "objType" as superclass
    // the set contains the class "objType" itself
    Set<ASTCDClass> astSubClasses = new HashSet<>();
    for (ASTCDClass astClass : astClasses) {
      if (CDInheritanceHelper.isSuperOf(
          objType, astClass.getSymbol().getInternalQualifiedName(), scope)) {
        astSubClasses.add(astClass);
      }
    }

    // initialize output variable
    Set<String> subClasses = new HashSet<>();

    // transform set of AST nodes to a set of their full qualified names
    for (ASTCDType cdType : astSubClasses) {
      subClasses.add(cdType.getSymbol().getInternalQualifiedName());
    }

    // remove the class "objType" from the set of its subclasses
    if (classSymbol.isPresent()) {
      subClasses.remove(classSymbol.get().getInternalQualifiedName());
    } else {
      // print warning if symbol does not exist
      // does not affect the correctness of the overall output
      ASTCDCompilationUnit cd = (ASTCDCompilationUnit) scope.getAstNode();
      String cdName = cd.getCDDefinition().getName();
      Log.warn("class symbol of " + objType + " not found in CD \"" + cdName + "\"");
    }

    return subClasses;
  }

  /**
   * Returns the set of "instanceof" from an object if present in a stereotype Returns
   * Optional.empty() otherwise
   */
  public static Optional<Set<String>> getSuperSetFromStereotype(ASTODObject obj) {

    // get object modifier
    ASTModifier modifier = obj.getModifier();
    // check if stereotype is present
    if (modifier.isPresentStereotype()) {
      // check if stereotype contains "instanceOf"
      if (modifier.getStereotype().contains(INSTANCE_OF_STEREOTYPE)) {

        // get String value of "instanceOf"
        String instanceStereotype = modifier.getStereotype().getValue(INSTANCE_OF_STEREOTYPE);
        // delete whitespaces
        instanceStereotype = instanceStereotype.replace(" ", "");
        // split String by ","
        String[] temp = instanceStereotype.split(",");
        // return split "instanceOf" values in a set
        Set<String> superSet = new HashSet<>();
        Collections.addAll(superSet, temp);
        return Optional.of(superSet);
      }
    }

    // no stereotype found or stereotype does not contain "instanceOf"
    return Optional.empty();
  }

  /*methods not in use*/
  private boolean isClassInPackage(String objType) {
    return objType.contains(".");
  }

  private boolean isClassInPackage(ASTODObject obj) {

    String objType = obj.getMCObjectType().printType();
    return isClassInPackage(objType);
  }

  private List<String> getPackageNames(String objType) {

    if (!isClassInPackage(objType)) {
      Log.warn("error in getPackageName(): class is not in a package");
    }

    String[] temp = objType.split("\\.");
    List<String> fixedPackageList = Arrays.asList(temp);
    List<String> packageList = new ArrayList<>(fixedPackageList);
    packageList.remove((packageList.size() - 1));

    return packageList;
  }

  private String getTypeWithoutPackage(String objType) {

    if (!isClassInPackage(objType)) {
      Log.warn("error in getTypeWithoutPackage(): class is not in a package");
    }

    String[] temp = objType.split("\\.");
    return temp[(temp.length - 1)];
  }
}
