/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.parser;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCD4AnalysisParserTest extends CD4AnalysisTestBasis {

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

  @Test
  public void testInviDas() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/real_world_examples/InviDas.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }

    @Test
  public void testMaCoCo() throws RecognitionException, IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/real_world_examples/MaCoCo.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
  }
}
