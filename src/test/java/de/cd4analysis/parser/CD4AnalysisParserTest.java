/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.cd4analysis.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._parser.CDCompilationUnitMCParser;

public class CD4AnalysisParserTest {
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/cd4analysis/parser/SocNet.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample1() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/cd4analysis/parser/Example1.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }

  @Test
  public void testExample2() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/cd4analysis/parser/Example2.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
}
