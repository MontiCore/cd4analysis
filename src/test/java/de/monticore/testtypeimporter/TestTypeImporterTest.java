/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testtypeimporter;

import com.google.common.collect.Lists;
import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.testtypeimporter._ast.ASTCompilationUnit;
import de.monticore.testtypeimporter._parser.TestTypeImporterParser;
import de.monticore.testtypeimporter._symboltable.TestTypeImporterArtifactScope;
import de.monticore.testtypeimporter._symboltable.TestTypeImporterGlobalScope;
import de.monticore.testtypeimporter._symboltable.TestTypeImporterSymbolTableCreatorDelegator;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestTypeImporterTest extends TestBasis {
  @Test
  public void createST() throws IOException {
    final CDSymbolTableHelper symbolTableHelper = new CDSymbolTableHelper();

    final TestTypeImporterGlobalScope globalScope = TestTypeImporterMill.testTypeImporterGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension("def")
        .build();

    final CD4CodeGlobalScope cdGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .setSymbolTableHelper(symbolTableHelper)
        .addBuiltInTypes()
        .build();

    globalScope.addAdaptedOOTypeSymbolResolvingDelegate((foundSymbols, name, modifier, predicate) -> {
      List<OOTypeSymbol> result = Lists.newArrayList();
      Optional<CDTypeSymbol> typeSymbolOpt = cdGlobalScope.resolveCDType(name, modifier);
      if (typeSymbolOpt.isPresent()) {
        OOTypeSymbol res = typeSymbolOpt.get();
        result.add(res);
      }
      Optional<OOTypeSymbol> ooTypeSymbolOpt = cdGlobalScope.resolveOOType(name, modifier);
      if (ooTypeSymbolOpt.isPresent()) {
        OOTypeSymbol res = ooTypeSymbolOpt.get();
        result.add(res);
      }
      return result;
    });

    final TestTypeImporterSymbolTableCreatorDelegator symbolTableCreator = TestTypeImporterMill
        .testTypeImporterSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    final TestTypeImporterParser parser = new TestTypeImporterParser();
    final Optional<ASTCompilationUnit> cu = parser.parse(getFilePath("testtypeimporter/Simple.def"));
    assertTrue(cu.isPresent());

    final ASTCompilationUnit compilationUnit = cu.get();
    final TestTypeImporterArtifactScope symbolTable = symbolTableCreator.createFromAST(compilationUnit);

    final Optional<OOTypeSymbol> string = symbolTable.resolveOOType("java.lang.String");
    //final Optional<OOTypeSymbol> string = symbolTable.resolveOOType("String");
    assertTrue(string.isPresent());
    assertEquals("java.lang.String", string.get().getFullName());

    compilationUnit.getDefinition().streamElements().forEach(e -> assertNotNull(e.getSymbol().getType().getTypeInfo()));
  }
}
