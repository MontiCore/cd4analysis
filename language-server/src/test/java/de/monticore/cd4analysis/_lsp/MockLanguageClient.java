package de.monticore.cd4analysis._lsp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.services.LanguageClient;

public class MockLanguageClient implements LanguageClient {
  public static List<WorkspaceFolder> workspaceFolders;

  @Override
  public void telemetryEvent(Object object) {}

  @Override
  public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {}

  @Override
  public void showMessage(MessageParams messageParams) {}

  @Override
  public CompletableFuture<MessageActionItem> showMessageRequest(
      ShowMessageRequestParams requestParams) {
    return null;
  }

  @Override
  public void logMessage(MessageParams message) {}

  @Override
  public CompletableFuture<List<WorkspaceFolder>> workspaceFolders() {
    return CompletableFuture.completedFuture(workspaceFolders);
  }
}
