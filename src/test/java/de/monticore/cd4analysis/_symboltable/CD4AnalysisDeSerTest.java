/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisDeSerTest extends CD4AnalysisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    System.out.println(deSer.serialize(scope));
  }

  @Test
  public void simple() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/SimpleSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(node);
    System.out.println(deSer.serialize(scope));
  }
}
