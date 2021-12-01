/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testtypeimporter;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.testtypeimporter._ast.ASTCompilationUnit;
import de.monticore.testtypeimporter._parser.TestTypeImporterParser;
import de.monticore.testtypeimporter._symboltable.ITestTypeImporterArtifactScope;
import de.monticore.testtypeimporter._symboltable.ITestTypeImporterGlobalScope;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestTypeImporterTest extends TestBasis {
  @Test
  public void createST() throws IOException {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    TestTypeImporterMill.reset();
    TestTypeImporterMill.init();
    final ITestTypeImporterGlobalScope globalScope = TestTypeImporterMill.globalScope();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    BuiltInTypes.addBuiltInTypes(globalScope);

    final ICD4CodeGlobalScope cdGlobalScope = CD4CodeMill.globalScope();
    cdGlobalScope.clear();
    cdGlobalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    final CD4CodeResolver c = new CD4CodeResolver(cdGlobalScope);
    globalScope.addAdaptedOOTypeSymbolResolver(c);
    globalScope.addAdaptedTypeSymbolResolver(c);
    globalScope.addAdaptedFieldSymbolResolver(c);
    globalScope.addAdaptedVariableSymbolResolver(c);
    globalScope.addAdaptedMethodSymbolResolver(c);
    globalScope.addAdaptedFunctionSymbolResolver(c);

    final TestTypeImporterParser parser = new TestTypeImporterParser();
    final Optional<ASTCompilationUnit> cu = parser.parse(getFilePath("testtypeimporter/Simple.def"));
    assertTrue(cu.isPresent());

    final ASTCompilationUnit compilationUnit = cu.get();
    final ITestTypeImporterArtifactScope symbolTable = TestTypeImporterMill.scopesGenitorDelegator().createFromAST(compilationUnit);

    final Optional<OOTypeSymbol> stringOOType = symbolTable.resolveOOType("java.lang.String");
    assertTrue(stringOOType.isPresent());
    assertEquals("java.lang.String", stringOOType.get().getFullName());

    final Optional<TypeSymbol> stringType = symbolTable.resolveType("java.lang.String");
    assertTrue(stringType.isPresent());
    assertEquals("java.lang.String", stringType.get().getFullName());

    assertEquals(stringOOType.get(), stringType.get());

    final Optional<FieldSymbol> a = symbolTable.resolveField("a");
    assertTrue(a.isPresent());
    assertEquals("java.lang.String", a.get().getType().getTypeInfo().getFullName());

    compilationUnit.getDefinition().streamElements().forEach(e -> assertNotNull(e.getSymbol().getType().getTypeInfo()));
  }
}
