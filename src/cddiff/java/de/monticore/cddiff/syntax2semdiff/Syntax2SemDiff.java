package de.monticore.cddiff.syntax2semdiff;

import de.monticore.cddiff.alloy2od.Alloy2ODGenerator;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperGenerator;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiffODGenerator;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiffGenerator;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDWrapperSyntaxDiff;
import net.sourceforge.plantuml.Log;

import java.io.File;
import java.util.List;

import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper.printOD;

public class Syntax2SemDiff {
  public static List<ASTODArtifact> computeSemDiff(
      ASTCDCompilationUnit ast1,
      ASTCDCompilationUnit ast2,
      CDSemantics cdSemantics) {

    // generate CDWrapper
    CDWrapperGenerator cd1Generator = new CDWrapperGenerator();
    CDWrapperGenerator cd2Generator = new CDWrapperGenerator();
    CDWrapper cdw1 = cd1Generator.generateCDWrapper(ast1, cdSemantics);
    CDWrapper cdw2 = cd2Generator.generateCDWrapper(ast2, cdSemantics);

    // calculate syntax diff
    CDWrapperSyntaxDiffGenerator cdw2CDDiffGenerator4CDW1WithCDW2 =
        new CDWrapperSyntaxDiffGenerator();
    CDWrapperSyntaxDiff cg = cdw2CDDiffGenerator4CDW1WithCDW2.generateCDSyntaxDiff(cdw1, cdw2, cdSemantics);

    // generate ODs
    CDSyntax2SemDiffODGenerator odGenerator = new CDSyntax2SemDiffODGenerator();
    return odGenerator.generateObjectDiagrams(cdw1, cg, cdSemantics);
  }

  public static String printSemDiff(
      ASTCDCompilationUnit ast1,
      ASTCDCompilationUnit ast2,
      CDSemantics cdSemantics) {

    List<ASTODArtifact> ods1 = computeSemDiff(ast1, ast2, cdSemantics);
    List<ASTODArtifact> ods2 = computeSemDiff(ast2, ast1, cdSemantics);

    StringBuilder result = new StringBuilder();
    if (ods1.size() == 0 && ods2.size() == 0) {
      return "\t ********************************************************************* \n" +
          "\t ************************  Equivalent Semantics ********************** \n" +
          "\t ********************************************************************* \n";
    }
    else {
      result.append("\t ******************************************************************** \n");
      result.append("\t ******************  SemanticDiff from CD1 to CD2  ****************** \n");
      switch (cdSemantics) {
        case SIMPLE_CLOSED_WORLD:
          result.append("\t *********************  in Simple-Closed-World  ********************* \n");
          break;
        case MULTI_INSTANCE_CLOSED_WORLD:
          result.append("\t *****************  in Multi-Instance-Closed-World  ***************** \n");
          break;
        default:
          break;
      }
      result.append("\t ******************************************************************** \n");
      for (ASTODArtifact od : ods1) {
        result.append(printOD(od)).append('\n');
      }
      result.append("\t ******************************************************************** \n");
      result.append("\t ******************  SemanticDiff from CD2 to CD1  ****************** \n");
      switch (cdSemantics) {
        case SIMPLE_CLOSED_WORLD:
          result.append("\t *********************  in Simple-Closed-World  ********************* \n");
          break;
        case MULTI_INSTANCE_CLOSED_WORLD:
          result.append("\t *****************  in Multi-Instance-Closed-World  ***************** \n");
          break;
        default:
          break;
      }
      result.append("\t ******************************************************************** \n");
      for (ASTODArtifact od : ods2) {
        result.append(printOD(od)).append('\n');
      }
    }
    return result.toString();
  }

  public static void printODs2Dir(List<ASTODArtifact> ods, String outputDirectory){
    try {
      File out = new File(outputDirectory);
      for (ASTODArtifact od : ods) {
        String odDesc = printOD(od);
        Alloy2ODGenerator.saveOD(odDesc,od.getObjectDiagram().getName(),out);
      }
    } catch (Exception e){
      e.printStackTrace();
      Log.error("0xCDD10: Could not print ODs to directory " + outputDirectory);
    }
  }

}
