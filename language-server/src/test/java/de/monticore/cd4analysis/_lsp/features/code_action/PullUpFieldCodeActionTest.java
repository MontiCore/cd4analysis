package de.monticore.cd4analysis._lsp.features.code_action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._lsp.AbstractLspServerTest;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;

class PullUpFieldCodeActionTest extends AbstractLspServerTest {

  @Override
  protected Path getPath() {
    return Paths.get("src", "test", "resources", "refactoring", "pull-up-field");
  }

  @Test
  void testSimple() throws IOException {
    String modelUri = getPath().resolve("Simple.cd").toUri().toString();

    Range fieldRange = new Range(new Position(4, 11), new Position(4, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldRange, new CodeActionContext()))
            .join();

    assertEquals(1, codeActions.size());
    // We cant use the map directly because of case differences in the uri;
    Map<String, List<TextEdit>> changes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    changes.putAll(codeActions.get(0).getRight().getEdit().getChanges());
    assertEquals(1, changes.size());
    List<TextEdit> textEdits = changes.get(modelUri);
    assertEquals(1, textEdits.size());

    String changedContent = textEdits.get(0).getNewText();
    ASTCDCompilationUnit compilationUnit =
        CD4AnalysisMill.parser().parse_String(changedContent).orElseThrow();
    List<ASTCDClass> classes = compilationUnit.getCDDefinition().getCDClassesList();
    assertEquals(2, classes.size());

    ASTCDClass superClass = classes.get(0);
    assertEquals("SuperClass", superClass.getName());
    assertEquals(1, superClass.getCDAttributeList().size());
    ASTCDAttribute attribute = superClass.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());

    ASTCDClass childClass = classes.get(1);
    assertEquals("ChildClass", childClass.getName());
    assertTrue(childClass.getCDAttributeList().isEmpty());
  }

  @Test
  void testNameConflict() throws IOException {
    String modelUri = getPath().resolve("NameConflict.cd").toUri().toString();

    Range fieldRange = new Range(new Position(6, 11), new Position(6, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldRange, new CodeActionContext()))
            .join();

    assertEquals(1, codeActions.size());
    // We cant use the map directly because of case differences in the uri;
    Map<String, List<TextEdit>> changes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    changes.putAll(codeActions.get(0).getRight().getEdit().getChanges());
    assertEquals(1, changes.size());
    List<TextEdit> textEdits = changes.get(modelUri);
    assertEquals(1, textEdits.size());

    String changedContent = textEdits.get(0).getNewText();
    ASTCDCompilationUnit compilationUnit =
        CD4AnalysisMill.parser().parse_String(changedContent).orElseThrow();
    List<ASTCDClass> classes = compilationUnit.getCDDefinition().getCDClassesList();
    assertEquals(2, classes.size());

    ASTCDClass superClass = classes.get(0);
    assertEquals("SuperClass", superClass.getName());
    assertEquals(2, superClass.getCDAttributeList().size());
    ASTCDAttribute existingAttribute = superClass.getCDAttributeList().get(0);
    assertEquals("field", existingAttribute.getName());
    assertEquals("String", existingAttribute.getMCType().printType());
    ASTCDAttribute newAttribute = superClass.getCDAttributeList().get(1);
    assertEquals("field1", newAttribute.getName());
    assertEquals("String", newAttribute.getMCType().printType());

    ASTCDClass childClass = classes.get(1);
    assertEquals("ChildClass", childClass.getName());
    assertTrue(childClass.getCDAttributeList().isEmpty());
  }

  @Test
  void testDifferentArtifact() throws IOException {
    String modelUri =
        getPath().resolve("different-artifacts").resolve("DifferentArtifact.cd").toUri().toString();
    String otherArtifactUri =
        getPath().resolve("different-artifacts").resolve("TargetArtifact.cd").toUri().toString();

    Range fieldRange = new Range(new Position(2, 11), new Position(2, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldRange, new CodeActionContext()))
            .join();

    assertEquals(1, codeActions.size());
    // We cant use the map directly because of case differences in the uri;
    Map<String, List<TextEdit>> changes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    changes.putAll(codeActions.get(0).getRight().getEdit().getChanges());
    assertEquals(2, changes.size());

    // DifferentArtifact.cd
    List<TextEdit> textEdits = changes.get(modelUri);
    assertEquals(1, textEdits.size());

    String changedContent = textEdits.get(0).getNewText();
    ASTCDCompilationUnit compilationUnit =
        CD4AnalysisMill.parser().parse_String(changedContent).orElseThrow();
    List<ASTCDClass> classes = compilationUnit.getCDDefinition().getCDClassesList();
    assertEquals(1, classes.size());

    ASTCDClass childClass = classes.get(0);
    assertEquals("ChildClass", childClass.getName());
    assertTrue(childClass.getCDAttributeList().isEmpty());

    // TargetArtifact.cd
    textEdits = changes.get(otherArtifactUri);
    assertEquals(1, textEdits.size());

    changedContent = textEdits.get(0).getNewText();
    compilationUnit = CD4AnalysisMill.parser().parse_String(changedContent).orElseThrow();
    classes = compilationUnit.getCDDefinition().getCDClassesList();
    assertEquals(1, classes.size());

    ASTCDClass superClass = classes.get(0);
    assertEquals("TargetClass", superClass.getName());
    assertEquals(1, superClass.getCDAttributeList().size());
    ASTCDAttribute attribute = superClass.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());
  }
}
