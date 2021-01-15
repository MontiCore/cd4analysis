/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.trafo.CD4AnalysisTrafo4Defaults;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis.trafo.CD4CodeBasisTrafo4Defaults;
import de.monticore.cdassociation.trafo.CDAssociationTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisTrafo4Defaults;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumTrafo4Defaults;

public class CD4CodeTrafo4DefaultsDelegator {
  protected CD4CodeTraverser traverser;
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
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;

    this.traverser = CD4CodeMill.traverser();

    final CDBasisTrafo4Defaults cdBasis = new CDBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDBasisVisitor().get());
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);

    final CDAssociationTrafo4Defaults cdAssociation = new CDAssociationTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDAssociationVisitor().get());
    traverser.add4CDAssociation(cdAssociation);
    traverser.setCDAssociationHandler(cdAssociation);

    final CDInterfaceAndEnumTrafo4Defaults cdInterfaceAndEnum = new CDInterfaceAndEnumTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCDInterfaceAndEnumVisitor().get());
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnum);
    traverser.setCDInterfaceAndEnumHandler(cdInterfaceAndEnum);

    final CD4AnalysisTrafo4Defaults cd4Analysis = new CD4AnalysisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4AnalysisVisitor().get());
    traverser.add4CD4Analysis(cd4Analysis);
    traverser.setCD4AnalysisHandler(cd4Analysis);

    final CD4CodeBasisTrafo4Defaults cd4CodeBasis = new CD4CodeBasisTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4CodeBasisVisitor().get());
    traverser.add4CD4CodeBasis(cd4CodeBasis);
    traverser.setCD4CodeBasisHandler(cd4CodeBasis);

    final CD4CodeTrafo4Defaults cd4Code = new CD4CodeTrafo4Defaults(cdAfterParseHelper, symbolTableCreator.getCD4CodeVisitor().get());
    traverser.add4CD4Code(cd4Code);
    traverser.setCD4CodeHandler(cd4Code);
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
