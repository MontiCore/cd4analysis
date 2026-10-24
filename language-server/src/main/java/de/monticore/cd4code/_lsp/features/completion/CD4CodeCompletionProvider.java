/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.features.completion;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCardinalityCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationNavigationCompletionStrategy;
import de.monticore.cd4code._lsp.language_access.CD4CodeLanguageAccess;

public class CD4CodeCompletionProvider extends CD4CodeCompletionProviderTOP {
  
  public CD4CodeCompletionProvider(CommonLanguageServer languageServer,
      DocumentManager documentManager, CD4CodeLanguageAccess languageAccess,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageAccess, symbolUsageResolutionProvider);
    
    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationCompletionStrategy(symbolUsageResolutionProvider,
            documentManager));
    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationCardinalityCompletionStrategy(symbolUsageResolutionProvider,
            documentManager));
    completionStrategyManager.registerCompletionStrategy(
        new CD4AnalysisAssociationNavigationCompletionStrategy(symbolUsageResolutionProvider,
            documentManager));
  }
  
}
