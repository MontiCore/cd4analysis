// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition._symboltable;

import de.monticore.symtabdefinition._ast.ASTSTDFunction;
import de.monticore.symtabdefinition._ast.ASTSTDVariable;
import de.monticore.symtabdefinition._visitor.SymTabDefinitionVisitor2;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class SymTabDefinitionSymbolTableCompleter implements SymTabDefinitionVisitor2 {

  protected ISynthesize typeSynthesizer;

  public SymTabDefinitionSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  @Override
  public void endVisit(ASTSTDFunction node) {
    final TypeCheckResult typeResult = typeSynthesizer.synthesizeType(node.getMCReturnType());
    if (!typeResult.isPresentResult() || typeResult.getResult().isObscureType()) {
      Log.error(
          "0xEDA90 the return type of " + node.getName() + "could not be calculated",
          node.getMCReturnType().get_SourcePositionStart(),
          node.getMCReturnType().get_SourcePositionEnd());
    } else {
      node.getSymbol().setType(typeResult.getResult());
    }
  }

  @Override
  public void endVisit(ASTSTDVariable node) {
    final TypeCheckResult typeResult = typeSynthesizer.synthesizeType(node.getMCType());
    if (!typeResult.isPresentResult() || typeResult.getResult().isObscureType()) {
      Log.error(
          "0xEDA91 the type of " + node.getName() + "could not be calculated",
          node.getMCType().get_SourcePositionStart(),
          node.getMCType().get_SourcePositionEnd());
    } else {
      node.getSymbol().setType(typeResult.getResult());
    }
  }
}
