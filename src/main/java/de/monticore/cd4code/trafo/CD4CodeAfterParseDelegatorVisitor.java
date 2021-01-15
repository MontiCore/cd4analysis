/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis.trafo.CD4CodeBasisAfterParseTrafo;
import de.monticore.cdassociation.trafo.CDAssociationAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisAfterParseTrafo;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumAfterParseTrafo;

public class CD4CodeAfterParseDelegatorVisitor {
  protected CD4CodeTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4CodeAfterParseDelegatorVisitor() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeAfterParseDelegatorVisitor(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4CodeMill.traverser();

    final CDBasisAfterParseTrafo cdBasis = new CDBasisAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);
    cdBasis.setTraverser(traverser);

    final CDAssociationAfterParseTrafo cdAssociation = new CDAssociationAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDAssociation(cdAssociation);
    traverser.setCDAssociationHandler(cdAssociation);
    cdAssociation.setTraverser(traverser);

    final CDInterfaceAndEnumAfterParseTrafo cDInterfaceAndEnum = new CDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CDInterfaceAndEnum(cDInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cDInterfaceAndEnum);
    cDInterfaceAndEnum.setTraverser(traverser);

    final CD4AnalysisAfterParseTrafo cd4Analysis = new CD4AnalysisAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CD4Analysis(cd4Analysis);
    traverser.setCD4AnalysisHandler(cd4Analysis);
    cd4Analysis.setTraverser(traverser);

    final CD4CodeBasisAfterParseTrafo cd4CodeBasis = new CD4CodeBasisAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CD4CodeBasis(cd4CodeBasis);
    traverser.setCD4CodeBasisHandler(cd4CodeBasis);
    cd4CodeBasis.setTraverser(traverser);

    final CD4CodeAfterParseTrafo cd4Code = new CD4CodeAfterParseTrafo(cdAfterParseHelper);
    traverser.add4CD4Code(cd4Code);
    traverser.setCD4CodeHandler(cd4Code);
    cd4Code.setTraverser(traverser);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getTraverser());
  }
}
