/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;

public class CD4AnalysisSymbolTablePrinter
    extends CD4AnalysisSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4AnalysisSymbolTablePrinter() {
    init();
  }

  public CD4AnalysisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();

    this.cDAssociationSymbolTablePrinterDelegate.setSymbolTablePrinterHelper(this.symbolTablePrinterHelper);
  }

  public void serializeSymAssociations() {
    this.cDAssociationSymbolTablePrinterDelegate.serializeSymAssociations();
  }

  @Override
  public void endVisit(CD4AnalysisArtifactScope node) {
    serializeSymAssociations();
    super.endVisit(node);
  }

  @Override
  public void handle(OOTypeSymbol node) {
    this.cDAssociationSymbolTablePrinterDelegate.handle(node);
  }
}
