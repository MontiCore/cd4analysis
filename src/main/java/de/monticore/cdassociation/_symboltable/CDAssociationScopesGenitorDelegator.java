/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.se_rwth.commons.logging.Log;

// TODO: MB Lösche die Klasse, wenn die methoden generiert werden
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
