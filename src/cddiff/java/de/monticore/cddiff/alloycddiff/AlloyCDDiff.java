/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffRunner;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloySolutionHandler;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * A collection of functions to compute the difference between class diagrams
 */
public class AlloyCDDiff {

  public static Optional<AlloyDiffSolution> getAlloyDiffSolution(ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2, int k) {
    return getAlloyDiffSolution(cd1, cd2, k, CDSemantics.SIMPLE_CLOSED_WORLD, "target/generated/cddiff-test/");
  }

  public static Optional<AlloyDiffSolution> getAlloyDiffSolution(ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2, int k, CDSemantics semantics, String outputPathName) {
    // Initialize result
    Optional<AlloyDiffSolution> result = Optional.empty();

    // Set output path
    Path outputDirectory = Paths.get(outputPathName,
        cd1.getCDDefinition().getName() + "_" + cd2.getCDDefinition().getName());

    // Generate the module
    Path moduleFile = DiffModuleGenerator.generateDiffPredicateToFile(cd1, cd2, k, semantics,
        outputDirectory.toFile());

    // Run alloy on module
    AlloyDiffRunner diffRunner = new AlloyDiffRunner();
    List<AlloySolutionHandler> results = diffRunner.runAlloy(moduleFile);

    // Test parameters of solution
    if (results.size() == 1) {
      // If correct extract solution object
      result = Optional.of((AlloyDiffSolution) results.get(0));
    }

    // clean-up
    try {
      PathUtils.delete(outputDirectory);
    }
    catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", outputDirectory.getFileName(),
          e.getMessage()));
    }


    return result;
  }

  public static Optional<AlloyDiffSolution> getAlloyDiffSolution(ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2, int k, CDSemantics semantics) {
    // Initialize result
    Optional<AlloyDiffSolution> result = Optional.empty();

    // Generate the module
    String module = DiffModuleGenerator.generateDiffPredicate(cd1, cd2, k, semantics);

    // Run alloy on module
    AlloyDiffRunner diffRunner = new AlloyDiffRunner();
    List<AlloySolutionHandler> results = diffRunner.runAlloy(module);

    // Test parameters of solution
    if (results.size() == 1) {
      // If correct extract solution object
      result = Optional.of((AlloyDiffSolution) results.get(0));
    }


    return result;
  }

}
