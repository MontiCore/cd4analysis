/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;

public class CDBasisSymbols2Json extends CDBasisSymbols2JsonTOP implements CDBasisHandler {
  public CDBasisSymbols2Json() {
    getTraverser().setCDBasisHandler(this);
  }

  public CDBasisSymbols2Json(CDBasisTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    getTraverser().setCDBasisHandler(this);
  }

  @Override
  public void traverse(ICDBasisScope node) {
    CDBasisHandler.super.traverse(node);

    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      getTraverser().traverse(s.getSpannedScope());
    }
  }
}
