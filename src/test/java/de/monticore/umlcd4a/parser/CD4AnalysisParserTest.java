/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;

public class CD4AnalysisParserTest {
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/SocNet.cd");
    CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample1() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example1.cd");
    CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample2() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example2.cd");
    CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
}
