package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cdassociation._symboltable.phased.CDAssociationSTCompleteTypes;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._symboltable.phased.CDBasisSTCompleteTypes;
import de.monticore.cdinterfaceandenum._symboltable.phased.CDInterfaceAndEnumSTCompleteTypes;

import java.util.Deque;

public class CD4CodeSTCompleteTypesDelegator extends CD4CodeDelegatorVisitor {
  protected final Deque<ICDBasisScope> scopeStack = new java.util.ArrayDeque<>();
  protected final CDSymbolTableHelper symbolTableHelper;
  protected final ICD4CodeGlobalScope globalScope;

  public CD4CodeSTCompleteTypesDelegator(ICD4CodeGlobalScope globalScope) {
    this.scopeStack.push(globalScope);
    this.globalScope = globalScope;
    setRealThis(this);

    this.symbolTableHelper = ((CD4CodeGlobalScope) globalScope).getSymbolTableHelper();

    setCDBasisVisitor(new CDBasisSTCompleteTypes(scopeStack, symbolTableHelper));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumSTCompleteTypes(scopeStack, symbolTableHelper));
    setCDAssociationVisitor(new CDAssociationSTCompleteTypes(symbolTableHelper));
    setCD4CodeVisitor(new CD4CodeSTCompleteTypes(scopeStack, symbolTableHelper));
  }
}
