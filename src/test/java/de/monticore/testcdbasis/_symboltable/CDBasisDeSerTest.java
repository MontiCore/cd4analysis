/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis._symboltable;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.junit.Before;
import org.junit.Test;

public class CDBasisDeSerTest {

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
  public void serializationTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdbasis/symtabs/SerializationCD.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisDefaultPackageTrafo trafo = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo);
    ast.accept(t);

    // create symbol table
    ITestCDBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    artifactScope.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());

    // complete symbol table
    TestCDBasisTraverser t2 = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabComp);
    t2.add4OOSymbols(symTabComp);
    ast.accept(t2);

    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);

    // check for contents
    assertTrue(serialized.contains("\"name\":\"A\""));
    assertTrue(serialized.contains("\"name\":\"B\""));
    assertTrue(serialized.contains("\"name\":\"c\""));
    assertTrue(serialized.contains("\"name\":\"D\""));
    assertTrue(serialized.contains("\"name\":\"E\""));
    assertTrue(serialized.contains("\"name\":\"f.g.h\""));
    assertTrue(serialized.contains("\"name\":\"J\""));

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void deserializationTest() {
    ITestCDBasisGlobalScope gs = TestCDBasisMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    assertTrue(gs.getSubScopes().isEmpty());
    gs.loadFileForModelName("de.monticore.cdbasis.symtabs.SerializationCD");
    assertEquals(1, gs.getSubScopes().size());

    // resolve for class A
    Optional<CDTypeSymbol> a = gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.A");
    assertTrue(a.isPresent());

    // resolve for class B
    Optional<CDTypeSymbol> b = gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.B");
    assertTrue(b.isPresent());
    Optional<FieldSymbol> a_field =
        gs.resolveField("de.monticore.cdbasis.symtabs.SerializationCD.B.a");
    assertTrue(a_field.isPresent());

    // resolve for class D
    Optional<CDTypeSymbol> d = gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.c.D");
    assertTrue(d.isPresent());

    // resolve for class D
    Optional<CDTypeSymbol> e = gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.c.E");
    assertTrue(e.isPresent());
    Optional<FieldSymbol> a1_field =
        gs.resolveField("de.monticore.cdbasis.symtabs.SerializationCD.c.E.a1");
    assertTrue(a1_field.isPresent());

    // resolve for class I
    Optional<CDTypeSymbol> i =
        gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.f.g.h.I");
    assertTrue(i.isPresent());

    // resolve for class J
    Optional<CDTypeSymbol> j =
        gs.resolveCDType("de.monticore.cdbasis.symtabs.SerializationCD.f.g.h.J");
    assertTrue(j.isPresent());
    Optional<FieldSymbol> a2_field =
        gs.resolveField("de.monticore.cdbasis.symtabs.SerializationCD.f.g.h.J.a2");
    assertTrue(a2_field.isPresent());
  }

  protected ASTCDCompilationUnit loadModel(String pathToArtifact) {
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
