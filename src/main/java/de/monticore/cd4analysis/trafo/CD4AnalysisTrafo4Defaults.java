/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;

public class CD4AnalysisTrafo4Defaults extends CDAfterParseHelper
    implements CD4AnalysisVisitor {
  protected CD4AnalysisVisitor realThis;
  protected CD4AnalysisVisitor symbolTableCreator;

  public CD4AnalysisTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4AnalysisMill.cD4AnalysisSymbolTableCreator());
  }

  public CD4AnalysisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
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
