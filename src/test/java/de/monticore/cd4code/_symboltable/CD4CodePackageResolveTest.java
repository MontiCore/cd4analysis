/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodePackageResolveTest extends CD4CodeTestBasis {
  
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    CD4CodeMill.init();
  }

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());
    checkLogError();

    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(node);
  }

  @Test
  public void resolveCDRole() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdassociation/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);
    final ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());

    new CD4CodeTrafo4Defaults().transform(node);

    final Optional<CDRoleSymbol> c2_0 = artifactScope.resolveCDRole("C1.c2");
    assertTrue(c2_0.isPresent());

    final List<CDRoleSymbol> c2_1 = artifactScope.resolveCDRoleMany("C1.one_to_two");
    assertEquals(1, c2_1.size());

    final Optional<CDRoleSymbol> c2_2 = artifactScope.resolveCDRole("C1.c2_custom");
    assertTrue(c2_2.isPresent());
  }

  @Test
  public void resolveJavaTypes() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/UseJavaTypes.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);
    final ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());

    new CD4CodeTrafo4Defaults().transform(node);

    checkLogError();

    final Optional<FieldSymbol> fieldSymbol = artifactScope.resolveField("A.l");
    assertTrue(fieldSymbol.isPresent());
    final SymTypeExpression type = fieldSymbol.get().getType();
    assertTrue(type.isGenericType());
    assertEquals("java.util.List", ((SymTypeOfGenerics)type).getFullName());
    assertEquals("java.lang.String", ((SymTypeOfGenerics)type).getArgumentList().get(0).getTypeInfo().getFullName());

    final Optional<OOTypeSymbol> str1 = artifactScope.resolveOOType("java.lang.String");
    assertTrue(str1.isPresent());

    final Optional<TypeSymbol> str2 = artifactScope.resolveType("String");
    assertTrue(str2.isPresent());

    assertEquals(str1.get(), str2.get());

    final Optional<OOTypeSymbol> opt = artifactScope.resolveOOType("java.util.Optional");
    assertTrue(opt.isPresent());

    String s = new OOSymbolsSymbols2Json().serialize(str1.get().getSpannedScope());
    String s2 = symbols2Json.serialize(artifactScope);
    System.out.println(s2);
  }
}
