/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
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

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();
  }

  @Override
  public void endVisit(CD4CodeArtifactScope node) {
    CDAssociationSymbolTablePrinter.serializeSymAssociations(printer, symbolTablePrinterHelper);
    super.endVisit(node);
  }
}
