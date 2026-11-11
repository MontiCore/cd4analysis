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
  public static boolean isAttributInSuper(ASTCDAttribute attribute, ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    return findAttributeInSuper(attribute, cdType, artifactScope).isPresent();
  }
  
  /** find duplicate attribute in superclass/interface */
  public static Optional<ASTCDAttribute> findAttributeInSuper(ASTCDAttribute attribute,
      ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
      if (supertype != cdType) {
        for (ASTCDAttribute duplicate : supertype.getCDAttributeList()) {
          if (attribute.getName().equals(duplicate.getName()) && attribute.getMCType().printType()
              .equals(duplicate.getMCType().printType())) {
            return Optional.of(duplicate);
          }
        }
      }
    }
    return Optional.empty();
  }
  
  /** return all superclasses and interfaces of cdType */
  public static Set<ASTCDType> getAllSuper(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> superSet = new LinkedHashSet<>(getDirectSuperClasses(cdType, artifactScope));
    superSet.addAll(getDirectInterfaces(cdType, artifactScope));
    
    Set<ASTCDType> nextSuperSuperSet = new LinkedHashSet<>();
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
  public static Set<ASTCDType> getDirectSuperClasses(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> extendsSet = new LinkedHashSet<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      resolveClosestType(cdType, superType.printType(), artifactScope).ifPresent(extendsSet::add);
    }
    return extendsSet;
  }
  
  /** return all interfaces from InterfaceList since I cannot use getSymbol().getInterfaceList() */
  public static Set<ASTCDType> getDirectInterfaces(ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    Set<ASTCDType> interfaceSet = new LinkedHashSet<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      resolveClosestType(cdType, superType.printType(), artifactScope).ifPresent(interfaceSet::add);
    }
    return interfaceSet;
  }
  
  /** helper-method to resolve extended/implemented class/interface */
  public static Optional<ASTCDType> resolveClosestType(ASTCDType srcNode, String targetName,
      ICD4CodeArtifactScope scope) {
    
    ICDBasisScope currentScope = srcNode.getEnclosingScope();
    
    List<CDTypeSymbol> symbolList = currentScope.resolveCDTypeMany(mkFullName(targetName, scope));
    
    while (currentScope != null && currentScope != scope) {
      symbolList.addAll(currentScope.resolveCDTypeDownMany(internalQualifiedName(targetName,
          scope)));
      currentScope = currentScope.getEnclosingScope();
    }
    
    symbolList.addAll(scope.resolveCDTypeDownMany(internalQualifiedName(targetName, scope)));
    
    if (symbolList.isEmpty()) {
      Log.error(String.format("0xCDD08: Could not resolve %s", internalQualifiedName(targetName,
          scope)));
      return Optional.empty();
    }
    
    CDTypeSymbol current = symbolList.get(0);
    int currentMatch = getPositionWhereTextDiffer(current.getFullName(), srcNode.getSymbol()
        .getFullName());
    int nextMatch;
    
    for (CDTypeSymbol symbol : symbolList) {
      nextMatch = getPositionWhereTextDiffer(symbol.getFullName(), srcNode.getSymbol()
          .getFullName());
      if (currentMatch < nextMatch) {
        current = symbol;
      }
    }
    
    if (!current.getFullName().contains(scope.getFullName())) {
      Log.error(String.format("0xCDD09: Could not resolve %s in %s", internalQualifiedName(
          targetName, scope), scope.getFullName()));
    }
    
    return Optional.of(current.getAstNode());
  }
  
  /** could not find an existing method like that */
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
    Optional<CDTypeSymbol> optTarget = scope.resolveCDTypeDown(targetName);
    if (optSrc.isPresent() && optTarget.isPresent()) {
      return CDInheritanceHelper.getAllSuper(optTarget.get().getAstNode(), scope).contains(optSrc
          .get().getAstNode());
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
  
  protected static String mkFullName(String name, ICD4CodeArtifactScope artifactScope) {
    String artifactName = "";
    if (!artifactScope.getPackageName().isEmpty()) {
      artifactName += artifactScope.getPackageName() + ".";
    }
    if (artifactScope.isPresentName()) {
      artifactName += artifactScope.getName() + ".";
    }
    if (!name.startsWith(artifactName)) {
      return artifactName + name;
    }
    return name;
  }
  
  protected static String internalQualifiedName(String fullName,
      ICD4CodeArtifactScope artifactScope) {
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
