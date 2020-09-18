/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumScope;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;

import java.util.Deque;

public class CDInterfaceAndEnumTrafo4Defaults extends CDAfterParseHelper
    implements CDInterfaceAndEnumVisitor {
  protected CDInterfaceAndEnumVisitor realThis;
  protected CDInterfaceAndEnumVisitor symbolTableCreator;

  public CDInterfaceAndEnumTrafo4Defaults(Deque<ICDInterfaceAndEnumScope> scopeStack) {
    this(new CDAfterParseHelper(),
        CDInterfaceAndEnumMill.cDInterfaceAndEnumSymbolTableCreatorBuilder().setScopeStack(scopeStack).build());
  }

  public CDInterfaceAndEnumTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDInterfaceAndEnumVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDInterfaceAndEnumVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDInterfaceAndEnumVisitor realThis) {
    this.realThis = realThis;
  }
}
