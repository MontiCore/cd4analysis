package de.monticore.cd._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbolTablePrinter;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonPrinter;

public class OOSymbolsSymbolTablePrinterWithDuplicateCheck
    extends OOSymbolsSymbolTablePrinter {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public OOSymbolsSymbolTablePrinterWithDuplicateCheck() {
  }

  public OOSymbolsSymbolTablePrinterWithDuplicateCheck(JsonPrinter printer) {
    super(printer);
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  public void handle(OOTypeSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }

  @Override
  public void handle(FieldSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }

  @Override
  public void handle(MethodSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }
}
