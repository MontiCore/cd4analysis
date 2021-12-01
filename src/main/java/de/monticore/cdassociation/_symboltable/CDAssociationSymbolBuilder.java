/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

public class CDAssociationSymbolBuilder extends CDAssociationSymbolBuilderTOP {
  @Override
  public CDAssociationSymbol build() {
    final CDAssociationSymbol symbol = super.build();

    if (symbol.isPresentAssoc()) {
      symbol.getAssoc().setAssociation(symbol);
    }

    return symbol;
  }
}
