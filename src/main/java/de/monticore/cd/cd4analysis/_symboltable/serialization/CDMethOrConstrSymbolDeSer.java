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
 * TODO
 *
 * @author (last commit)
 * @version , 05.12.2019
 * @since TODO
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

}
