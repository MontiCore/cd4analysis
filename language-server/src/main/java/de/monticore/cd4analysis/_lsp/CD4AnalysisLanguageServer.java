package de.monticore.cd4analysis._lsp;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.CD4AnalysisCodeActionProvider;
import de.monticore.cd4analysis._lsp.features.code_action.CollapseHierarchyCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.ExtractSuperClassCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.PullUpFieldCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.UnfoldByAttributesStrategy;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisScopeManager;
import de.monticore.io.paths.MCPath;

public class CD4AnalysisLanguageServer extends CD4AnalysisLanguageServerTOP {

  public CD4AnalysisLanguageServer(
      DocumentManager documentManager,
      MCPath modelPath,
      CD4AnalysisScopeManager scopeManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager, modelPath, scopeManager, symbolUsageResolutionProvider);

    CD4AnalysisCodeActionProvider codeActionProvider =
        new CD4AnalysisCodeActionProvider(
            documentManager, languageAccess.getPrettyPrinter().orElseThrow());
    codeActionProvider.addCodeActionStrategy(
        new PullUpFieldCodeActionStrategy(
            documentManager,
            symbolUsageResolutionProvider,
            languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(
        new ExtractSuperClassCodeActionStrategy(
            documentManager, languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(
        new CollapseHierarchyCodeActionStrategy(
            documentManager,
            languageAccess.getPrettyPrinter().orElseThrow(),
            symbolUsageResolutionProvider));
    codeActionProvider.addCodeActionStrategy(
        new UnfoldByAttributesStrategy(
            documentManager, languageAccess.getPrettyPrinter().orElseThrow()));
    textDocumentService.register(codeActionProvider);
  }
}
