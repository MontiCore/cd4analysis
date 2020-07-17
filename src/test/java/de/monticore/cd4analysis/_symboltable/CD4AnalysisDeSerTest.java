/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisDeSerTest extends CD4AnalysisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    //System.out.println(deSer.serialize(scope));
    assertFalse(deSer.serialize(scope).isEmpty());
  }

  @Test
  public void simple() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/SimpleSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    //System.out.println(deSer.serialize(scope));
    assertFalse(deSer.serialize(scope).isEmpty());
  }

  @Test
  public void minimal() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);

    final String serializedST = deSer.serialize(scope);

    System.out.println(serializedST);

    final CD4AnalysisArtifactScope deserialize = deSer.deserialize(serializedST);
    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("A");
    assertTrue(a.isPresent());
    /*final CDMemberVisitor cdMemberVisitor = CDMill.cDMemberVisitor(CDMemberVisitor.Options.ROLES);
    a.get().accept(cdMemberVisitor);
    final List<ASTCDRole> roles = cdMemberVisitor.getElements();
    assertEquals(2, roles.size());*/
  }
}
