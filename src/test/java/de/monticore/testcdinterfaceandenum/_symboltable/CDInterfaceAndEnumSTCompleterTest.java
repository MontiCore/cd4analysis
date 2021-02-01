package de.monticore.testcdinterfaceandenum._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisDeSer;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis.CDBasisMill;
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
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class CDInterfaceAndEnumSTCompleterTest {

  private static final String MODEL_PATH = "src/test/resources/";
  CD4AnalysisParser parser;
  CD4AnalysisDeSer scopeDeser;

  @Before
  public void setup() {
    // reset the GlobalScope
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    CD4CodeMill.globalScope().clear();
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

    String artifact = MODEL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    new CD4AnalysisAfterParseTrafo().transform(ast);

    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    assertEquals(1, artifactScope.getSubScopes().size());
    ICD4AnalysisScope diagramScope = artifactScope.getSubScopes().get(0);

    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = diagramScope.getCDTypeSymbols();
    assertEquals(3, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));
    assertTrue(cdTypeSymbols.containsKey("D"));
    assertTrue(cdTypeSymbols.containsKey("MyInterface"));

    assertEquals(1, cdTypeSymbols.get("D").size());
    CDTypeSymbol classD = cdTypeSymbols.get("D").get(0);

    List<FieldSymbol> dFields = classD.getFieldList();
    assertEquals(4, dFields.size());

    FieldSymbol cField = dFields.get(0);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.D.c", cField.getFullName());
    assertEquals("C", cField.getType().getTypeInfo().getName());

    FieldSymbol someImportedTypeField = dFields.get(1);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.D.x", someImportedTypeField.getFullName());
    assertEquals("SomeImportedType", someImportedTypeField.getType().getTypeInfo().getName());

    FieldSymbol iField = dFields.get(2);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.D.i", iField.getFullName());
    assertEquals("MyOtherInterface", iField.getType().getTypeInfo().getName());

    FieldSymbol eField = dFields.get(3);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.D.e", eField.getFullName());
    assertEquals("MyEnum", eField.getType().getTypeInfo().getName());

    assertEquals(1, cdTypeSymbols.get("MyInterface").size());
    CDTypeSymbol myInterfaceSymbol = cdTypeSymbols.get("MyInterface").get(0);
    assertTrue(myInterfaceSymbol.isIsInterface());
    assertEquals(1, myInterfaceSymbol.getSuperTypesList().size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    String artifact = MODEL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("C");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("D");
    assertEquals(1, resolvedTypes2.size());

    List<TypeSymbol> resolvedTypes3 = artifactScope.resolveTypeMany("E");
    assertEquals(0, resolvedTypes3.size());

    List<TypeSymbol> resolvedTypes4 = artifactScope.resolveTypeMany("MyInterface");
    assertEquals(1, resolvedTypes4.size());

    assertEquals(1, resolvedTypes4.get(0).getSuperTypesList().size());
    assertEquals( "MyOtherInterface", resolvedTypes4.get(0).getSuperTypesList().get(0).getTypeInfo().getName());

    List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("CorrectTypeUsagesEnumInterface");
    assertEquals(1, resolvedDiagram.size());

    CD4AnalysisMill.globalScope().addSubScope(artifactScope);
    assertSame(artifactScope, CD4AnalysisMill.globalScope().getSubScopes().get(0));

    List<TypeSymbol> resolvedTypesGS1 = CD4AnalysisMill.globalScope().resolveTypeMany("C");
    assertEquals(1, resolvedTypesGS1.size());

    List<TypeSymbol> resolvedTypesGS2 = CD4AnalysisMill.globalScope().resolveTypeMany("D");
    assertEquals(1, resolvedTypesGS2.size());

    List<TypeSymbol> resolvedTypesGS3 = CD4AnalysisMill.globalScope().resolveTypeMany("E");
    assertEquals(0, resolvedTypesGS3.size());

    List<TypeSymbol> resolvedTypesGS4 = CD4AnalysisMill.globalScope().resolveTypeMany("MyInterface");
    assertEquals(1, resolvedTypesGS4.size());

    List<DiagramSymbol> resolvedDiagramGS = CD4AnalysisMill.globalScope().resolveDiagramMany("CorrectTypeUsagesEnumInterface");
    assertEquals(1, resolvedDiagramGS.size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void serializationTest() {
    String artifact = MODEL_PATH + "de/monticore/cdinterfaceenum/symboltable/EnumAndInterface.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ICD4AnalysisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    String serialized = scopeDeser.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Ignore("ignored until deserialization fixed in MC6.8")
  @Test
  public void symbolTableCompleterNoErrorTest() {
    String artifact = MODEL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
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
