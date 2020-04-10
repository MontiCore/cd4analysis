/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.symboltable.serialization.JsonDeSers.*;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {

  protected void deserializeCDMethOrConstrSymbol(JsonObject symbolJson, ICD4AnalysisScope scope) {
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
      ICD4AnalysisScope scope) {
    if (subScopeJson.hasMember(SCOPE_SPANNING_SYMBOL)) {
      JsonObject symbolRef = subScopeJson.getObjectMember(SCOPE_SPANNING_SYMBOL);
      String spanningSymbolName = symbolRef.getStringMember(NAME);
      String spanningSymbolKind = symbolRef.getStringMember(KIND);
      if (spanningSymbolKind.equals(cDTypeSymbolDeSer.getSerializedKind())) {
        Optional<CDTypeSymbol> spanningSymbol = scope.resolveCDTypeLocally(spanningSymbolName);
        if (spanningSymbol.isPresent()) {
          spanningSymbol.get().setSpannedScope(subScope);
        }
        else {
          Log.error("Spanning symbol of scope " + subScopeJson
              + " could not be found during deserialization!");
        }
      }
      else if (spanningSymbolKind.equals(cDAssociationSymbolDeSer.getSerializedKind())) {
        Optional<CDAssociationSymbol> spanningSymbol = scope
            .resolveCDAssociationLocally(spanningSymbolName);
        if (spanningSymbol.isPresent()) {
          spanningSymbol.get().setSpannedScope(subScope);
        }
        else {
          Log.error("Spanning symbol of scope " + subScopeJson
              + " could not be found during deserialization!");
        }
      }
      else if (spanningSymbolKind.equals(cDDefinitionSymbolDeSer.getSerializedKind())) {
        Optional<CDDefinitionSymbol> spanningSymbol = scope
            .resolveCDDefinitionLocally(spanningSymbolName);
        if (spanningSymbol.isPresent()) {
          spanningSymbol.get().setSpannedScope(subScope);
        }
        else {
          Log.error("Spanning symbol of scope " + subScopeJson
              + " could not be found during deserialization!");
        }
      }
      else if (spanningSymbolKind.equals(cDMethOrConstrSymbolDeSer.getSerializedKind())) {
        CDMethOrConstrSymbol symbol = cDMethOrConstrSymbolDeSer
            .deserializeCDMethOrConstrSymbol(symbolRef, scope);
        scope.add(symbol);
        symbol.setSpannedScope(subScope);
      }
      else {
        Log.error("Unknown kind of scope spanning symbol: " + spanningSymbolKind);
      }
    }
  }
}
