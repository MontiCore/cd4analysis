package de.monticore.cddiff.syndiff;

import static org.junit.Assert.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.cdsyntax2semdiff.DiffHelper;
import de.monticore.cddiff.syntax2semdiff.Syntax2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import java.util.List;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;

public class ComplexityTest extends CDDiffTestBasis {
  @Test
  @Ignore
  public void testRuntime4Performance() {

    String path = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String output = "./target/runtime-test/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 6; i++) {

      filePath1 = path + 20 * i + "A.cd";
      filePath2 = path + 20 * i + "B.cd";
      System.out.println("*******  Test for " + 20 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new2 = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new2 = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      ReductionTrafo.handleAssocDirections(ast1_old, ast2_old);
      Optional<AlloyDiffSolution> optS =
          AlloyCDDiff.getAlloyDiffSolution(ast1_old, ast2_old, 2, cdSemantics, output);
      List<ASTODArtifact> ods_old = optS.get().generateODs();
      long endTime_old = System.currentTimeMillis(); // end time

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time

      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      // List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new,
      // cdSemantics);
      DiffHelper diffHelper = new DiffHelper(ast1_new2, ast2_new2);
      List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness v1 size: " + ods_new.size());
      System.out.println("Runtime of new method v1: " + (endTime_new - startTime_new) + "ms");
      System.out.println("new witness v2 size: " + witnesses.size());
      System.out.println("Runtime of new method v2: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  @Ignore
  public void testRunTime4PerformanceNoLink() {
    String path = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String output = "./target/runtime-test/";

    String filePath1;
    String filePath2;
    for (int i = 2; i <= 6; i++) {

      filePath1 = path + 20 * i + "A_NoLink.cd";
      filePath2 = path + 20 * i + "B_NoLink.cd";
      System.out.println("*******  Test for " + 20 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new2 = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new2 = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      ReductionTrafo.handleAssocDirections(ast1_old, ast2_old);
      Optional<AlloyDiffSolution> optS =
          AlloyCDDiff.getAlloyDiffSolution(ast1_old, ast2_old, 2, cdSemantics, output);
      List<ASTODArtifact> ods_old = optS.get().generateODs();
      long endTime_old = System.currentTimeMillis(); // end time

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time

      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      // List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new,
      // cdSemantics);
      DiffHelper diffHelper = new DiffHelper(ast1_new2, ast2_new2);
      List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness v1 size: " + ods_new.size());
      System.out.println("Runtime of new method v1: " + (endTime_new - startTime_new) + "ms");
      System.out.println("new witness v2 size: " + witnesses.size());
      System.out.println("Runtime of new method v2: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  @Ignore
  public void testRunTime4Performance100() {
    String path = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String output = "./target/runtime-test/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {

      filePath1 = path + "100A_" + i + ".cd";
      filePath2 = path + "100B_" + i + ".cd";
      System.out.println("*******  Test for " + i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new2 = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new2 = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      ReductionTrafo.handleAssocDirections(ast1_old, ast2_old);
      Optional<AlloyDiffSolution> optS =
          AlloyCDDiff.getAlloyDiffSolution(ast1_old, ast2_old, 2, cdSemantics, output);
      List<ASTODArtifact> ods_old = optS.get().generateODs();
      long endTime_old = System.currentTimeMillis(); // end time

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time

      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      // List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new,
      // cdSemantics);
      DiffHelper diffHelper = new DiffHelper(ast1_new2, ast2_new2);
      List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness v1 size: " + ods_new.size());
      System.out.println("Runtime of new method v1: " + (endTime_new - startTime_new) + "ms");
      System.out.println("new witness v2 size: " + witnesses.size());
      System.out.println("Runtime of new method v2: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  public void wrongODs() {
    int n = 0;
    int k = 0;
    String path = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 6; i++) {

      filePath1 = path + 20 * i + "A.cd";
      filePath2 = path + 20 * i + "B.cd";
      System.out.println("*******  Test for " + 20 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time
      for (ASTODArtifact od : ods_new) {
        k++;
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, ast1_new, ast2_new, od)) {
          n++;
        }
      }
    }

    String path2 = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String filePath11;
    String filePath22;
    for (int i = 2; i <= 6; i++) {

      filePath11 = path2 + 20 * i + "A_NoLink.cd";
      filePath22 = path2 + 20 * i + "B_NoLink.cd";
      System.out.println("*******  Test for " + 20 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_new = parseModel(filePath11);
      ASTCDCompilationUnit ast2_new = parseModel(filePath22);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time
      for (ASTODArtifact od : ods_new) {
        k++;
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, ast1_new, ast2_new, od)) {
          n++;
        }
      }
    }

    String path3 = "src/cddifftest/resources/de/monticore/cddiff/Performance/";

    String output = "./target/runtime-test/";

    String filePath111;
    String filePath222;
    for (int i = 1; i <= 5; i++) {

      filePath111 = path3 + "100A_" + i + ".cd";
      filePath222 = path3 + "100B_" + i + ".cd";
      System.out.println("*******  Test for " + i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_new = parseModel(filePath111);
      ASTCDCompilationUnit ast2_new = parseModel(filePath222);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // new method
      long startTime_new = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_new = Syntax2SemDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
      long endTime_new = System.currentTimeMillis(); // end time

      for (ASTODArtifact od : ods_new) {
        k++;
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, ast1_new, ast2_new, od)) {
          n++;
        }
      }
    }
    System.out.println("wrong ods: " + n + " of " + k);
  }
}
