/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.cd.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.prettyprint.CDPrettyPrinterConcreteVisitor;
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

public class CD4AnalysisPrettyPrinterTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testSocNet() throws RecognitionException, IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/prettyprint/SocNet.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    
    // prettyprinting input
    IndentPrinter i = new IndentPrinter();
    CDPrettyPrinterConcreteVisitor prettyprinter = new CDPrettyPrinterConcreteVisitor(i);
    String output = prettyprinter.prettyprint(cdDef.get());

    // parsing output of prettyprinter
    Optional<ASTCDCompilationUnit> printedCdDef = parser.parseCDCompilationUnit(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(printedCdDef.isPresent());

    assertTrue(cdDef.get().deepEquals(printedCdDef.get()));

  }

  @Test
  public void testReadOnly() throws RecognitionException, IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/ReadOnly.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());

    // prettyprinting input
    IndentPrinter i = new IndentPrinter();
    CDPrettyPrinterConcreteVisitor prettyprinter = new CDPrettyPrinterConcreteVisitor(i);
    String output = prettyprinter.prettyprint(cdDef.get());

    // parsing output of prettyprinter
    Optional<ASTCDCompilationUnit> printedCdDef = parser.parseCDCompilationUnit(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(printedCdDef.isPresent());

    assertTrue(cdDef.get().deepEquals(printedCdDef.get()));
  }
  
}
