package de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.SemanticTokenTypesWrapper;
import de.monticore.cd4analysis._lsp.features.syntax_highlighting.CD4AnalysisLexerProvider;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisLanguageAccess;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisScopeManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class HighlightPackageNameTest {

  @Test
  void testInterfaceName() throws IOException {
    CD4AnalysisLexerProvider lexerProvider =
        new CD4AnalysisLexerProvider(
            new CD4AnalysisLanguageAccess(new DocumentManager(), new CD4AnalysisScopeManager()));
    Path pathToModel = Paths.get("src/test/resources/highlighting/ClassNamesNested.cd");
    String content = IOUtils.toString(pathToModel.toUri(), StandardCharsets.UTF_8);

    List<Token> classifiedTokens = lexerProvider.getTokensForInput(content);

    // Check interface name
    int namespaceToken = 4;
    assertEquals(
        SemanticTokenTypesWrapper.Namespace.value, classifiedTokens.get(namespaceToken).getName());

    for (int i = 0; i < classifiedTokens.size(); i++) {
      if (i != namespaceToken) {
        assertNotEquals(
            SemanticTokenTypesWrapper.Namespace.value, classifiedTokens.get(i).getName());
      }
    }
  }
}
