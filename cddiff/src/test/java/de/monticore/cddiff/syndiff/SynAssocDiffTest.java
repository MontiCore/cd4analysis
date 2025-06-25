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

    assertEquals(1, synDiff.getChangedAssocs().size());
    assertEquals(1, synDiff.getAddedAssocs().size());
    assertEquals(1, synDiff.getDeletedAssocs().size());
    assertEquals(1, synDiff.getAddedClasses().size());
    assertEquals(3, synDiff.getMatchedClasses().size());
    assertEquals(1, synDiff.getMatchedAssocs().size());

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

    assertEquals(1, synDiff.getChangedAssocs().size());
    assertEquals(1, synDiff.getMatchedAssocs().size());
    assertEquals(3, synDiff.getMatchedClasses().size());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc3() {
    parseModels("Source3.cd", "Target3.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    assertEquals(2, synDiff.getChangedTypes().size());
    assertEquals(3, synDiff.getChangedAssocs().size());
    assertEquals(3, synDiff.getMatchedAssocs().size());
    assertEquals(4, synDiff.getMatchedClasses().size());

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

    assertEquals(4, synDiff.getAddedAssocs().size());
    assertEquals(4, synDiff.getDeletedAssocs().size());
    assertEquals(0, synDiff.getMatchedAssocs().size());
    assertEquals(4, synDiff.getMatchedClasses().size());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testAssoc5() {
    parseModels("Source5.cd", "Target5.cd");
    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    assertEquals(1, synDiff.getChangedAssocs().size());
    assertEquals(1, synDiff.getMatchedAssocs().size());
    assertEquals(2, synDiff.getMatchedClasses().size());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

}
