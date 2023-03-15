/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisDeSerTest extends CD4AnalysisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);

    final ICD4AnalysisArtifactScope scope =
        CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);

    {
      final CD4AnalysisTraverser traverser =
          new CD4AnalysisSymbolTableCompleter(node).getTraverser();
      node.accept(traverser);

      checkLogError();
    }

    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDTypeSymbol> d = deserialize.resolveCDType("D");
    assertTrue(d.isPresent());
    assertEquals(1, d.get().getFieldList("DEE").size());
  }

  @Test
  public void simple() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4analysis/parser/SimpleSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);

    final ICD4AnalysisArtifactScope scope =
        CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());

    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDTypeSymbol> b = deserialize.resolveCDType("B");
    assertTrue(b.isPresent());
    assertEquals(1, b.get().getCDRoleList("item").size());
  }

  @Test
  public void minimal() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);

    final ICD4AnalysisArtifactScope scope =
        CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());

    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("A");
    assertTrue(a.isPresent());
    final List<CDRoleSymbol> roles = a.get().getCDRoleList();
    assertEquals(2, roles.size());

    final Optional<CDAssociationSymbol> s = deserialize.resolveCDAssociation("atoa");
    assertTrue(s.isPresent());
    final CDRoleSymbol left = s.get().getLeft();
    assertEquals("lower", left.getName());
    assertFalse(left.isIsDefinitiveNavigable());
    final CDRoleSymbol right = s.get().getRight();
    assertEquals("upper", right.getName());
    assertTrue(right.isIsDefinitiveNavigable());
  }

  @Ignore("no inner classes are allowed")
  @Test
  public void innerClass() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);

    final ICD4AnalysisArtifactScope scope =
        CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    final Optional<CDTypeSymbol> c = scope.resolveCDType("A");
    final ICD4AnalysisScope innerSpanningScope = CD4AnalysisMill.scope();
    innerSpanningScope.setEnclosingScope((ICD4AnalysisScope) c.get().getSpannedScope());
    innerSpanningScope.add(
        OOSymbolsMill.fieldSymbolBuilder()
            .setName("field")
            .setType(SymTypeExpressionFactory.createTypeObject("A", innerSpanningScope))
            .build());
    final CDTypeSymbol inner =
        CDBasisMill.cDTypeSymbolBuilder()
            .setName("Inner")
            .setIsClass(true)
            .setIsStatic(true)
            .setSpannedScope(innerSpanningScope)
            .build();
    c.get().getSpannedScope().add(inner);
    innerSpanningScope.setSpanningSymbol(inner);

    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDTypeSymbol> innerType =
        deserialize.resolveCDTypeMany("Inner").stream().distinct().findAny();
    assertTrue(innerType.isPresent());

    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("A");
    assertTrue(a.isPresent());

    final Optional<CDTypeSymbol> aInnerType =
        a.get().getSpannedScope().resolveCDTypeLocally("Inner");
    assertTrue(aInnerType.isPresent());
    assertEquals(innerType, aInnerType);

    final Optional<FieldSymbol> fieldSymbol =
        aInnerType.get().getSpannedScope().resolveFieldLocally("field");
    assertTrue(fieldSymbol.isPresent());
  }

  private List<CDTypeSymbol> uniqueList(List<CDTypeSymbol> inner) {
    return inner.stream().distinct().collect(Collectors.toList());
  }

  public ICD4AnalysisArtifactScope getGlobalScopeForDeserialization(String serializedST) {
    final ICD4AnalysisArtifactScope deserialize = symbols2Json.deserialize(serializedST);

    // explicitly not using the mill for initializing a global scope
    final CD4AnalysisGlobalScope globalScopeForDeserialization = new CD4AnalysisGlobalScope();
    globalScopeForDeserialization.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScopeForDeserialization);
    globalScopeForDeserialization.addSubScope(deserialize);
    return deserialize;
  }
}
