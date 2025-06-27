/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.CDConformanceChecker;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SyntaxDiffTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/";
  }

  @Test
  public void testMember1() {
    parseModels("syndiff/MemberDiff/Source1.cd", "syndiff/MemberDiff/Target1.cd");

    ASTCDClass cNew = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("A", tgt.getCDDefinition());

    Assertions.assertNotNull(cNew);
    Assertions.assertNotNull(cOld);

    ASTNode attributeNew = CDTestHelper.getAttribute(cNew, "a");
    ASTNode attributeOld = CDTestHelper.getAttribute(cOld, "a");

    CDMemberDiff attrDiff = new CDMemberDiff(attributeNew, attributeOld);

    assertEquals(new HashSet<>(attrDiff.getBaseDiff()), Set.of(DiffTypes.CHANGED_ATTRIBUTE_TYPE, DiffTypes.CHANGED_ATTRIBUTE_MODIFIER));
  }

  @Test
  public void testDTs() {
    parseModels("DigitalTwins/DigitalTwin3.cd", "DigitalTwins/DigitalTwin1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertAddedClasses(4)
      .assertMatchedClasses(4)
      .assertAddedAssocs(2)
      .assertChangedAssocs(3)
      .assertMatchedAssocs(3)
      .assertChangedTypes(2)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 2L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_TYPE_EXTENDS, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_MODIFIER, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff1() {
    parseModels("syndiff/SyntaxDiff/Source1.cd", "syndiff/SyntaxDiff/Target1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertAddedClasses(1)
      .assertDeletedClasses(1)
      .assertMatchedClasses(3)
      .assertChangedTypes(5)
      .assertAddedAssocs(2)
      .assertDeletedAssocs(2)
      .assertChangedAssocs(2)
      .assertMatchedAssocs(2)
      .assertAddedEnums(1)
      .assertDeletedEnums(1)
      .assertMatchedEnums(1)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 3L);
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_NAME, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 2L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_TYPE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_NAME, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_MODIFIER, 1L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff2() {
    parseModels("syndiff/SyntaxDiff/Source2.cd", "syndiff/SyntaxDiff/Target2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertAddedClasses(2)
      .assertDeletedClasses(2)
      .assertMatchedClasses(2)
      .assertAddedAssocs(1)
      .assertDeletedAssocs(1)
      .assertMatchedAssocs(1)
      .assertAddedEnums(1)
      .assertDeletedEnums(1)
      .assertMatchedEnums(1)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff3() {
    parseModels("syndiff/SyntaxDiff/TechStoreV2.cd", "syndiff/SyntaxDiff/TechStoreV1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertDeletedClasses(2)
      .assertMatchedClasses(11)
      .assertChangedTypes(3)
      .assertAddedAssocs(4)
      .assertDeletedAssocs(2)
      .assertChangedAssocs(5)
      .assertMatchedAssocs(6)
      .assertMatchedEnums(1)
      .assertRemainingEmpty();


    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_NAME, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_ROLE, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_DIRECTION, 3L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CLASS, 2L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_TYPE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_NAME, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff4() {
    parseModels("syndiff/SyntaxDiff/TechStoreV9.cd", "syndiff/SyntaxDiff/TechStoreV10.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertMatchedClasses(2)
      .assertDeletedAssocs(1)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff5() {
    parseModels("syndiff/SyntaxDiff/TechStoreV11.cd", "syndiff/SyntaxDiff/TechStoreV12.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertChangedTypes(3)
      .assertMatchedClasses(3)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 2L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testMaCoCo() {
    CDDiffUtil.setUseJavaTypes(true);
    parseMaCoCo("syndiff/SyntaxDiff/MaCoCo_v1.cd", "syndiff/SyntaxDiff/MaCoCo_v2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());

    new AssertSynDiff(synDiff)
      .assertAddedClasses(1)
      .assertAddedEnums(1)
      .assertAddedAssocs(1)
      .assertMatchedAssocs(91)
      .assertMatchedClasses(94)
      .assertMatchedEnums(45)
      .assertRemainingEmpty();

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));

    // Conformance Checking without stereotype mapping constitutes a refinement check
    boolean conform = new CDConformanceChecker(Set.of(CDConfParameter.STEREOTYPE_MAPPING,
        CDConfParameter.NAME_MAPPING, CDConfParameter.SRC_TARGET_ASSOC_MAPPING,
        CDConfParameter.ALLOW_CARD_RESTRICTION)).checkConformance(src, tgt, "incarnates");

    assertTrue(conform);

    // Syn2SemDiff produces correct diff-witnesses
    Syn2SemDiff semDiff = new Syn2SemDiff(src, tgt);
    OD2CDMatcher matcher = new OD2CDMatcher();
    List<ASTODArtifact> ods = semDiff.generateODs(false);

    assertFalse(ods.isEmpty());
    assertTrue(semDiff.generateODs(false).stream().allMatch(od -> matcher.checkIfDiffWitness(
        CDSemantics.SIMPLE_CLOSED_WORLD, src, tgt, od)));

    synDiff = new CDSyntaxDiff(tgt, src, List.of());
    new AssertSynDiff(synDiff)
      .assertDeletedClasses(1)
      .assertDeletedEnums(1)
      .assertDeletedAssocs(1)
      .assertMatchedAssocs(91)
      .assertMatchedClasses(94)
      .assertMatchedEnums(45)
      .assertRemainingEmpty();

    expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));

    // Conformance Checking without stereotype mapping constitutes a refinement check
    conform = new CDConformanceChecker(Set.of(CDConfParameter.STEREOTYPE_MAPPING,
        CDConfParameter.NAME_MAPPING, CDConfParameter.SRC_TARGET_ASSOC_MAPPING,
        CDConfParameter.ALLOW_CARD_RESTRICTION)).checkConformance(tgt, src, "incarnates");

    assertFalse(conform);

    // Syn2SemDiff produces correct diff-witnesses
    semDiff = new Syn2SemDiff(tgt, src);
    ods = semDiff.generateODs(false);

    assertTrue(ods.isEmpty());
  }

  public void parseMaCoCo(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src = CD4CodeMill.parser().parseCDCompilationUnit(dir
        + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CDDiffUtil.refreshSymbolTable(src.get());
        CDDiffUtil.refreshSymbolTable(tgt.get());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        fail(String.format("Parsing src: '%s', tgt: '%s'.", src.isPresent() ? "success" : "failure", tgt.isPresent() ? "success" : "failure"));
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
