/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScopeBuilder;

public class CD4CodeGlobalScopeBuilder extends CD4CodeGlobalScopeBuilderTOP {
  protected boolean addBuiltInTypes;
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeGlobalScopeBuilder() {
    addBuiltInTypes = false;
    modelFileExtension = CD4AnalysisGlobalScope.EXTENSION;
    symbolTableHelper = new CDSymbolTableHelper();
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

  public CD4CodeGlobalScopeBuilder setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
    return this;
  }

  @Override
  public CD4CodeGlobalScope build() {
    final CD4CodeGlobalScope globalScope = super.build();
    if (addBuiltInTypes) {
      globalScope.addBuiltInTypes();
    }
    globalScope.setSymbolTableHelper(symbolTableHelper);
    return globalScope;
  }
}
