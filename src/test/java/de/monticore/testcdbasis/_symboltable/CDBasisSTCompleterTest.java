package de.monticore.testcdbasis._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisDeSer;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.*;

public class CDBasisSTCompleterTest {

  private static final String MODEL_PATH = "src/test/resources/";
  CD4AnalysisParser parser;
  CD4AnalysisDeSer scopeDeser;

  @Before
  public void setup() {
    // reset the GlobalScope
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    CD4AnalysisMill.globalScope().clear();
    CD4AnalysisMill.globalScope().setModelPath(new ModelPath(Paths.get(MODEL_PATH)));

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);

    this.parser = CD4AnalysisMill.parser();
    scopeDeser = new CD4AnalysisDeSer();
  }

  @Test
  public void genitorTest() {
    // reset the GlobalScope

    String artifact = MODEL_PATH + "de/monticore/cdbasis/symboltable/CorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    new CD4AnalysisAfterParseTrafo().transform(ast);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    assertEquals(1, artifactScope.getSubScopes().size());
    ICD4AnalysisScope diagramScope = artifactScope.getSubScopes().get(0);

    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = diagramScope.getCDTypeSymbols();
    assertEquals(2, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));
    assertTrue(cdTypeSymbols.containsKey("D"));

    assertEquals(1, cdTypeSymbols.get("D").size());
    CDTypeSymbol classD = cdTypeSymbols.get("D").get(0);

    List<FieldSymbol> dFields = classD.getFieldList();
    assertEquals(2, dFields.size());

    FieldSymbol cField = dFields.get(0);
    assertEquals("de.monticore.cdbasis.symboltable.D.c", cField.getFullName());
    assertEquals("C", cField.getType().getTypeInfo().getName());

    FieldSymbol someImportedTypeField = dFields.get(1);
    assertEquals("de.monticore.cdbasis.symboltable.D.x", someImportedTypeField.getFullName());
    assertEquals("SomeImportedType", someImportedTypeField.getType().getTypeInfo().getName());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    String artifact = MODEL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("SomeImportedType");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("NOTEXISTING");
    assertEquals(0, resolvedTypes2.size());

    List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("MyTypes");
    assertEquals(1, resolvedDiagram.size());

    CD4AnalysisMill.globalScope().addSubScope(artifactScope);
    assertSame(artifactScope, CD4AnalysisMill.globalScope().getSubScopes().get(0));

    List<TypeSymbol> resolvedTypesGS = CD4AnalysisMill.globalScope().resolveTypeMany("SomeImportedType");
    assertEquals(1, resolvedTypesGS.size());

    List<TypeSymbol> resolvedTypesGS2 = CD4AnalysisMill.globalScope().resolveTypeMany("NOTEXISTING");
    assertEquals(0, resolvedTypesGS2.size());

    List<DiagramSymbol> resolvedDiagramGS = CD4AnalysisMill.globalScope().resolveDiagramMany("MyTypes");
    assertEquals(1, resolvedDiagramGS.size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void serializationTest() {
    String artifact = MODEL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    new CD4AnalysisAfterParseTrafo().transform(ast);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    String serialized = scopeDeser.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolveSerializeDeserializeResolveTest() {

    String artifact = MODEL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    {
      // RESOLVE
      List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("MyTypes");
      assertEquals(1, resolvedDiagram.size());

      List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("SomeImportedType");
      assertEquals(1, resolvedTypes1.size());

      List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("NOTEXISTING");
      assertEquals(0, resolvedTypes2.size());

      assertEquals(0, Log.getErrorCount());
    }

    // SERIALIZE + DESERIALIZE
    String serialized = scopeDeser.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    artifactScope = scopeDeser.deserialize(serialized);
    assertEquals(0, Log.getErrorCount());

    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    CD4AnalysisMill.globalScope().setModelPath(new ModelPath(Paths.get(MODEL_PATH)));

    CD4AnalysisMill.globalScope().addSubScope(artifactScope);

    {
      // RESOLVE
      List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("MyTypes");
      assertEquals(1, resolvedDiagram.size());

      Set<TypeSymbol> resolvedTypes1 = new HashSet<>(artifactScope.resolveTypeMany("SomeImportedType"));
      assertFalse(resolvedTypes1.isEmpty());

      // UNCOMMENT THE FOLLOWING LINE AFTER THE DOUBLE RESOLVE BUG IS FIXED
      //      assertEquals(2, resolvedTypes1.size());

      Set<TypeSymbol> resolvedTypes2 = new HashSet<>(artifactScope.resolveTypeMany("NOTEXISTING"));
      assertEquals(0, resolvedTypes2.size());

      assertEquals(0, Log.getErrorCount());
    }
  }

  @Test
  public void symbolTableCompleterNoErrorTest() {
    String artifact = MODEL_PATH + "de/monticore/cdbasis/symboltable/CorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter c = new CDBasisSymbolTableCompleter(imports, packageDecl);
    CD4AnalysisTraverser t = CD4AnalysisMill.traverser();
    t.add4CDBasis(c);
    t.add4OOSymbols(c);

    ast.accept(t);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterTypeDoesNotExistTest() {
    String artifact = MODEL_PATH + "de/monticore/cdbasis/symboltable/IncorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter c = new CDBasisSymbolTableCompleter(imports, packageDecl);
    CD4AnalysisTraverser t = CD4AnalysisMill.traverser();
    t.add4CDBasis(c);
    t.add4OOSymbols(c);

    ast.accept(t);

    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDA80"));
  }

  @Test
  public void symbolTableCompleterTypeAmbiguousTest() {
    String artifact = MODEL_PATH + "de/monticore/cdbasis/symboltable/IncorrectTypeAmbiguous.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter c = new CDBasisSymbolTableCompleter(imports, packageDecl);
    CD4AnalysisTraverser t = CD4AnalysisMill.traverser();
    t.add4CDBasis(c);
    t.add4OOSymbols(c);

    ast.accept(t);

    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDA81"));
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

  private ICD4AnalysisArtifactScope createSymbolTableFromAST(ASTCDCompilationUnit ast) {
    return CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
  }
}
