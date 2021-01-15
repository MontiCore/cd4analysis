/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis._visitor.CD4AnalysisHandler;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor2;

public class CD4AnalysisAfterParseTrafo extends CDAfterParseHelper
    implements CD4AnalysisVisitor2, CD4AnalysisHandler {
  protected CD4AnalysisTraverser traverser;

  public CD4AnalysisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
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
