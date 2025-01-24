/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.runtime.ODRule;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

public class CDTransformationRunnerTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void resetFindings() {
    Log.clearFindings();
  }
  
  @Test
  public void testTransformWithoutParams() throws Exception {
    try (
        MockedStatic<CDTransformationLibType> trafoTypeMock = Mockito.mockStatic(
            CDTransformationLibType.class)) {
      // create new mocked transformation type
      CDTransformationLibType testTrafo = Mockito.mock(CDTransformationLibType.class);
      String testTrafoName = "TEST_TRANSFORMATION";
      Mockito.doReturn(0).when(testTrafo).ordinal();
      Mockito.doReturn(testTrafoName).when(testTrafo).name();
      Mockito.when(CDTransformationLibType.valueOf(eq(testTrafoName))).thenReturn(testTrafo);
      
      // assume successful transformation
      Mockito.doReturn(true).when(testTrafo)
          .apply(Mockito.any(ASTCDCompilationUnit.class), Mockito.anyMap());
      
      ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
      
      CDTransformationRunner runner = new CDTransformationRunner(ast);
      runner.transform("TEST_TRANSFORMATION");
      
      assertEquals(0, Log.getFindingsCount());
      verify(testTrafo, Mockito.times(1)).apply(same(ast), argThat(Map::isEmpty));
    }
  }
  
  @Test
  public void testTransformWithParams() throws Exception {
    try (
        MockedStatic<CDTransformationLibType> trafoTypeMock = Mockito.mockStatic(
            CDTransformationLibType.class)) {
      // create new mocked transformation type
      CDTransformationLibType testTrafo = Mockito.mock(CDTransformationLibType.class);
      String testTrafoName = "TEST_TRANSFORMATION";
      Mockito.doReturn(0).when(testTrafo).ordinal();
      Mockito.doReturn(testTrafoName).when(testTrafo).name();
      Mockito.when(CDTransformationLibType.valueOf(eq(testTrafoName))).thenReturn(testTrafo);
      
      // assume successful transformation
      Mockito.doReturn(true).when(testTrafo)
          .apply(Mockito.any(ASTCDCompilationUnit.class), Mockito.anyMap());
      
      ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
      Map<String, Object> params = new HashMap<>();
      params.put("param1", "value1");
      params.put("param2", 2);
      
      CDTransformationRunner runner = new CDTransformationRunner(ast);
      runner.transform("TEST_TRANSFORMATION", params);
      
      assertEquals(0, Log.getFindingsCount());
      verify(testTrafo, Mockito.times(1)).apply(same(ast), argThat(x -> x.size() == 2));
    }
  }
  
  @Test
  public void testTransformWithException() throws Exception {
    try (
        MockedStatic<CDTransformationLibType> trafoTypeMock = Mockito.mockStatic(
            CDTransformationLibType.class)) {
      // create new mocked transformation type
      CDTransformationLibType testTrafo = Mockito.mock(CDTransformationLibType.class);
      String testTrafoName = "TEST_TRANSFORMATION";
      Mockito.doReturn(0).when(testTrafo).ordinal();
      Mockito.doReturn(testTrafoName).when(testTrafo).name();
      Mockito.when(CDTransformationLibType.valueOf(eq(testTrafoName))).thenReturn(testTrafo);
      
      // assume successful transformation
      Mockito.doThrow(new IOException()).when(testTrafo)
          .apply(Mockito.any(ASTCDCompilationUnit.class), Mockito.anyMap());
      
      ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
      Map<String, Object> params = new HashMap<>();
      
      CDTransformationRunner runner = new CDTransformationRunner(ast);
      runner.transform("TEST_TRANSFORMATION", params);
      
      assertEquals(1, Log.getFindingsCount());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A521:"));
      verify(testTrafo, Mockito.times(1)).apply(same(ast), argThat(Map::isEmpty));
    }
  }
  
  @Test
  public void testTransformUnknownTransformation() throws Exception {
    ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
    Map<String, Object> params = new HashMap<>();
    params.put("param1", "value1");
    params.put("param2", 2);
    
    CDTransformationRunner runner = new CDTransformationRunner(ast);
    runner.transform("UNKNOWN", params);
    
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A523:"));
  }
  
  @Test
  public void testGenericTransformWithoutParams() {
    ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
    TestRule rule = Mockito.mock(TestRule.class);
    
    Mockito.doReturn(true).when(rule).doPatternMatching();
    
    CDTransformationRunner runner = new CDTransformationRunner(ast);
    runner.genericTransform(rule);
    
    assertEquals(0, Log.getFindingsCount());
    verify(rule, Mockito.times(0)).set_$param1(any());
    verify(rule, Mockito.times(0)).set_$param2(any());
    verify(rule, Mockito.times(1)).doPatternMatching();
    verify(rule, Mockito.times(1)).doReplacement();
  }
  
  @Test
  public void testGenericTransformWithoutParams2() {
    ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
    TestRule rule = Mockito.mock(TestRule.class);
    
    Mockito.doReturn(false).when(rule).doPatternMatching();
    
    CDTransformationRunner runner = new CDTransformationRunner(ast);
    runner.genericTransform(rule);
    
    assertEquals(0, Log.getFindingsCount());
    verify(rule, Mockito.times(0)).set_$param1(any());
    verify(rule, Mockito.times(0)).set_$param2(any());
    verify(rule, Mockito.times(1)).doPatternMatching();
    verify(rule, Mockito.times(0)).doReplacement();
  }
  
  @Test
  public void testGenericTransform() throws NoSuchMethodException {
    ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
    TestRule rule = Mockito.mock(TestRule.class);
    
    Mockito.doReturn(true).when(rule).doPatternMatching();
    
    Map<String, Object> params = new HashMap<>();
    params.put("param1", "value1");
    params.put("param2", 2);
    
    CDTransformationRunner runner = new CDTransformationRunner(ast);
    runner.genericTransform(rule, params);
    
    assertEquals(0, Log.getFindingsCount());
    verify(rule, Mockito.times(1)).set_$param1(eq("value1"));
    verify(rule, Mockito.times(1)).set_$param2(eq("2"));
    verify(rule, Mockito.times(1)).doPatternMatching();
    verify(rule, Mockito.times(1)).doReplacement();
  }
  
  @Test
  public void testGenericTransformWithUnknownParameter() throws NoSuchMethodException {
    ASTCDCompilationUnit ast = Mockito.mock(ASTCDCompilationUnit.class);
    TestRule rule = Mockito.mock(TestRule.class);
    
    Mockito.doReturn(true).when(rule).doPatternMatching();
    
    Map<String, Object> params = new HashMap<>();
    params.put("param1", "value1");
    params.put("param2", 2);
    params.put("param3", 3);
    
    CDTransformationRunner runner = new CDTransformationRunner(ast);
    runner.genericTransform(rule, params);
    
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("Ignoring unknown parameter: param3"));
    verify(rule, Mockito.times(1)).set_$param1(eq("value1"));
    verify(rule, Mockito.times(1)).set_$param2(eq("2"));
    verify(rule, Mockito.times(1)).doPatternMatching();
    verify(rule, Mockito.times(1)).doReplacement();
  }
  
  private static class TestRule extends ODRule {
    
    @Override
    public boolean doPatternMatching() {
      return false;
    }
    
    @Override
    public void doReplacement() {
    
    }
    
    public void set_$param1(String param1) {
    
    }
    
    public void set_$param2(String param1) {
    
    }
  }
}
