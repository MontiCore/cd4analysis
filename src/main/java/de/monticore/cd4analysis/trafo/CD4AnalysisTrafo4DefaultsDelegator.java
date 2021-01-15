/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation.trafo.CDAssociationTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisTrafo4Defaults;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumTrafo4Defaults;

public class CD4AnalysisTrafo4DefaultsDelegator {
  protected CD4AnalysisTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CD4AnalysisDelegatorVisitor symbolTableCreator;

  public CD4AnalysisTrafo4DefaultsDelegator() {
    this(new CDAfterParseHelper(),
        CD4AnalysisMill.cD4AnalysisSymbolTableCreatorDelegator());
  }

  public CD4AnalysisTrafo4DefaultsDelegator(CD4AnalysisDelegatorVisitor symbolTableCreator) {
    this(new CDAfterParseHelper(), symbolTableCreator);
  }

  public CD4AnalysisTrafo4DefaultsDelegator(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisDelegatorVisitor symbolTableCreator) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;

    this.traverser = CD4AnalysisMill.traverser();

    final CDBasisTrafo4Defaults cdBasis = new CDBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);
    cdBasis.setTraverser(traverser);

    final CDAssociationTrafo4Defaults cdAssociationTrafo4Defaults = new CDAssociationTrafo4Defaults(cdAfterParseHelper, symbolTableCreator);
    traverser.add4CDAssociation(cdAssociationTrafo4Defaults);
    traverser.setCDAssociationHandler(cdAssociationTrafo4Defaults);
    cdAssociationTrafo4Defaults.setTraverser(traverser);

    final CDInterfaceAndEnumTrafo4Defaults cdInterfaceAndEnum = new CDInterfaceAndEnumTrafo4Defaults(cdAfterParseHelper, symbolTableCreator);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cdInterfaceAndEnum);
    cdInterfaceAndEnum.setTraverser(traverser);

    final CD4AnalysisTrafo4Defaults cD4Analysis = new CD4AnalysisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator);
    traverser.add4CD4Analysis(cD4Analysis);
    traverser.setCD4AnalysisHandler(cD4Analysis);
    cD4Analysis.setTraverser(traverser);
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
