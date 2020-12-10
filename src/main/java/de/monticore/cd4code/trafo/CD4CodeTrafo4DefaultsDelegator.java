/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.trafo.CD4AnalysisTrafo4Defaults;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4codebasis.trafo.CD4CodeBasisTrafo4Defaults;
import de.monticore.cdassociation.trafo.CDAssociationTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisTrafo4Defaults;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumTrafo4Defaults;

public class CD4CodeTrafo4DefaultsDelegator
    extends CD4CodeDelegatorVisitor {
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CD4CodeDelegatorVisitor symbolTableCreator;

  public CD4CodeTrafo4DefaultsDelegator() {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreatorDelegator());
  }

  public CD4CodeTrafo4DefaultsDelegator(CD4CodeDelegatorVisitor symbolTableCreator) {
    this(new CDAfterParseHelper(), symbolTableCreator);
  }

  public CD4CodeTrafo4DefaultsDelegator(CDAfterParseHelper cdAfterParseHelper, CD4CodeDelegatorVisitor symbolTableCreator) {
    setRealThis(this);
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;

    setCDBasisVisitor(new CDBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDBasisVisitor().get()));
    setCDAssociationVisitor(new CDAssociationTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDAssociationVisitor().get()));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDInterfaceAndEnumVisitor().get()));
    setCD4AnalysisVisitor(new CD4AnalysisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4AnalysisVisitor().get()));
    setCD4CodeBasisVisitor(new CD4CodeBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4CodeBasisVisitor().get()));
    setCD4CodeVisitor(new CD4CodeTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4CodeVisitor().get()));
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    compilationUnit.accept(getRealThis());
  }
}
