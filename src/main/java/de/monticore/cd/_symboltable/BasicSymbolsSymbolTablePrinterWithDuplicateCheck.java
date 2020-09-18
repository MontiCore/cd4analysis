package de.monticore.cd._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbolTablePrinter;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.serialization.JsonPrinter;

public class BasicSymbolsSymbolTablePrinterWithDuplicateCheck
    extends BasicSymbolsSymbolTablePrinter {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public BasicSymbolsSymbolTablePrinterWithDuplicateCheck() {
  }

  public BasicSymbolsSymbolTablePrinterWithDuplicateCheck(JsonPrinter printer) {
    super(printer);
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  public void handle(TypeSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }

  @Override
  public void handle(VariableSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }

  @Override
  public void handle(FunctionSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }
}
