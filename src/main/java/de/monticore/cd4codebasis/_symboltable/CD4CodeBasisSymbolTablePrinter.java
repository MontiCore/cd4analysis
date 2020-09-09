/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbolTablePrinter;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;
import java.util.Stack;

public class CD4CodeBasisSymbolTablePrinter
    extends CD4CodeBasisSymbolTablePrinterTOP {
  protected OOSymbolsSymbolTablePrinter typeSymbolsSymbolTablePrinterDelegate;
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Stack<ICD4CodeBasisScope> scopeStack;

  public CD4CodeBasisSymbolTablePrinter() {
    init();
  }

  public CD4CodeBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.typeSymbolsSymbolTablePrinterDelegate = new OOSymbolsSymbolTablePrinter(printer);
    this.scopeStack = new Stack<>();
  }

  @Override
  public void serializeCDMethodSignatureExceptions(List<SymTypeExpression> exceptions) {
    SymTypeExpressionDeSer.serializeMember(printer, "exceptions", exceptions);
  }

  @Override
  public void serializeCDMethodSignatureReturnType(SymTypeExpression returnType) {
    this.typeSymbolsSymbolTablePrinterDelegate.serializeMethodReturnType(returnType);
  }

  public void traverse(ICD4CodeBasisScope node) {
    if (!node.getLocalCDMethodSignatureSymbols().isEmpty()) {
      node.getLocalCDMethodSignatureSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    getRealThis().traverse((de.monticore.cdbasis._symboltable.ICDBasisScope) node);
    getRealThis().traverse((de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumScope) node);
    getRealThis().traverse((de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope) node);
  }

  @Override
  public void traverse(CDMethodSignatureSymbol node) {
    if (node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
      printer.beginArray("symbols");
      node.getSpannedScope().accept(getRealThis());
      printer.endArray();
    }
  }

  @Override
  public void handle(ICD4CodeBasisScope node) {
    scopeStack.push(node);

    // don't call visit, because we don't want the scope information
    super.traverse(node);

    scopeStack.pop();
  }
}
