package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cd4codebasis._symboltable.phased.CD4CodeBasisScopeSkeletonsCreator;

import java.util.Deque;

public class CD4CodeBasisScopeSkeletonsCreatorForCD4Code
    extends CD4CodeBasisScopeSkeletonsCreator {
  public CD4CodeBasisScopeSkeletonsCreatorForCD4Code(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
  }

  public CD4CodeBasisScopeSkeletonsCreatorForCD4Code(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
  }

  public de.monticore.cd4code._symboltable.ICD4CodeScope createScope(boolean shadowing) {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.cD4CodeScopeBuilder().build();
    scope.setShadowing(shadowing);
    return scope;
  }
}
