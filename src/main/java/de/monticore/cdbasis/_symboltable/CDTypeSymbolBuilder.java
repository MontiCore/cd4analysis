/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

/**
 * @deprecated remove when the builder are setting the spanned scope
 */
@Deprecated
public class CDTypeSymbolBuilder extends CDTypeSymbolBuilderTOP {
  public CDTypeSymbolBuilder() {
    this.realBuilder = this;
  }

  @Override
  public CDTypeSymbol build() {
    CDTypeSymbol symbol = super.build();
    if (spannedScope != null) {
      symbol.setSpannedScope(this.spannedScope);
    }
    return symbol;
  }
}
