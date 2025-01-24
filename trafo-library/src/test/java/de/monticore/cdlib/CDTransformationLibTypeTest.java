/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTransformationLibTypeTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void resetFindings() {
    Log.clearFindings();
  }
  
  @Test
  public void getStringParam() {
    Map<String, CDTransformationParameter> params = new HashMap<>();
    params.put("param1", CDTransformationParameter.fromObject("value1"));
    params.put("param2", CDTransformationParameter.fromObject("value2"));
    
    String result = CDTransformationLibType.getStringParam(params, "param1");
    
    assertEquals("value1", result);
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void getMissingStringParam() {
    Map<String, CDTransformationParameter> params = new HashMap<>();
    
    String result = CDTransformationLibType.getStringParam(params, "unknown");
    
    assertEquals("", result);
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A524:"));
  }
  
  @Test
  public void getListParam() {
    Map<String, CDTransformationParameter> params = new HashMap<>();
    params.put("param1", CDTransformationParameter.fromObject("value1"));
    params.put("param2", CDTransformationParameter.fromObject(List.of("valueA", "valueB")));
    
    List<String> result = CDTransformationLibType.getListParam(params, "param2");
    
    assertEquals(List.of("valueA", "valueB"), result);
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void getMissingListParam() {
    Map<String, CDTransformationParameter> params = new HashMap<>();
    
    List<String> result = CDTransformationLibType.getListParam(params, "unknown");
    
    assertEquals(Collections.emptyList(), result);
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A524:"));
  }
}
