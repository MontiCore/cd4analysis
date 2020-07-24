/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Map;

public class CDAssociationScopeDeSer extends CDAssociationScopeDeSerTOP {
  public static String SYM_ASSOCIATION_TYPE = "de.monticore.cdassociation._symboltable.SymAssociation";

  public static SymAssociation deserializeSymAssociation(Map<Integer, SymAssociation> symAssociations, JsonObject symbolJson) {
    JsonDeSers.checkCorrectDeSerForKind(SYM_ASSOCIATION_TYPE, symbolJson);

    // don't use the builder, because the symAssociation is just partial
    final SymAssociation symAssociation = new SymAssociation();
    symAssociation.setIsAssociation(symbolJson.getBooleanMemberOpt("isAssociation").orElse(false));
    symAssociation.setIsComposition(symbolJson.getBooleanMemberOpt("isComposition").orElse(false));

    symAssociations.put(symbolJson.getIntegerMember(JsonDeSers.NAME), symAssociation);

    return symAssociation;
  }

  public static void deserializeSymAssociations(Map<Integer, SymAssociation> symAssociations, JsonObject scopeJson) {
    if (scopeJson.hasArrayMember("SymAssociations")) {
      scopeJson.getArrayMember("SymAssociations").forEach(s -> deserializeSymAssociation(symAssociations, s.getAsJsonObject()));
    }
  }
}
