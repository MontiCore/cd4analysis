/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis._parser.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4codebasis._parser.CD4CodeBasisAfterParseTrafo;
import de.monticore.cdassociation._parser.CDAssociationAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._parser.CDBasisAfterParseTrafo;
import de.monticore.cdinterfaceandenum._parser.CDInterfaceAndEnumAfterParseTrafo;

public class CD4CodeAfterParseDelegatorVisitor
    extends CD4CodeDelegatorVisitor {
  protected final CDAfterParseHelper cdAfterParseHelper;

  public CD4CodeAfterParseDelegatorVisitor() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeAfterParseDelegatorVisitor(CDAfterParseHelper cdAfterParseHelper) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    setRealThis(this);

    setCDBasisVisitor(new CDBasisAfterParseTrafo(cdAfterParseHelper));
    setCDAssociationVisitor(new CDAssociationAfterParseTrafo(cdAfterParseHelper));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper));
    setCD4AnalysisVisitor(new CD4AnalysisAfterParseTrafo(cdAfterParseHelper));
    setCD4CodeBasisVisitor(new CD4CodeBasisAfterParseTrafo(cdAfterParseHelper));
    setCD4CodeVisitor(new CD4CodeAfterParseTrafo(cdAfterParseHelper));
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getRealThis());
  }
}
