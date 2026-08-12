/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeDiffTest extends SynDiffTestBasis {
  
  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/syndiff/TypeDiff/";
  }
  
  // Test for all kinds of changes in attributes
  @Test
  public void testType1() {
    parseModels("Source1.cd", "Target1.cd");
    
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    
    new AssertSynDiff(synDiff).assertMatchedClasses(3).assertMatchedEnums(1).assertChangedTypes(4)
        .assertAddedClasses(1).assertDeletedClasses(1).assertAddedEnums(1).assertDeletedEnums(1)
        .assertAddedAssocs(1).assertDeletedAssocs(2).assertRemainingEmpty();
    
    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_TYPE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    
    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }
  
  // Tests for all kinds of changes in enum constants
  @Test
  public void testType2() {
    parseModels("Source2.cd", "Target2.cd");
    
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    
    new AssertSynDiff(synDiff).assertMatchedClasses(2).assertMatchedEnums(1).assertChangedTypes(2)
        .assertAddedEnums(1).assertDeletedEnums(1).assertRemainingEmpty();
    
    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_TYPE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_TYPE_EXTENDS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    
    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }
  
  // Test for change of modifiers, extensions, and implementations
  @Test
  public void testType3() {
    parseModels("Source3.cd", "Target3.cd");
    
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    
    new AssertSynDiff(synDiff).assertMatchedClasses(5).assertChangedTypes(3).assertRemainingEmpty();
    
    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_MODIFIER, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_TYPE_IMPLEMENTS, 2L);
    expectedDiffTypes.put(DiffTypes.CHANGED_TYPE_EXTENDS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    
    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }
  
  // Test for inherited attributes
  @Test
  public void testType4() {
    parseModels("Source4.cd", "Target4.cd");
    
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    
    new AssertSynDiff(synDiff).assertMatchedClasses(3).assertChangedTypes(3).assertRemainingEmpty();
    
    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 1L);
    
    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }
  
}
