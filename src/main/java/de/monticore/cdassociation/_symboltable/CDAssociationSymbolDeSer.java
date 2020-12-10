/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CDAssociationSymbolDeSer extends CDAssociationSymbolDeSerTOP {
  protected Map<Integer, SymAssociation> symAssociations;

  public CDAssociationSymbolDeSer() {
    setSymAssociations(new HashMap<>());
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  public Optional<SymAssociation> deserializeAssociation(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    return symbolJson.getIntegerMemberOpt("association")
        .flatMap(a -> Optional.ofNullable(symAssociations.get(a)));
  }
}
