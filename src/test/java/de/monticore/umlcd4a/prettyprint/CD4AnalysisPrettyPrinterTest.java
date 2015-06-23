/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a.cd4analysis._parser.CDCompilationUnitMCParser;

public class CD4AnalysisPrettyPrinterTest {
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/prettyprint/Example1.cd");
    CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    
    // prettyprinting input
    IndentPrinter i = new IndentPrinter();
    CDPrettyPrinterConcreteVisitor prettyprinter = new CDPrettyPrinterConcreteVisitor(i);
    String output = prettyprinter.prettyprint(cdDef.get());
    
    // parsing output of prettyprinter
    Optional<ASTCDCompilationUnit> printedCdDef = parser.parse(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(printedCdDef.isPresent());
    
    // TODO (MB,GV) Does not work
    // assertTrue(cdDef.get().deepEquals(printedCdDef.get()));

  }
  
}
