/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

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
  public Optional<ASTCDCardinality> deserializeCardinality(JsonObject symbolJson) {
    if (symbolJson.hasMember("cardinality")) {
      return Optional.ofNullable(CDCardinalityDeSer.fromString(symbolJson.getStringMember("cardinality")));
    }
    return Optional.empty();
  }

  @Override
  public Optional<VariableSymbol> deserializeAttributeQualifier(JsonObject symbolJson) {
    if (symbolJson.hasMember("attributeQualifier")) {
      final String fieldName = symbolJson.getStringMember("attributeQualifier");
      final SymTypeExpression type = SymTypeExpressionDeSer.deserializeMember("type", symbolJson, null);
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
  public Optional<SymTypeExpression> deserializeTypeQualifier(JsonObject symbolJson) {
    if (symbolJson.hasMember("typeQualifier")) {
      return Optional.of(SymTypeExpressionDeSer.deserializeMember("typeQualifier", symbolJson, null));
    }
    return Optional.empty();
  }

  @Override
  public Optional<SymAssociation> deserializeAssoc(JsonObject symbolJson) {
    return symbolJson.getIntegerMemberOpt("association")
        .flatMap(a -> Optional.ofNullable(symAssociations.get(a)));
  }

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    //third parameter enclosingScope will be removed in 6.7.0-SNAPSHOT, not used in this version anymore
    //thus the third parameter is null
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, null);
  }

  @Override
  public CDRoleSymbol deserializeCDRoleSymbol(JsonObject symbolJson) {
    // copy from super.deserializeCDRoleSymbol
    de.monticore.symboltable.serialization.JsonDeSers.checkCorrectDeSerForKind(getSerializedKind(), symbolJson);
    de.monticore.cdassociation._symboltable.CDRoleSymbolBuilder builder = de.monticore.cdassociation.CDAssociationMill.cDRoleSymbolBuilder();
    builder.setFullName(symbolJson.getStringMember(de.monticore.symboltable.serialization.JsonDeSers.NAME));
    builder.setName(de.monticore.utils.Names.getSimpleName(builder.getFullName()));
    builder.setIsDefinitiveNavigable(deserializeIsDefinitiveNavigable(symbolJson));
    if (deserializeCardinality(symbolJson).isPresent()) {
      builder.setCardinality(deserializeCardinality(symbolJson).get());
    }
    else {
      builder.setCardinalityAbsent();
    }
    if (deserializeAttributeQualifier(symbolJson).isPresent()) {
      builder.setAttributeQualifier(deserializeAttributeQualifier(symbolJson).get());
    }
    else {
      builder.setAttributeQualifierAbsent();
    }
    if (deserializeTypeQualifier(symbolJson).isPresent()) {
      builder.setTypeQualifier(deserializeTypeQualifier(symbolJson).get());
    }
    else {
      builder.setTypeQualifierAbsent();
    }
    deserializeAssoc(symbolJson).ifPresent(builder::setAssoc);
    builder.setIsOrdered(deserializeIsOrdered(symbolJson));
    builder.setIsPrivate(deserializeIsPrivate(symbolJson));
    builder.setIsProtected(deserializeIsProtected(symbolJson));
    builder.setIsPublic(deserializeIsPublic(symbolJson));
    builder.setIsStatic(deserializeIsStatic(symbolJson));
    builder.setIsFinal(deserializeIsFinal(symbolJson));
    builder.setType(deserializeType(symbolJson));
    builder.setIsReadOnly(deserializeIsReadOnly(symbolJson));
    // this is the only change
    de.monticore.cdassociation._symboltable.CDRoleSymbol symbol = builder.build(symbolJson.getBooleanMemberOpt("isLeft").orElse(false));
    deserializeAddons(symbol, symbolJson);
    return symbol;
  }
}
