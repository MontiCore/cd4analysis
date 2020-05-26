/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4code;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CD4CodeParserTest {

  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testLanguageTeaser() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/cd4code/CD4CodeLanguageTeaser.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(Log.getFindings().stream().map(Finding::buildMsg).collect(Collectors.joining()), parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }

}
