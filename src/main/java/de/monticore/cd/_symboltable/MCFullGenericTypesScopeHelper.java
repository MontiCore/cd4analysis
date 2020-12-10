/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCFullGenericTypesScopeHelper
    implements MCFullGenericTypesVisitor {
  protected MCFullGenericTypesVisitor realThis;

  public MCFullGenericTypesScopeHelper() {
    setRealThis(this);
  }

  @Override
  public MCFullGenericTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCFullGenericTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTMCWildcardTypeArgument node) {
    if (node.isPresentLowerBound()) {
      node.getLowerBound().setEnclosingScope(node.getEnclosingScope());
    }
    if (node.isPresentUpperBound()) {
      node.getUpperBound().setEnclosingScope(node.getEnclosingScope());
    }
  }

  @Override
  public void visit(ASTMCMultipleGenericType node) {
    node.getMCBasicGenericType().setEnclosingScope(node.getEnclosingScope());
    node.getMCInnerTypeList().forEach(i -> i.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCInnerType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }
}
