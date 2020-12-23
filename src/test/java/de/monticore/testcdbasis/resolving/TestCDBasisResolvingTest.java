/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis.resolving;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDBasisResolvingTest extends TestBasis {
  protected static ICDBasisGlobalScope globalScope;

  @BeforeClass
  public static void parseCompleteModel() throws IOException {
    final TestCDBasisParser p = new TestCDBasisParser();
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit compilationUnit = astcdCompilationUnit.get();

    CDBasisMill.reset();
    CDBasisMill.init();

    globalScope = CDBasisMill.globalScope();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.setFileExt(CD4AnalysisGlobalScope.EXTENSION);

    CDBasisMill.cDBasisSymbolTableCreatorDelegator().createFromAST(compilationUnit);
    checkLogError();
  }

  @Test
  public void resolveCDType() {
    final Optional<CDTypeSymbol> a = globalScope.resolveCDType("A");
    assertTrue("CDType A could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a2 = globalScope.resolveCDType("a.A");
    assertTrue("CDType a.A could not be resolved:\n" + getJoinedErrors(), a2.isPresent());
    checkLogError();
    assertEquals(a, a2);

    final Optional<CDTypeSymbol> a3 = globalScope.resolveCDType("a.b.c.C");
    assertTrue("CDType a.b.c.C could not be resolved:\n" + getJoinedErrors(), a3.isPresent());
    checkLogError();
  }

  @Test
  public void resolveOOType() {
    final Optional<OOTypeSymbol> a = globalScope.resolveOOType("A");
    assertTrue("OOTypeSymbol A could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();
  }

  @Test
  public void resolveField() {
    final Optional<FieldSymbol> b = globalScope.resolveField("B.a1");
    assertTrue("Field B.a1 could not be resolved:\n" + getJoinedErrors(), b.isPresent());
    checkLogError();
  }
}
