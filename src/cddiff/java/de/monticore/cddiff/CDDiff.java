/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4ASTODHelper.printOD;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloy2od.Alloy2ODGenerator;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syntax2semdiff.Syntax2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CDDiff {

  /**
   * Computes the semantic difference between ast1 and ast2 by applying Syntax2SemDiff and prints
   * the resulting diff-witness(es) to stdout or the specified directory. Applies open-to-closed
   * world reduction to enable open-world diff. todo: open-world diff does not work because
   * Syntax2SemDiff is currently incompatible
   */
  public static void computeRuleBasedSemDiff(
      ASTCDCompilationUnit ast1,
      ASTCDCompilationUnit ast2,
      String outputPath,
      boolean openWorld,
      boolean toDir)
      throws NumberFormatException, IOException {
    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(ast1, ast2);

      if (toDir) {
        CDDiffUtil.saveDiffCDs2File(ast1, ast2, outputPath);
      }
      semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    }

    if (toDir) {
      CDDiff.printODs2Dir(CDDiff.computeSyntax2SemDiff(ast1, ast2, semantics), outputPath);
    } else {
      Log.print(CDDiff.printWitnesses2stdout(CDDiff.computeSyntax2SemDiff(ast1, ast2, semantics)));
    }
  }

  /**
   * Computes the semantic difference between ast1 and ast2 by applying AlloySemDiff and prints the
   * resulting diff-witness(es) to stdout or the specified directory.
   */
  public static void computeSemDiff(
      ASTCDCompilationUnit ast1,
      ASTCDCompilationUnit ast2,
      String outputPath,
      int diffsize,
      int difflimit,
      boolean openWorld,
      boolean reductionBased,
      boolean toDir)
      throws NumberFormatException, IOException {

    CDSemantics semantics = CDSemantics.SIMPLE_CLOSED_WORLD;

    // determine if open-world should be applied
    if (openWorld) {

      // determine which method should be used to compute the diff-witnesses
      if (reductionBased) {
        ReductionTrafo trafo = new ReductionTrafo();
        trafo.transform(ast1, ast2);

        // reparse to fix source-positions
        ast1 = CDDiffUtil.reparseCD(ast1);
        ast2 = CDDiffUtil.reparseCD(ast2);

        if (toDir) {
          CDDiffUtil.saveDiffCDs2File(ast1, ast2, outputPath);
        }
        semantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
      } else {

        semantics = CDSemantics.MULTI_INSTANCE_OPEN_WORLD;

        // handle unspecified association directions for open-world
        ReductionTrafo.handleAssocDirections(ast1, ast2);

        // add subclasses to interfaces and abstract classes
        ReductionTrafo.addSubClasses4Diff(ast1);
        ReductionTrafo.addSubClasses4Diff(ast2);

        // add dummy-class for associations
        String dummyClassName = "Dummy4Diff";
        ReductionTrafo.addDummyClass4Associations(ast1, dummyClassName);
        ReductionTrafo.addDummyClass4Associations(ast2, dummyClassName);
      }
    } else {
      // handle unspecified association directions for closed-world
      ReductionTrafo.handleAssocDirections(ast1, ast2);
    }

    if (toDir) {
      CDDiff.printODs2Dir(
          CDDiff.computeAlloySemDiff(ast1, ast2, diffsize, difflimit, semantics), outputPath);
    } else {
      Log.print(
          CDDiff.printWitnesses2stdout(
              CDDiff.computeAlloySemDiff(ast1, ast2, diffsize, difflimit, semantics)));
    }
  }

  public static int getDefaultDiffsize(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2) {
    int diffsize;
    int cd1size =
        ast1.getCDDefinition().getCDClassesList().size()
            + ast1.getCDDefinition().getCDInterfacesList().size();

    int cd2size =
        ast2.getCDDefinition().getCDClassesList().size()
            + ast2.getCDDefinition().getCDInterfacesList().size();

    diffsize = 2 * Math.max(cd1size, cd2size);
    return diffsize;
  }

  /**
   * Computes the semantic difference between cd1 and cd2 via translation to Alloy.
   *
   * @param diffsize defines the search space for alloy (number of objects in witness)
   * @param difflimit defines the maximum number of witnesses that should be computed
   * @param semantics can be: SIMPLE_CLOSED_WORLD, MULTI_INSTANCE_CLOSED_WORLD,
   *     MULTI_INSTANCE_OPEN_WORLD
   * @return list of diff-witnesses
   */
  public static List<ASTODArtifact> computeAlloySemDiff(
      ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2,
      int diffsize,
      int difflimit,
      CDSemantics semantics) {

    // compute AlloyDiffSolution for semdiff(cd1,cd2)
    Optional<AlloyDiffSolution> optSol =
        AlloyCDDiff.getAlloyDiffSolution(cd1, cd2, diffsize, semantics);

    // test if solution is present
    if (optSol.isEmpty()) {
      Log.error("0xCDD01: Could not compute semdiff.");
      return new ArrayList<>();
    }
    AlloyDiffSolution sol = optSol.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(difflimit);
    sol.setLimited(true);

    // generate ODs
    List<ASTODArtifact> diffWitnesses = sol.generateODs();

    // join unidirectional links for bidirectional associations
    JoinLinksTrafo joinLinksTrafo = new JoinLinksTrafo(cd1);
    diffWitnesses.forEach(joinLinksTrafo::transform);

    return diffWitnesses;
  }

  /**
   * Computes the semantic difference between cd1 and cd2 via syntax analysis.
   *
   * @param cdSemantics can be: SIMPLE_CLOSED_WORLD, MULTI_INSTANCE_CLOSED_WORLD
   * @return list of diff-witnesses
   */
  public static List<ASTODArtifact> computeSyntax2SemDiff(
      ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2, CDSemantics cdSemantics) {

    List<ASTODArtifact> diffWitnesses = Syntax2SemDiff.computeSemDiff(ast1, ast2, cdSemantics);

    // join unidirectional links for bidirectional associations
    JoinLinksTrafo joinLinksTrafo = new JoinLinksTrafo(ast1);
    diffWitnesses.forEach(joinLinksTrafo::transform);

    return diffWitnesses;
  }

  public static String printWitnesses2stdout(List<ASTODArtifact> witnesses) {

    StringBuilder result = new StringBuilder();
    if (witnesses.isEmpty()) {
      result.append("\t ************************ No Diff Witnesses  ************************ \n");
    } else {
      result.append("\t *************************  Diff Witnesses  ************************* \n");
      for (ASTODArtifact od : witnesses) {
        result.append(printOD(od)).append(System.lineSeparator());
      }
    }
    return result.toString();
  }

  public static void printODs2Dir(List<ASTODArtifact> ods, String outputDirectory) {
    try {
      File out = new File(outputDirectory);
      for (ASTODArtifact od : ods) {
        String odDesc = printOD(od);
        Alloy2ODGenerator.saveOD(odDesc, od.getObjectDiagram().getName(), out);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.error("0xCDD10: Could not print ODs to directory " + outputDirectory);
    }
  }
}
