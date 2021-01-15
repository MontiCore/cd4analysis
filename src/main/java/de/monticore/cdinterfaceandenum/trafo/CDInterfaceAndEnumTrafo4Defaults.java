/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumHandler;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;

public class CDInterfaceAndEnumTrafo4Defaults extends CDAfterParseHelper
    implements CDInterfaceAndEnumVisitor2, CDInterfaceAndEnumHandler {
  protected CDInterfaceAndEnumTraverser traverser;
  protected CDInterfaceAndEnumVisitor symbolTableCreator;

  public CDInterfaceAndEnumTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CDInterfaceAndEnumMill.cDInterfaceAndEnumSymbolTableCreator());
  }

  public CDInterfaceAndEnumTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDInterfaceAndEnumVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDInterfaceAndEnumTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDInterfaceAndEnumTraverser traverser) {
    this.traverser = traverser;
  }
}
