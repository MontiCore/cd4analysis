package de.monticore.cd4analysis._symboltable;

import de.monticore.cdbasis._symboltable.CDBasisDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.deserializeFurtherObjects;
import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.serializeFurtherObjects;

public class CD4AnalysisDeSer extends CD4AnalysisDeSerTOP {
  @Override
  protected void deserializeSymbols(ICD4AnalysisScope scope, JsonObject scopeJson) {
    super.deserializeSymbols(scope, scopeJson);
    CDBasisDeSer.moveCDTypeSymbolsToPackage(scope);
  }

  @Override
  public void serializeAddons(ICD4AnalysisArtifactScope toSerialize, CD4AnalysisSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(s2j.printer);
  }

  @Override
  public void deserializeAddons(ICD4AnalysisArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(scopeJson);
  }
}
