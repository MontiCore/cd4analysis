/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

public class CDRoleSymbolBuilder extends CDRoleSymbolBuilderTOP {
  @Override
  public CDRoleSymbol build() {
    final CDRoleSymbol symbol = super.build();
    if (symbol.isPresentAssoc()) {
      if (symbol.isIsLeft()) {
        symbol.getAssoc().setLeft(symbol);
      }
      else {
        symbol.getAssoc().setRight(symbol);
      }
    }
    return symbol;
  }

  public CDRoleSymbol build(boolean isLeft) {
    final CDRoleSymbol symbol = super.build();
    if (symbol.isPresentAssoc()) {
      if (isLeft) {
        symbol.getAssoc().setLeft(symbol);
      }
      else {
        symbol.getAssoc().setRight(symbol);
      }
    }
    return symbol;
  }
}
