/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis._visitor.CD4AnalysisHandler;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;

public class CD4AnalysisSymbols2Json extends CD4AnalysisSymbols2JsonTOP
    implements CD4AnalysisHandler {
  public CD4AnalysisSymbols2Json() {
    getTraverser().setCD4AnalysisHandler(this);
  }

  public CD4AnalysisSymbols2Json(CD4AnalysisTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    getTraverser().setCD4AnalysisHandler(this);
  }

  @Override
  public void traverse(ICD4AnalysisScope node) {
    CD4AnalysisHandler.super.traverse(node);

    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      getTraverser().traverse((ICD4AnalysisScope) s.getSpannedScope());
    }
  }
}
