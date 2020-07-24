/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisDeSerTest extends CD4AnalysisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);

    final String serializedST = deSer.serialize(scope);
    final CD4AnalysisArtifactScope deserialize = deSer.deserialize(serializedST);

    final CD4AnalysisGlobalScope globalScopeForDeserialization = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    globalScopeForDeserialization.addSubScope(deserialize);

    assertTrue(deserialize.resolveField("D.DEE").isPresent());
  }

  @Test
  public void simple() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/SimpleSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);

    final String serializedST = deSer.serialize(scope);
    final CD4AnalysisArtifactScope deserialize = deSer.deserialize(serializedST);

    final CD4AnalysisGlobalScope globalScopeForDeserialization = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    globalScopeForDeserialization.addSubScope(deserialize);

    assertTrue(deserialize.resolveCDRole("B.item").isPresent());
  }

  @Test
  public void minimal() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    final String serializedST = deSer.serialize(scope);
    final CD4AnalysisArtifactScope deserialize = deSer.deserialize(serializedST);

    final CD4AnalysisGlobalScope globalScopeForDeserialization = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    globalScopeForDeserialization.addSubScope(deserialize);

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

  @Test
  public void innerClass() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    final Optional<CDTypeSymbol> c = scope.resolveCDType("A");
    final CD4AnalysisScope innerSpanningScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().setEnclosingScope((ICD4AnalysisScope) c.get().getSpannedScope()).build();
    innerSpanningScope.add(OOSymbolsMill.fieldSymbolBuilder().setName("field").setType(SymTypeExpressionFactory.createTypeObject("A", innerSpanningScope)).build());
    final CDTypeSymbol inner = CDBasisMill.cDTypeSymbolBuilder().setName("Inner").setIsClass(true).setIsStatic(true).setSpannedScope(innerSpanningScope).build();
    c.get().getSpannedScope().add(inner);
    innerSpanningScope.setSpanningSymbol(inner);

    final String serializedST = deSer.serialize(scope);
    final CD4AnalysisArtifactScope deserialize = deSer.deserialize(serializedST);

    final CD4AnalysisGlobalScope globalScopeForDeserialization = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    globalScopeForDeserialization.addSubScope(deserialize);

    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("Inner");
    assertTrue(a.isPresent());

    final Optional<CDTypeSymbol> a2 = deserialize.resolveCDType("A.Inner");
    assertTrue(a2.isPresent());

    assertEquals(a, a2);

    final Optional<FieldSymbol> fieldSymbol = a.get().getEnclosingScope().resolveField("A.Inner.field");
    assertTrue(fieldSymbol.isPresent());
  }
}
