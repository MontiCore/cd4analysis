package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;

public class CD4CodeSymbolTableCreatorDelegator
    extends CD4CodeSymbolTableCreatorDelegatorTOP {
  public CD4CodeSymbolTableCreatorDelegator(ICD4CodeGlobalScope globalScope) {
    super(globalScope);
    this.priorityList.add(new CD4CodeSTCompleteTypesDelegator(globalScope));
  }
}
