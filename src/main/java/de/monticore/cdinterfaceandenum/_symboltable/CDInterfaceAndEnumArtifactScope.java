/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.symboltable.ImportStatement;
import java.util.List;
import java.util.Optional;

public class CDInterfaceAndEnumArtifactScope extends CDInterfaceAndEnumArtifactScopeTOP {
  public CDInterfaceAndEnumArtifactScope() {}

  public CDInterfaceAndEnumArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CDInterfaceAndEnumArtifactScope(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
          Optional<ICDInterfaceAndEnumScope> enclosingScope,
      String packageName,
      List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }
}
