/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;

// TODO: MB Lösche die Klasse, wenn die Methoden generiert werden
@Deprecated
public class CD4AnalysisScopesGenitorDelegator extends CD4AnalysisScopesGenitorDelegatorTOP {

  public CD4AnalysisTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }

  public  void putOnStack (ICD4AnalysisScope scope) {
    scopeStack.addLast(scope);
  }
}
