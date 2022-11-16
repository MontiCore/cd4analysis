/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import de.monticore.cd4code._visitor.CD4CodeHandler;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;

public class CD4CodeSymbols2Json extends CD4CodeSymbols2JsonTOP implements CD4CodeHandler {
  public CD4CodeSymbols2Json() {
    getTraverser().setCD4CodeHandler(this);
  }

  public CD4CodeSymbols2Json(CD4CodeTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    getTraverser().setCD4CodeHandler(this);
  }

  @Override
  public void traverse(ICD4CodeScope node) {
    CD4CodeHandler.super.traverse(node);

    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      getTraverser().traverse((ICD4CodeScope) s.getSpannedScope());
    }
  }
}
