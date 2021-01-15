/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation.trafo.CDAssociationAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisAfterParseTrafo;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumAfterParseTrafo;

public class CD4AnalysisAfterParseDelegatorVisitor {
  protected CD4AnalysisTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4AnalysisAfterParseDelegatorVisitor() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisAfterParseDelegatorVisitor(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4AnalysisMill.traverser();

    final CDBasisAfterParseTrafo cdBasis = new CDBasisAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);
    cdBasis.setTraverser(traverser);

    final CDAssociationAfterParseTrafo cdAssociation = new CDAssociationAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDAssociation(cdAssociation);
    traverser.setCDAssociationHandler(cdAssociation);
    cdAssociation.setTraverser(traverser);

    final CDInterfaceAndEnumAfterParseTrafo cdInterfaceAndEnum = new CDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cdInterfaceAndEnum);
    cdInterfaceAndEnum.setTraverser(traverser);

    final CD4AnalysisAfterParseTrafo cd4Analysis = new CD4AnalysisAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CD4Analysis(cd4Analysis);
    traverser.setCD4AnalysisHandler(cd4Analysis);
    cd4Analysis.setTraverser(traverser);
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
