/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;

public class CDInterfaceAndEnumAfterParseTrafo extends CDAfterParseHelper
    implements CDInterfaceAndEnumVisitor {
  protected CDInterfaceAndEnumVisitor realThis;

  public CDInterfaceAndEnumAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDInterfaceAndEnumAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
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
