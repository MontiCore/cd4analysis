package de.monticore.cd4analysis._lsp.features.code_lens;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.code_lens.CodeLensStrategy;
import de.mclsg.lsp.features.reference.CommonReferencesProvider;
import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import de.monticore.cd4analysis._lsp.AbstractLspServerTest;
import de.monticore.cd4analysis._lsp.CD4AnalysisLanguageServer;
import de.monticore.cd4analysis._lsp.CD4AnalysisSymbolUsageResolutionProvider;
import de.monticore.cd4analysis._lsp.CD4AnalysisTextDocumentService;
import de.monticore.cd4analysis._lsp.MockLanguageClient;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisScopeManager;
import de.monticore.io.paths.MCPath;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCodeLensTest extends AbstractLspServerTest {
  protected static CommonReferencesProvider referencesProvider;
  protected DocumentManager documentManager;
  protected CD4AnalysisSymbolUsageResolutionProvider symbolUsageResolutionProvider;

  @BeforeEach
  public void startServer() {
    AsyncUtilWithSyncExec.init();

    documentManager = new DocumentManager();
    symbolUsageResolutionProvider = new CD4AnalysisSymbolUsageResolutionProvider();
    languageServer =
        new MockLanguageServer(
            documentManager,
            new MCPath(getPath()),
            new CD4AnalysisScopeManager(),
            symbolUsageResolutionProvider);

    MockLanguageClient client = new MockLanguageClient();
    languageServer.connect(client);
    languageServer.initialized(new InitializedParams());
  }

  public List<? extends CodeLens> codeLens(String uri) {
    Optional<DocumentInformation> documentInformation =
        documentManager.getDocumentInformation(new TextDocumentIdentifier(uri));
    if (documentInformation.isEmpty()) return List.of();

    return documentInformation.get().getMatchedNameTokens().stream()
        .filter(matchedToken -> getCodeLensStrategy().matches(matchedToken))
        .map(matchedToken -> getCodeLensStrategy().apply(matchedToken))
        .flatMap(Optional::stream)
        .collect(Collectors.toList());
  }

  protected abstract CodeLensStrategy getCodeLensStrategy();

  private static class MockLanguageServer extends CD4AnalysisLanguageServer {
    public MockLanguageServer(
        DocumentManager documentManager,
        MCPath modelPath,
        CD4AnalysisScopeManager scopeManager,
        ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
      super(documentManager, modelPath, scopeManager, symbolUsageResolutionProvider);
    }

    @Override
    protected CD4AnalysisTextDocumentService initTextDocumentService() {
      return new CD4AnalysisTextDocumentService(
          this,
          documentManager,
          this.languageClient,
          languageAccess,
          symbolUsageResolutionProvider) {
        @Override
        protected void registerDefaultProviders() {
          super.registerDefaultProviders();
          AbstractCodeLensTest.referencesProvider = referencesProvider;
        }
      };
    }
  }
}
