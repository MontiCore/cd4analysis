/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
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
  public void serializeCDAssociationAssociation(Optional<SymAssociation> association) {
    if (association.isPresent()) {
      printer.member("association", handleSymAssociation(association.get()));
    }
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
  public void serializeCDRoleAttributeQualifier(Optional<VariableSymbol> attributeQualifier) {
    attributeQualifier.ifPresent(fieldSymbol -> printer.member("attributeQualifier", fieldSymbol.getName()));
  }

  @Override
  public void serializeCDRoleTypeQualifier(Optional<SymTypeExpression> typeQualifier) {
    typeQualifier.ifPresent(symTypeExpression -> SymTypeExpressionDeSer.serializeMember(printer, "typeQualifier", symTypeExpression));
  }

  @Override
  public void visit(CDRoleSymbol node) {
    super.visit(node);
    if (node.isPresentAssociation()) {
      printer.member("association", handleSymAssociation(node.getAssociation()));
    }
    printer.member("isLeft", node.isIsLeft());
  }

  @Override
  public void serializeCDRoleAssociation(Optional<SymAssociation> association) {
    if (association != null && association.isPresent()) {
      printer.member("association", handleSymAssociation(association.get()));
    }
  }

  @Override
  public void serializeCDRoleType(SymTypeExpression type) {
    SymTypeExpressionDeSer.serializeMember(printer, "type", type);
  }

  @Override
  public void traverse(CDAssociationSymbol node) {
    // don't handle the role symbols here, because they are already serialized as CDRoleSymbol in the CDType
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
}
