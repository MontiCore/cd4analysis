/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCreator;
import de.monticore.cd4codebasis._symboltable.phased.CD4CodeBasisScopeSkeletonsCreator;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdassociation._symboltable.phased.CDAssociationScopeSkeletonsCreator;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreator;
import de.monticore.cdbasis._symboltable.phased.CDBasisScopeSkeletonsCreator;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreator;
import de.monticore.cdinterfaceandenum._symboltable.phased.CDInterfaceAndEnumScopeSkeletonsCreator;

public class CD4CodeScopeSkeletonsCreatorDelegator
    extends CD4CodeScopeSkeletonsCreatorDelegatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeScopeSkeletonsCreatorDelegator(ICD4CodeGlobalScope globalScope) {
    super(globalScope);
    setRealThis(this);

    setSymbolTableHelper(((CD4CodeGlobalScope) globalScope).getSymbolTableHelper());
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;

    ((CDBasisScopeSkeletonsCreator) this.getCDBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDInterfaceAndEnumScopeSkeletonsCreator) this.getCDInterfaceAndEnumVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDAssociationScopeSkeletonsCreator) this.getCDAssociationVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CD4CodeBasisScopeSkeletonsCreator) this.getCD4CodeBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
  }
}
