/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class CDMethodSignatureSymbolDeSer
    extends CDMethodSignatureSymbolDeSerTOP {
  @Override
  public List<SymTypeExpression> deserializeExceptions(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("exceptions", symbolJson, null);
  }

  @Override
  public SymTypeExpression deserializeReturnType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson, null);
  }
}
