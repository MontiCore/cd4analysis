/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.resolving;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDBasisResolvingTest extends TestBasis {
  protected static ICDBasisGlobalScope globalScope;
  protected static ICDBasisArtifactScope artifactScope;

  @BeforeClass
  public static void parseCompleteModel() throws IOException {
    final TestCDBasisParser p = TestCDBasisMill.parser();
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cdbasis/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit compilationUnit = astcdCompilationUnit.get();

    // Rund default trafos
    TestCDBasisTraverser traverser = TestCDBasisMill.traverser();
    traverser.add4CDBasis(new CDBasisCombinePackagesTrafo());
    compilationUnit.accept(traverser);

    TestCDBasisMill.reset();
    TestCDBasisMill.init();

    globalScope = CDBasisMill.globalScope();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    artifactScope = TestCDBasisMill.scopesGenitorDelegator().createFromAST(compilationUnit);
    checkLogError();
  }

  @Test
  public void resolveCDType() {
    final Optional<CDTypeSymbol> a = globalScope.resolveCDType("cdbasis.parser.Packages.A");
    assertFalse("CDType A could be resolved but shouldn't.", a.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a1 = globalScope.resolveCDType("A");
    assertFalse(
        "CDType cdbasis.parser.Packages.A could be resolved but shouldn't.", a1.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a2 = globalScope.resolveCDType("cdbasis.parser.Packages.a.A");
    assertTrue(
        "CDType cdbasis.parser.Packages.a.A could not be resolved:\n" + getJoinedErrors(),
        a2.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a3 = globalScope.resolveCDType("cdbasis.parser.Packages.a.b.c.C");
    assertTrue(
        "CDType cdbasis.parser.Packages.a.b.c.C could not be resolved:\n" + getJoinedErrors(),
        a3.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a4 = artifactScope.resolveCDType("A");
    assertFalse("CDType A could be resolved but shouldn't.", a4.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a5 = artifactScope.resolveCDTypeDown("a.A");
    assertTrue("CDType a.A could not be resolved:\n" + getJoinedErrors(), a5.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a6 = artifactScope.resolveCDType("a.A");
    assertTrue(a6.isPresent());
    checkLogError();

    final ICDBasisScope enclosingScopeA = a2.get().getEnclosingScope();

    final Optional<CDTypeSymbol> b0 = enclosingScopeA.resolveCDType("B");
    assertTrue(b0.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> c0 = enclosingScopeA.resolveCDType("a.b.c.C");
    assertTrue(c0.isPresent());
    checkLogError();
  }

  @Test
  public void resolveOOType() {
    final Optional<OOTypeSymbol> a = globalScope.resolveOOType("A");
    assertFalse("OOTypeSymbol A could be resolved but shouldn't.", a.isPresent());
    checkLogError();

    final Optional<OOTypeSymbol> a2 = globalScope.resolveOOType("cdbasis.parser.Packages.a.A");
    assertTrue("OOTypeSymbol A could not be resolved:\n" + getJoinedErrors(), a2.isPresent());
    checkLogError();
  }

  @Test
  public void resolveField() {
    final Optional<FieldSymbol> b = globalScope.resolveField("B.a1");
    assertFalse("Field B.a1 could be resolved but shouldn't.", b.isPresent());
    checkLogError();

    final Optional<FieldSymbol> b2 = globalScope.resolveField("cdbasis.parser.Packages.a.B.a1");
    assertTrue("Field a.B.a1 could not be resolved:\n" + getJoinedErrors(), b2.isPresent());
    checkLogError();

    final Optional<FieldSymbol> b3 = artifactScope.resolveField("a.B.a1");
    assertTrue("Field a.B.a1 could not be resolved:\n" + getJoinedErrors(), b2.isPresent());
    checkLogError();
  }
}
