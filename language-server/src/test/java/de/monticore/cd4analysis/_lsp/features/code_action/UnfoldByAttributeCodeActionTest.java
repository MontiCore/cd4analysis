package de.monticore.cd4analysis._lsp.features.code_action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._lsp.AbstractLspServerTest;
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

class UnfoldByAttributeCodeActionTest extends AbstractLspServerTest {

  @Override
  protected Path getPath() {
    return Paths.get("src", "test", "resources", "refactoring", "unfold-by-attribute");
  }

  @Test
  void testWithNoSuperclass() throws IOException {
    String modelUri = getPath().resolve("WithoutSuperclass.cd").toUri().toString();

    Range fieldNameRange = new Range(new Position(2, 4), new Position(2, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldNameRange, new CodeActionContext()))
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

    ASTCDClass class1 = classes.get(0);
    assertEquals("Class1", class1.getName());
    assertEquals(0, class1.getCDAttributeList().size());
    ASTCDClass class2 = classes.get(1);
    assertEquals("SuperClass1", class2.getName());
    assertEquals(1, class2.getCDAttributeList().size());
  }

  @Test
  void testWithSuperclass() throws IOException {
    String modelUri = getPath().resolve("WithSuperClass.cd").toUri().toString();

    Range fieldNameRange = new Range(new Position(2, 4), new Position(2, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldNameRange, new CodeActionContext()))
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
    assertEquals(3, classes.size());

    ASTCDClass class1 = classes.get(0);
    assertEquals("Class1", class1.getName());
    assertEquals(0, class1.getCDAttributeList().size());
    assertEquals(1, class1.getSuperclassList().size());
    ASTCDClass class2 = classes.get(1);
    assertEquals("Class", class2.getName());
    assertEquals(1, class2.getCDAttributeList().size());
    ASTCDClass class3 = classes.get(2);
    assertEquals("SuperClass1", class3.getName());
    assertEquals(1, class3.getCDAttributeList().size());
    assertEquals(1, class3.getSuperclassList().size());
  }

  @Test
  void testWithTakenSuperclassName() throws IOException {
    String modelUri = getPath().resolve("WithTakenSuperClassName.cd").toUri().toString();

    Range fieldNameRange = new Range(new Position(2, 4), new Position(2, 16));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), fieldNameRange, new CodeActionContext()))
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
    assertEquals(3, classes.size());

    ASTCDClass class1 = classes.get(0);
    assertEquals("Class", class1.getName());
    assertEquals(0, class1.getCDAttributeList().size());
    assertEquals(1, class1.getSuperclassList().size());
    ASTCDClass class2 = classes.get(1);
    assertEquals("SuperClass", class2.getName());
    assertEquals(1, class2.getCDAttributeList().size());
    ASTCDClass class3 = classes.get(2);
    assertEquals("SuperClass1", class3.getName());
    assertEquals(1, class3.getCDAttributeList().size());
    assertEquals(1, class3.getSuperclassList().size());
  }
}
