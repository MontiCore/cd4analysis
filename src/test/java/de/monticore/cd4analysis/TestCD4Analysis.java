/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCD4Analysis extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();

  @Test
  public void parseCompleteModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }

  @Test
  public void testLanguageTeaser() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/MyLife.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }
}
