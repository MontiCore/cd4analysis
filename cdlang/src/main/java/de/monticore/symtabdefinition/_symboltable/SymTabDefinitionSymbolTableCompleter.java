/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition._symboltable;

import de.monticore.symtabdefinition._ast.ASTSTDFunction;
import de.monticore.symtabdefinition._ast.ASTSTDVariable;
import de.monticore.symtabdefinition._visitor.SymTabDefinitionVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SymTabDefinitionSymbolTableCompleter implements SymTabDefinitionVisitor2 {
  
  public SymTabDefinitionSymbolTableCompleter() {
  }
  
  @Override
  public void endVisit(ASTSTDFunction node) {
    final SymTypeExpression typeResult = TypeCheck3.symTypeFromAST(node.getMCReturnType());
    if (typeResult == null || typeResult.isObscureType()) {
      Log.error("0xEDA90 the return type of " + node.getName() + "could not be calculated", node
          .getMCReturnType().get_SourcePositionStart(), node.getMCReturnType()
              .get_SourcePositionEnd());
    }
    else {
      node.getSymbol().setType(typeResult);
    }
  }
  
  @Override
  public void endVisit(ASTSTDVariable node) {
    final SymTypeExpression typeResult = TypeCheck3.symTypeFromAST(node.getMCType());
    if (typeResult == null || typeResult.isObscureType()) {
      Log.error("0xEDA91 the type of " + node.getName() + "could not be calculated", node
          .getMCType().get_SourcePositionStart(), node.getMCType().get_SourcePositionEnd());
    }
    else {
      node.getSymbol().setType(typeResult);
    }
  }
  
}
