package de.monticore.cddiff;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloy2od.Alloy2ODGenerator;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperGenerator;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiffODGenerator;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiffGenerator;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDWrapperSyntaxDiff;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4ASTODHelper.printOD;

public class CDDiff {

  public static List<ASTODArtifact> computeAlloySemDiff(ASTCDCompilationUnit cd1,
      ASTCDCompilationUnit cd2, int diffsize, int difflimit, CDSemantics semantics,
      String outputPathName) {

    // compute AlloyDiffSolution for semdiff(cd1,cd2)
    Optional<AlloyDiffSolution> optSol = AlloyCDDiff.getAlloyDiffSolution(cd1, cd2, diffsize,
        semantics, outputPathName);

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

  public static List<ASTODArtifact> computeSyntax2SemDiff(ASTCDCompilationUnit ast1,
      ASTCDCompilationUnit ast2, CDSemantics cdSemantics) {

    // generate CDWrapper
    CDWrapperGenerator cd1Generator = new CDWrapperGenerator();
    CDWrapperGenerator cd2Generator = new CDWrapperGenerator();
    CDWrapper cdw1 = cd1Generator.generateCDWrapper(ast1, cdSemantics);
    CDWrapper cdw2 = cd2Generator.generateCDWrapper(ast2, cdSemantics);

    // calculate syntax diff
    CDWrapperSyntaxDiffGenerator cdw2CDDiffGenerator4CDW1WithCDW2 =
        new CDWrapperSyntaxDiffGenerator();
    CDWrapperSyntaxDiff cg = cdw2CDDiffGenerator4CDW1WithCDW2.generateCDSyntaxDiff(cdw1, cdw2,
        cdSemantics);

    // generate ODs
    CDSyntax2SemDiffODGenerator odGenerator = new CDSyntax2SemDiffODGenerator();
    List<ASTODArtifact> diffWitnesses = odGenerator.generateObjectDiagrams(cdw1, cg, cdSemantics);

    // join unidirectional links for bidirectional associations
    JoinLinksTrafo joinLinksTrafo = new JoinLinksTrafo(ast1);
    diffWitnesses.forEach(joinLinksTrafo::transform);

    return diffWitnesses;
  }

  public static String printWitnesses2stdout(List<ASTODArtifact> witnesses) {

    StringBuilder result = new StringBuilder();
    result.append("\t *************************  Diff Witnesses  ************************* \n");
    OD4ReportMill.init();
    for (ASTODArtifact od : witnesses) {
      result.append(printOD(od)).append(System.lineSeparator());
    }
    OD4ReportMill.reset();
    return result.toString();
  }

  public static void printODs2Dir(List<ASTODArtifact> ods, String outputDirectory) {
    try {
      File out = new File(outputDirectory);
      OD4ReportMill.init();
      for (ASTODArtifact od : ods) {
        String odDesc = printOD(od);
        Alloy2ODGenerator.saveOD(odDesc, od.getObjectDiagram().getName(), out);
      }
      OD4ReportMill.reset();
    }
    catch (Exception e) {
      e.printStackTrace();
      Log.error("0xCDD10: Could not print ODs to directory " + outputDirectory);
    }
  }

}
