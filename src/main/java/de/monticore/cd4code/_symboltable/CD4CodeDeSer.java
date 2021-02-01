package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Map;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.deserializeFurtherObjects;
import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.serializeFurtherObjects;

public class CD4CodeDeSer extends CD4CodeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
  }

  @Override
  protected void serializeAddons(ICD4CodeScope toSerialize, CD4CodeSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(symbolTablePrinterHelper, s2j.printer);
  }

  @Override
  protected void serializeAddons(ICD4CodeArtifactScope toSerialize, CD4CodeSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(symbolTablePrinterHelper, s2j.printer);
  }

  @Override
  protected void deserializeAddons(ICD4CodeScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }

  @Override
  protected void deserializeAddons(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }
}
