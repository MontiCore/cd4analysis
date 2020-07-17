/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Stack;

public class CD4AnalysisSymbolTablePrinter
    extends CD4AnalysisSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Stack<CD4AnalysisScope> scopeStack;

  public CD4AnalysisSymbolTablePrinter() {
    init();
  }

  public CD4AnalysisSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();
    this.scopeStack = new Stack<>();
  }

  public void serializeSymAssociations() {
    CDAssociationSymbolTablePrinter.serializeSymAssociations(printer, symbolTablePrinterHelper);
  }

  @Override
  public void visit(CD4AnalysisArtifactScope node) {
    if (!printer.isInObject()) {
      printer.beginObject();
    }
    printer.member(JsonDeSers.KIND, "de.monticore.cd4analysis._symboltable.CD4AnalysisArtifactScope");
    if (node.isPresentName()) {
      printer.member(JsonDeSers.NAME, node.getName());
    }
    if (!node.getRealPackageName().isEmpty()) {
      printer.member(JsonDeSers.PACKAGE, node.getRealPackageName());
    }
    serializeAdditionalArtifactScopeAttributes(node);
  }

  @Override
  public void endVisit(CD4AnalysisArtifactScope node) {
    serializeSymAssociations();
    super.endVisit(node);
  }

  @Override
  public void traverse(CD4AnalysisArtifactScope node) {
    super.traverse(node);
    // elements of subscopes should be flat in the artifact scope with qualified name
    printer.beginArray("symbols");
    node.getSubScopes().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }

  @Override
  public void handle(CD4AnalysisScope node) {
    scopeStack.push(node);

    // don't call visit, because we don't want the scope information
    super.traverse(node);

    if (scopeStack.size() == 3) {
      super.endVisit(node);
    }
    scopeStack.pop();
  }
}
