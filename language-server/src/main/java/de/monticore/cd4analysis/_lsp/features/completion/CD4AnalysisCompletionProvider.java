package de.monticore.cd4analysis._lsp.features.completion;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCardinalityCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationNavigationCompletionStrategy;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisLanguageAccess;

public class CD4AnalysisCompletionProvider extends CD4AnalysisCompletionProviderTOP {

  public CD4AnalysisCompletionProvider(
      CommonLanguageServer languageServer,
      DocumentManager documentManager,
      CD4AnalysisLanguageAccess languageAccess,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageAccess, symbolUsageResolutionProvider);

    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationCompletionStrategy(
            symbolUsageResolutionProvider, documentManager));
    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationCardinalityCompletionStrategy(
            symbolUsageResolutionProvider, documentManager));
    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationNavigationCompletionStrategy(
            symbolUsageResolutionProvider, documentManager));
  }
}
