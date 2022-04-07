/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisScopesGenitorDelegator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;

public class CD4CodeTrafo4Defaults {
  protected CD4CodeTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CD4AnalysisScopesGenitorDelegator symbolTableCreator;

  public CD4CodeTrafo4Defaults() {
    this(new CDAfterParseHelper(), CD4AnalysisMill.scopesGenitorDelegator());
  }

  public CD4CodeTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisScopesGenitorDelegator symbolTableCreator) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.traverser = CD4CodeMill.traverser();
    this.symbolTableCreator = symbolTableCreator;

    init(cdAfterParseHelper, symbolTableCreator, traverser);
  }

  public void init(CDAfterParseHelper cdAfterParseHelper, CD4AnalysisScopesGenitorDelegator symbolTableCreator, CD4CodeTraverser traverser) {
    final CDAssociationRoleNameTrafo cdAssociation = new CDAssociationRoleNameTrafo(cdAfterParseHelper, symbolTableCreator);
    traverser.add4CDAssociation(cdAssociation);
    traverser.setCDAssociationHandler(cdAssociation);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    if (!compilationUnit.getCDDefinition().isPresentSymbol()) {
      final String msg = "0xCD0B3: can't start the transformation, the symbol table is missing";
      Log.error(msg);
      throw new RuntimeException(msg);
    }

    ((CDAssociationRoleNameTrafo)traverser.getCDAssociationHandler().get()).init(compilationUnit);

    compilationUnit.accept(getTraverser());
  }
}
