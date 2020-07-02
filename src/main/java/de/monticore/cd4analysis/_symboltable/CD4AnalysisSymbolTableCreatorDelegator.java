/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis.typescalculator.DeriveSymTypeOfCD4Analysis;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreator;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreator;

public class CD4AnalysisSymbolTableCreatorDelegator
    extends CD4AnalysisSymbolTableCreatorDelegatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4AnalysisSymbolTableCreatorDelegator(ICD4AnalysisGlobalScope globalScope) {
    super(globalScope);
    setRealThis(this);

    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Analysis()));
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;

    ((CDBasisSymbolTableCreator) this.getCDBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDInterfaceAndEnumSymbolTableCreator) this.getCDInterfaceAndEnumVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDAssociationSymbolTableCreator) this.getCDAssociationVisitor().get()).setSymbolTableHelper(symbolTableHelper);
  }
}
