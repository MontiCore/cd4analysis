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

class ExtractSuperClassCodeActionTest extends AbstractLspServerTest {

  @Override
  protected Path getPath() {
    return Paths.get("src", "test", "resources", "refactoring", "extract-superclass");
  }

  @Test
  void testSimple() throws IOException {
    String modelUri = getPath().resolve("Simple.cd").toUri().toString();

    Range range = new Range(new Position(1, 2), new Position(7, 3));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), range, new CodeActionContext()))
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

    ASTCDClass class2 = classes.get(1);
    assertEquals("Class2", class2.getName());
    assertEquals(0, class2.getCDAttributeList().size());

    ASTCDClass superClass = classes.get(2);
    assertEquals("SuperClass", superClass.getName());
    assertEquals(1, superClass.getCDAttributeList().size());
    ASTCDAttribute attribute = superClass.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());
  }

  @Test
  void testNoFields() {
    String modelUri = getPath().resolve("NoFields.cd").toUri().toString();

    Range range = new Range(new Position(1, 2), new Position(3, 15));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), range, new CodeActionContext()))
            .join();

    assertEquals(0, codeActions.size());
  }

  @Test
  void testNoEqualFields() {
    String modelUri = getPath().resolve("NoEqualFields.cd").toUri().toString();

    Range range = new Range(new Position(1, 2), new Position(7, 3));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), range, new CodeActionContext()))
            .join();

    assertEquals(0, codeActions.size());
  }

  @Test
  void testSomeNotEqualFields() throws IOException {
    String modelUri = getPath().resolve("SomeNotEqualFields.cd").toUri().toString();

    Range range = new Range(new Position(1, 2), new Position(11, 3));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), range, new CodeActionContext()))
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
    assertEquals(4, classes.size());

    ASTCDClass class1 = classes.get(0);
    assertEquals("Class1", class1.getName());
    assertEquals(1, class1.getCDAttributeList().size());
    ASTCDAttribute attribute = class1.getCDAttributeList().get(0);
    assertEquals("a", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());

    ASTCDClass class2 = classes.get(1);
    assertEquals("Class2", class2.getName());
    assertEquals(1, class2.getCDAttributeList().size());
    attribute = class2.getCDAttributeList().get(0);
    assertEquals("name", attribute.getName());
    assertEquals("String", attribute.getMCType().printType());

    ASTCDClass class3 = classes.get(2);
    assertEquals("Class3", class3.getName());
    assertEquals(0, class3.getCDAttributeList().size());

    ASTCDClass superClass = classes.get(3);
    assertEquals("SuperClass", superClass.getName());
    assertEquals(1, superClass.getCDAttributeList().size());
    attribute = superClass.getCDAttributeList().get(0);
    assertEquals("field", attribute.getName());
    assertEquals("int", attribute.getMCType().printType());
  }

  @Test
  void testExistingSuperclass() {
    String modelUri = getPath().resolve("ExistingSuperclass.cd").toUri().toString();

    Range range = new Range(new Position(1, 2), new Position(7, 3));
    List<Either<Command, CodeAction>> codeActions =
        languageServer
            .getTextDocumentService()
            .codeAction(
                new CodeActionParams(
                    new TextDocumentIdentifier(modelUri), range, new CodeActionContext()))
            .join();

    assertEquals(0, codeActions.size());
  }
}
