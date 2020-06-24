/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis._visitor.CDBasisVisitor;

public class CDBasisAfterParseTrafo extends CDAfterParseHelper
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;

  public CDBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }
}
