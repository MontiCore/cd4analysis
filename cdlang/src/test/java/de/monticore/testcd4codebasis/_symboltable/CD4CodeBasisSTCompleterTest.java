/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis._symboltable;

import static org.junit.Assert.*;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.monticore.testcd4codebasis._visitor.TestCD4CodeBasisTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Test;

public class CD4CodeBasisSTCompleterTest extends CD4CodeBasisTestBasis {

  private static final String SYMBOL_PATH = "src/test/resources/";

  @Test
  public void genitorTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCD4CodeBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    ast.accept(new TestCD4CodeBasisSymbolTableCompleter(ast).getTraverser());

    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = artifactScope.getCDTypeSymbols();
    assertEquals(1, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));

    CDTypeSymbol cSymbol = cdTypeSymbols.get("C").get(0);
    MethodSymbol method = cSymbol.getSpannedScope().resolveMethodLocally("foo").get();
    assertEquals("foo", method.getName());
    assertEquals("MyInteger", method.getType().getTypeInfo().getName());
    assertEquals("bar", method.getParameterList().get(0).getName());
    assertEquals("int", method.getParameterList().get(0).getType().getTypeInfo().getName());
    assertEquals("baz", method.getParameterList().get(1).getName());
    assertEquals("MyInteger", method.getParameterList().get(1).getType().getTypeInfo().getName());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCD4CodeBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("C");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("D");
    assertEquals(0, resolvedTypes2.size());

    List<MethodSymbol> cMethods = artifactScope.resolveMethodDownMany("C.foo");
    assertEquals(1, cMethods.size());
  }

  @Test
  public void serializationTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/MyIntegerType.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCD4CodeBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    TestCD4CodeBasisSymbols2Json symbols2Json = new TestCD4CodeBasisSymbols2Json();
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterNoErrorTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    TestCD4CodeBasisSymbolTableCompleter cd4codeBasisCompleter =
        new TestCD4CodeBasisSymbolTableCompleter(imports, packageDecl);

    ast.accept(cd4codeBasisCompleter.getTraverser());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterErrorsTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/IncorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);
    ast.accept(new TestCD4CodeBasisSymbolTableCompleter(ast).getTraverser());

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter cdBasisCompleter = new CDBasisSymbolTableCompleter();
    CD4CodeBasisSymbolTableCompleter cd4codeCompleter = new CD4CodeBasisSymbolTableCompleter();
    TestCD4CodeBasisTraverser t = TestCD4CodeBasisMill.inheritanceTraverser();
    t.add4CDBasis(cdBasisCompleter);
    t.add4OOSymbols(cdBasisCompleter);
    t.add4CD4CodeBasis(cd4codeCompleter);
    t.add4CDBasis(cd4codeCompleter);

    ast.accept(t);

    assertEquals(4, Log.getErrorCount());
    Log.clearFindings();
  }

  @Test
  public void createDefaultConstructorTest() {
    ASTCDCompilationUnit ast =
        loadModel(SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/DefaultConstructor.cd");
    createSymbolTableFromAST(ast);

    assertTrue(ast.getCDDefinition().getCDClassesList().get(0).getCDConstructorList().isEmpty());
    assertEquals(1, ast.getCDDefinition().getCDClassesList().get(1).getCDConstructorList().size());
    List<CDMethodSignatureSymbol> constructor =
        ((ICD4CodeBasisScope) ast.getCDDefinition().getCDClassesList().get(1).getSpannedScope())
            .getCDMethodSignatureSymbols()
            .get("B");

    CD4CodeBasisSymbolTableCompleter completer = new CD4CodeBasisSymbolTableCompleter();
    CD4CodeBasisTraverser traverser = CD4CodeBasisMill.inheritanceTraverser();
    traverser.add4CD4CodeBasis(completer);
    traverser.add4CDBasis(completer);
    ast.accept(traverser);

    assertTrue(ast.getCDDefinition().getCDClassesList().get(0).getCDConstructorList().isEmpty());
    assertEquals(
        1,
        ((ICD4CodeBasisScope) ast.getCDDefinition().getCDClassesList().get(0).getSpannedScope())
            .getCDMethodSignatureSymbols()
            .size());
    assertTrue(
        ((ICD4CodeBasisScope) ast.getCDDefinition().getCDClassesList().get(0).getSpannedScope())
            .getCDMethodSignatureSymbols()
            .get("A")
            .get(0)
            .isIsConstructor());

    assertEquals(1, ast.getCDDefinition().getCDClassesList().get(1).getCDConstructorList().size());
    assertEquals(
        constructor,
        ((ICD4CodeBasisScope) ast.getCDDefinition().getCDClassesList().get(1).getSpannedScope())
            .getCDMethodSignatureSymbols()
            .get("B"));
  }

  private ASTCDCompilationUnit loadModel(String pathToArtifact) {
    try {
      return p.parse(Paths.get(pathToArtifact).toString()).orElseThrow(NoSuchElementException::new);
    } catch (IOException | NoSuchElementException e) {
      System.err.println("Loading artifact: " + pathToArtifact + " failed: " + e.getMessage());
      fail();
    }
    throw new IllegalStateException("Something went wrong..");
  }

  private ITestCD4CodeBasisArtifactScope createSymbolTableFromAST(ASTCDCompilationUnit ast) {
    ITestCD4CodeBasisArtifactScope as =
        TestCD4CodeBasisMill.scopesGenitorDelegator().createFromAST(ast);
    return as;
  }
}
