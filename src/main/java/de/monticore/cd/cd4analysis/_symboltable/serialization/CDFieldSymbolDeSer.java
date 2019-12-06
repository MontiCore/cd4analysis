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
public class CDFieldSymbolDeSer extends CDFieldSymbolDeSerTOP {

  @Override protected CDTypeSymbolLoader deserializeType(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    return new CDTypeSymbolLoader(symbolJson.getStringMember(CD4AnalysisSymbolTablePrinter.TYPE), enclosingScope);
  }
}
