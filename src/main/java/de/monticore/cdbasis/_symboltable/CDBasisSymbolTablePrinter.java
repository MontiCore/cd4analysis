/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class CDBasisSymbolTablePrinter extends CDBasisSymbolTablePrinterTOP {
  public CDBasisSymbolTablePrinter() {
  }

  public CDBasisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  @Override
  protected void serializeCDTypeSuperTypes(List<SymTypeExpression> superTypes) {
    //this.typeSymbolsSymbolTablePrinterDelegate.serializeOOTypeSuperTypes(superTypes);
    super.serializeCDTypeSuperTypes(superTypes);
  }
}
