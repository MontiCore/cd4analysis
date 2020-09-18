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

  // TODO SVa: remove, when calculateQualifiedNames is removed and getPackageName returns the correct package
  @Override
  public void visit(ICDBasisArtifactScope node) {
    printer.beginObject();
    if (node.isPresentName()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
    }
    // use RealPackageName here
    if (!node.getRealPackageName().isEmpty()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, node.getPackageName());
    }
    printKindHierarchy();
    serializeAdditionalArtifactScopeAttributes(node);
    printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);
  }

  @Override
  public void handle(CDTypeSymbol node) {
    if (symbolTablePrinterHelper.visit(node.getFullName())) {
      super.handle(node);
    }
  }
}
