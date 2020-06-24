/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class CD4CodeBasisSymbolTablePrinter
    extends CD4CodeBasisSymbolTablePrinterTOP {
  public CD4CodeBasisSymbolTablePrinter() {
  }

  public CD4CodeBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  @Override
  protected void serializeCDMethodSignatureExceptions(List<SymTypeExpression> exceptions) {
    super.serializeCDMethodSignatureExceptions(exceptions);
  }

  @Override
  protected void serializeCDMethodSignatureReturnType(SymTypeExpression returnType) {
    //this.typeSymbolsSymbolTablePrinterDelegate.serializeMethodReturnType(superTypes);
    super.serializeCDMethodSignatureReturnType(returnType);
  }
}
