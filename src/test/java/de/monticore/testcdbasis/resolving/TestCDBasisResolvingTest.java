/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis.resolving;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisGlobalScope;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreatorDelegator;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDBasisResolvingTest extends TestBasis {
  protected static final CDBasisGlobalScope globalScope = CDBasisMill
      .cDBasisGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(PATH)))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .build();

  @BeforeClass
  public static void parseCompleteModel() throws IOException {
    final TestCDBasisParser p = new TestCDBasisParser();
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit compilationUnit = astcdCompilationUnit.get();

    final CDBasisSymbolTableCreatorDelegator symbolTableCreator = CDBasisMill
        .cDBasisSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    symbolTableCreator.createFromAST(compilationUnit);
    checkLogError();
  }

  @Test
  public void resolveCDType() {
    final Optional<CDTypeSymbol> a = globalScope.resolveCDType("A");
    assertTrue("CDType A could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();

    final Optional<CDTypeSymbol> a2 = globalScope.resolveCDType("a.A");
    assertTrue("CDType a.A could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();
    assertEquals(a, a2);

    globalScope.resolveCDType("a.b.c.C");
    assertTrue("CDType a.b.c.C could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();
  }

  @Test
  public void resolveOOType() {
    final Optional<OOTypeSymbol> a = globalScope.resolveOOType("A");
    assertTrue("OOTypeSymbol A could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();

    final Optional<OOTypeSymbol> str = globalScope.resolveOOType("java.lang.String");
    assertTrue("OOTypeSymbol java.lang.String could not be resolved:\n" + getJoinedErrors(), a.isPresent());
    checkLogError();
  }

  @Ignore
  @Test
  public void resolveField() {
    final Optional<FieldSymbol> b = globalScope.resolveField("B.a1");
    assertTrue("Field B.a1 could not be resolved:\n" + getJoinedErrors(), b.isPresent());
    checkLogError();
  }
}
