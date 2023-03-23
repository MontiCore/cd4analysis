/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SymbolTableSerializationTest {

  @Before
  public void before() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testLowerDirectly() throws IOException {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCd =
        parser.parse("src/test/resources/de/monticore/cd4code/_symboltable/a/b/clower.cd");
    assertFalse(parser.hasErrors());
    assertTrue(optCd.isPresent());

    ASTCDCompilationUnit cd = optCd.get();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    assertEquals(0, gs.getSubScopes().size());

    ICD4CodeArtifactScope as = createSymbolTable(cd);
    assertEquals(1, gs.getSubScopes().size());
    assertEquals(as, gs.getSubScopes().get(0));

    Optional<CDTypeSymbol> ts = gs.resolveCDType("a.b.clower.d.E");
    assertTrue(ts.isPresent());
  }

  @Test
  public void testUpperDirectly() throws IOException {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCd =
        parser.parse("src/test/resources/de/monticore/cd4code/_symboltable/a/b/CUpper.cd");
    assertFalse(parser.hasErrors());
    assertTrue(optCd.isPresent());

    ASTCDCompilationUnit cd = optCd.get();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    assertEquals(0, gs.getSubScopes().size());

    ICD4CodeArtifactScope as = createSymbolTable(cd);
    assertEquals(1, gs.getSubScopes().size());
    assertEquals(as, gs.getSubScopes().get(0));

    Optional<CDTypeSymbol> ts = gs.resolveCDType("a.b.CUpper.d.E");
    assertTrue(ts.isPresent());
  }

  @Test
  public void testLowerStoreAndLoad() throws IOException {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCd =
        parser.parse("src/test/resources/de/monticore/cd4code/_symboltable/a/b/clower.cd");
    assertFalse(parser.hasErrors());
    assertTrue(optCd.isPresent());

    ASTCDCompilationUnit cd = optCd.get();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    assertEquals(0, gs.getSubScopes().size());

    ICD4CodeArtifactScope as = createSymbolTable(cd);
    assertEquals(1, gs.getSubScopes().size());
    assertEquals(as, gs.getSubScopes().get(0));

    CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
    symbols2Json.store(as, "target/serialized/a/b/" + as.getName() + ".cdsym");

    // reset mill and global scope
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();

    assertEquals(0, gs.getSubScopes().size());

    gs = CD4CodeMill.globalScope();
    gs.setSymbolPath(new MCPath("target/serialized/"));

    Optional<CDTypeSymbol> ts = gs.resolveCDType("a.b.clower.d.E");
    assertTrue(ts.isPresent());
    assertEquals(1, gs.getSubScopes().size());
  }

  @Test
  public void testUpperStoreAndLoad() throws IOException {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCd =
        parser.parse("src/test/resources/de/monticore/cd4code/_symboltable/a/b/CUpper.cd");
    assertFalse(parser.hasErrors());
    assertTrue(optCd.isPresent());

    ASTCDCompilationUnit cd = optCd.get();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    assertEquals(0, gs.getSubScopes().size());

    ICD4CodeArtifactScope as = createSymbolTable(cd);
    assertEquals(1, gs.getSubScopes().size());
    assertEquals(as, gs.getSubScopes().get(0));

    CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
    symbols2Json.store(as, "target/serialized/a/b/" + as.getName() + ".cdsym");

    // reset mill and global scope
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();

    assertEquals(0, gs.getSubScopes().size());

    gs = CD4CodeMill.globalScope();
    gs.setSymbolPath(new MCPath("target/serialized/"));

    Optional<CDTypeSymbol> ts = gs.resolveCDType("a.b.CUpper.d.E");
    assertTrue(ts.isPresent());
    assertEquals(1, gs.getSubScopes().size());
  }

  @Test
  public void testSerializedTypeRef() throws IOException {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCd =
        parser.parse("src/test/resources/de/monticore/cd4code/_symboltable/Use.cd");
    assertFalse(parser.hasErrors());
    assertTrue(optCd.isPresent());

    ASTCDCompilationUnit cd = optCd.get();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    gs.setSymbolPath(new MCPath("src/test/resources/de/monticore/cd4code/_symboltable/"));

    assertEquals(0, gs.getSubScopes().size());

    ICD4CodeArtifactScope as = createSymbolTable(cd);
    assertEquals(2, gs.getSubScopes().size());
    assertEquals(as, gs.getSubScopes().get(0));

    Optional<FieldSymbol> attrSym = as.resolveFieldDown("X.e");
    assertTrue(attrSym.isPresent());

    CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
    String serialized = symbols2Json.serialize(as);
    assertTrue(serialized.contains("\"objName\":\"a.b.c.d.E\""));
  }

  public ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast) {
    CD4CodeScopesGenitorDelegatorTOP genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    return scope;
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
  }
}
