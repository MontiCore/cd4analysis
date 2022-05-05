package de.monticore.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class CDInheritanceHelper {

  protected static MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  /**
   * add each inheritance-relations exclusive to srcCD to targetCD
   * unless it causes cyclical inheritance
   */
  public static void copyInheritance(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit targetCD) {

    ICD4CodeArtifactScope scope2;

    // for each class in first, find the corresponding class in second and add all
    // legal extends/implements relations
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {

      // re-build symbol table
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);

      ASTCDClass targetClass = null;

      // I don't use resolve to avoid reflection
      for (ASTCDClass someClass : targetCD.getCDDefinition().getCDClassesList()){
        if (srcClass.getSymbol().getFullName().equals(someClass.getSymbol().getFullName())){
          targetClass = someClass;
        }
      }

      if (targetClass == null) {
        Log.error(
            String.format("0xCDD08: Could not find class %s", srcClass.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetClass.getSuperclassList());
        for (ASTMCObjectType superType : srcClass.getSuperclassList()) {
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            extendsList.add(superType);
          }
          targetClass.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());

        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetClass.getInterfaceList());
        for (ASTMCObjectType superType : srcClass.getInterfaceList()) {
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            interfaceList.add(superType);
          }
          targetClass.setCDInterfaceUsage(
              CD4CodeMill.cDInterfaceUsageBuilder().addAllInterface(interfaceList).build());
        }
      }
    }
    for (ASTCDInterface srcInterface : srcCD.getCDDefinition().getCDInterfacesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);

      ASTCDInterface targetInterface = null;

      for (ASTCDInterface someInterface : targetCD.getCDDefinition().getCDInterfacesList()){
        if (srcInterface.getSymbol().getFullName().equals(someInterface.getSymbol().getFullName())){
          targetInterface = someInterface;
        }
      }

      if (targetInterface == null) {
        Log.error(
            String.format("0xCDD09: Could not find interface %s",
                srcInterface.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetInterface.getInterfaceList());
        for (ASTMCObjectType superType : srcInterface.getInterfaceList()) {
          if (isNewSuper(superType, targetInterface,scope2) && inducesNoInheritanceCycle(superType,
              targetInterface,scope2)) {
            extendsList.add(superType);
          }
          targetInterface.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);
    removeRedundantAttributes(targetCD);
  }

  /**
   * check if newSuper is not already a superclass/interface of targetNode
   */
  public static boolean isNewSuper(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType oldSuper : getAllSuper(targetNode, artifactScope)) {
      if (oldSuper.getSymbol().getFullName().contains(newSuper.printType(pp))) {
        return false;
      }
    }
    return true;
  }

  /**
   * check if newSuper does not cause cyclical inheritance
   */
  public static boolean inducesNoInheritanceCycle(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {

    for (ASTCDType superSuper : getAllSuper(resolveClosestType(targetNode, newSuper.printType(pp),
        artifactScope), artifactScope)) {
      if (superSuper.getSymbol().getFullName().equals(targetNode.getSymbol().getFullName())) {
        return false;
      }
    }
    return true;
  }

  /**
   * remove redundant attributes
   */
  public static void removeRedundantAttributes(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuper(attribute, astcdClass, artifactScope)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (findInSuper(attribute, astcdInterface, artifactScope)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
  }

  /**
   * check if attribute is in superclass/interface
   */
  public static boolean findInSuper(ASTCDAttribute attribute1, ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
      if (supertype != cdType) {
        for (ASTCDAttribute attribute2 : supertype.getCDAttributeList()) {
          if (attribute1.getName().equals(attribute2.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * return all superclasses and interfaces of cdType
   */
  public static List<ASTCDType> getAllSuper(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType,artifactScope));
    superList.addAll(getDirectInterfaces(cdType,artifactScope));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for (ASTCDType nextSuper : superList) {
      nextSuperSuperList.addAll(getAllSuper(nextSuper, artifactScope));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  /**
   * return all superclasses from SuperClassList
   * since I cannot use getSymbol().getSuperClassesOnly()
   */
  public static List<ASTCDType> getDirectSuperClasses(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      extendsList.add(resolveClosestType(cdType,superType.printType(pp),artifactScope));
    }
    return extendsList;
  }

  /**
   * return all interfaces from InterfaceList
   * since I cannot use getSymbol().getInterfaceList()
   */
  public static List<ASTCDType> getDirectInterfaces(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      interfaceList.add(resolveClosestType(cdType,superType.printType(pp),artifactScope));
    }
    return interfaceList;
  }

  /**
   * helper-method to resolve extended/implemented class/interface
   */
  public static ASTCDType resolveClosestType(ASTCDType srcNode,
      String targetName, ICD4CodeArtifactScope artifactScope){

    List<CDTypeSymbol> symbolList =
        artifactScope.resolveCDTypeDownMany(targetName);

    if (symbolList.isEmpty()){
      Log.error(String.format("0xCDD15: Could not resolve %s", targetName));
    }

    CDTypeSymbol current = symbolList.get(0);
    int currentMatch = getPositionWhereTextDiffer(current.getFullName(),srcNode.getSymbol().getFullName());
    int nextMatch;

    for (CDTypeSymbol symbol : symbolList){
      nextMatch = getPositionWhereTextDiffer(symbol.getFullName(),
          srcNode.getSymbol().getFullName());
      if (currentMatch < nextMatch){
        current = symbol;
      }

    }

    return current.getAstNode();

  }

  /**
   * could not find an existing method like that
   */
  private static int getPositionWhereTextDiffer(String a, String b) {
    int position = 0;
    while ( b.length() > position &&
        a.length() > position &&
        a.charAt(position) == b.charAt(position)) {
      position++;
    }
    return position;
  }


  protected static <T,E> Stream<T> retainAll(Collection<T> input, Collection<E> otherSet,
      BiFunction<T,E,Boolean> pred){
    return input.stream()
        .filter(z->
            otherSet.stream().anyMatch(o->pred.apply(z,o))
        );
  }

}
