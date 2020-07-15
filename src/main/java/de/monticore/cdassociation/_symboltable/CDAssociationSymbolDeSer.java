/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class CDAssociationSymbolDeSer extends CDAssociationSymbolDeSerTOP {
  protected Map<Integer, SymAssociation> symAssociations;

  public CDAssociationSymbolDeSer() {
    setSymAssociations(new HashMap<>());
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  public SymAssociation deserializeAssociation(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    final int association = symbolJson.getIntegerMember("association");
    if (!symAssociations.containsKey(association)) {
      Log.error(String.format(
          "0xCD004: SymAssociation %d is not loaded",
          association));
    }
    return symAssociations.get(association);
  }
}
