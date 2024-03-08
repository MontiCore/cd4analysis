/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;

public class CDAssociationSymbol extends CDAssociationSymbolTOP {
  public CDAssociationSymbol(String name) {
    super(name);
  }

  public CDRoleSymbol getLeft() {
    return getAssoc().getLeft();
  }

  public CDRoleSymbol getRight() {
    return getAssoc().getRight();
  }

  // CDAssociationSymbols belong to package <CDPackage>.<CDName> similarly to CDTypeSymbols
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
}
