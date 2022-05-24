package de.monticore.ow2cw;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CDInheritanceHelper {

  protected static MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

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

    for (ASTCDType superSuper : getAllSuper(
        resolveClosestType(targetNode, newSuper.printType(pp), artifactScope), artifactScope)) {
      if (superSuper.getSymbol().getFullName().equals(targetNode.getSymbol().getFullName())) {
        return false;
      }
    }
    return true;
  }

  /**
   * check if attribute is already in superclass/interface
   */
  public static boolean isAttributInSuper(ASTCDAttribute attribute, ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    return findAttributeInSuper(attribute, cdType, artifactScope).isPresent();
  }

  /**
   * find duplicate attribute in superclass/interface
   */
  public static Optional<ASTCDAttribute> findAttributeInSuper(ASTCDAttribute attribute,
      ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
      if (supertype != cdType) {
        for (ASTCDAttribute duplicate : supertype.getCDAttributeList()) {
          if (attribute.getName().equals(duplicate.getName()) && attribute.printType()
              .equals(duplicate.printType())) {
            return Optional.of(duplicate);
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * return all superclasses and interfaces of cdType
   */
  public static List<ASTCDType> getAllSuper(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType, artifactScope));
    superList.addAll(getDirectInterfaces(cdType, artifactScope));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for (ASTCDType nextSuper : superList) {
      nextSuperSuperList.addAll(getAllSuper(nextSuper, artifactScope));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  /**
   * return all superclasses from SuperClassList since I cannot use getSymbol()
   * .getSuperClassesOnly()
   */
  public static List<ASTCDType> getDirectSuperClasses(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      extendsList.add(resolveClosestType(cdType, superType.printType(pp), artifactScope));
    }
    return extendsList;
  }

  /**
   * return all interfaces from InterfaceList since I cannot use getSymbol().getInterfaceList()
   */
  public static List<ASTCDType> getDirectInterfaces(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      interfaceList.add(resolveClosestType(cdType, superType.printType(pp), artifactScope));
    }
    return interfaceList;
  }

  /**
   * helper-method to resolve extended/implemented class/interface
   */
  public static ASTCDType resolveClosestType(ASTCDType srcNode, String targetName,
      ICD4CodeArtifactScope artifactScope) {

    List<CDTypeSymbol> symbolList = artifactScope.resolveCDTypeDownMany(targetName);

    if (symbolList.isEmpty()) {
      Log.error(String.format("0xCDD15: Could not resolve %s", targetName));
    }

    CDTypeSymbol current = symbolList.get(0);
    int currentMatch = getPositionWhereTextDiffer(current.getFullName(),
        srcNode.getSymbol().getFullName());
    int nextMatch;

    for (CDTypeSymbol symbol : symbolList) {
      nextMatch = getPositionWhereTextDiffer(symbol.getFullName(),
          srcNode.getSymbol().getFullName());
      if (currentMatch < nextMatch) {
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
    while (b.length() > position && a.length() > position && a.charAt(position) == b.charAt(
        position)) {
      position++;
    }
    return position;
  }

  public static boolean isSuperOf(String srcName, String targetName, ICD4CodeArtifactScope scope) {

    Optional<CDTypeSymbol> optSrc = scope.resolveCDTypeDown(srcName);
    Optional<CDTypeSymbol> targetSrc = scope.resolveCDTypeDown(targetName);
    if (optSrc.isPresent() && targetSrc.isPresent()) {
      return CDInheritanceHelper.getAllSuper(targetSrc.get().getAstNode(), scope)
          .contains(optSrc.get());
    }
    return false;
  }

}
