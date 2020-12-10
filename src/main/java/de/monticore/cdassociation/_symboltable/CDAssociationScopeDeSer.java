/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Map;

public class CDAssociationScopeDeSer extends CDAssociationScopeDeSerTOP {
  public static final String SYM_ASSOCIATION_TYPE = "de.monticore.cdassociation._symboltable.SymAssociation";

  public static void deserializeSymAssociation(Map<Integer, SymAssociation> symAssociations, String name, JsonObject symbolJson) {
    JsonDeSers.checkCorrectDeSerForKind(SYM_ASSOCIATION_TYPE, symbolJson);

    // don't use the builder, because the symAssociation is just partial
    final SymAssociation symAssociation = new SymAssociation();
    symAssociation.setIsAssociation(symbolJson.getBooleanMemberOpt("isAssociation").orElse(false));
    symAssociation.setIsComposition(symbolJson.getBooleanMemberOpt("isComposition").orElse(false));

    symAssociations.put(Integer.valueOf(name), symAssociation);
  }

  public static void deserializeSymAssociations(Map<Integer, SymAssociation> symAssociations, Map.Entry<String, de.monticore.symboltable.serialization.json.JsonElement> entry) {
    final String member = entry.getValue().getAsJsonObject().getStringMember(JsonDeSers.KIND);
    if (member.equals(SYM_ASSOCIATION_TYPE)) {
      deserializeSymAssociation(symAssociations, entry.getKey(), entry.getValue().getAsJsonObject());
    }
  }
}
