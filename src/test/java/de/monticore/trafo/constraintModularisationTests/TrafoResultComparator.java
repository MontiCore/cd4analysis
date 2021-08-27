/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo.constraintModularisationTests;


import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Alexander Wilts on 30.04.2017.
 *
 * Util class to help compare ASTs before and after transformations.
 */
public class TrafoResultComparator {

  ASTCDCompilationUnit astBefore;
  ASTCDCompilationUnit astAfter;

  public TrafoResultComparator(ASTCDCompilationUnit astBefore, ASTCDCompilationUnit astAfter){
    this.astBefore=astBefore;
    this.astAfter=astAfter;
  }

  private boolean checkForChangedMethods(ASTCDCompilationUnit astBefore, ASTCDCompilationUnit astAfter, String className, String... addedMethods){

    Optional<ASTCDClass> cDClassBefore = astBefore.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    Optional<ASTCDClass> cDClassAfter = astAfter.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    if(!cDClassBefore.isPresent() || !cDClassAfter.isPresent() ) {
      return false;
    }
    else{
      List<ASTCDMethod> cdMethodsAfter = cDClassAfter.get().getCDMemberList()
        .stream().filter(x->x instanceof ASTCDMethod)
        .map(x->(ASTCDMethod)x).collect(Collectors.toList());

      for(String methodName : addedMethods){

        Optional<ASTCDMethod> cDMethod = cdMethodsAfter
            .stream()
            .filter(x -> x.getName().equals(methodName))
            .findAny();
        if(!cDMethod.isPresent()){
          return false;
        }
      }
    }
    return true;
  }

  public boolean checkAddedMethods(String className, String... addedMethods){
    return checkForChangedMethods(astBefore,astAfter,className, addedMethods);
  }

  public boolean checkRemovedMethods(String className, String... addedMethods){
    return checkForChangedMethods(astAfter,astBefore,className, addedMethods);
  }

  //Expects negative changedBy-count in order to check for removed methods
  public boolean checkChangedMethodCount(String className, int changedBy){
    Optional<ASTCDClass> cDClassBefore = astBefore.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    Optional<ASTCDClass> cDClassAfter = astAfter.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    if(!cDClassBefore.isPresent()) {
      return false;
    }
    if(!cDClassAfter.isPresent()) {
      return false;
    }

    int methodCountBefore = cDClassBefore.get().getCDMethodList().size();
    int methodCountAfter = cDClassAfter.get().getCDMethodList().size();

    if(methodCountBefore+changedBy!=methodCountAfter){
      return false;
    }
    return true;
  }

  public boolean checkAddedClass(String className) {

    Optional<ASTCDClass> cDClassBefore = astBefore.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    Optional<ASTCDClass> cDClassAfter = astAfter.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    if(cDClassBefore.isPresent()){
      return false;
    }

    if(!cDClassAfter.isPresent()){
      return false;
    }

    return true;
  }

  //Expects negative changedBy-count in order to check for removed methods
  public boolean checkChangedClassCount(int changedBy){

    int classCountBefore = (int)astBefore.getCDDefinition()
        .streamCDElements().filter(x->x instanceof ASTCDClass).count();

    int classCountAfter = (int)astAfter.getCDDefinition()
        .streamCDElements().filter(x->x instanceof ASTCDClass).count();

    if(classCountBefore+changedBy!=classCountAfter){
      return false;
    }
    return true;
  }

  public boolean checkAddedAttribute(String className, String attributeName) {

    Optional<ASTCDClass> cDClassBefore = astBefore.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    Optional<ASTCDClass> cDClassAfter = astAfter.getCDDefinition()
        .getCDElementList()
        .stream()
        .filter(x -> (x instanceof ASTCDClass))
        .map(x -> (ASTCDClass) x)
        .filter(x -> x.getName().equals(className))
        .findAny();

    if(!cDClassBefore.isPresent()){
      return false;
    }

    if(!cDClassAfter.isPresent()){
      return false;
    }

    Optional<ASTCDAttribute> attributeBefore = cDClassBefore.get()
        .getCDAttributeList()
        .stream()
        .filter(x -> x.getName().equals(attributeName))
        .findAny();

    Optional<ASTCDAttribute> attributeAfter = cDClassAfter.get()
        .getCDAttributeList()
        .stream()
        .filter(x -> x.getName().equals(attributeName))
        .findAny();

    if(attributeBefore.isPresent()){
      return false;
    }

    if(!attributeAfter.isPresent()){
      return false;
    }

    return true;
  }
}

