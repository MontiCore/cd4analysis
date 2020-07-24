/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

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

  public void serializeSymAssociations() {
    CDAssociationSymbolTablePrinter.serializeSymAssociations(printer, symbolTablePrinterHelper);
  }

  @Override
  public void visit(CD4CodeArtifactScope node) {
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
  public void endVisit(CD4CodeArtifactScope node) {
    serializeSymAssociations();
    super.endVisit(node);
  }

  @Override
  public void traverse(CD4CodeArtifactScope node) {
    super.traverse(node);
    // elements of subscopes should be flat in the artifact scope with qualified name
    printer.beginArray("symbols");
    node.getSubScopes().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }

  @Override
  public void handle(CD4CodeScope node) {
    // don't call visit and endVisit, because we don't want the scope information
    super.traverse(node);
  }
}
