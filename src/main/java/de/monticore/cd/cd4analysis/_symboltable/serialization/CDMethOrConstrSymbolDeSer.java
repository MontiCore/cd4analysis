/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads non-primitive attributes "returnType" and "exceptions" of CDMethOrConstrSymbols.
 */
public class CDMethOrConstrSymbolDeSer extends CDMethOrConstrSymbolDeSerTOP {

  @Override protected CDTypeSymbolLoader deserializeReturnType(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    return new CDTypeSymbolLoader(symbolJson.getStringMember(CD4AnalysisSymbolTablePrinter.RETURN_TYPE), enclosingScope);
  }

  @Override protected List<CDTypeSymbolLoader> deserializeExceptions(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    List<CDTypeSymbolLoader> result = new ArrayList<>();
    for(JsonElement e:symbolJson.getArrayMember(CD4AnalysisSymbolTablePrinter.EXCEPTIONS)){
      result.add(new CDTypeSymbolLoader(e.getAsJsonString().getValue(), enclosingScope));
    }
    return result;
  }

  /**
   * create empty spanned scope
   * @param symbolJson
   * @param enclosingScope
   * @return
   */
  @Override public CDMethOrConstrSymbol deserializeCDMethOrConstrSymbol(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    CDMethOrConstrSymbol symbol = super.deserializeCDMethOrConstrSymbol(symbolJson, enclosingScope);
    CD4AnalysisScope scope = CD4AnalysisMill.cD4AnalysisScopeBuilder()
        .setSpanningSymbol(symbol)
        .setExportingSymbols(true)
        .setEnclosingScope(enclosingScope)
        .build();
    symbol.setSpannedScope(scope);
    return symbol;
  }
}
