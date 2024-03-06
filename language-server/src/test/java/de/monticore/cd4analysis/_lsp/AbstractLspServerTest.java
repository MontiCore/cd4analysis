package de.monticore.cd4analysis._lsp;

import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import de.monticore.io.paths.MCPath;
import java.nio.file.Path;

import org.eclipse.lsp4j.InitializedParams;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractLspServerTest {
  protected CD4AnalysisLanguageServer languageServer;

  @BeforeEach
  public void startServer() {
    AsyncUtilWithSyncExec.init();

    languageServer =
        new CD4AnalysisLanguageServerBuilder().modelPath(new MCPath(getPath())).build();

    MockLanguageClient client = new MockLanguageClient();
    languageServer.connect(client);
    languageServer.initialized(new InitializedParams());
    System.out.println("Calling initialized!");
  }

  protected abstract Path getPath();
}
