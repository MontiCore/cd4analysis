/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor2;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCArrayTypesScopeHelper
    implements MCArrayTypesVisitor2 {

  @Override
  public void visit(ASTMCArrayType node) {
    node.getMCType().setEnclosingScope(node.getEnclosingScope());
  }
}
