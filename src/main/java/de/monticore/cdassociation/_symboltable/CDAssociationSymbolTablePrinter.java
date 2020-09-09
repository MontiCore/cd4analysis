/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

import static de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer.FURTHER_OBJECTS_MAP;
import static de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer.SYM_ASSOCIATION_TYPE;

public class CDAssociationSymbolTablePrinter
    extends CDAssociationSymbolTablePrinterTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;

  public CDAssociationSymbolTablePrinter() {
    init();
  }

  public CDAssociationSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
    init();
  }

  public static void serializeSymAssociations(JsonPrinter printer, CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
      symbolTablePrinterHelper.getSymAssociations().forEach(a -> CDAssociationSymbolTablePrinter.serializeSymAssociation(printer, a));
  }

  public static void serializeSymAssociation(JsonPrinter printer, SymAssociation symAssociation) {
    printer.beginObject(String.valueOf(symAssociation.hashCode()));

    printer.member(JsonDeSers.KIND, SYM_ASSOCIATION_TYPE);
    printer.member("isAssociation", symAssociation.isAssociation());
    printer.member("isComposition", symAssociation.isComposition());

    printer.endObject();
  }

  protected void init() {
    this.symbolTablePrinterHelper = new CDSymbolTablePrinterHelper();
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public int handleSymAssociation(SymAssociation association) {
    // don't serialize the SymAssociation, just add it to the list and generate an identifier
    this.symbolTablePrinterHelper.addSymAssociation(association);
    return association.hashCode();
  }

  @Override
  public void serializeCDAssociationAssociation(SymAssociation association) {
    printer.member("association", handleSymAssociation(association));
  }

  @Override
  public void serializeCDRoleCardinality(Optional<ASTCDCardinality> cardinality) {
    if (cardinality.isPresent()) {
      final CDAssociationPrettyPrinter cdAssociationPrettyPrinter = new CDAssociationPrettyPrinter(new IndentPrinter());
      cardinality.get().accept(cdAssociationPrettyPrinter);
      printer.member("cardinality", cdAssociationPrettyPrinter.getPrinter().getContent());
    }
  }

  @Override
  public void serializeCDRoleAttributeQualifier(Optional<FieldSymbol> attributeQualifier) {
    attributeQualifier.ifPresent(fieldSymbol -> printer.member("attributeQualifier", fieldSymbol.getName()));
  }

  @Override
  public void serializeCDRoleTypeQualifier(Optional<SymTypeExpression> typeQualifier) {
    typeQualifier.ifPresent(symTypeExpression -> SymTypeExpressionDeSer.serializeMember(printer, "typeQualifier", symTypeExpression));
  }

  @Override
  public void visit(CDRoleSymbol node) {
    super.visit(node);
    printer.member("association", handleSymAssociation(node.getAssociation()));
    printer.member("isLeft", node.isLeft());
  }

  @Override
  public void serializeCDRoleAssociation(SymAssociation association) {
    printer.member("association", handleSymAssociation(association));
  }

  @Override
  public void serializeCDRoleType(SymTypeExpression type) {
    SymTypeExpressionDeSer.serializeMember(printer, "type", type);
  }

  @Override
  public void endVisit(ICDAssociationArtifactScope node) {
    if (!symbolTablePrinterHelper.getSymAssociations().isEmpty()) {
      printer.beginObject(FURTHER_OBJECTS_MAP);
      serializeSymAssociations(printer, symbolTablePrinterHelper);
      printer.endObject();
    }

    super.endVisit(node);
  }

  @Override
  public void handle(ICDAssociationScope node) {
    // don't call visit, because we don't want the scope information
    super.traverse(node);
  }

  @Override
  public void traverse(CDAssociationSymbol node) {
    if (node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
      printer.beginArray("symbols");
      node.getSpannedScope().accept(getRealThis());
      printer.endArray();
    }
  }

  @Override
  public void traverse(ICDAssociationScope node) {
    if (!node.getLocalCDAssociationSymbols().isEmpty()) {
      node.getLocalCDAssociationSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    // don't serialize the cdrolesymbols here, because we can reconstruct them by their symassociation
    if (!node.getLocalCDRoleSymbols().isEmpty()) {
      node.getLocalCDRoleSymbols().forEach(s -> {
        if (symbolTablePrinterHelper.visit(s.getFullName())) {
          s.accept(getRealThis());
        }
      });
    }
    getRealThis().traverse((de.monticore.cdbasis._symboltable.ICDBasisScope) node);
  }
}
