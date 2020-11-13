/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;

/**
 * This visitor sets the enclosing scope of the inner types,
 * so that the typechecks can be used
 */
public class MCArrayTypesScopeHelper
    implements MCArrayTypesVisitor {
  protected MCArrayTypesVisitor realThis;

  public MCArrayTypesScopeHelper() {
    setRealThis(this);
  }

  @Override
  public MCArrayTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCArrayTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTMCArrayType node) {
    node.getMCType().setEnclosingScope(node.getEnclosingScope());
  }
}
