/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Optional;

public class CDRoleSymbolDeSer extends CDRoleSymbolDeSerTOP {
  @Override
  protected Optional<ASTCDCardinality> deserializeCardinality(JsonObject symbolJson, ICDAssociationScope enclosingScope) {
    // TODO SVa: how to read strings (with parser? -> not existing for component grammars)
    if (symbolJson.hasMember("cardinality")) {
      //symbolJson.getMember("cardinality").
    }
    return Optional.empty();
  }
}
