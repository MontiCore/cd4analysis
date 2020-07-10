/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

public class CD4AnalysisGlobalScopeBuilder
    extends CD4AnalysisGlobalScopeBuilderTOP {
  protected boolean addBuiltInTypes;

  public CD4AnalysisGlobalScopeBuilder() {
    addBuiltInTypes = false;
    modelFileExtension = CD4AnalysisGlobalScope.EXTENSION;
  }

  public CD4AnalysisGlobalScopeBuilder addBuiltInTypes(boolean add) {
    addBuiltInTypes = add;
    return this;
  }

  public CD4AnalysisGlobalScopeBuilder addBuiltInTypes() {
    addBuiltInTypes = true;
    return this;
  }

  public CD4AnalysisGlobalScopeBuilder dontAddBuiltInTypes() {
    addBuiltInTypes = false;
    return this;
  }

  @Override
  public CD4AnalysisGlobalScope build() {
    final CD4AnalysisGlobalScope globalScope = super.build();
    if (addBuiltInTypes) {
      globalScope.addBuiltInTypes();
    }
    return globalScope;
  }
}
