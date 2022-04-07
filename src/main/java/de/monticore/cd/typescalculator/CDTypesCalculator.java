/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.visitor.ITraverser;

public abstract class CDTypesCalculator implements IDerive, ISynthesize {

  protected ITraverser traverser;

  protected ITraverser getTraverser() {
    return traverser;
  }

  protected TypeCheckResult typeCheckResult;

  protected TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  @Override
  public TypeCheckResult deriveType(ASTExpression expr) {
    this.getTypeCheckResult().reset();
    expr.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult deriveType(ASTLiteral lit) {
    this.getTypeCheckResult().reset();
    lit.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    this.getTypeCheckResult().reset();
    type.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    this.getTypeCheckResult().reset();
    type.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    this.getTypeCheckResult().reset();
    qName.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }
}
