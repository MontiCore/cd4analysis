/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code._visitor.CD4CodeVisitor;

public class CD4CodeAfterParseTrafo extends CDAfterParseHelper
    implements CD4CodeVisitor {
  protected CD4CodeVisitor realThis;

  public CD4CodeAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CD4CodeVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeVisitor realThis) {
    this.realThis = realThis;
  }
}
