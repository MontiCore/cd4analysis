/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;

public class CD4AnalysisAfterParseTrafo {
  protected CD4AnalysisTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4AnalysisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4AnalysisMill.traverser();

    init(cdAfterParseHelper, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisTraverser traverser) {
    final CDBasisDefaultPackageTrafo cdBasis = new CDBasisDefaultPackageTrafo();
    traverser.add4CDBasis(cdBasis);

    CD4AnalysisDirectCompositionTrafo.init(cdAfterParseHelper, traverser);
  }

  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getTraverser());
  }
}
