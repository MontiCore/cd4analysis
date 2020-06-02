/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCD4Analysis extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/cd4a.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }
}
