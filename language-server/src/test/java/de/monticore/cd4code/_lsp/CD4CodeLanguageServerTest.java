/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.sematic_tokens.impl.SemanticTokenTypesWrapper;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayoutBuilder;
import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import de.monticore.cd4analysis._lsp.MockLanguageClient;
import de.monticore.cd4code._lsp.features.syntax_highlighting.CD4CodeLexerProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.junit.jupiter.api.Test;

class CD4CodeLanguageServerTest {
  
  @Test
  void indexesCodeModelAndReusesAnalysisFeatures() throws IOException {
    AsyncUtilWithSyncExec.init();
    Path modelDirectory = Paths.get("src", "test", "resources", "cd4code");
    Path model = modelDirectory.resolve("SharedFeatures.cd");
    DocumentManager documentManager = new DocumentManager();
    CD4CodeLanguageServer server = new CD4CodeLanguageServerBuilder().layout(
        new ProjectLayoutBuilder().projectpath(modelDirectory).build()).documentManager(
            documentManager).build();
    server.connect(new MockLanguageClient());
    server.initialized(new InitializedParams());
    
    String uri = model.toUri().toString();
    assertTrue(documentManager.getDocumentInformation(uri).isPresent());
    assertNotNull(documentManager.getDocumentInformation(uri).orElseThrow().ast);
    
    var lenses = server.getTextDocumentService().codeLens(new CodeLensParams(
        new TextDocumentIdentifier(uri))).join();
    assertEquals(2, lenses.size());
    assertTrue(lenses.stream().allMatch(lens -> lens.getCommand().getTitle().equals(
        "Part of 1 Association")));
    
    var tokens = new CD4CodeLexerProvider(server.getLanguageAccess()).getTokensForInput(Files
        .readString(model));
    assertFalse(tokens.isEmpty());
    assertTrue(tokens.stream().anyMatch(token -> token.getName().equals(
        SemanticTokenTypesWrapper.Class.value)));
  }
  
}
