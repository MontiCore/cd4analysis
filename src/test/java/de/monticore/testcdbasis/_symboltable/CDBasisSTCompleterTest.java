/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis._symboltable;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class CDBasisSTCompleterTest {

  private static final String SYMBOL_PATH = "src/test/resources/";
  TestCDBasisParser parser;
  TestCDBasisSymbols2Json symbols2Json;

  @Before
  public void setup() {
    // reset the GlobalScope
    TestCDBasisMill.reset();
    TestCDBasisMill.init();
    TestCDBasisMill.globalScope().clear();
    TestCDBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);

    this.parser = TestCDBasisMill.parser();
    symbols2Json = new TestCDBasisSymbols2Json();
  }

  @Test
  public void genitorTest() {
    // reset the GlobalScope

    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symboltable/CorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisDefaultPackageTrafo trafo = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo);
    ast.accept(t);

    // create symbol table
    ITestCDBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    // complete symbol table
    TestCDBasisTraverser t2 = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabComp);
    t2.add4OOSymbols(symTabComp);
    ast.accept(t2);

    assertEquals(2, artifactScope.getSubScopes().size());
    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = artifactScope.getCDTypeSymbols();
    assertEquals(2, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));
    assertTrue(cdTypeSymbols.containsKey("D"));

    assertEquals(1, cdTypeSymbols.get("D").size());
    CDTypeSymbol classD = cdTypeSymbols.get("D").get(0);

    List<FieldSymbol> dFields = classD.getFieldList();
    assertEquals(2, dFields.size());

    FieldSymbol cField = dFields.get(0);
    assertEquals("D.c", cField.getFullName());
    assertEquals("C", cField.getType().getTypeInfo().getName());

    FieldSymbol someImportedTypeField = dFields.get(1);
    assertEquals("D.x", someImportedTypeField.getFullName());
    assertEquals("SomeImportedType", someImportedTypeField.getType().getTypeInfo().getName());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCDBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("SomeImportedType");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("NOTEXISTING");
    assertEquals(0, resolvedTypes2.size());

    List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("MyTypes");
    assertEquals(1, resolvedDiagram.size());

    TestCDBasisMill.globalScope().addSubScope(artifactScope);
    assertSame(artifactScope, TestCDBasisMill.globalScope().getSubScopes().get(0));

    List<TypeSymbol> resolvedTypesGS =
        TestCDBasisMill.globalScope().resolveTypeMany("SomeImportedType");
    assertEquals(1, resolvedTypesGS.size());

    List<TypeSymbol> resolvedTypesGS2 =
        TestCDBasisMill.globalScope().resolveTypeMany("NOTEXISTING");
    assertEquals(0, resolvedTypesGS2.size());

    List<DiagramSymbol> resolvedDiagramGS =
        TestCDBasisMill.globalScope().resolveDiagramMany("MyTypes");
    assertEquals(1, resolvedDiagramGS.size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void serializationTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisDefaultPackageTrafo trafo = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo);
    ast.accept(t);

    ITestCDBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolveSerializeDeserializeResolveTest() {

    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symtabs/MyTypes.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCDBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);

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
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    artifactScope = symbols2Json.deserialize(serialized);
    assertEquals(0, Log.getErrorCount());

    TestCDBasisMill.reset();
    TestCDBasisMill.init();
    TestCDBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));

    TestCDBasisMill.globalScope().addSubScope(artifactScope);

    {
      // RESOLVE
      List<DiagramSymbol> resolvedDiagram = artifactScope.resolveDiagramMany("MyTypes");
      assertEquals(1, resolvedDiagram.size());

      Set<TypeSymbol> resolvedTypes1 =
          new HashSet<>(artifactScope.resolveTypeMany("SomeImportedType"));
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
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symboltable/CorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    // complete symbol table
    TestCDBasisTraverser t2 = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabComp);
    t2.add4OOSymbols(symTabComp);
    ast.accept(t2);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterTypeDoesNotExistTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symboltable/IncorrectTypeUsages.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    createSymbolTableFromAST(ast);

    ASTMCQualifiedName packageDecl = ast.getMCPackageDeclaration().getMCQualifiedName();
    List<ASTMCImportStatement> imports = ast.getMCImportStatementList();

    CDBasisSymbolTableCompleter c = new CDBasisSymbolTableCompleter();
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    t.add4CDBasis(c);
    t.add4OOSymbols(c);

    ast.accept(t);

    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0324"));
  }

  @Test
  public void modifiersTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symboltable/Modifiers.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);

    ASTCDDefinition cd = ast.getCDDefinition();

    assertEquals(2, cd.getCDElementList().size());
    ASTCDClass c1 = (ASTCDClass) cd.getCDElement(0);
    ASTCDClass c2 = (ASTCDClass) cd.getCDElement(1);
    assertEquals(1, c2.getCDAttributeList().size());
    ASTCDAttribute a = c2.getCDAttributeList().get(0);

    // check ast modifiers
    assertTrue(c1.getModifier().isPrivate());
    assertFalse(c1.getModifier().isProtected());
    assertFalse(c1.getModifier().isPublic());

    assertFalse(c2.getModifier().isPrivate());
    assertFalse(c2.getModifier().isProtected());
    assertTrue(c2.getModifier().isPublic());

    assertFalse(a.getModifier().isPrivate());
    assertTrue(a.getModifier().isProtected());
    assertFalse(a.getModifier().isPublic());

    // create symbol table
    createSymbolTableFromAST(ast);

    // check symbol table modifiers before completion
    assertFalse(c1.getSymbol().isIsPrivate());
    assertFalse(c1.getSymbol().isIsProtected());
    assertFalse(c1.getSymbol().isIsPublic());

    assertFalse(c2.getSymbol().isIsPrivate());
    assertFalse(c2.getSymbol().isIsProtected());
    assertFalse(c2.getSymbol().isIsPublic());

    assertFalse(a.getSymbol().isIsPrivate());
    assertFalse(a.getSymbol().isIsProtected());
    assertFalse(a.getSymbol().isIsPublic());

    // complete symbol table
    TestCDBasisTraverser t2 = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabComp);
    t2.add4OOSymbols(symTabComp);
    ast.accept(t2);

    // check symbol table modifiers after completion

    assertTrue(c1.getSymbol().isIsPrivate());
    assertFalse(c1.getSymbol().isIsProtected());
    assertFalse(c1.getSymbol().isIsPublic());

    assertFalse(c2.getSymbol().isIsPrivate());
    assertFalse(c2.getSymbol().isIsProtected());
    assertTrue(c2.getSymbol().isIsPublic());

    assertFalse(a.getSymbol().isIsPrivate());
    assertTrue(a.getSymbol().isIsProtected());
    assertFalse(a.getSymbol().isIsPublic());

    assertEquals(0, Log.getErrorCount());
  }

  private ASTCDCompilationUnit loadModel(String pathToArtifact) {
    try {
      return parser
          .parse(Paths.get(pathToArtifact).toString())
          .orElseThrow(NoSuchElementException::new);
    } catch (IOException | NoSuchElementException e) {
      System.err.println("Loading artifact: " + pathToArtifact + " failed: " + e.getMessage());
      fail();
    }
    throw new IllegalStateException("Something went wrong..");
  }

  protected ITestCDBasisArtifactScope createSymbolTableFromAST(ASTCDCompilationUnit ast) {
    ITestCDBasisArtifactScope as = TestCDBasisMill.scopesGenitorDelegator().createFromAST(ast);

    // add imports
    List<ImportStatement> imports = Lists.newArrayList();
    ast.getMCImportStatementList()
        .forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    as.setImportsList(imports);

    return as;
  }
}
