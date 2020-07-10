/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTablePrinter;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTablePrinter;

public class CD4CodeScopeDeSer extends CD4CodeScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4CodeScopeDeSer() {
    setSymbolTablePrinterHelper(new CDSymbolTablePrinterHelper());
  }

  public CDSymbolTablePrinterHelper getSymbolTablePrinterHelper() {
    return symbolTablePrinterHelper;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
    ((CDBasisSymbolTablePrinter) this.symbolTablePrinter.getCDBasisVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CDInterfaceAndEnumSymbolTablePrinter) this.symbolTablePrinter.getCDInterfaceAndEnumVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CDAssociationSymbolTablePrinter) this.symbolTablePrinter.getCDAssociationVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CD4AnalysisSymbolTablePrinter) this.symbolTablePrinter.getCD4AnalysisVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CD4CodeBasisSymbolTablePrinter) this.symbolTablePrinter.getCD4CodeBasisVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    ((CD4CodeSymbolTablePrinter) this.symbolTablePrinter.getCD4CodeVisitor().get()).setSymbolTablePrinterHelper(symbolTablePrinterHelper);
  }
}
