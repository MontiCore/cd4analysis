/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SynAssocDiffTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/syndiff/AssocDiff/";
  }

  @Test
  public void testAssoc1() {
    parseModels("Source1.cd", "Target1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertAddedClasses(1)
      .assertMatchedClasses(3)
      .assertAddedAssocs(1)
      .assertDeletedAssocs(1)
      .assertChangedAssocs(1)
      .assertMatchedAssocs(1)
      .assertRemainingEmpty();

    // Check that no other diffs exist
    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc2() {
    parseModels("Source2.cd", "Target2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertMatchedClasses(3)
      .assertChangedAssocs(1)
      .assertMatchedAssocs(1)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc3() {
    parseModels("Source3.cd", "Target3.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertMatchedClasses(4)
      .assertChangedTypes(2)
      .assertChangedAssocs(3)
      .assertMatchedAssocs(3)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_DIRECTION, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_TYPE_EXTENDS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_MODIFIER, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc4() {
    parseModels("Source4.cd", "Target4.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertMatchedClasses(4)
      .assertAddedAssocs(4)
      .assertDeletedAssocs(4)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc5() {
    parseModels("Source5.cd", "Target5.cd");
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertMatchedClasses(2)
      .assertChangedAssocs(1)
      .assertMatchedAssocs(1)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

}
