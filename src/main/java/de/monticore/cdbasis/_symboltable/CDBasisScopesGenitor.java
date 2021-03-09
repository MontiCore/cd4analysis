/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cdbasis._ast.ASTCDDefinition;

import java.util.Deque;

public class CDBasisScopesGenitor extends CDBasisScopesGenitorTOP {
  public CDBasisScopesGenitor(ICDBasisScope enclosingScope) {
    super(enclosingScope);
  }

  public CDBasisScopesGenitor(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
  }

  public CDBasisScopesGenitor() {
    super();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ICDBasisScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    super.visit(node);
  }
}
