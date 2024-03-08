/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class CDInheritanceHelper {

  /** check if attribute is already in superclass/interface */
  public static boolean isAttributInSuper(
      ASTCDAttribute attribute, ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    return findAttributeInSuper(attribute, cdType, artifactScope).isPresent();
  }

  /** find duplicate attribute in superclass/interface */
  public static Optional<ASTCDAttribute> findAttributeInSuper(
      ASTCDAttribute attribute, ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
      if (supertype != cdType) {
        for (ASTCDAttribute duplicate : supertype.getCDAttributeList()) {
          if (attribute.getName().equals(duplicate.getName())
              && attribute.getMCType().printType().equals(duplicate.printType())) {
            return Optional.of(duplicate);
          }
        }
      }
    }
    return Optional.empty();
  }

  /** return all superclasses and interfaces of cdType */
  public static Set<ASTCDType> getAllSuper(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> superSet = new HashSet<>(getDirectSuperClasses(cdType, artifactScope));
    superSet.addAll(getDirectInterfaces(cdType, artifactScope));

    Set<ASTCDType> nextSuperSuperSet = new HashSet<>();
    for (ASTCDType nextSuper : superSet) {
      nextSuperSuperSet.addAll(getAllSuper(nextSuper, artifactScope));
    }
    superSet.addAll(nextSuperSuperSet);
    superSet.add(cdType);
    return superSet;
  }

  /**
   * return all superclasses from SuperClassList since I cannot use getSymbol()
   * .getSuperClassesOnly()
   */
  public static Set<ASTCDType> getDirectSuperClasses(
      ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> extendsSet = new HashSet<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      String internalName = internalQualifiedName(superType.printType(), artifactScope);
      extendsSet.add(resolveClosestType(cdType, internalName, artifactScope));
    }
    return extendsSet;
  }

  /** return all interfaces from InterfaceList since I cannot use getSymbol().getInterfaceList() */
  public static Set<ASTCDType> getDirectInterfaces(
      ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> interfaceSet = new HashSet<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      interfaceSet.add(resolveClosestType(cdType, superType.printType(), artifactScope));
    }
    return interfaceSet;
  }

  /** helper-method to resolve extended/implemented class/interface */
  public static ASTCDType resolveClosestType(
      ASTCDType srcNode, String targetName, ICDBasisScope scope) {

    ICDBasisScope currentScope = srcNode.getEnclosingScope();
    List<CDTypeSymbol> symbolList;

    while (currentScope != scope) {
      symbolList = currentScope.resolveCDTypeDownMany(targetName);
      if (!symbolList.isEmpty()) {
        break;
      }
      currentScope = currentScope.getEnclosingScope();
    }

    symbolList = currentScope.resolveCDTypeDownMany(targetName);

    if (symbolList.isEmpty()) {
      Log.error(String.format("0xCDD08: Could not resolve %s", targetName));
    }

    CDTypeSymbol current = symbolList.get(0);
    int currentMatch =
        getPositionWhereTextDiffer(
            current.getInternalQualifiedName(), srcNode.getSymbol().getInternalQualifiedName());
    int nextMatch;

    for (CDTypeSymbol symbol : symbolList) {
      nextMatch =
          getPositionWhereTextDiffer(
              symbol.getInternalQualifiedName(), srcNode.getSymbol().getInternalQualifiedName());
      if (currentMatch < nextMatch) {
        current = symbol;
      }
    }

    return current.getAstNode();
  }

  /** could not find an existing method like that */
  private static int getPositionWhereTextDiffer(String a, String b) {
    int position = 0;
    while (b.length() > position
        && a.length() > position
        && a.charAt(position) == b.charAt(position)) {
      position++;
    }
    return position;
  }

  public static boolean isSuperOf(String srcName, String targetName, ICD4CodeArtifactScope scope) {

    Optional<CDTypeSymbol> optSrc = scope.resolveCDTypeDown(srcName);
    Optional<CDTypeSymbol> optTarget = scope.resolveCDTypeDown(targetName);
    if (optSrc.isPresent() && optTarget.isPresent()) {
      return CDInheritanceHelper.getAllSuper(optTarget.get().getAstNode(), scope)
          .contains(optSrc.get().getAstNode());
    }
    return false;
  }

  public static boolean isSuperOf(String srcName, String targetName, ASTCDCompilationUnit cd) {

    Optional<CDTypeSymbol> optSrc = cd.getEnclosingScope().resolveCDTypeDown(srcName);
    Optional<CDTypeSymbol> optTarget = cd.getEnclosingScope().resolveCDTypeDown(targetName);
    if (optSrc.isPresent() && optTarget.isPresent()) {
      return CDDiffUtil.getAllSuperTypes(optTarget.get().getAstNode(), cd.getCDDefinition())
          .contains(optSrc.get().getAstNode());
    }
    return false;
  }

  protected static String internalQualifiedName(
      String fullName, ICD4CodeArtifactScope artifactScope) {
    String artifactName = "";
    if (!artifactScope.getPackageName().isEmpty()) {
      artifactName += artifactScope.getPackageName() + ".";
    }
    if (artifactScope.isPresentName()) {
      artifactName += artifactScope.getName() + ".";
    }
    if (fullName.startsWith(artifactName)) {
      return fullName.substring(artifactName.length());
    }
    return fullName;
  }
}
