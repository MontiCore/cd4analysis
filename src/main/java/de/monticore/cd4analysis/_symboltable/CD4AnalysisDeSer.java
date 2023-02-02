/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.deserializeFurtherObjects;
import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.serializeFurtherObjects;

import de.monticore.symboltable.serialization.json.JsonObject;

public class CD4AnalysisDeSer extends CD4AnalysisDeSerTOP {

  @Override
  public void serializeAddons(ICD4AnalysisArtifactScope toSerialize, CD4AnalysisSymbols2Json s2j) {
    serializeFurtherObjects(s2j.printer);
  }

  @Override
  public void deserializeAddons(ICD4AnalysisArtifactScope scope, JsonObject scopeJson) {
    deserializeFurtherObjects(scopeJson);
  }
}
