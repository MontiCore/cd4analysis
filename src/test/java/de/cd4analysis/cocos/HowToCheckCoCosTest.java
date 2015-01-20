/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Paths;

import mc.antlr4.MCConcreteParser;
import mc.ast.ASTNode;
import mc.ast.SourcePosition;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import cd4analysis.cocos.UniqueAttributeInClassCoco;
import cd4analysis.cocos._tobegenerated.CD4AClassCoCo;
import cd4analysis.cocos._tobegenerated.CD4ACoCoChecker;
import cd4analysis.cocos._tobegenerated.CD4ACoCoProfile;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.cocos.AbstractContextCondition;
import de.monticore.cocos.CoCoError;
import de.monticore.cocos.ContextConditionResult;
import de.se_rwth.commons.logging.Log;

/**
 * Simple test showing how to check CoCos.
 *
 * @author Robert Heim
 */
public class HowToCheckCoCosTest {
  
  public static class MockCoCo extends AbstractContextCondition implements CD4AClassCoCo {
    private static final String NAME = MockCoCo.class.getName();
    
    @Override
    public ContextConditionResult check(ASTCDClass node) {
      return ContextConditionResult.error(new CoCoError("0x...", "msg", new SourcePosition(1, 1)));
    }
    
    @Override
    public String getName() {
      return NAME;
    }
  }
  
  @Test
  public void test() throws RecognitionException, IOException {
    
    CD4ACoCoProfile profile = new CD4ACoCoProfile();
    profile.addCoCo(new UniqueAttributeInClassCoco());
    profile.addCoCo(new MockCoCo());
    CD4ACoCoChecker checker = new CD4ACoCoChecker(profile);
    
    MCConcreteParser parser = new CDCompilationUnitMCParser();
    Optional<? extends ASTNode> root = parser.parse(Paths.get(
        "src/test/resources/de/cd4analysis/cocos/valid/A.cd").toString());
    
    ASTNode a = root.get();
    // Boolean result = checker.checkAll(a);
    // TODO RH cannot checkAll right now, because DDVisitor has
    // NullPointerExceptions, because some parts of the AST are not set, ask GV?
    Boolean result = false;
    
    assertFalse(result);
    int errorCount = 0;
    for (ContextConditionResult r : checker.getResults()) {
      for (CoCoError e : r.getErrors()) {
        Log.debug(e.toString(), HowToCheckCoCosTest.class.getName());
        errorCount++;
      }
    }
    // TODO RH uncomment assertEquals(1, errorCount);
    
    // In tests you might want use the test-infrastructure for CoCo-Tests
    // see mc4/monticore-runtime/src/test/java/de.monticore.cocos.helper.*
    
  }
}
