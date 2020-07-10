/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTablePrinter;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4AnalysisScopeDeSer() {
    setSymbolTablePrinterHelper(new CDSymbolTablePrinterHelper());
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
    ((CDBasisSymbolTablePrinter)this.symbolTablePrinter.getCDBasisVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CDInterfaceAndEnumSymbolTablePrinter)this.symbolTablePrinter.getCDInterfaceAndEnumVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CDAssociationSymbolTablePrinter)this.symbolTablePrinter.getCDAssociationVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CD4AnalysisSymbolTablePrinter)this.symbolTablePrinter.getCD4AnalysisVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
  }

  public CDSymbolTablePrinterHelper getSymbolTablePrinterHelper() {
    return symbolTablePrinterHelper;
  }
}
