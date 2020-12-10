package de.monticore.cd4code._symboltable.phased;

import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._symboltable.phased.CDBasisScopeSkeletonsCreator;

import java.util.Deque;

public class CDBasisScopeSkeletonsCreatorForCD4Code extends CDBasisScopeSkeletonsCreator {
  public CDBasisScopeSkeletonsCreatorForCD4Code(ICDBasisScope enclosingScope) {
    super(enclosingScope);
  }

  public CDBasisScopeSkeletonsCreatorForCD4Code(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
  }

  public  de.monticore.cd4code._symboltable.ICD4CodeScope createScope (boolean shadowing)  {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.cD4CodeScopeBuilder().build();
    scope.setShadowing(shadowing);
    return scope;
  }
}
