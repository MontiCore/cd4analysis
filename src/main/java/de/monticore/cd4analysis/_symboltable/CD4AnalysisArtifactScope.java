/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.symboltable.ImportStatement;

import java.util.List;
import java.util.Optional;

public class CD4AnalysisArtifactScope extends CD4AnalysisArtifactScopeTOP {
  public CD4AnalysisArtifactScope() {
  }

  public CD4AnalysisArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CD4AnalysisArtifactScope(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ICD4AnalysisScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @Override
  public String getPackageName() {
    return "";
  }

  public String getRealPackageName() {
    return super.getPackageName();
  }
}
