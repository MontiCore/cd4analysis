/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

// TODO: MB Lösche die Klasse, wenn die Methoden generiert werden
@Deprecated
public class CDAssociationScopesGenitorDelegator extends CDAssociationScopesGenitorDelegatorTOP {

  public de.monticore.cdassociation._visitor.CDAssociationTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(de.monticore.cdassociation._visitor.CDAssociationTraverser traverser) {
    this.traverser = traverser;
  }

  public  void putOnStack (de.monticore.cdassociation._symboltable.ICDAssociationScope scope) {
    scopeStack.addLast(scope);
  }
}
