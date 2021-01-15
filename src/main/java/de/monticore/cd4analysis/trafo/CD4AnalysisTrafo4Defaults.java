/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisHandler;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor2;

public class CD4AnalysisTrafo4Defaults extends CDAfterParseHelper
    implements CD4AnalysisVisitor2, CD4AnalysisHandler {
  protected CD4AnalysisTraverser traverser;
  protected CD4AnalysisVisitor symbolTableCreator;

  public CD4AnalysisTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4AnalysisMill.cD4AnalysisSymbolTableCreator());
  }

  public CD4AnalysisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }
}
