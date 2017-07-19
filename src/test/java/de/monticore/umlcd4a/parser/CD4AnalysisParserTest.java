/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
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
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

public class CD4AnalysisParserTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSocNet() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/SocNet.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testAutomaton() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Automaton.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testFeatureModel() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/FeatureModel.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample1() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example1.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
  @Test
  public void testExample2() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/parser/Example2.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cdDef = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(cdDef.isPresent());
  }
  
}
