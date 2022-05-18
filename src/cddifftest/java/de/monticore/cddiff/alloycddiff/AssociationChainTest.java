/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODObject;
import edu.mit.csail.sdg.translator.A4Solution;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test classes to test the generation of different alloy modules for cddiff
 */
public class AssociationChainTest extends CDDiffTestBasis {
  /**
   * A helper function to test solution
   *
   * @param optS Optional AlloyDiffSolution
   * @param k    bound for the number of solutions
   */
  private void testSolution(Optional<AlloyDiffSolution> optS, int k) {
    // Test if generation was successful
    assertTrue(optS.isPresent());

    // Extract solution
    AlloyDiffSolution s = optS.get();

    // The solution generation should not be limited by default
    assertFalse(s.isLimited());

    // The number of satisfiable solutions must match the size of parsed ODs
    int theoreticSize = s.getNumberOfSatSolutions();
    List<ASTODArtifact> ods = s.generateODs();
    assertEquals(theoreticSize, ods.size());

    // This must also be true for the number of satisfiable solutions
    List<A4Solution> sols = s.getAllSatSolutions();
    assertEquals(theoreticSize, sols.size());

    // Check if bound k was not violated
    for (ASTODArtifact od : ods) {
      assertTrue(od.getObjectDiagram()
          .getODElementList()
          .stream()
          .filter(e -> e instanceof ASTODObject)
          .count() <= k);
    }
  }

  @Test
  public void testLength12() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/AssociationChains" + "/AssocChainLen1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/AssociationChains" + "/AssocChainLen2.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Compute diff solutions

    // Generate first solution
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 1);
    // Test first solution
    testSolution(optS1, 1);
    // Write solution to location
    Path outputDirectoryS1 = Paths.get(
        "target/generated/cddiff-test/diff_" + 1 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());

    // Assertion that no difference should be found in this scope
    assertTrue(optS1.isPresent() && optS1.get().generateODs().size() == 0);
    // Generate outputs
    optS1.get().generateSolutionsToPath(outputDirectoryS1);

    // Generate second solution
    Optional<AlloyDiffSolution> optS2 = ClassDifference.cddiff(astV1, astV2, 2);
    // Test first solution
    testSolution(optS2, 2);
    // Write solution to location
    Path outputDirectoryS2 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());

    // Assertion that a difference should be found in this scope
    assertTrue(optS2.isPresent() && optS2.get().generateODs().size() > 0);
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);

    // Generate inversion of second solution
    optS2 = ClassDifference.cddiff(astV2, astV1, 2);
    // Test first solution
    testSolution(optS2, 2);
    // Write solution to location
    outputDirectoryS2 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Assertion that no difference should be found in this scope
    assertTrue(optS2.isPresent() && optS2.get().generateODs().size() == 0);
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);

  }

}
