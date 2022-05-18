/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class CDMethodSignatureSymbolDeSer
    extends CDMethodSignatureSymbolDeSerTOP {
  @Override
  protected void serializeExceptions(List<SymTypeExpression> exceptions, CD4CodeBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "exceptions", exceptions);
  }

  @Override
  protected void serializeType(SymTypeExpression returnType, CD4CodeBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.printer, "returnType", returnType);
  }

  @Override
  public List<SymTypeExpression> deserializeExceptions(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("exceptions", symbolJson);
  }

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson);
  }
}
