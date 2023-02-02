/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.deserializeFurtherObjects;
import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.serializeFurtherObjects;

import de.monticore.symboltable.serialization.json.JsonObject;

public class CD4CodeDeSer extends CD4CodeDeSerTOP {

  @Override
  public void serializeAddons(ICD4CodeArtifactScope toSerialize, CD4CodeSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(s2j.printer);
  }

  @Override
  public void deserializeAddons(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(scopeJson);
  }
}
