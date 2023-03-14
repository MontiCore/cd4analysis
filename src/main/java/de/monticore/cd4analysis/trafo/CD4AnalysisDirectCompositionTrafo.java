/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis.trafo.CDBasisDirectCompositionTrafo;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumDirectCompositionTrafo;

public class CD4AnalysisDirectCompositionTrafo {
  protected CD4AnalysisTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4AnalysisDirectCompositionTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisDirectCompositionTrafo(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4AnalysisMill.traverser();

    init(cdAfterParseHelper, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisTraverser traverser) {
    final CDBasisDirectCompositionTrafo cdBasisDirectCompositionTrafo =
        new CDBasisDirectCompositionTrafo(cdAfterParseHelper);
    traverser.add4CDBasis(cdBasisDirectCompositionTrafo);
    traverser.setCDBasisHandler(cdBasisDirectCompositionTrafo);
    cdBasisDirectCompositionTrafo.setTraverser(traverser);

    final CDAssociationDirectCompositionTrafo cdAssociation =
        new CDAssociationDirectCompositionTrafo();
    traverser.add4CDAssociation(cdAssociation);
    traverser.add4CDBasis(cdAssociation);

    final CDInterfaceAndEnumDirectCompositionTrafo cDInterfaceAndEnum =
        new CDInterfaceAndEnumDirectCompositionTrafo(cdAfterParseHelper);
    traverser.add4CDInterfaceAndEnum(cDInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cDInterfaceAndEnum);
    cDInterfaceAndEnum.setTraverser(traverser);
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
