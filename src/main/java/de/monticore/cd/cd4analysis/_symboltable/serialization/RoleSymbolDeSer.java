/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * Loads non-primitive attributes "cdInterfaces" and "superClass" of CDTypeSymbols.
 */
public class RoleSymbolDeSer extends RoleSymbolDeSerTOP {

  @Override protected CDTypeSymbolLoader deserializeAssociationTarget(JsonObject symbolJson,
      ICD4AnalysisScope enclosingScope) {
    return new CDTypeSymbolLoader(
        symbolJson.getStringMember(CD4AnalysisSymbolTablePrinter.ASSOCIATION_TARGET), enclosingScope);
  }
}
