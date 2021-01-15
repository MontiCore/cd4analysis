/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testtypeimporter;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.io.paths.ModelPath;
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
    final ITestTypeImporterGlobalScope globalScope = TestTypeImporterMill.globalScope();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.setFileExt("def");

    final ICD4CodeGlobalScope cdGlobalScope = CD4CodeMill.globalScope();
    cdGlobalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    cdGlobalScope.setFileExt(CD4AnalysisGlobalScope.EXTENSION);
    if (cdGlobalScope instanceof CD4CodeGlobalScope) {
      ((CD4CodeGlobalScope) cdGlobalScope).addBuiltInTypes();
    }

    final CD4CodeResolver c = CD4CodeMill.cD4CodeResolvingDelegate(cdGlobalScope);
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
    final ITestTypeImporterArtifactScope symbolTable = TestTypeImporterMill.testTypeImporterSymbolTableCreatorDelegator().createFromAST(compilationUnit);

    final Optional<OOTypeSymbol> stringOOType = symbolTable.resolveOOType("String");
    assertTrue(stringOOType.isPresent());
    assertEquals("java.lang.String", stringOOType.get().getFullName());

    final Optional<TypeSymbol> stringType = symbolTable.resolveType("String");
    assertTrue(stringType.isPresent());
    assertEquals("java.lang.String", stringType.get().getFullName());

    final Optional<FieldSymbol> a = symbolTable.resolveField("a");
    assertTrue(a.isPresent());
    assertEquals("String", a.get().getType().getTypeInfo().getName());

    compilationUnit.getDefinition().streamElements().forEach(e -> assertNotNull(e.getSymbol().getType().getTypeInfo()));
  }
}
