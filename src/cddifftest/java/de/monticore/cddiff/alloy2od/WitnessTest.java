/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloy2od;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import org.junit.Test;

import java.nio.file.Path;
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

    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(astV1, astV2, 7);

    // Test if generation was successful
    assertTrue(optS.isPresent());

    // Extract solution
    AlloyDiffSolution s = optS.get();
    List<ASTODArtifact> ods = s.generateODs();
    s.generateSolutionsToPath(Path.of("target/TEMP"));

    // Check for each od in ods if od is an instance of cd1 and not cd2
    OD2CDMatcher matcher = new OD2CDMatcher();

    //TODO: Fix matcher
    int i=0;
    for (ASTODArtifact od : ods) {
      od.getObjectDiagram().setName("witness"+i);
      //assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, od, astV1));
      //assertFalse(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, od, astV2));
      i++;
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

    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(astV1, astV2, 7,
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

    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(astV1, astV2, 7,
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
