/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff.classDifference;

import de.monticore.cddiff.alloycddiff.alloyGenerator.DiffModuleGenerator;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffRunner;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloySolutionHandler;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * A collection of functions to compute the difference between class diagrams
 *
 */
public class ClassDifference {

  public static Optional<AlloyDiffSolution> cddiff(ASTCDCompilationUnit cd1,
                                                             ASTCDCompilationUnit cd2,
                                                             int k) {
    return cddiff(cd1,cd2,k,"gen/");
  }
  
  public static Optional<AlloyDiffSolution> cddiff(ASTCDCompilationUnit cd1,
                                                   ASTCDCompilationUnit cd2,
                                                   int k, String outputPathName) {
    // Initialize result
    Optional<AlloyDiffSolution> result = Optional.empty();
    
    // Set output path
    Path outputDirectory = Paths
        .get(outputPathName,
            cd1.getCDDefinition().getName() + "_" + cd2.getCDDefinition().getName());
    
    // Generate the module
    Path moduleFile = DiffModuleGenerator.generateDiffPredicateToFile(cd1, cd2, k,
        outputDirectory.toFile());
    
    // Run alloy on module
    AlloyDiffRunner diffRunner = new AlloyDiffRunner();
    List<AlloySolutionHandler> results = diffRunner.runAlloy(moduleFile);
    
    // Test parameters of solution
    if (results.size() == 1) {
      // If correct extract solution object
      result = Optional.of((AlloyDiffSolution) results.get(0));
    }
    
    return result;
  }

}
