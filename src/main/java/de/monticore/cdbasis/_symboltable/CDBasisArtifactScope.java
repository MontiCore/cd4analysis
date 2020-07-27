/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.ImportStatement;

import java.util.List;
import java.util.Optional;

public class CDBasisArtifactScope extends CDBasisArtifactScopeTOP {
  public CDBasisArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CDBasisArtifactScope(Optional<ICDBasisScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @Override
  public boolean checkIfContinueAsSubScope(String symbolName) {
    return true;

    /*
      always check the subscopes
      there are 2 constellations, what the symbolName could contain:
      1. an absolute name like "a.b.A":
         in this case, we traverse further in the subscopes
         (possibly with the package name removed, when this.getName() has parts of the package name)
      2. a QualifiedName without a package, like "A" or "A.name":
         search in all subscopes, for any defined type with this name
     */
  }
}