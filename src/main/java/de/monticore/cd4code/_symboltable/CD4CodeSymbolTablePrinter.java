/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

import static de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer.FURTHER_OBJECTS_MAP;

public class CD4CodeSymbolTablePrinter extends CD4CodeSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4CodeSymbolTablePrinter() {
    init();
  }

  public CD4CodeSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();
  }

  public void serializeFurtherObjects() {
    printer.beginObject(FURTHER_OBJECTS_MAP);
    CDAssociationSymbolTablePrinter.serializeSymAssociations(printer, symbolTablePrinterHelper);
    printer.endObject();
  }

  @Override
  public void visit(ICD4CodeArtifactScope node) {
    if (!printer.isInObject()) {
      printer.beginObject();
    }
    printer.member(JsonDeSers.KIND, "de.monticore.cd4code._symboltable.CD4CodeArtifactScope");
    if (node.isPresentName()) {
      printer.member(JsonDeSers.NAME, node.getName());
    }
    if (!node.getRealPackageName().isEmpty()) {
      printer.member(JsonDeSers.PACKAGE, node.getRealPackageName());
    }
    serializeAdditionalArtifactScopeAttributes(node);
  }

  @Override
  public void endVisit(ICD4CodeArtifactScope node) {
    serializeFurtherObjects();
    super.endVisit(node);
  }

  @Override
  public void traverse(ICD4CodeArtifactScope node) {
    super.traverse(node);
    // elements of subscopes should be flat in the artifact scope with qualified name
    printer.beginArray("symbols");
    node.getSubScopes().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }

  @Override
  public void handle(ICD4CodeScope node) {
    // don't call visit and endVisit, because we don't want the scope information
    super.traverse(node);
  }
}
