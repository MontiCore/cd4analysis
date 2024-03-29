/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cddiff.cd2alloy.generator.OpenWorldGenerator;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * All functions needed to generate an alloy module containing a diff predicate for two class
 * diagrams
 */
public class DiffModuleGenerator {
  /** Helper function to create the diff module predicate. */
  private static String diffPredicateGenerator(
      ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2, CDSemantics semantics) {
    // Create inputs
    Set<ASTCDCompilationUnit> cds = new HashSet<>();
    cds.add(cd1);
    cds.add(cd2);

    String alloyModule;
    // Generate general module
    switch (semantics) {
      case STA_CLOSED_WORLD:
        // code block
        alloyModule = CD2AlloyGenerator.getInstance().generateModule(cds, true);
        break;
      case STA_OPEN_WORLD:
        // code block
        alloyModule = OpenWorldGenerator.getInstance().generateModule(cds, true);
        break;
      default:
        alloyModule = CD2AlloyGenerator.getInstance().generateModule(cds, false);
        // code block
    }

    // Generate diff predicate
    alloyModule += System.lineSeparator();
    alloyModule += System.lineSeparator();
    alloyModule += "pred diff {" + System.lineSeparator();
    alloyModule += cd1.getCDDefinition().getName();
    alloyModule += " and not ";
    alloyModule += cd2.getCDDefinition().getName();
    alloyModule += System.lineSeparator() + "}" + System.lineSeparator();
    alloyModule += System.lineSeparator();

    return alloyModule;
  }

  /**
   * Generates alloy module to compare the class diagram cd1 with cd2 using scope k (maximal number
   * of objects in OD)
   *
   * @param cd1 Class diagram which is used as base
   * @param cd2 Class diagram the base is compared to
   * @param k Scope for the execution of the alloy module
   * @return String for an alloy module comparing cd1 and cd2
   */
  public static String generateDiffPredicate(
      ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2, int k, CDSemantics semantics) {
    // Create module
    String alloyModule = diffPredicateGenerator(cd1, cd2, semantics);

    // Add run command for predicate with k as object limit
    alloyModule += "run diff for " + k + System.lineSeparator();

    return alloyModule;
  }

  public static Path generateDiffPredicateToFile(
      ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2,
      int k,
      CDSemantics semantics,
      File outputDirectory) {

    // Initialize set of asts
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(cd1);
    asts.add(cd2);

    // Generate the name of the module
    String moduleName = CD2AlloyGenerator.getInstance().generateModuleName(asts);

    // Generate module
    String module = generateDiffPredicate(cd1, cd2, k, semantics);

    // Save module in file

    return CD2AlloyGenerator.getInstance().saveModulePath(module, moduleName, outputDirectory);
  }
}
