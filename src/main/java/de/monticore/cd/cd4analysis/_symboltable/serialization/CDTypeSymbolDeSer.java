/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads non-primitive attributes "cdInterfaces" and "superClass" of CDTypeSymbols.
 */
public class CDTypeSymbolDeSer extends CDTypeSymbolDeSerTOP {

  @Override protected List<CDTypeSymbolLoader> deserializeCdInterfaces(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    List<CDTypeSymbolLoader> result = new ArrayList<>();
    for(JsonElement e:symbolJson.getArrayMember(CD4AnalysisSymbolTablePrinter.CD_INTERFACES)){
      result.add(new CDTypeSymbolLoader(e.getAsJsonString().getValue(), enclosingScope));
    }
    return result;
  }

  @Override protected Optional<CDTypeSymbolLoader> deserializeSuperClass(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    List<CDTypeSymbolLoader> result = new ArrayList<>();
    if(symbolJson.hasMember(CD4AnalysisSymbolTablePrinter.SUPER_CLASS)){
      String name = symbolJson.getStringMember(CD4AnalysisSymbolTablePrinter.SUPER_CLASS);
      return Optional.of(new CDTypeSymbolLoader(name, enclosingScope));
    }
    return Optional.empty();
  }
}
