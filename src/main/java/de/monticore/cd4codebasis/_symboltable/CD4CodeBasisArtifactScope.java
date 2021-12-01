/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._symboltable;

import de.monticore.symboltable.ImportStatement;

import java.util.List;
import java.util.Optional;

public class CD4CodeBasisArtifactScope extends CD4CodeBasisArtifactScopeTOP {
  public CD4CodeBasisArtifactScope() {
  }

  public CD4CodeBasisArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public CD4CodeBasisArtifactScope(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ICD4CodeBasisScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

}
