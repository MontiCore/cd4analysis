/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCCollectionTypesScopeHelper
    implements MCCollectionTypesVisitor {
  protected MCCollectionTypesVisitor realThis;

  public MCCollectionTypesScopeHelper() {
    setRealThis(this);
  }

  @Override
  public MCCollectionTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCCollectionTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTMCGenericType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCListType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCOptionalType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCMapType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCSetType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCBasicTypeArgument node) {
    node.getMCQualifiedType().setEnclosingScope(node.getEnclosingScope());
  }

  @Override
  public void visit(ASTMCPrimitiveTypeArgument node) {
    node.getMCPrimitiveType().setEnclosingScope(node.getEnclosingScope());
  }
}
