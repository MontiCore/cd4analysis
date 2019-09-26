/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4code;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4code._parser.CD4CodeParser;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CD4CodePrettyPrinterTest {
  
  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testScope() throws RecognitionException, IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/monticore/cd4code/CD1.cd");
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    
    // prettyprinting input
    IndentPrinter i = new IndentPrinter();
    CD4CodePrettyPrinterDelegator prettyprinter = new CD4CodePrettyPrinterDelegator(i);
    String output = prettyprinter.prettyprint(cdDef.get());

    // parsing output of prettyprinter
    Optional<ASTCDCompilationUnit> printedCdDef = parser.parseCDCompilationUnit(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(printedCdDef.isPresent());

    assertTrue(cdDef.get().deepEquals(printedCdDef.get()));

  }
  
}
