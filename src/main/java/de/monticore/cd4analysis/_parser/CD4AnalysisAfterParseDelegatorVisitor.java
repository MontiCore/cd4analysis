/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.cdassociation._parser.CDAssociationAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._parser.CDBasisAfterParseTrafo;
import de.monticore.cdinterfaceandenum._parser.CDInterfaceAndEnumAfterParseTrafo;

public class CD4AnalysisAfterParseDelegatorVisitor
    extends CD4AnalysisDelegatorVisitor {
  protected CDAfterParseHelper cdAfterParseHelper;

  public CD4AnalysisAfterParseDelegatorVisitor() {
    this(new CDAfterParseHelper());
  }

  public CD4AnalysisAfterParseDelegatorVisitor(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    setRealThis(this);

    setCDBasisVisitor(new CDBasisAfterParseTrafo(cdAfterParseHelper));
    setCDAssociationVisitor(new CDAssociationAfterParseTrafo(cdAfterParseHelper));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper));
    setCD4AnalysisVisitor(new CD4AnalysisAfterParseTrafo(cdAfterParseHelper));
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getRealThis());
  }
}
