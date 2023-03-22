/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum._symboltable;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcdinterfaceandenum.TestCDInterfaceAndEnumMill;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import de.monticore.testcdinterfaceandenum._visitor.TestCDInterfaceAndEnumTraverser;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class CDInterfaceAndEnumDeSerTest {

  private static final String SYMBOL_PATH = "src/test/resources/";
  TestCDInterfaceAndEnumParser parser;
  TestCDInterfaceAndEnumSymbols2Json symbols2Json;

  @Before
  public void setup() {
    TestCDInterfaceAndEnumMill.reset();
    TestCDInterfaceAndEnumMill.init();
    TestCDInterfaceAndEnumMill.globalScope().clear();
    TestCDInterfaceAndEnumMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);

    parser = TestCDInterfaceAndEnumMill.parser();
    symbols2Json = new TestCDInterfaceAndEnumSymbols2Json();
  }

  @Test
  public void serializationTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/SerializationCD.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);

    // after parse trafo
    TestCDInterfaceAndEnumTraverser t = TestCDInterfaceAndEnumMill.traverser();
    CDBasisCombinePackagesTrafo trafo = new CDBasisCombinePackagesTrafo();
    t.add4CDBasis(trafo);
    ast.accept(t);

    // create symbol table
    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymbolTableFromAST(ast);

    // complete symbol table
    TestCDInterfaceAndEnumTraverser t2 = TestCDInterfaceAndEnumMill.traverser();
    CDBasisSymbolTableCompleter symTabCompCdBasis = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabCompCdBasis);
    t2.add4OOSymbols(symTabCompCdBasis);
    CDInterfaceAndEnumSymbolTableCompleter symTabCompCdIAE =
        new CDInterfaceAndEnumSymbolTableCompleter();
    t2.add4CDInterfaceAndEnum(symTabCompCdIAE);
    ast.accept(t2);

    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);

    // check for contents
    assertTrue(serialized.contains("\"name\":\"A\""));
    assertTrue(serialized.contains("\"name\":\"B\""));
    assertTrue(serialized.contains("\"name\":\"FOO\""));
    assertTrue(serialized.contains("\"name\":\"BAR\""));
    assertTrue(serialized.contains("\"name\":\"D\""));
    assertTrue(serialized.contains("\"name\":\"E\""));
    assertTrue(serialized.contains("\"name\":\"FOO2\""));
    assertTrue(serialized.contains("\"name\":\"BAR2\""));
    assertTrue(serialized.contains("\"name\":\"F\""));
    assertTrue(serialized.contains("\"name\":\"g\""));

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void deserializationTest() {
    ITestCDInterfaceAndEnumGlobalScope gs = TestCDInterfaceAndEnumMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    assertTrue(gs.getSubScopes().isEmpty());
    gs.loadFileForModelName("de.monticore.cdinterfaceenum.symboltable.SerializationCD");
    assertEquals(1, gs.getSubScopes().size());

    // resolve for interface A
    Optional<CDTypeSymbol> a =
        gs.resolveCDType("de.monticore.cdinterfaceenum.symboltable.SerializationCD.A");
    assertTrue(a.isPresent());

    // resolve for enum B
    Optional<CDTypeSymbol> b =
        gs.resolveCDType("de.monticore.cdinterfaceenum.symboltable.SerializationCD.B");
    assertTrue(b.isPresent());
    Optional<FieldSymbol> foo =
        gs.resolveField("de.monticore.cdinterfaceenum.symboltable.SerializationCD.B.FOO");
    assertTrue(foo.isPresent());
    Optional<FieldSymbol> bar =
        gs.resolveField("de.monticore.cdinterfaceenum.symboltable.SerializationCD.B.BAR");
    assertTrue(bar.isPresent());

    // resolve for interface D
    Optional<CDTypeSymbol> d =
        gs.resolveCDType("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.D");
    assertTrue(d.isPresent());

    // resolve for enum E
    Optional<CDTypeSymbol> e =
        gs.resolveCDType("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.E");
    assertTrue(e.isPresent());
    Optional<FieldSymbol> foo2 =
        gs.resolveField("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.E.FOO2");
    assertTrue(foo2.isPresent());
    Optional<FieldSymbol> bar2 =
        gs.resolveField("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.E.BAR2");
    assertTrue(bar2.isPresent());

    // resolve for class F
    Optional<CDTypeSymbol> f =
        gs.resolveCDType("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.F");
    assertTrue(f.isPresent());
    Optional<FieldSymbol> g =
        gs.resolveField("de.monticore.cdinterfaceenum.symboltable.SerializationCD.c.F.g");
    assertTrue(g.isPresent());
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

  protected ITestCDInterfaceAndEnumArtifactScope createSymbolTableFromAST(
      ASTCDCompilationUnit ast) {
    ITestCDInterfaceAndEnumArtifactScope as =
        TestCDInterfaceAndEnumMill.scopesGenitorDelegator().createFromAST(ast);

    // add imports
    List<ImportStatement> imports = Lists.newArrayList();
    ast.getMCImportStatementList()
        .forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    as.setImportsList(imports);

    return as;
  }
}
