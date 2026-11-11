/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4code._lsp.features.code_lens.CD4CodeCodeLensProvider;
import de.monticore.cd4code._lsp.language_access.CD4CodeLanguageAccess;
import org.eclipse.lsp4j.services.LanguageClient;

public class CD4CodeTextDocumentService extends CD4CodeTextDocumentServiceTOP {
  
  public CD4CodeTextDocumentService(CommonLanguageServer languageServer,
      DocumentManager documentManager, LanguageClient languageClient,
      CD4CodeLanguageAccess languageAccess,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageClient, languageAccess,
        symbolUsageResolutionProvider);
  }
  
  @Override
  protected void registerDefaultCodeLensProvider() {
    register(new CD4CodeCodeLensProvider(referencesProvider, documentManager,
        symbolUsageResolutionProvider));
  }
  
}
