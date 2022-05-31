/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloy2od;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Test if diff-witnesses match first class diagram and not second
 */
public class WitnessTest extends CDDiffTestBasis {
  @Test
  public void testManger() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
    assertNotNull(astV2);

    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(astV1, astV2, 7);

    // Test if generation was successful
    assertTrue(optS.isPresent());

    // Extract solution
    AlloyDiffSolution s = optS.get();
    List<ASTODArtifact> ods = s.generateODs();

    // Check for each od in ods if od is an instance of cd1 and not cd2
    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();

    ASTCDDefinition cd1 = astV1.getCDDefinition();
    ASTCDDefinition cd2 = astV2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      assertTrue(matcher.checkODConsistency(cd1, od.getObjectDiagram()));
      assertFalse(matcher.checkODConsistency(cd2, od.getObjectDiagram()));
    }

  }

  @Test
  public void testOWAlloyDiff() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
    assertNotNull(astV2);

    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(astV1, astV2, 7,
        CDSemantics.MULTI_INSTANCE_OPEN_WORLD, "target/generated/cddiff-test/");

    // Test if generation was successful
    assertTrue(optS.isPresent());

    // Extract solution
    AlloyDiffSolution s = optS.get();

    // limit number of generated diff-witnesses
    s.setSolutionLimit(1);
    s.setLimited(true);

    List<ASTODArtifact> ods = s.generateODs();

    assertFalse(ods.isEmpty());
  }

  @Test
  public void testNoOWAlloyDiff() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV2);

    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(astV1, astV2, 7,
        CDSemantics.MULTI_INSTANCE_OPEN_WORLD, "target/generated/cddiff-test/");

    // Test if generation was successful
    assertTrue(optS.isPresent());

    // Extract solution
    AlloyDiffSolution s = optS.get();

    // limit number of generated diff-witnesses
    s.setSolutionLimit(1);
    s.setLimited(true);

    List<ASTODArtifact> ods = s.generateODs();

    assertTrue(ods.isEmpty());
  }

}
