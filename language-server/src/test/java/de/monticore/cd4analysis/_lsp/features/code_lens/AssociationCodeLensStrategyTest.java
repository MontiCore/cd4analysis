package de.monticore.cd4analysis._lsp.features.code_lens;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.mclsg.lsp.features.code_lens.CodeLensStrategy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

class AssociationCodeLensStrategyTest extends AbstractCodeLensTest {
  private static final String TITLE = "Part of 1 Association";

  @Override
  protected Path getPath() {
    return Paths.get("src", "test", "resources", "code-lens", "association");
  }

  @Override
  protected CodeLensStrategy getCodeLensStrategy() {
    return new AssociationCodeLensStrategy(
        referencesProvider, documentManager, symbolUsageResolutionProvider);
  }

  @Test
  void testSimple() {
    String modelUri = getPath().resolve("Simple.cd").toUri().toString();
    List<? extends CodeLens> codeLenses = codeLens(modelUri);

    assertEquals(2, codeLenses.size());

    Range firstLine = new Range(new Position(1, 8), new Position(1, 9));
    Range secondLine = new Range(new Position(2, 8), new Position(2, 9));

    assertEquals(codeLenses.get(0).getRange(), firstLine);
    assertEquals(codeLenses.get(0).getCommand().getTitle(), TITLE);
    assertEquals(codeLenses.get(1).getRange(), secondLine);
    assertEquals(codeLenses.get(1).getCommand().getTitle(), TITLE);
  }

  @Test
  void testNoAssociation() {
    String modelUri = getPath().resolve("NoAssociation.cd").toUri().toString();
    List<? extends CodeLens> codeLenses = codeLens(modelUri);

    assertEquals(0, codeLenses.size());
  }

  @Test
  void testSuperclass() {
    String modelUri = getPath().resolve("Superclass.cd").toUri().toString();
    List<? extends CodeLens> codeLenses = codeLens(modelUri);

    assertEquals(3, codeLenses.size());

    Range firstLine = new Range(new Position(1, 8), new Position(1, 9));
    Range secondLine = new Range(new Position(2, 8), new Position(2, 9));
    Range thirdLine = new Range(new Position(3, 8), new Position(3, 9));

    assertEquals(codeLenses.get(0).getRange(), firstLine);
    assertEquals(codeLenses.get(0).getCommand().getTitle(), TITLE);
    assertEquals(codeLenses.get(1).getRange(), secondLine);
    assertEquals(codeLenses.get(1).getCommand().getTitle(), TITLE);
    assertEquals(codeLenses.get(2).getRange(), thirdLine);
    assertEquals(codeLenses.get(2).getCommand().getTitle(), TITLE);
  }

  @Test
  void testDifferentArtifact() {
    String modelUri =
        getPath()
            .resolve(Paths.get("different-artifacts", "DifferentArtifact.cd"))
            .toUri()
            .toString();
    List<? extends CodeLens> codeLenses = codeLens(modelUri);

    assertEquals(1, codeLenses.size());

    Range firstLine = new Range(new Position(3, 8), new Position(3, 9));

    assertEquals(codeLenses.get(0).getRange(), firstLine);
    assertEquals(codeLenses.get(0).getCommand().getTitle(), TITLE);
  }
}
