/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodePackageResolveTest extends CD4CodeTestBasis {

  @BeforeClass
  public static void beforeClass() throws Exception {
    CD4CodeMill.reset();
    CD4CodeMill.init();
  }

  @Test
  public void completeModel() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cd4code/parser/Packages.cd");
    ICD4CodeArtifactScope artifactScope =
        CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    astcdCompilationUnit.accept(
        new CD4CodeSymbolTableCompleter(astcdCompilationUnit).getTraverser());
    checkLogError();
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();

    final Optional<CDTypeSymbol> aType1 = artifactScope.resolveCDType("A");
    assertFalse(aType1.isPresent());

    final Optional<CDTypeSymbol> aType2 = artifactScope.resolveCDType("a.A");
    assertTrue(aType2.isPresent());

    final Optional<CDTypeSymbol> aType3 = gs.resolveCDType("Packages.a.A");
    assertTrue(aType3.isPresent());

    final Optional<CDTypeSymbol> aType4 = artifactScope.resolveCDType("Packages.a.A");
    assertTrue(aType4.isPresent());

    final Optional<CDTypeSymbol> aType5 = artifactScope.resolveCDTypeDown("a.A");
    assertTrue(aType5.isPresent());

    final Optional<CDTypeSymbol> bType1 = aType3.get().getSpannedScope().resolveCDType("B");
    assertTrue(bType1.isPresent());

    final Optional<CDTypeSymbol> bType2 = aType3.get().getEnclosingScope().resolveCDType("a.B");
    assertTrue(bType2.isPresent());

    final Optional<CDTypeSymbol> cType1 = aType3.get().getSpannedScope().resolveCDType("C");
    assertFalse(cType1.isPresent());
  }

  @Test
  public void resolvingTests() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cdassociation/parser/Simple.cd");
    ICD4CodeArtifactScope artifactScope =
        CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    astcdCompilationUnit.accept(
        new CD4CodeSymbolTableCompleter(astcdCompilationUnit).getTraverser());
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();

    // Test Resolving CDTypeSymbol
    // TODO
    /*
        final Optional<CDTypeSymbol> aType1 = gs.resolveCDType("Simple.A");
        assertTrue(aType1.isPresent());

        final Optional<CDTypeSymbol> aType2 = artifactScope.resolveCDType("A");
        assertTrue(aType2.isPresent());

        final Optional<CDTypeSymbol> aType3 = gs.resolveCDType("A");
        assertFalse(aType3.isPresent());

        Optional<CDTypeSymbol> c1Type1 = artifactScope.resolveCDType("C1");
        assertTrue(c1Type1.isPresent());

        Optional<CDTypeSymbol> c1Type2 = aType2.get().getEnclosingScope().resolveCDType("C1");
        assertTrue(c1Type2.isPresent());
    */
    // Test resolving CDRole
    final Optional<CDRoleSymbol> c2_0 = artifactScope.resolveCDRole("C1.c2");
    assertTrue(c2_0.isPresent());

    final List<CDRoleSymbol> c2_1 = artifactScope.resolveCDRoleMany("C1.c2");
    assertEquals(1, c2_1.size());

    final Optional<CDRoleSymbol> c2_2 = artifactScope.resolveCDRoleDown("C1.c2_custom");
    assertTrue(c2_2.isPresent());
  }

  @Test
  public void resolveJavaTypes() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4code/parser/UseJavaTypes.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);
    final ICD4CodeArtifactScope artifactScope =
        CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
    artifactScope.addImports(new ImportStatement("java.lang", true));
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());

    checkLogError();

    final Optional<FieldSymbol> fieldSymbol = artifactScope.resolveFieldDown("A.l");
    assertTrue(fieldSymbol.isPresent());
    final SymTypeExpression type = fieldSymbol.get().getType();
    assertTrue(type.isGenericType());
    assertEquals("java.util.List", ((SymTypeOfGenerics) type).getFullName());
    assertEquals(
        "java.lang.String",
        ((SymTypeOfGenerics) type).getArgumentList().get(0).getTypeInfo().getFullName());

    final Optional<OOTypeSymbol> str1 = artifactScope.resolveOOType("java.lang.String");
    assertTrue(str1.isPresent());

    final Optional<TypeSymbol> str2 = artifactScope.resolveType("String");
    assertTrue(str2.isPresent());

    assertEquals(str1.get(), str2.get());

    final Optional<OOTypeSymbol> opt = artifactScope.resolveOOType("java.util.Optional");
    assertTrue(opt.isPresent());

    // TODO: Code ist (1) sinnlos und (2) fehlerhaft
    // String s = new OOSymbolsSymbols2Json().serialize(str1.get().getSpannedScope());
    // String s2 = symbols2Json.serialize(artifactScope);
    // System.out.println(s2);
  }
}
