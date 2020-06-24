/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;

import java.util.Optional;

public class CDAssociationSymbolTablePrinter
    extends CDAssociationSymbolTablePrinterTOP {
  public CDAssociationSymbolTablePrinter() {
  }

  public CDAssociationSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  public void serializeSymAssociation(SymAssociation association) {
    printer.beginObject();
    printer.member(JsonDeSers.KIND, "de.monticore.cdassociation._symboltable.SymAssociation");
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.EXPORTS_SYMBOLS, true);

    // TODO SVa: print member name
    if (association.isPresentAssociation()) {
      serializeCDAssociation(association.getAssociation());
    }

    // TODO SVa: print member name
    serializeCDRole(association.getLeft());

    // TODO SVa: print member name
    serializeCDRole(association.getRight());
  }

  @Override
  protected void serializeCDAssociationAssociation(SymAssociation association) {
    super.serializeCDAssociationAssociation(association);
  }

  @Override
  protected void serializeCDRoleCardinality(Optional<ASTCDCardinality> cardinality) {
    super.serializeCDRoleCardinality(cardinality);
  }

  @Override
  protected void serializeCDRoleAttributeQualifier(Optional<FieldSymbol> attributeQualifier) {
    super.serializeCDRoleAttributeQualifier(attributeQualifier);
  }

  @Override
  protected void serializeCDRoleTypeQualifier(Optional<SymTypeExpression> typeQualifier) {
    super.serializeCDRoleTypeQualifier(typeQualifier);
  }

  @Override
  protected void serializeCDRoleAssociation(SymAssociation association) {
    super.serializeCDRoleAssociation(association);
  }

  @Override
  protected void serializeCDRoleType(SymTypeExpression type) {
    super.serializeCDRoleType(type);
  }
}
