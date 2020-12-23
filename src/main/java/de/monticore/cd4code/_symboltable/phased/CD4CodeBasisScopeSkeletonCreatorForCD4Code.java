package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cd4codebasis._symboltable.phased.CD4CodeBasisScopeSkeletonCreator;

import java.util.Deque;

public class CD4CodeBasisScopeSkeletonCreatorForCD4Code
    extends CD4CodeBasisScopeSkeletonCreator {
  public CD4CodeBasisScopeSkeletonCreatorForCD4Code(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
  }

  public CD4CodeBasisScopeSkeletonCreatorForCD4Code(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
  }

  public de.monticore.cd4code._symboltable.ICD4CodeScope createScope(boolean shadowing) {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.scope();
    scope.setShadowing(shadowing);
    return scope;
  }
}
