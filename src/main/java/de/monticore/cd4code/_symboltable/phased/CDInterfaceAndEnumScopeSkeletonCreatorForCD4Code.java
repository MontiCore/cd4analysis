package de.monticore.cd4code._symboltable.phased;

import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumScope;
import de.monticore.cdinterfaceandenum._symboltable.phased.CDInterfaceAndEnumScopeSkeletonCreator;

import java.util.Deque;

public class CDInterfaceAndEnumScopeSkeletonCreatorForCD4Code extends CDInterfaceAndEnumScopeSkeletonCreator {
  public CDInterfaceAndEnumScopeSkeletonCreatorForCD4Code(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
  }

  public CDInterfaceAndEnumScopeSkeletonCreatorForCD4Code(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public ICDInterfaceAndEnumScope createScope(boolean shadowing) {
    de.monticore.cd4code._symboltable.ICD4CodeScope scope = de.monticore.cd4code.CD4CodeMill.cD4CodeScopeBuilder().build();
    scope.setShadowing(shadowing);
    return scope;
  }
}
