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
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HighlightClassNameTest {

  static Stream<Arguments> provideTestData() {
    String basePath = "src/test/resources/highlighting/";
    return Stream.of(
        Arguments.arguments(basePath + "CDName.cd", List.of(1)),
        Arguments.arguments(basePath + "ClassNamesNested.cd", List.of(1, 7)),
        Arguments.arguments(basePath + "ExtendClassName.cd", List.of(1, 5, 8, 10)));
  }

  @ParameterizedTest
  @MethodSource("provideTestData")
  void testClassDiagramName(String path, List<Integer> tokenIndices) throws IOException {
    CD4AnalysisLexerProvider lexerProvider =
        new CD4AnalysisLexerProvider(
            new CD4AnalysisLanguageAccess(new DocumentManager(), new CD4AnalysisScopeManager()));
    Path pathToModel = Paths.get(path);
    String content = IOUtils.toString(pathToModel.toUri(), StandardCharsets.UTF_8);

    List<Token> classifiedTokens = lexerProvider.getTokensForInput(content);

    // CD Name is Class Type
    for (Integer tokenIndex : tokenIndices) {
      assertEquals(
          SemanticTokenTypesWrapper.Class.value, classifiedTokens.get(tokenIndex).getName());
    }

    // All other name tokens are not Class Type

    for (int i = 0; i < classifiedTokens.size(); i++) {
      if (!tokenIndices.contains(i)) {
        assertNotEquals(SemanticTokenTypesWrapper.Class.value, classifiedTokens.get(i).getName());
      }
    }
  }
}
