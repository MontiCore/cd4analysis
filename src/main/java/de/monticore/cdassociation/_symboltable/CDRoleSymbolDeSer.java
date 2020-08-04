/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
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
      // crate a surrogate to link to the existing variable
      return Optional.of(OOSymbolsMill
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

  @Override
  public CDRoleSymbol deserializeCDRoleSymbol(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    // copy from super.deserializeCDRoleSymbol
    de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind(getSerializedKind(), symbolJson);
    de.monticore.cdassociation._symboltable.CDRoleSymbolBuilder builder = de.monticore.cdassociation.CDAssociationMill.cDRoleSymbolBuilder();
    builder.setFullName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
    builder.setName(de.monticore.utils.Names.getSimpleName(builder.getFullName()));
    builder.setIsDefinitiveNavigable(deserializeIsDefinitiveNavigable(symbolJson, enclosingScope));
    if (deserializeCardinality(symbolJson, enclosingScope).isPresent()) {
      builder.setCardinality(deserializeCardinality(symbolJson, enclosingScope).get());
    }
    else {
      builder.setCardinalityAbsent();
    }
    if (deserializeAttributeQualifier(symbolJson, enclosingScope).isPresent()) {
      builder.setAttributeQualifier(deserializeAttributeQualifier(symbolJson, enclosingScope).get());
    }
    else {
      builder.setAttributeQualifierAbsent();
    }
    if (deserializeTypeQualifier(symbolJson, enclosingScope).isPresent()) {
      builder.setTypeQualifier(deserializeTypeQualifier(symbolJson, enclosingScope).get());
    }
    else {
      builder.setTypeQualifierAbsent();
    }
    builder.setAssociation(deserializeAssociation(symbolJson, enclosingScope));
    builder.setIsOrdered(deserializeIsOrdered(symbolJson, enclosingScope));
    builder.setIsPrivate(deserializeIsPrivate(symbolJson, enclosingScope));
    builder.setIsProtected(deserializeIsProtected(symbolJson, enclosingScope));
    builder.setIsPublic(deserializeIsPublic(symbolJson, enclosingScope));
    builder.setIsStatic(deserializeIsStatic(symbolJson, enclosingScope));
    builder.setIsFinal(deserializeIsFinal(symbolJson, enclosingScope));
    builder.setType(deserializeType(symbolJson, enclosingScope));
    builder.setIsReadOnly(deserializeIsReadOnly(symbolJson, enclosingScope));
    // this is the only change
    de.monticore.cdassociation._symboltable.CDRoleSymbol symbol = builder.build(symbolJson.getBooleanMemberOpt("isLeft").orElse(false));
    deserializeAdditionalCDRoleSymbolAttributes(symbol, symbolJson, enclosingScope);
    return symbol;
  }
}
