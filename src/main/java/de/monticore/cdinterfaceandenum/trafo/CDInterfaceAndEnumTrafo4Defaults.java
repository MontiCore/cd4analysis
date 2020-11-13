/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;

public class CDInterfaceAndEnumTrafo4Defaults extends CDAfterParseHelper
    implements CDInterfaceAndEnumVisitor {
  protected CDInterfaceAndEnumVisitor realThis;
  protected CDInterfaceAndEnumVisitor symbolTableCreator;

  public CDInterfaceAndEnumTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CDInterfaceAndEnumMill.cDInterfaceAndEnumSymbolTableCreator());
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
