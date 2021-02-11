/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisScopesGenitor;
import de.monticore.cdassociation._symboltable.CDAssociationScopesGenitor;
import de.monticore.cdbasis._symboltable.CDBasisScopesGenitor;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumScopesGenitor;

public class CD4CodeScopesGenitorDelegator
    extends CD4CodeScopesGenitorDelegatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeScopesGenitorDelegator() {
    super();

    setSymbolTableHelper(((CD4CodeGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CD4CodeScopesGenitorDelegator(ICD4CodeGlobalScope globalScope) {
    super(globalScope);

    setSymbolTableHelper(((CD4CodeGlobalScope) globalScope).getSymbolTableHelper());
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
    ((CD4CodeBasisScopesGenitor) traverser.getCD4CodeBasisVisitorList().get(0)).setSymbolTableHelper(symbolTableHelper);
  }
}
