/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.cd4analysis.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import mc.helper.IndentPrinter;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import cd4analysis.prettyprint.CDConcretePrettyPrinter;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._parser.CDCompilationUnitMCParser;

public class CD4AnalysisPrettyPrinterTest {
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    // Parsing input
    Path model = Paths.get("src/test/resources/de/cd4analysis/prettyprint/Example1.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
    
    // prettyprinting input
    IndentPrinter i = new IndentPrinter();
    CDConcretePrettyPrinter prettyprinter = new CDConcretePrettyPrinter();
    prettyprinter.prettyPrint(cdDef.get(), i);
    
    // parsing output of prettyprinter
    Optional<ASTCDCompilationUnit> printedCdDef = parser.parse(new StringReader(i.getContent()));
    assertFalse(parser.hasErrors());
    assertTrue(printedCdDef.isPresent());
    
    // TODO (MB,GV) Does not work
    // assertTrue(cdDef.get().deepEquals(printedCdDef.get()));

  }
  
}
