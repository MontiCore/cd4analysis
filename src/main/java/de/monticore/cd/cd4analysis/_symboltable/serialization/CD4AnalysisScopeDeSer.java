/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisScope;
import de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.symboltable.serialization.json.JsonObject;

import static de.monticore.symboltable.serialization.JsonConstants.*;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {

  protected void deserializeCDMethOrConstrSymbol(
      de.monticore.symboltable.serialization.json.JsonObject symbolJson,
      de.monticore.cd.cd4analysis._symboltable.CD4AnalysisScope scope) {
    //The deserialization should not be performed here, it is performed in spanning symbol deserialization instead
  }

  /**
   * This method is overridden to distinguish between different spanning symbols of the same name
   * (e.g., for methods with same name and different parameters)
   *
   * @param subScopeJson
   * @param subScope
   * @param scope
   */
  @Override
  protected void addAndLinkSpanningSymbol(JsonObject subScopeJson, ICD4AnalysisScope subScope,
      CD4AnalysisScope scope) {
    if (subScopeJson.hasMember(SCOPE_SPANNING_SYMBOL)) {
      JsonObject symbolRef = subScopeJson.getObjectMember(SCOPE_SPANNING_SYMBOL);
      String spanningSymbolKind = symbolRef.getStringMember(KIND);
      if (spanningSymbolKind.equals(cDMethOrConstrSymbolDeSer.getSerializedKind())) {
        CDMethOrConstrSymbol symbol = cDMethOrConstrSymbolDeSer.deserialize(symbolRef, scope);
        scope.add(symbol);
        symbol.setSpannedScope(subScope);
      }
      else {
        super.addAndLinkSpanningSymbol(subScopeJson, subScope, scope);
      }
    }
  }
}
