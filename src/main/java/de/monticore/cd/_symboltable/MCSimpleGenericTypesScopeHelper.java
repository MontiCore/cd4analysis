/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCSimpleGenericTypesScopeHelper
    implements MCSimpleGenericTypesVisitor {
  protected MCSimpleGenericTypesVisitor realThis;

  public MCSimpleGenericTypesScopeHelper() {
    setRealThis(this);
  }

  @Override
  public MCSimpleGenericTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCSimpleGenericTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTMCBasicGenericType node) {
    node.getMCTypeArgumentsList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCCustomTypeArgument node) {
    node.getMCType().setEnclosingScope(node.getEnclosingScope());
  }
}
