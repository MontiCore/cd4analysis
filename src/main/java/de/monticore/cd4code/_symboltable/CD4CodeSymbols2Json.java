/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbols2Json;
import de.monticore.symboltable.serialization.JsonPrinter;

import static de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer.FURTHER_OBJECTS_MAP;

public class CD4CodeSymbols2Json extends CD4CodeSymbols2JsonTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CD4CodeSymbols2Json() {
    init();
  }

  public CD4CodeSymbols2Json(JsonPrinter printer) {
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
    if (!symbolTablePrinterHelper.getSymAssociations().isEmpty()) {
      printer.beginObject(FURTHER_OBJECTS_MAP);
      CDAssociationSymbols2Json.serializeSymAssociations(printer, symbolTablePrinterHelper);
      printer.endObject();
    }
  }

  @Override
  public void endVisit(ICD4CodeArtifactScope node) {
    printer.endArray();
    serializeFurtherObjects();
    printer.endObject();
  }

  /**
   * copy of {@link de.monticore.cd4code._visitor.CD4CodeVisitor#traverse(ICD4CodeScope)}
   * but with an adapted order
   *
   * @param node
   */
  @Override
  public void traverse(ICD4CodeScope node) {
    // traverse symbols within the scope
    for (de.monticore.cdbasis._symboltable.CDTypeSymbol s : node.getLocalCDTypeSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.cdassociation._symboltable.CDAssociationSymbol s : node.getLocalCDAssociationSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.cdassociation._symboltable.CDRoleSymbol s : node.getLocalCDRoleSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol s : node.getLocalCDMethodSignatureSymbols()) {
      s.accept(getRealThis());
    }

    for (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol s : node.getLocalOOTypeSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.oosymbols._symboltable.MethodSymbol s : node.getLocalMethodSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.oosymbols._symboltable.FieldSymbol s : node.getLocalFieldSymbols()) {
      s.accept(getRealThis());
    }

    for (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol s : node.getLocalTypeSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol s : node.getLocalFunctionSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.VariableSymbol s : node.getLocalVariableSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol s : node.getLocalDiagramSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol s : node.getLocalTypeVarSymbols()) {
      s.accept(getRealThis());
    }

  }

  // TODO SVa: remove, when calculateQualifiedNames is removed and getPackageName returns the correct package
  @Override
  public void visit(ICD4CodeArtifactScope node) {
    printer.beginObject();
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
