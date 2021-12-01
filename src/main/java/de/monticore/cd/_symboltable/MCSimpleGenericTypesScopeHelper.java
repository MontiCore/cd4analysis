/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor2;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCSimpleGenericTypesScopeHelper
    implements MCSimpleGenericTypesVisitor2 {


  @Override
  public void visit(ASTMCBasicGenericType node) {
    node.getMCTypeArgumentList().forEach(a -> a.setEnclosingScope(node.getEnclosingScope()));
  }

  @Override
  public void visit(ASTMCCustomTypeArgument node) {
    node.getMCType().setEnclosingScope(node.getEnclosingScope());
  }
}
