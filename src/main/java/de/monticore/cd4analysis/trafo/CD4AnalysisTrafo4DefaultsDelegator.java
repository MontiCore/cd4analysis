/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.cdassociation.trafo.CDAssociationTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisTrafo4Defaults;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumTrafo4Defaults;

public class CD4AnalysisTrafo4DefaultsDelegator
    extends CD4AnalysisDelegatorVisitor {
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CD4AnalysisDelegatorVisitor symbolTableCreator;

  public CD4AnalysisTrafo4DefaultsDelegator(ICD4AnalysisGlobalScope globalScope) {
    this(new CDAfterParseHelper(),
        CD4AnalysisMill.cD4AnalysisSymbolTableCreatorDelegatorBuilder().setGlobalScope(globalScope).build());
  }

  public CD4AnalysisTrafo4DefaultsDelegator(CD4AnalysisDelegatorVisitor symbolTableCreator) {
    this(new CDAfterParseHelper(), symbolTableCreator);
  }

  public CD4AnalysisTrafo4DefaultsDelegator(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisDelegatorVisitor symbolTableCreator) {
    setRealThis(this);
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;

    setCDBasisVisitor(new CDBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator));
    setCDAssociationVisitor(new CDAssociationTrafo4Defaults(cdAfterParseHelper, symbolTableCreator));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumTrafo4Defaults(cdAfterParseHelper, symbolTableCreator));
    setCD4AnalysisVisitor(new CD4AnalysisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator));
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getRealThis());
  }
}
