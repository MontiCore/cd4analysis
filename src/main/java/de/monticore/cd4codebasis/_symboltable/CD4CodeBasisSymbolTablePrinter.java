/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymbolTablePrinter;

import java.util.List;

public class CD4CodeBasisSymbolTablePrinter
    extends CD4CodeBasisSymbolTablePrinterTOP {
  protected TypeSymbolsSymbolTablePrinter typeSymbolsSymbolTablePrinterDelegate;
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4CodeBasisSymbolTablePrinter() {
    this.typeSymbolsSymbolTablePrinterDelegate = new TypeSymbolsSymbolTablePrinter(printer);
  }

  public CD4CodeBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    this.typeSymbolsSymbolTablePrinterDelegate = new TypeSymbolsSymbolTablePrinter(printer);
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  public void serializeCDMethodSignatureExceptions(List<SymTypeExpression> exceptions) {
    SymTypeExpressionDeSer.serializeMember(printer, "exceptions", exceptions);
  }

  @Override
  public void serializeCDMethodSignatureReturnType(SymTypeExpression returnType) {
    this.typeSymbolsSymbolTablePrinterDelegate.serializeMethodReturnType(returnType);
  }
}
