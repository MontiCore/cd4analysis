/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

// TODO: MB Lösche die Klasse, wenn die Methoden generiert werden
@Deprecated
public class CD4CodeScopesGenitorDelegator extends CD4CodeScopesGenitorDelegatorTOP {

  public de.monticore.cd4code._visitor.CD4CodeTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(de.monticore.cd4code._visitor.CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }
}
