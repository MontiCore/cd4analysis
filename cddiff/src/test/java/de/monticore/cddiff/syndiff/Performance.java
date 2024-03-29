package de.monticore.cddiff.syndiff;

import static org.junit.Assert.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class Performance extends CDDiffTestBasis {

  @Test
  @Ignore
  public void test() {
    String path = "src/test/resources/validation/Performance/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {
      filePath1 = path + 5 * i + "A.cd";
      filePath2 = path + 5 * i + "B.cd";
      System.out.println("*******  Test for " + 5 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_old =
          CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 5, 1, cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 1, 5, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness size: " + witnesses.size());
      System.out.println("Runtime of new method: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  @Ignore
  public void test10() {
    String path = "src/test/resources/validation/Performance/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {
      filePath1 = path + 5 * i + "A.cd";
      filePath2 = path + 5 * i + "B.cd";
      System.out.println("*******  Test for " + 5 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_old =
          CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 10, 5, cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 5, 10, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness size: " + witnesses.size());
      System.out.println("Runtime of new method: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  @Ignore
  public void test15() {
    String path = "src/test/resources/validation/Performance/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {
      filePath1 = path + 5 * i + "A.cd";
      filePath2 = path + 5 * i + "B.cd";
      System.out.println("*******  Test for " + 5 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_old =
          CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 15, 5, cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 5, 15, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness size: " + witnesses.size());
      System.out.println("Runtime of new method: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }

  @Test
  @Ignore
  public void testOpenW() {
    String path = "src/test/resources/validation/Performance/";

    String output = "./target/runtime-test/";

    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {
      filePath1 = path + 5 * i + "A.cd";
      filePath2 = path + 5 * i + "B.cd";
      System.out.println("*******  Test for " + 5 * i + "  *******");

      CDSemantics cdSemantics = CDSemantics.STA_OPEN_WORLD;
      ASTCDCompilationUnit ast1_old = parseModel(filePath1);
      ASTCDCompilationUnit ast2_old = parseModel(filePath2);
      ASTCDCompilationUnit ast1_new = parseModel(filePath1);
      ASTCDCompilationUnit ast2_new = parseModel(filePath2);
      assertNotNull(ast1_old);
      assertNotNull(ast2_old);
      assertNotNull(ast1_new);
      assertNotNull(ast2_new);

      // old method
      long startTime_old = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_old =
          CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 15, 5, CDSemantics.STA_OPEN_WORLD);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(ast1_new, ast2_new);
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 5, 15, true);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
      long endTime_new2 = System.currentTimeMillis(); // end time

      System.out.println("old witness size: " + ods_old.size());
      System.out.println("Runtime of old method: " + (endTime_old - startTime_old) + "ms");
      System.out.println("new witness size: " + witnesses.size());
      System.out.println("Runtime of new method: " + (endTime_new2 - startTime_new2) + "ms");
    }
  }
}
