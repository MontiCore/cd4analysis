/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public interface CDTypesCalculator {
  Optional<SymTypeExpression> calculateType(ASTExpression ex);

  Optional<SymTypeExpression> calculateType(ASTLiteral lit);

  Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit);

  Optional<SymTypeExpression> calculateType(ASTMCType type);

  Optional<SymTypeExpression> calculateType(ASTMCBasicTypesNode node);

  void reset();

  Optional<SymTypeExpression> getResult();
}
