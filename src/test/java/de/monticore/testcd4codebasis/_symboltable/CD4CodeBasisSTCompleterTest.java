package de.monticore.testcd4codebasis._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeDeSer;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeOverridden;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class CD4CodeBasisSTCompleterTest {

  private static final String MODEL_PATH = "src/test/resources/";
  CD4CodeParser parser;
  CD4CodeSymbols2Json symbols2Json;

  @Before
  public void setup() {
    // reset the GlobalScope
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
    BasicSymbolsMill.initializePrimitives();

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);

    this.parser = CD4CodeMill.parser();
    symbols2Json = new CD4CodeSymbols2Json();
  }

  @Test
  public void genitorTest() {
    String artifact = MODEL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = artifactScope.getCDTypeSymbols();
    assertEquals(1, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));

    CDTypeSymbol cSymbol = cdTypeSymbols.get("C").get(0);
    MethodSymbol method = cSymbol.getSpannedScope().resolveMethodLocally("foo").get();
    assertEquals("foo", method.getName());
    assertEquals("MyInteger", method.getReturnType().getTypeInfo().getName());
    assertEquals("bar", method.getParameterList().get(0).getName());
    assertEquals("int", method.getParameterList().get(0).getType().getTypeInfo().getName());
    assertEquals("baz", method.getParameterList().get(1).getName());
    assertEquals("MyInteger", method.getParameterList().get(1).getType().getTypeInfo().getName());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    String artifact = MODEL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("C");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("D");
    assertEquals(0, resolvedTypes2.size());

    List<MethodSymbol> cMethods = artifactScope.resolveMethodMany("C.foo");
    assertEquals(1, cMethods.size());
  }

  @Test
  public void serializationTest() {
    String artifact = MODEL_PATH + "de/monticore/cd4codebasis/symboltable/MyIntegerType.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    new CD4CodeAfterParseTrafo().transform(ast);
    ICD4CodeArtifactScope artifactScope = createSymbolTableFromAST(ast);

    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterNoErrorTest() {
    String artifact = MODEL_PATH + "de/monticore/cd4codebasis/symboltable/CorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CD4CodeSymbolTableCompleter cd4codeCompleter = new CD4CodeSymbolTableCompleter(imports, packageDecl);

    ast.accept(cd4codeCompleter.getTraverser());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterErrorsTest() {
    String artifact = MODEL_PATH + "de/monticore/cd4codebasis/symboltable/IncorrectMethodUsage.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter cdBasisCompleter = new CDBasisSymbolTableCompleter(imports, packageDecl);
    CD4CodeBasisSymbolTableCompleter cd4codeCompleter = new CD4CodeBasisSymbolTableCompleter(imports, packageDecl);
    CD4CodeTraverser t = CD4CodeMill.traverser();
    t.add4CDBasis(cdBasisCompleter);
    t.add4OOSymbols(cdBasisCompleter);
    t.add4CD4CodeBasis(cd4codeCompleter);

    ast.accept(t);

    assertEquals(8, Log.getErrorCount());
  }

  private ASTCDCompilationUnit loadModel(String pathToArtifact) {
    try {
      return parser.parse(Paths.get(pathToArtifact).toString()).orElseThrow(NoSuchElementException::new);
    }
    catch (IOException | NoSuchElementException e) {
      System.err.println("Loading artifact: " + pathToArtifact + " failed: " + e.getMessage());
      fail();
    }
    throw new IllegalStateException("Something went wrong..");
  }

  private ICD4CodeArtifactScope createSymbolTableFromAST(ASTCDCompilationUnit ast) {
    return CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
  }
}
