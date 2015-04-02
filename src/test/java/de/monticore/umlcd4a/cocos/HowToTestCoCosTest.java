/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import mc.ast.SourcePosition;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.cocos.helper.Assert;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;
import de.se_rwth.commons.logging.Log;

/**
 * Simple test showing how to test CoCos.
 *
 * @author Robert Heim
 */
public class HowToTestCoCosTest {
  
  private ASTCDCompilationUnit astRoot;
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    CoCoLog.getFindings().clear();
    
    Path model = Paths.get("src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/A.cd");
    CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
    com.google.common.base.Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertTrue(cdDef.isPresent());
    this.astRoot = cdDef.get();
  }
  
  @Test
  public void testSingleCoCos() {
    ASTCDClass clazz = astRoot.getCDDefinition().getCDClasses().get(0);
    
    // succeeding coco
    SucceedingCoCo succeedingCoCo = new SucceedingCoCo();
    succeedingCoCo.check(clazz);
    
    assertTrue(CoCoLog.getFindings().isEmpty());
    
    // failing coco with expected errors
    FailingCoCo failingCoCo = new FailingCoCo();
    failingCoCo.check(clazz);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error("0x...", "msg", new SourcePosition(1, 1))
        );
    
    Assert.assertErrors(expectedErrors, CoCoLog.getFindings());
  }
  
  @Test
  public void testWithChecker() {
    
    // checker and cocos
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    FailingCoCo failingCoCo = new FailingCoCo();
    SucceedingCoCo succeedingCoco = new SucceedingCoCo();
    
    // no errors check
    checker.addCoCo(succeedingCoco);
    checker.checkAll(astRoot);
    assertTrue(CoCoLog.getFindings().isEmpty());
    
    // check that expected errors occure
    checker.addCoCo(failingCoCo);
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error("0x...", "msg", new SourcePosition(1, 1))
        );
    checker.checkAll(astRoot);
    Assert.assertErrors(expectedErrors, CoCoLog.getFindings());
  }
  
  private static class SucceedingCoCo implements CD4AnalysisASTCDClassCoCo {
    public void check(ASTCDClass node) {
      // nothing
    }
  }
  
  private static class FailingCoCo implements CD4AnalysisASTCDClassCoCo {
    public void check(ASTCDClass node) {
      CoCoLog.error("0x...", "msg", new SourcePosition(1, 1));
    }
  }
}
