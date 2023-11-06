package de.monticore.cd4analysis._lsp;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_lens.CD4AnalysisCodeLensProvider;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisLanguageAccess;
import org.eclipse.lsp4j.services.LanguageClient;

public class CD4AnalysisTextDocumentService extends CD4AnalysisTextDocumentServiceTOP {
  public CD4AnalysisTextDocumentService(
      CommonLanguageServer languageServer,
      DocumentManager documentManager,
      LanguageClient languageClient,
      CD4AnalysisLanguageAccess languageAccess,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(
        languageServer,
        documentManager,
        languageClient,
        languageAccess,
        symbolUsageResolutionProvider);
  }

  @Override
  protected void registerDefaultCodeLensProvider() {
    register(
        new CD4AnalysisCodeLensProvider(
            referencesProvider, documentManager, symbolUsageResolutionProvider));
  }
}
