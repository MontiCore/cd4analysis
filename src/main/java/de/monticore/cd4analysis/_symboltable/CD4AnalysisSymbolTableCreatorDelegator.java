/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreator;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreator;

/**
 * @deprecated use {@link CD4AnalysisScopesGenitorDelegator} instead
 */
@Deprecated
public class CD4AnalysisSymbolTableCreatorDelegator
    extends CD4AnalysisSymbolTableCreatorDelegatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4AnalysisSymbolTableCreatorDelegator() {
    super();
    setRealThis(this);
    
    setSymbolTableHelper(((CD4AnalysisGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CD4AnalysisSymbolTableCreatorDelegator(ICD4AnalysisGlobalScope globalScope) {
    super(globalScope);
    setRealThis(this);

    setSymbolTableHelper(((CD4AnalysisGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;

    ((CDBasisSymbolTableCreator) this.getCDBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDInterfaceAndEnumSymbolTableCreator) this.getCDInterfaceAndEnumVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDAssociationSymbolTableCreator) this.getCDAssociationVisitor().get()).setSymbolTableHelper(symbolTableHelper);
  }
}
