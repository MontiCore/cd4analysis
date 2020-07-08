/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;

public class CD4CodeGlobalScopeBuilder extends CD4CodeGlobalScopeBuilderTOP {
  protected boolean addBuiltInTypes;

  public CD4CodeGlobalScopeBuilder() {
    addBuiltInTypes = false;
    modelFileExtension = CD4AnalysisGlobalScope.EXTENSION;
  }

  public CD4CodeGlobalScopeBuilder addBuiltInTypes(boolean add) {
    addBuiltInTypes = add;
    return this;
  }

  public CD4CodeGlobalScopeBuilder addBuiltInTypes() {
    addBuiltInTypes = true;
    return this;
  }

  public CD4CodeGlobalScopeBuilder dontAddBuiltInTypes() {
    addBuiltInTypes = false;
    return this;
  }

  @Override
  public CD4CodeGlobalScope build() {
    final CD4CodeGlobalScope globalScope = super.build();
    if (addBuiltInTypes) {
      globalScope.addBuiltInTypes();
    }
    return globalScope;
  }
}
