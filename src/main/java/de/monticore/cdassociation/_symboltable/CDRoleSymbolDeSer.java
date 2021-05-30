/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDDeSerHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
import de.monticore.cdassociation.prettyprint.CDAssociationFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.handleSymAssociation;

public class CDRoleSymbolDeSer extends CDRoleSymbolDeSerTOP {
  @Override
  protected void serializeCardinality(Optional<ASTCDCardinality> cardinality, CDAssociationSymbols2Json s2j) {
    if (cardinality.isPresent()) {
      final CDAssociationFullPrettyPrinter cdAssociationPrettyPrinter = new CDAssociationFullPrettyPrinter(new IndentPrinter());
      cardinality.get().accept(cdAssociationPrettyPrinter.getTraverser());
      s2j.printer.member("cardinality", cdAssociationPrettyPrinter.getPrinter().getContent());
    }
  }

  @Override
  protected void serializeField(Optional<FieldSymbol> field, CDAssociationSymbols2Json s2j) {
    if (field.isPresent()) {
      s2j.printer.member("field", field.get().getFullName());
    }
  }

  @Override
  protected void serializeAttributeQualifier(Optional<VariableSymbol> attributeQualifier, CDAssociationSymbols2Json s2j) {
    attributeQualifier.ifPresent(fieldSymbol -> s2j.printer.member("attributeQualifier", fieldSymbol.getName()));
  }

  @Override
  protected void serializeTypeQualifier(Optional<SymTypeExpression> typeQualifier, CDAssociationSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "typeQualifier", typeQualifier);
  }

  @Override
  protected void serializeAssoc(Optional<SymAssociation> assoc, CDAssociationSymbols2Json s2j) {
    if (assoc != null && assoc.isPresent()) {
      s2j.printer.member("association", handleSymAssociation(assoc.get()));
    }
  }

  @Override
  protected void serializeType(SymTypeExpression type, CDAssociationSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "type", type);
  }

  @Override
  protected Optional<ASTCDCardinality> deserializeCardinality(JsonObject symbolJson) {
    if (symbolJson.hasMember("cardinality")) {
      return Optional.ofNullable(CDCardinalityDeSer.fromString(symbolJson.getStringMember("cardinality")));
    }
    return Optional.empty();
  }

  @Override
  protected Optional<FieldSymbol> deserializeField(JsonObject symbolJson) {
    if (symbolJson.hasMember("field")) {
      return CDAssociationMill.globalScope().resolveField(symbolJson.getStringMember("field"));
    }
    return Optional.empty();
  }

  @Override
  protected Optional<VariableSymbol> deserializeAttributeQualifier(JsonObject symbolJson) {
    if (symbolJson.hasMember("attributeQualifier")) {
      final String fieldName = symbolJson.getStringMember("attributeQualifier");
      final SymTypeExpression type = SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
      // crate a surrogate to link to the existing variable
      return Optional.of(OOSymbolsMill
          .fieldSymbolSurrogateBuilder()
          .setName(fieldName)
          .setEnclosingScope(CDAssociationMill.globalScope())
          .setType(type)
          .build());
    }
    return Optional.empty();
  }

  @Override
  protected Optional<SymTypeExpression> deserializeTypeQualifier(JsonObject symbolJson) {
    if (symbolJson.hasMember("typeQualifier")) {
      return Optional.of(SymTypeExpressionDeSer.deserializeMember("typeQualifier", symbolJson));
    }
    return Optional.empty();
  }

  @Override
  protected Optional<SymAssociation> deserializeAssoc(JsonObject symbolJson) {
    return symbolJson.getIntegerMemberOpt("association")
        .flatMap(a -> Optional.ofNullable(CDDeSerHelper.getInstance().getSymAssocForDeserialization().get(a)));
  }

  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
  }
}
