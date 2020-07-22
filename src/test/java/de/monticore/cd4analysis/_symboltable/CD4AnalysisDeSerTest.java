/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.ModelPath;
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
}
