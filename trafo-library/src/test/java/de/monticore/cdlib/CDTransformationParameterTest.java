/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTransformationParameterTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void resetFindings() {
    Log.clearFindings();
  }
  
  @Test
  public void fromObjectWithStringAsString() {
    CDTransformationParameter param = CDTransformationParameter.fromObject("abc");
    assertEquals("abc", param.asString());
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithStringAsList() {
    CDTransformationParameter param = CDTransformationParameter.fromObject("abc");
    assertEquals(1, param.asList().size());
    assertEquals("abc", param.asList().get(0));
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithStringListAsString() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("ab", "cd", "ef"));
    assertEquals("", param.asString());
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A520:"));
  }
  
  @Test
  public void fromObjectWithStringListAsList() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("ab", "cd", "ef"));
    assertEquals(3, param.asList().size());
    assertEquals("ab", param.asList().get(0));
    assertEquals("cd", param.asList().get(1));
    assertEquals("ef", param.asList().get(2));
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithIntAsString() {
    CDTransformationParameter param = CDTransformationParameter.fromObject(42);
    assertEquals("42", param.asString());
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithIntAsList() {
    CDTransformationParameter param = CDTransformationParameter.fromObject("42");
    assertEquals(1, param.asList().size());
    assertEquals("42", param.asList().get(0));
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithIntListAsString() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("12", "34", "56"));
    assertEquals("", param.asString());
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A520:"));
  }
  
  @Test
  public void fromObjectWithIntListAsList() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("12", "34", "56"));
    assertEquals(3, param.asList().size());
    assertEquals("12", param.asList().get(0));
    assertEquals("34", param.asList().get(1));
    assertEquals("56", param.asList().get(2));
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithNestedListAsString() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of(List.of("ab", "cd")));
    assertEquals("[ab, cd]", param.asString());
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithNestedListAsList() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of(List.of("ab", "cd")));
    assertEquals(1, param.asList().size());
    assertEquals("[ab, cd]", param.asList().get(0));
    assertEquals(0, Log.getFindingsCount());
  }
  
  @Test
  public void fromObjectWithMixedAsString() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("abc", 123, List.of("ab", "cd")));
    assertEquals("", param.asString());
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x4A520:"));
  }
  
  @Test
  public void fromObjectWithMixedAsList() {
    CDTransformationParameter param =
        CDTransformationParameter.fromObject(List.of("abc", 123, List.of("ab", "cd")));
    assertEquals(3, param.asList().size());
    assertEquals("abc", param.asList().get(0));
    assertEquals("123", param.asList().get(1));
    assertEquals("[ab, cd]", param.asList().get(2));
    assertEquals(0, Log.getFindingsCount());
  }
  
}
