/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.ImportStatement;
import java.util.List;
import java.util.Optional;

public class CDBasisArtifactScope extends CDBasisArtifactScopeTOP {
  public CDBasisArtifactScope() {}

  public CDBasisArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CDBasisArtifactScope(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
          Optional<ICDBasisScope> enclosingScope,
      String packageName,
      List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }
}
