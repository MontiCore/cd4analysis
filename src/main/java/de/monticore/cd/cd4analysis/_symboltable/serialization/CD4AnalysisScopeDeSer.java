/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

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
  protected void addAndLinkSpanningSymbol(
      de.monticore.symboltable.serialization.json.JsonObject subScopeJson,
      de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope subScope,
      de.monticore.cd.cd4analysis._symboltable.CD4AnalysisScope scope) {
    if (subScopeJson
        .hasMember(de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL)) {
      de.monticore.symboltable.serialization.json.JsonObject symbolRef = subScopeJson
          .getObjectMember(
              de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL);
      String spanningSymbolName = symbolRef
          .getStringMember(de.monticore.symboltable.serialization.JsonConstants.NAME);
      String spanningSymbolKind = symbolRef
          .getStringMember(de.monticore.symboltable.serialization.JsonConstants.KIND);
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
        Optional<de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol> spanningSymbol = scope
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
        Optional<de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol> spanningSymbol = scope
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
        de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol symbol = cDMethOrConstrSymbolDeSer
            .deserialize(symbolRef, scope);
        scope.add(symbol);
        symbol.setSpannedScope(subScope);
      }
      else {
        Log.error("Unknown kind of scope spanning symbol: " + spanningSymbolKind);
      }
    }
  }
}
