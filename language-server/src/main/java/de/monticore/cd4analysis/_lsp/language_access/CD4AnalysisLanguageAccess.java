package de.monticore.cd4analysis._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import java.util.Optional;

public class CD4AnalysisLanguageAccess extends CD4AnalysisLanguageAccessTOP {
  private final CD4AnalysisAstPrettyPrinter prettyPrinter;

  public CD4AnalysisLanguageAccess(
      DocumentManager documentManager, CD4AnalysisScopeManager scopeManager) {
    super(documentManager, scopeManager);
    this.prettyPrinter = new CD4AnalysisAstPrettyPrinter();
  }

  @Override
  public Optional<AstPrettyPrinter<ASTCDCompilationUnit>> getPrettyPrinter() {
    return Optional.of(prettyPrinter);
  }
}
