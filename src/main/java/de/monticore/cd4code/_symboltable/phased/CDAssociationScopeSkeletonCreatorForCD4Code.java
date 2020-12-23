package de.monticore.cd4code._symboltable.phased;

import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdassociation._symboltable.phased.CDAssociationScopeSkeletonCreator;

import java.util.Deque;

public class CDAssociationScopeSkeletonCreatorForCD4Code
    extends CDAssociationScopeSkeletonCreator {
  public CDAssociationScopeSkeletonCreatorForCD4Code(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
  }

  public CDAssociationScopeSkeletonCreatorForCD4Code(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
  }

  public de.monticore.cd4code._symboltable.ICD4CodeScope createScope(boolean shadowing) {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.scope();
    scope.setShadowing(shadowing);
    return scope;
  }
}
