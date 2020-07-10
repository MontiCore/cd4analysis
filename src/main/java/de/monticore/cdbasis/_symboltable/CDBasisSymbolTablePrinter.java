/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class CDBasisSymbolTablePrinter extends CDBasisSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CDBasisSymbolTablePrinter() {
  }

  public CDBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  public void serializeCDTypeSuperTypes(List<SymTypeExpression> superTypes) {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  /*
  {"kind":"de.monticore.cd4analysis._symboltable.CD4AnalysisArtifactScope","name":"STTest","cDTypeSymbols":[{"kind":"de.monticore.cdbasis._symboltable.CDTypeSymbol","name":"A","isClass":true,"isInterface":false,"isEnum":false,"isAbstract":false,"isPrivate":false,"isProtected":false,"isPublic":false,"isStatic":false,{"kind":"de.monticore.cd4analysis._symboltable.CD4AnalysisScope","name":"A","isShadowingScope":false
    */
  @Override
  public void traverse(CDTypeSymbol node) {
    if (node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
      isSpannedScope = true;

      node.getSpannedScope().accept(getRealThis());
    }
  }
}
