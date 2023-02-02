/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.resolving;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDBasisResolvingTest extends TestBasis {
  protected static ICDBasisGlobalScope globalScope;

  @BeforeClass
  public static void parseCompleteModel() throws IOException {
    final TestCDBasisParser p = new TestCDBasisParser();
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cdbasis/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit compilationUnit = astcdCompilationUnit.get();

    TestCDBasisMill.reset();
    TestCDBasisMill.init();

    globalScope = CDBasisMill.globalScope();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    TestCDBasisMill.scopesGenitorDelegator().createFromAST(compilationUnit);
    checkLogError();
  }

  @Test
  public void resolveCDType() {
    final Optional<CDTypeSymbol> a = globalScope.resolveCDType("A");
    assertFalse("CDType A could be resolved but shouldn't.", a.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a2 = globalScope.resolveCDType("a.A");
    assertTrue("CDType a.A could not be resolved:\n" + getJoinedErrors(), a2.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a3 = globalScope.resolveCDType("a.b.c.C");
    assertTrue("CDType a.b.c.C could not be resolved:\n" + getJoinedErrors(), a3.isPresent());
    checkLogError();
  }

  @Test
  public void resolveOOType() {
    final Optional<OOTypeSymbol> a = globalScope.resolveOOType("A");
    assertFalse("OOTypeSymbol A could be resolved but shouldn't.", a.isPresent());
    checkLogError();

    final Optional<OOTypeSymbol> a2 = globalScope.resolveOOType("a.A");
    assertTrue("OOTypeSymbol A could not be resolved:\n" + getJoinedErrors(), a2.isPresent());
    checkLogError();
  }

  @Test
  public void resolveField() {
    final Optional<FieldSymbol> b = globalScope.resolveField("B.a1");
    assertFalse("Field B.a1 could be resolved but shouldn't.", b.isPresent());
    checkLogError();

    final Optional<FieldSymbol> b2 = globalScope.resolveField("a.B.a1");
    assertTrue("Field a.B.a1 could not be resolved:\n" + getJoinedErrors(), b2.isPresent());
    checkLogError();
  }
}
