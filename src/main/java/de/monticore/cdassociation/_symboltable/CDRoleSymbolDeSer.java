/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CDRoleSymbolDeSer extends CDRoleSymbolDeSerTOP {
  protected Map<Integer, SymAssociation> symAssociations;

  public CDRoleSymbolDeSer() {
    setSymAssociations(new HashMap<>());
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  public Optional<ASTCDCardinality> deserializeCardinality(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    if (symbolJson.hasMember("cardinality")) {
      return Optional.ofNullable(CDCardinalityDeSer.fromString(symbolJson.getStringMember("cardinality")));
    }
    return Optional.empty();
  }

  @Override
  public Optional<FieldSymbol> deserializeAttributeQualifier(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    if (symbolJson.hasMember("attributeQualifier")) {
      final String fieldName = symbolJson.getStringMember("attributeQualifier");
      final SymTypeExpression type = SymTypeExpressionDeSer.deserializeMember("type", symbolJson, enclosingScope);
      return Optional.of(TypeSymbolsMill
          .fieldSymbolSurrogateBuilder()
          .setName(fieldName)
          .setEnclosingScope(enclosingScope)
          .setType(type)
          .build());
    }
    return Optional.empty();
  }

  @Override
  public Optional<SymTypeExpression> deserializeTypeQualifier(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    if (symbolJson.hasMember("typeQualifier")) {
      return Optional.of(SymTypeExpressionDeSer.deserializeMember("typeQualifier", symbolJson, enclosingScope));
    }
    return Optional.empty();
  }

  @Override
  public SymAssociation deserializeAssociation(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    final int association = symbolJson.getIntegerMember("association");
    if (!symAssociations.containsKey(association)) {
      Log.error(String.format(
          "0xCD003: SymAssociation %d is not loaded",
          association));
    }
    return symAssociations.get(association);
  }

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, enclosingScope);
  }
}
