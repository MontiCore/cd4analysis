/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class CDTypeSymbolDeSer extends CDTypeSymbolDeSerTOP {
  @Override
  public List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson, null);
  }
}
