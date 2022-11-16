/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.ImportStatement;
import java.util.List;
import java.util.Optional;

public class CDAssociationArtifactScope extends CDAssociationArtifactScopeTOP {
  public CDAssociationArtifactScope() {}

  public CDAssociationArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CDAssociationArtifactScope(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
          Optional<ICDAssociationScope> enclosingScope,
      String packageName,
      List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }
}
