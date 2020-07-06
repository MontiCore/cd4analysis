/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonPrinter;

public class CD4CodeSymbolTablePrinter extends CD4CodeSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4CodeSymbolTablePrinter() {
    init();
  }

  public CD4CodeSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();

    this.cD4AnalysisSymbolTablePrinterDelegate.setSymbolTablePrinterHelper(this.symbolTablePrinterHelper);
  }

  @Override
  public void endVisit(CD4CodeArtifactScope node) {
    this.cD4AnalysisSymbolTablePrinterDelegate.serializeSymAssociations();
    super.endVisit(node);
  }
}
