/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import static de.monticore.cocos.helper.Assert.assertExpectedCoCoErrors;
import static de.monticore.cocos.helper.Assert.assertNoCoCoErrors;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import mc.ast.SourcePosition;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.cocos.AbstractContextCondition;
import de.monticore.cocos.CoCoError;
import de.monticore.cocos.CoCoResultList;
import de.monticore.cocos.ContextConditionResult;
import de.monticore.cocos.helper.ExpectedCoCoError;

/**
 * Simple test showing how to test CoCos.
 *
 * @author Robert Heim
 */
public class HowToTestCoCosTest {
  
  private static String LOGNAME = HowToTestCoCosTest.class.getSimpleName();
  
  private ASTCDCompilationUnit astRoot;
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/de/cd4analysis/cocos/valid/A.cd");
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    com.google.common.base.Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
    assertTrue(cdDef.isPresent());
    this.astRoot = cdDef.get();
  }
  
  @Test
  public void testSingleCoCos() {
    ASTCDClass clazz = astRoot.getCDDefinition().getCDClasses().get(0);
    
    // succeeding coco
    SucceedingCoCo succeedingCoCo = new SucceedingCoCo();
    ContextConditionResult succeedingResult = succeedingCoCo.check(clazz);
    
    assertTrue(succeedingResult.isSucceeded());
    assertNoCoCoErrors(succeedingResult);
    
    // failing coco with expected errors
    FailingCoCo failingCoCo = new FailingCoCo();
    ContextConditionResult failingResult = failingCoCo.check(clazz);
    
    Collection<ExpectedCoCoError> expectedErrors = Arrays.asList(new ExpectedCoCoError.Builder(
        "0x...").msg("msg").build());
    
    assertFalse(failingResult.isSucceeded());
    assertExpectedCoCoErrors(failingResult.getErrors(), expectedErrors, Optional.empty(),
        LOGNAME);
  }
  
  @Test
  public void testWithChecker() {
    
    // checker and cocos
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    FailingCoCo failingCoCo = new FailingCoCo();
    SucceedingCoCo succeedingCoco = new SucceedingCoCo();
    
    // no errors check
    checker.addCoCo(succeedingCoco);
    CoCoResultList result = checker.checkAll(astRoot);
    assertTrue(result.isSucceeded());
    assertNoCoCoErrors(result);
    
    // check that expected errors occure
    checker.addCoCo(failingCoCo);
    Collection<ExpectedCoCoError> expectedErrors = Arrays.asList(new ExpectedCoCoError.Builder(
        "0x...").build());
    result = checker.checkAll(astRoot);
    assertFalse(result.isSucceeded());
    assertExpectedCoCoErrors(result.getErrors(), expectedErrors, Optional.empty(),
        LOGNAME);
    
    // disable cocos
    checker.disableAll();
    result = checker.checkAll(astRoot);
    assertNoCoCoErrors(result);
    
    // enable by prefix
    checker.enableByPrefixes("Failing");
    assertTrue(failingCoCo.isEnabled());
    assertFalse(succeedingCoco.isEnabled());
    result = checker.checkAll(astRoot);
    assertFalse(result.isSucceeded());
    assertExpectedCoCoErrors(result.getErrors(), expectedErrors, Optional.empty(),
        LOGNAME);
    
  }
  
  private static class SucceedingCoCo extends AbstractContextCondition implements
      CD4AnalysisASTCDClassCoCo {
    private static final String NAME = SucceedingCoCo.class.getSimpleName();
    
    @Override
    public ContextConditionResult check(ASTCDClass node) {
      return ContextConditionResult.empty(NAME);
    }
    
    @Override
    public String getName() {
      return NAME;
    }
  }
  
  private static class FailingCoCo extends AbstractContextCondition implements
      CD4AnalysisASTCDClassCoCo {
    private static final String NAME = FailingCoCo.class.getSimpleName();
    
    @Override
    public ContextConditionResult check(ASTCDClass node) {
      return ContextConditionResult.error(NAME, new CoCoError("0x...", "msg", new SourcePosition(1, 1)));
    }
    
    @Override
    public String getName() {
      return NAME;
    }
  }
}
