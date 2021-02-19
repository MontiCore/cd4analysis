/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.CDAssociationScopesGenitor;
import de.monticore.cdbasis._symboltable.CDBasisScopesGenitor;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumScopesGenitor;

public class CD4AnalysisScopesGenitorDelegator
    extends CD4AnalysisScopesGenitorDelegatorTOP {

  protected CDSymbolTableHelper symbolTableHelper;

  public CD4AnalysisScopesGenitorDelegator() {
    super();

    setSymbolTableHelper(((CD4AnalysisGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CD4AnalysisScopesGenitorDelegator(ICD4AnalysisGlobalScope globalScope) {
    super(globalScope);

    setSymbolTableHelper(((CD4AnalysisGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;

    ((CDBasisScopesGenitor) traverser.getCDBasisVisitorList().get(0)).setSymbolTableHelper(symbolTableHelper);
    ((CDInterfaceAndEnumScopesGenitor) traverser.getCDInterfaceAndEnumVisitorList().get(0)).setSymbolTableHelper(symbolTableHelper);
    ((CDAssociationScopesGenitor) traverser.getCDAssociationVisitorList().get(0)).setSymbolTableHelper(symbolTableHelper);
  }
}
