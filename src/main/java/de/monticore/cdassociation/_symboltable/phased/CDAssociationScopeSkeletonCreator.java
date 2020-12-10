/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable.phased;

import de.monticore.cdassociation._ast.ASTCDDirectComposition;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;

import java.util.Deque;

public class CDAssociationScopeSkeletonCreator
    extends CDAssociationScopeSkeletonCreatorTOP {

  public CDAssociationScopeSkeletonCreator(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
  }

  public CDAssociationScopeSkeletonCreator(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    throw new IllegalStateException("0xCDA66: Cannot create a symbol for CDDirectComposition, please transform to a CDAssociation.");
  }
}
