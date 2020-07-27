/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

public class CDRoleSymbolBuilder extends CDRoleSymbolBuilderTOP {
  @Override
  public CDRoleSymbol build() {
    final CDRoleSymbol symbol = super.build();
    if (symbol.getAssociation() != null) {
      if (symbol.isLeft()) {
        symbol.getAssociation().setLeft(symbol);
      }
      else {
        symbol.getAssociation().setRight(symbol);
      }
    }
    return symbol;
  }

  public CDRoleSymbol build(boolean isLeft) {
    final CDRoleSymbol symbol = super.build();
    if (symbol.getAssociation() != null) {
      if (isLeft) {
        symbol.getAssociation().setLeft(symbol);
      }
      else {
        symbol.getAssociation().setRight(symbol);
      }
    }
    return symbol;
  }
}