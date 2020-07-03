/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;
import java.util.stream.Collectors;

public class CDTypeSymbolDeSer extends CDTypeSymbolDeSerTOP {
  @Override
  public List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson, ICDBasisScope enclosingScope) {
    return symbolJson.getMember("superTypes").getAsJsonArray().getValues()
        .stream().map(i -> SymTypeExpressionDeSer.getInstance().deserialize(i, enclosingScope))
        .collect(Collectors.toList());
  }
}
