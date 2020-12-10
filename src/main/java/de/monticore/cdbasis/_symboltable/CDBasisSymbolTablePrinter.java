/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.symboltable.serialization.JsonDeSers;
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

    /*printer.array("superTypes", superTypes, e -> {
        JsonPrinter jp = new JsonPrinter();
        jp.beginObject();
        // Care: the following String needs to be adapted if the package was renamed
        jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeOfObject");
        jp.member("objName", e.getObjName());
        jp.endObject();
        return jp.getContent();
    });*/
  }

  // TODO SVa: remove, when calculateQualifiedNames is removed and getPackageName returns the correct package
  @Override
  public void visit(ICDBasisArtifactScope node) {
    printer.beginObject();
    printer.member("generated-using","www.MontiCore.de technology");
    if (node.isPresentName()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
    }
    // use RealPackageName here
    if (!node.getRealPackageName().isEmpty()) {
      printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, node.getRealPackageName());
    }
    printKindHierarchy();
    serializeAdditionalArtifactScopeAttributes(node);
    printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);
  }
}
