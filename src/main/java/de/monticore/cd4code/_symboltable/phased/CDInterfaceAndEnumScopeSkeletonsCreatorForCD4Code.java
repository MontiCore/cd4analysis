package de.monticore.cd4code._symboltable.phased;

import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumScope;
import de.monticore.cdinterfaceandenum._symboltable.phased.CDInterfaceAndEnumScopeSkeletonsCreator;

import java.util.Deque;

public class CDInterfaceAndEnumScopeSkeletonsCreatorForCD4Code extends CDInterfaceAndEnumScopeSkeletonsCreator {
  public CDInterfaceAndEnumScopeSkeletonsCreatorForCD4Code(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
  }

  public CDInterfaceAndEnumScopeSkeletonsCreatorForCD4Code(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public ICDInterfaceAndEnumScope createScope(boolean shadowing) {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.scope();
    scope.setShadowing(shadowing);
    return scope;
  }
}
