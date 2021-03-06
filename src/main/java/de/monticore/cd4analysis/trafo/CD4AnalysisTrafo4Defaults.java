/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation._symboltable.CDAssociationScopesGenitor;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;

public class CD4AnalysisTrafo4Defaults {
  protected CD4AnalysisTraverser traverser;
  protected final CDAfterParseHelper cdAfterParseHelper;
  protected final CDAssociationScopesGenitor symbolTableCreator;

  public CD4AnalysisTrafo4Defaults() {
    this(new CDAfterParseHelper(), new CDAssociationScopesGenitor());
  }

  public CD4AnalysisTrafo4Defaults(CDAssociationScopesGenitor symbolTableCreator) {
    this(new CDAfterParseHelper(), symbolTableCreator);
  }

  public CD4AnalysisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDAssociationScopesGenitor symbolTableCreator) {
    this.cdAfterParseHelper = cdAfterParseHelper;
    this.symbolTableCreator = symbolTableCreator;
    this.traverser = CD4AnalysisMill.traverser();

    init(cdAfterParseHelper, symbolTableCreator, traverser);
  }

  public static void init(CDAfterParseHelper cdAfterParseHelper, CDAssociationScopesGenitor symbolTableCreator, CD4AnalysisTraverser traverser) {
    final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo(cdAfterParseHelper, symbolTableCreator);
    traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
    traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
    cdAssociationRoleNameTrafo.setTraverser(traverser);
  }

  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }

  public void transform(ASTCDCompilationUnit compilationUnit) {
    if (!compilationUnit.getCDDefinition().isPresentSymbol()) {
      final String msg = "0xCD0B4: can't start the transformation, the symbol table is missing";
      Log.error(msg);
      throw new RuntimeException(msg);
    }

    ((CDAssociationRoleNameTrafo)traverser.getCDAssociationHandler().get()).init(compilationUnit);

    compilationUnit.accept(getTraverser());
  }
}
