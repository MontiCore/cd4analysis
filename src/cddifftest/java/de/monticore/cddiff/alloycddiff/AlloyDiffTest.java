/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
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
public class AlloyDiffTest extends AbstractTest {
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

    // Limiting the solution space must reduce number of solutions
    if (theoreticSize != 0) {
      s.setSolutionLimit(theoreticSize - 1);
      s.setLimited(true);

      ods = s.generateODs();
      assertEquals(theoreticSize - 1, ods.size());
    }

    // Check if bound k was not violated
    for (ASTODArtifact od : ods) {
      assertTrue(od.getObjectDiagram()
          .getODElementList()
          .stream()
          .filter(e -> e instanceof ASTODObject)
          .count() <= k);
    }

    // Check if unneccessary solutions are created
    assertEquals(s.generateUniqueODs().size(), sols.size());
  }

  @Test
  public void testManger() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Compute diff solutions

    // Generate first solution
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 5);
    // Test first solution
    //    testSolution(optS1, 5);
    // Write solution to location
    Path outputDirectoryS1 = Paths.get(
        "target/generated/cddiff-test/diff_" + 5 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());
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
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);
    Path outputDirectoryS2U = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName() + "_unique");
    optS2.get().generateUniqueSolutionsToPath(outputDirectoryS2U);

    for (A4Solution solution : optS2.get().getAllSatSolutions()) {
      System.out.println(solution);
    }

    System.out.println(optS2.get().getNumberOfSatSolutions());

    // Generate third solution
    Optional<AlloyDiffSolution> optS3 = ClassDifference.cddiff(astV2, astV1, 2);
    // Test third solution
    testSolution(optS3, 2);
    // Write solution to location
    Path outputDirectoryS3 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS3.get().generateSolutionsToPath(outputDirectoryS3);

  }

  @Test
  public void testQManger() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff" + "/QManager/Employees3.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff" + "/QManager/Employees4.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Compute diff solutions

    // Generate first solution
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 5);
    // Test first solution
    //    testSolution(optS1, 5);
    // Write solution to location
    Path outputDirectoryS1 = Paths.get(
        "target/generated/cddiff-test/diff_" + 5 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());
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
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);
    Path outputDirectoryS2U = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName() + "_unique");
    optS2.get().generateUniqueSolutionsToPath(outputDirectoryS2U);

    for (A4Solution solution : optS2.get().getAllSatSolutions()) {
      System.out.println(solution);
    }

    System.out.println(optS2.get().getNumberOfSatSolutions());

    // Generate third solution
    Optional<AlloyDiffSolution> optS3 = ClassDifference.cddiff(astV2, astV1, 2);
    // Test third solution
    testSolution(optS3, 2);
    // Write solution to location
    Path outputDirectoryS3 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS3.get().generateSolutionsToPath(outputDirectoryS3);

  }

  @Test
  public void testSimilarMangers() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv2.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Run alloy
    // TODO: We do not find witnesses (correct) but we cannot say that the are
    // equal?!
    // Possible Solution: "Syntactic" preprocessing. (Which classes, attributes
    // Generate firs solution
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 2);
    // Test first solution
    testSolution(optS1, 2);
    // Write solution to location
    Path outputDirectoryS1 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());
    // Generate outputs
    optS1.get().generateSolutionsToPath(outputDirectoryS1);

    // Generate second solution
    Optional<AlloyDiffSolution> optS2 = ClassDifference.cddiff(astV2, astV1, 2);
    // Test second solution
    testSolution(optS2, 2);
    // Write solution to location
    Path outputDirectoryS2 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);

    // Test with same input
    Optional<AlloyDiffSolution> optS3 = ClassDifference.cddiff(astV1, astV1, 2);
    // Test second solution
    testSolution(optS3, 2);
    // Write solution to location
    Path outputDirectoryS3 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS3.get().generateSolutionsToPath(outputDirectoryS3);

  }

  /**
   * This should cause a difference with the new semantics only.
   */
  @Test
  public void testRefactoredMangersWithNewSemantics() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/RefactoredManagers/Managers1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/RefactoredManagers/Managers2.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Run alloy
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 1, true,
        "target" + "/generated/cddiff-test");
    // Test first solution
    testSolution(optS1, 2);
    // Write solution to location
    Path outputDirectoryS1 = Paths.get(
        "target/generated/cddiff-test/diff_" + 1 + "_of_" + astV1.getCDDefinition().getName() + "_"
            + astV2.getCDDefinition().getName());
    // Generate outputs
    optS1.get().generateSolutionsToPath(outputDirectoryS1);

    // Generate second solution
    Optional<AlloyDiffSolution> optS2 = ClassDifference.cddiff(astV2, astV1, 1, true,
        "target" + "/generated/cddiff-test");
    // Test second solution
    testSolution(optS2, 2);
    // Write solution to location
    Path outputDirectoryS2 = Paths.get(
        "target/generated/cddiff-test/diff_" + 1 + "_of_" + astV2.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS2.get().generateSolutionsToPath(outputDirectoryS2);
  }

  @Test
  public void testEmptyClasses() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/InterfaceAbstact/abstractCD.cd");
    assertNotNull(astV1);

    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/InterfaceAbstact/interfaceCD.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Run alloy
    // Should find nothing
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV1, astV2, 2);
    // Test first solution
    testSolution(optS1, 2);

    // No solution should be found
    optS1.ifPresent(
        alloyDiffSolution -> assertEquals(0, alloyDiffSolution.getNumberOfSatSolutions()));
  }

  @Test
  public void testEmptyClassComparision() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/InterfaceAbstact/abstractCD.cd");
    assertNotNull(astV1);

    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/InterfaceAbstact/interfaceCD.cd");
    assertNotNull(astV2);

    final ASTCDCompilationUnit astV3 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/InterfaceAbstact/CD.cd");
    assertNotNull(astV3);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Run alloy
    // Should find nothing
    Optional<AlloyDiffSolution> optS1 = ClassDifference.cddiff(astV2, astV1, 2);
    // Test first solution
    testSolution(optS1, 2);

    // No solution should be found
    optS1.ifPresent(
        alloyDiffSolution -> assertEquals(0, alloyDiffSolution.getNumberOfSatSolutions()));

    Optional<AlloyDiffSolution> optS2 = ClassDifference.cddiff(astV3, astV1, 2);
    assertTrue(optS2.isPresent());
    // Write solution to location
    Path outputDirectoryS3 = Paths.get(
        "target/generated/cddiff-test/diff_" + 2 + "_of_" + astV3.getCDDefinition().getName() + "_"
            + astV1.getCDDefinition().getName());
    // Generate outputs
    optS2.get().generateUniqueSolutionsToPath(outputDirectoryS3);
    testSolution(optS2, 2);

    for (A4Solution solution : optS2.get().getAllSatSolutions()) {
      System.out.println(solution);
    }

    System.out.println(optS2.get().getNumberOfSatSolutions());
  }

}
