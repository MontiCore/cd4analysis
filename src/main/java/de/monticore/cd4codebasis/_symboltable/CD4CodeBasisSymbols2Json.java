/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;
import java.util.Stack;

public class CD4CodeBasisSymbols2Json
    extends CD4CodeBasisSymbols2JsonTOP {
  protected OOSymbolsSymbols2Json typeSymbolsSymbolTablePrinterDelegate;
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Stack<ICD4CodeBasisScope> scopeStack;

  public CD4CodeBasisSymbols2Json() {
    init();
  }

  public CD4CodeBasisSymbols2Json(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.typeSymbolsSymbolTablePrinterDelegate = new OOSymbolsSymbols2Json(printer);
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
}
