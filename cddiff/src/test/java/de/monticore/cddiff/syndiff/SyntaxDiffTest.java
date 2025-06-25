/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.CDConformanceChecker;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SyntaxDiffTest extends CDDiffTestBasis {

  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir = "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  @Test
  public void testDTs() {
    ASTCDCompilationUnit compilationUnitNew = parseModel(
        "src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel(
        "src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, List.of());
    assertEquals(4, synDiff.getAddedClasses().size());
    assertEquals(2, synDiff.getAddedAssocs().size());
    assertEquals(3, synDiff.getChangedAssocs().size());
    assertEquals(4, synDiff.getMatchedClasses().size());
    assertEquals(3, synDiff.getMatchedAssocs().size());

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
    parseModels("Source1.cd", "Target1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    // check added / deleted classes
    assertEquals(2, synDiff.getAddedClasses().size());
    assertEquals(2, synDiff.getDeletedClasses().size());

    // check added / deleted enums
    assertEquals(1, synDiff.getAddedEnums().size());
    assertEquals(1, synDiff.getDeletedEnums().size());

    // check changed types
    assertEquals(4, synDiff.getChangedTypes().size());

    // check associations
    assertEquals(2, synDiff.getChangedAssocs().size());
    assertEquals(2, synDiff.getAddedAssocs().size());
    assertEquals(2, synDiff.getDeletedAssocs().size());

    // check no changes
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.DELETED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.DELETED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ASSOCIATION, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ENUM, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_INHERITANCE, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 2L);
    expectedDiffTypes.put(DiffTypes.ADDED_CLASS, 1L);
    expectedDiffTypes.put(DiffTypes.ADDED_CONSTANT, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_NAME, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_ROLE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY, 2L);
    expectedDiffTypes.put(DiffTypes.CHANGED_ATTRIBUTE_TYPE, 1L);
    expectedDiffTypes.put(DiffTypes.CHANGED_CLASS_MODIFIER, 1L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff2() {
    parseModels("Source2.cd", "Target2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    // check changes
    assertEquals(2, synDiff.getAddedClasses().size());
    assertEquals(2, synDiff.getDeletedClasses().size());

    assertEquals(1, synDiff.getAddedEnums().size());
    assertEquals(1, synDiff.getDeletedEnums().size());

    assertEquals(1, synDiff.getAddedAssocs().size());
    assertEquals(1, synDiff.getDeletedAssocs().size());

    // check no changes
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getChangedTypes().isEmpty());
    assertTrue(synDiff.getChangedAssocs().isEmpty());

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
    parseModels("TechStoreV2.cd", "TechStoreV1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    assertEquals(2, synDiff.getDeletedClasses().size());
    assertEquals(3, synDiff.getChangedTypes().size());

    assertEquals(5, synDiff.getChangedAssocs().size());
    assertEquals(4, synDiff.getAddedAssocs().size());
    assertEquals(2, synDiff.getDeletedAssocs().size());

    // check no changes
    assertTrue(synDiff.getAddedClasses().isEmpty());
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getAddedEnums().isEmpty());
    assertTrue(synDiff.getDeletedEnums().isEmpty());

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
    parseModels("TechStoreV9.cd", "TechStoreV10.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    // check changes
    assertEquals(1, synDiff.getDeletedAssocs().size());

    // check no changes
    assertTrue(synDiff.getAddedClasses().isEmpty());
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getAddedEnums().isEmpty());
    assertTrue(synDiff.getDeletedEnums().isEmpty());
    assertTrue(synDiff.getChangedTypes().isEmpty());
    assertTrue(synDiff.getChangedAssocs().isEmpty());
    assertTrue(synDiff.getAddedAssocs().isEmpty());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.DELETED_ASSOCIATION, 1L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testSyntaxDiff5() {
    parseModels("TechStoreV11.cd", "TechStoreV12.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    // check changes
    assertEquals(3, synDiff.getChangedTypes().size());

    // check no changes
    assertTrue(synDiff.getAddedClasses().isEmpty());
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getAddedEnums().isEmpty());
    assertTrue(synDiff.getDeletedEnums().isEmpty());
    assertTrue(synDiff.getChangedAssocs().isEmpty());
    assertTrue(synDiff.getAddedAssocs().isEmpty());
    assertTrue(synDiff.getDeletedAssocs().isEmpty());

    Map<DiffTypes, Long> expectedDiffTypes = new HashMap<>();
    expectedDiffTypes.put(DiffTypes.ADDED_ATTRIBUTE, 1L);
    expectedDiffTypes.put(DiffTypes.INHERITED_ATTRIBUTE, 2L);

    assertEquals(expectedDiffTypes, getDiffTypesCount(synDiff));
  }

  @Test
  public void testMaCoCo() {
    CDDiffUtil.setUseJavaTypes(true);
    parseModels("MaCoCo_v1.cd", "MaCoCo_v2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());

    assertEquals(1, synDiff.getAddedClasses().size());
    assertEquals(1, synDiff.getAddedEnums().size());
    assertEquals(1, synDiff.getAddedAssocs().size());

    assertTrue(synDiff.getDeletedClasses().isEmpty());
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedEnums().isEmpty());
    assertTrue(synDiff.getChangedTypes().isEmpty());
    assertTrue(synDiff.getChangedAssocs().isEmpty());
    assertTrue(synDiff.getDeletedAssocs().isEmpty());

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
    sb = new SyntaxDiffPrinter(synDiff);
    Log.println(sb.printDiff());
    assertEquals(1, synDiff.getDeletedClasses().size());
    assertEquals(1, synDiff.getDeletedEnums().size());
    assertEquals(1, synDiff.getDeletedAssocs().size());

    assertTrue(synDiff.getAddedClasses().isEmpty());
    assertTrue(synDiff.getAddedInterfaces().isEmpty());
    assertTrue(synDiff.getDeletedInterfaces().isEmpty());
    assertTrue(synDiff.getAddedEnums().isEmpty());
    assertTrue(synDiff.getChangedTypes().isEmpty());
    assertTrue(synDiff.getChangedAssocs().isEmpty());
    assertTrue(synDiff.getAddedAssocs().isEmpty());

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

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src = CD4CodeMill.parser().parseCDCompilationUnit(dir
          + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CDDiffUtil.refreshSymbolTable(src.get());
        CDDiffUtil.refreshSymbolTable(tgt.get());
        this.tgt = tgt.get();
        this.src = src.get();
      }
      else {
        fail("Could not parse CDs.");
      }

    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }

  private Map<DiffTypes, Long> getDiffTypesCount(CDSyntaxDiff synDiff) {
    return synDiff.getBaseDiff().stream().collect(
      Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
      )
    );
  }
}
