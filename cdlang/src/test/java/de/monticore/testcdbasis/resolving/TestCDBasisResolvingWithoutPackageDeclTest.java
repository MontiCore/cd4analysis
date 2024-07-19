/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.resolving;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
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
public class TestCDBasisResolvingWithoutPackageDeclTest extends TestBasis {
  protected static ICDBasisGlobalScope globalScope;
  protected static ICDBasisArtifactScope artifactScope;

  @BeforeClass
  public static void parseCompleteModel() throws IOException {
    final TestCDBasisParser p = TestCDBasisMill.parser();
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cdbasis/parser/PackagesNoDecl.cd"));
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
    BuiltInTypes.addBuiltInTypes(globalScope);

    artifactScope = TestCDBasisMill.scopesGenitorDelegator().createFromAST(compilationUnit);

    // complete symbol table
    TestCDBasisTraverser t2 = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp = new CDBasisSymbolTableCompleter();
    t2.add4CDBasis(symTabComp);
    t2.add4OOSymbols(symTabComp);
    compilationUnit.accept(t2);

    checkLogError();
  }

  @Test
  public void resolveCDType() {
    final Optional<CDTypeSymbol> a = globalScope.resolveCDType("Packages.A");
    assertFalse("CDType A could be resolved but shouldn't.", a.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a1 = globalScope.resolveCDType("A");
    assertFalse(
        "CDType cdbasis.parser.Packages.A could be resolved but shouldn't.", a1.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a2 = globalScope.resolveCDType("Packages.a.A");
    assertTrue(
        "CDType cdbasis.parser.Packages.a.A could not be resolved:\n" + getJoinedErrors(),
        a2.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a3 = globalScope.resolveCDType("Packages.a.b.c.C");
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
    assertTrue("CDType a.A could not be resolved:\n" + getJoinedErrors(), a6.isPresent());
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

    final Optional<OOTypeSymbol> a2 = globalScope.resolveOOType("Packages.a.A");
    assertTrue("OOTypeSymbol A could not be resolved:\n" + getJoinedErrors(), a2.isPresent());
    checkLogError();
  }

  @Test
  public void resolveField() {
    final Optional<FieldSymbol> a1 = globalScope.resolveField("B.a1");
    assertFalse("Field B.a1 could be resolved but shouldn't.", a1.isPresent());
    checkLogError();

    final Optional<FieldSymbol> a1_2 = globalScope.resolveField("Packages.a.B.a1");
    assertTrue("Field a.B.a1 could not be resolved:\n" + getJoinedErrors(), a1_2.isPresent());
    checkLogError();

    final Optional<FieldSymbol> a1_3 = artifactScope.resolveField("a.B.a1");
    assertTrue("Field a.B.a1 could not be resolved:\n" + getJoinedErrors(), a1_3.isPresent());
    checkLogError();

    final Optional<FieldSymbol> a1_4 = artifactScope.resolveFieldDown("d.D.a1");
    assertTrue("Field d.D.a1 could not be resolved:\n" + getJoinedErrors(), a1_4.isPresent());
    checkLogError();

    final Optional<OOTypeSymbol> b = globalScope.resolveOOType("Packages.a.B");
    assertTrue("OOTypeSymbol a.B could not be resolved:\n" + getJoinedErrors(), b.isPresent());
    checkLogError();

    final Optional<FieldSymbol> a1_5 = b.get().getSpannedScope().resolveField("a1");
    assertTrue("Field a1 could not be resolved:\n" + getJoinedErrors(), a1_5.isPresent());
    checkLogError();

    final Optional<VariableSymbol> a1_6 = b.get().getSpannedScope().resolveVariable("a1");
    assertTrue("Field a1 could not be resolved:\n" + getJoinedErrors(), a1_6.isPresent());
    checkLogError();

    final Optional<OOTypeSymbol> d = globalScope.resolveOOType("Packages.d.D");
    assertTrue("OOTypeSymbol d.D could not be resolved:\n" + getJoinedErrors(), d.isPresent());
    checkLogError();

    final Optional<FieldSymbol> a1_7 = d.get().getSpannedScope().resolveField("a1");
    assertTrue("Field a1 could not be resolved:\n" + getJoinedErrors(), a1_7.isPresent());
    checkLogError();

    final Optional<VariableSymbol> a1_8 = d.get().getSpannedScope().resolveVariable("a1");
    assertTrue("Field a1 could not be resolved:\n" + getJoinedErrors(), a1_8.isPresent());
    checkLogError();
  }
}
