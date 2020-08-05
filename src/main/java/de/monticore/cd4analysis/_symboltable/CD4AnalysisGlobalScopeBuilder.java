/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;

public class CD4AnalysisGlobalScopeBuilder
    extends CD4AnalysisGlobalScopeBuilderTOP {
  protected boolean addBuiltInTypes;
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4AnalysisGlobalScopeBuilder() {
    addBuiltInTypes = false;
    modelFileExtension = CD4AnalysisGlobalScope.EXTENSION;
    symbolTableHelper = new CDSymbolTableHelper(CD4AnalysisMill.deriveSymTypeOfCD4Analysis());
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

  public CD4AnalysisGlobalScopeBuilder setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
    return this;
  }

  @Override
  public CD4AnalysisGlobalScope build() {
    final CD4AnalysisGlobalScope globalScope = super.build();
    if (addBuiltInTypes) {
      globalScope.addBuiltInTypes();
    }
    globalScope.setSymbolTableHelper(symbolTableHelper);
    return globalScope;
  }
}
