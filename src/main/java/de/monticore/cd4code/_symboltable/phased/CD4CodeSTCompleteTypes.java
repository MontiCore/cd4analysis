package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cdbasis._symboltable.ICDBasisScope;

import java.util.Deque;

public class CD4CodeSTCompleteTypes
    implements CD4CodeVisitor {
  protected Deque<ICDBasisScope> scopeStack;
  protected CDSymbolTableHelper symbolTableHelper;
  protected CD4CodeVisitor realThis = this;

  public CD4CodeSTCompleteTypes(Deque<ICDBasisScope> scopeStack, CDSymbolTableHelper symbolTableHelper) {
    this.scopeStack = scopeStack;
    this.symbolTableHelper = symbolTableHelper;
  }

  @Override
  public CD4CodeVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeVisitor realThis) {
    this.realThis = realThis;
  }
}
