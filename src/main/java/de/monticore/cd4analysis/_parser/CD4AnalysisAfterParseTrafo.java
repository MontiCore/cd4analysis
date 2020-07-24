/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;

public class CD4AnalysisAfterParseTrafo extends CDAfterParseHelper
    implements CD4AnalysisVisitor {
  protected CD4AnalysisVisitor realThis;

  public CD4AnalysisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CD4AnalysisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4AnalysisVisitor realThis) {
    this.realThis = realThis;
  }
}
