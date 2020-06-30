/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

// TODO SVa: find better solution

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCBasicTypesScopeHelper
    implements MCBasicTypesVisitor {
  protected MCBasicTypesVisitor realThis;

  public MCBasicTypesScopeHelper() {
    setRealThis(this);
  }

  @Override
  public MCBasicTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCBasicTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTMCQualifiedType node) {
    node.getMCQualifiedName().setEnclosingScope(node.getEnclosingScope());
  }

  @Override
  public void visit(ASTMCReturnType node) {
    if (node.isPresentMCType()) {
      node.getMCType().setEnclosingScope(node.getEnclosingScope());
    }
  }
}
