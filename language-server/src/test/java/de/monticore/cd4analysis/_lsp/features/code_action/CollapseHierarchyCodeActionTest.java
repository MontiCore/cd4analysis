package de.monticore.cd4analysis._lsp.features.code_action;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;

class CollapseHierarchyCodeActionTest extends AbstractLspServerTest {

  @Override
  protected Path getPath() {
    return Paths.get("src", "test", "resources", "refactoring", "collapse-hierarchy");
  }

  @Test
  void testSimple() throws IOException {
    String modelUri = getPath().resolve("Simple.cd").toUri().toString();

    Range classNameRange = new Range(new Position(4, 10), new Position(4, 10));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), classNameRange, new CodeActionContext()))
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
    assertEquals(1, classes.size());

    ASTCDClass class1 = classes.get(0);
    assertEquals("Class1", class1.getName());
    assertEquals(1, class1.getCDAttributeList().size());
    ASTCDAttribute attribute = class1.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());
  }

  @Test
  void testPackage() throws IOException {
    String modelUri = getPath().resolve("ClassInPackage.cd").toUri().toString();

    Range classNameRange = new Range(new Position(4, 14), new Position(4, 14));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), classNameRange, new CodeActionContext()))
            .join();

    assertEquals(1, codeActions.size());
    assertEquals("Collapse Hierarchy", codeActions.get(0).getRight().getTitle());
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
    assertEquals(1, classes.size());

    ASTCDClass class1 = classes.get(0);
    assertEquals("Entity", class1.getName());
    assertEquals(1, class1.getCDAttributeList().size());
    ASTCDAttribute attribute = class1.getCDAttributeList().get(0);
    assertEquals("birthday", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());
  }

  @Test
  void testDifferentClassesInDifferentFiles() throws IOException {
    String differentArtifact =
        getPath().resolve("different-artifacts/DifferentArtifact.cd").toUri().toString();
    String targetArtifact =
        getPath().resolve("different-artifacts/TargetArtifact.cd").toUri().toString();

    Range classNameRange = new Range(new Position(1, 14), new Position(1, 14));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(differentArtifact),
                    classNameRange,
                    new CodeActionContext()))
            .join();

    assertEquals(1, codeActions.size());
    assertEquals("Collapse Hierarchy", codeActions.get(0).getRight().getTitle());
    // We cant use the map directly because of case differences in the uri;
    Map<String, List<TextEdit>> changes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    changes.putAll(codeActions.get(0).getRight().getEdit().getChanges());
    assertEquals(2, changes.size());
    List<TextEdit> textEdits = changes.get(differentArtifact);
    assertEquals(1, textEdits.size());

    String changedContent = textEdits.get(0).getNewText();
    ASTCDCompilationUnit compilationUnit =
        CD4AnalysisMill.parser().parse_String(changedContent).orElseThrow();
    List<ASTCDClass> classes = compilationUnit.getCDDefinition().getCDClassesList();
    assertEquals(0, classes.size());

    List<TextEdit> textEditsTarget = changes.get(targetArtifact);
    assertEquals(1, textEditsTarget.size());

    String changedContentTarget = textEditsTarget.get(0).getNewText();
    ASTCDCompilationUnit compilationUnitTarget =
        CD4AnalysisMill.parser().parse_String(changedContentTarget).orElseThrow();
    List<ASTCDClass> classesTarget = compilationUnitTarget.getCDDefinition().getCDClassesList();
    assertEquals(1, classesTarget.size());

    ASTCDClass class1 = classesTarget.get(0);
    ASTCDAttribute attribute = class1.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());
  }
}
