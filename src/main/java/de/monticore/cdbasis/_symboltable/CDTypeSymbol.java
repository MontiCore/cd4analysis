/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;

import java.util.List;

public class CDTypeSymbol extends CDTypeSymbolTOP {
  public CDTypeSymbol(String name) {
    super(name);
  }

  /** get a list of all the methods the type definition can access */
  public List<CDMethodSignatureSymbol> getMethodSignatureList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    if (getSpannedScope() instanceof ICDAssociationScope) {
      return ((ICD4CodeBasisScope) getSpannedScope()).getLocalCDMethodSignatureSymbols();
    }

    return Lists.newArrayList();
  }

  /** search in the scope for methods with a specific name */
  public List<CDMethodSignatureSymbol> getMethodSignatureList(String methodname) {
    return ((ICD4CodeBasisScope) getSpannedScope()).resolveCDMethodSignatureMany(methodname);
  }

  /** get a list of all the roles the type definition can access */
  public List<CDRoleSymbol> getCDRoleList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    if (getSpannedScope() instanceof ICDAssociationScope) {
      return ((ICDAssociationScope) getSpannedScope()).getLocalCDRoleSymbols();
    }

    return Lists.newArrayList();
  }

  /** search in the scope for roles with a specific name */
  public List<CDRoleSymbol> getCDRoleList(String rolename) {
    return ((ICDAssociationScope) getSpannedScope())
        .resolveCDRoleLocallyMany(
            false,
            rolename,
            de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION,
            x -> true);
  }

  /** get a list of all the fields the type definition can access */
  public List<FieldSymbol> getFieldList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalFieldSymbols();
  }

  /** search in the scope for methods with a specific name */
  public List<FieldSymbol> getFieldList(String fieldname) {
    return getSpannedScope().resolveFieldMany(fieldname);
  }

  @Override
  protected String determinePackageName() {
    IScope optCurrentScope = enclosingScope;
    while (optCurrentScope != null) {
      final IScope currentScope = optCurrentScope;
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().getPackageName();
      } else if (currentScope instanceof IArtifactScope) {
        return ((IArtifactScope) currentScope).getFullName();
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }
    return "";
  }

  public String getInternalQualifiedName() {
    String internalName = getFullName();
    IScope as = getEnclosingScope();
    while (!(as instanceof IArtifactScope)) {
      as = as.getEnclosingScope();
    }
    String artifactName = ((IArtifactScope) as).getFullName();
    if (!artifactName.isEmpty()) {
      internalName = internalName.substring(artifactName.length() + 1);
    }
    return internalName;
  }
}
