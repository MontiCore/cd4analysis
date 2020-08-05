/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCreator;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreator;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreator;

public class CD4CodeSymbolTableCreatorDelegator
    extends CD4CodeSymbolTableCreatorDelegatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeSymbolTableCreatorDelegator(ICD4CodeGlobalScope globalScope) {
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

    ((CDBasisSymbolTableCreator) this.getCDBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDInterfaceAndEnumSymbolTableCreator) this.getCDInterfaceAndEnumVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CDAssociationSymbolTableCreator) this.getCDAssociationVisitor().get()).setSymbolTableHelper(symbolTableHelper);
    ((CD4CodeBasisSymbolTableCreator) this.getCD4CodeBasisVisitor().get()).setSymbolTableHelper(symbolTableHelper);
  }
}
