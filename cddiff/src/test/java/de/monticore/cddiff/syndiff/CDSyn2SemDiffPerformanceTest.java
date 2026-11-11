/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.odbasis._ast.ASTODArtifact;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CDSyn2SemDiffPerformanceTest extends CDDiffTestBasis {
  
  @Test
  @Disabled
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
      List<ASTODArtifact> ods_old = CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 5, 1,
          cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 1, 5, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time
      
      System.out.println("Number of witnesses of Alloy-based CDDiff: " + ods_old.size());
      System.out.println("Runtime of of Alloy-based CDDiff: " + (endTime_old - startTime_old)
          + "ms");
      System.out.println("Number of witnesses of CDSyn2SemDiff-based CDDiff: " + witnesses.size());
      System.out.println("Runtime of CDSyn2SemDiff-based CDDiff: " + (endTime_new2 - startTime_new2)
          + "ms");
    }
  }
  
  @Test
  @Disabled
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
      List<ASTODArtifact> ods_old = CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 10, 5,
          cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 5, 10, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time
      
      System.out.println("Number of witnesses of Alloy-based CDDiff: " + ods_old.size());
      System.out.println("Runtime of of Alloy-based CDDiff: " + (endTime_old - startTime_old)
          + "ms");
      System.out.println("Number of witnesses of CDSyn2SemDiff-based CDDiff: " + witnesses.size());
      System.out.println("Runtime of CDSyn2SemDiff-based CDDiff: " + (endTime_new2 - startTime_new2)
          + "ms");
    }
  }
  
  @Test
  @Disabled
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
      List<ASTODArtifact> ods_old = CDDiff.computeAlloySemDiff(ast1_old, ast2_old, 15, 5,
          cdSemantics);
      long endTime_old = System.currentTimeMillis(); // end time
      // new method
      long startTime_new2 = System.currentTimeMillis(); // start time
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_new, ast2_new, 5, 15, false);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
      long endTime_new2 = System.currentTimeMillis(); // end time
      
      System.out.println("Number of witnesses of Alloy-based CDDiff: " + ods_old.size());
      System.out.println("Runtime of of Alloy-based CDDiff: " + (endTime_old - startTime_old)
          + "ms");
      System.out.println("Number of witnesses of CDSyn2SemDiff-based CDDiff: " + witnesses.size());
      System.out.println("Runtime of CDSyn2SemDiff-based CDDiff: " + (endTime_new2 - startTime_new2)
          + "ms");
    }
  }
  
  @Test
  @Disabled
  public void testOpenWorldPerformance() {
    String path = "src/test/resources/validation/Performance/";
    String filePath1;
    String filePath2;
    for (int i = 1; i <= 5; i++) {
      filePath1 = path + 5 * i + "A.cd";
      filePath2 = path + 5 * i + "B.cd";
      System.out.println("*******  Test for " + 5 * i + "  *******");
      
      CDSemantics cdSemantics = CDSemantics.STA_OPEN_WORLD;
      ASTCDCompilationUnit ast1 = parseModel(filePath1);
      ASTCDCompilationUnit ast2 = parseModel(filePath2);
      assertNotNull(ast1);
      assertNotNull(ast2);
      
      // old method
      long startTime_alloy = System.currentTimeMillis(); // start time
      List<ASTODArtifact> ods_alloy = CDDiff.computeAlloySemDiff(ast1.deepClone(), ast2.deepClone(),
          10, 1, CDSemantics.STA_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time
      
      // old method
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo1 = new ReductionTrafo();
      ASTCDCompilationUnit ast1_tr1 = ast1.deepClone();
      ASTCDCompilationUnit ast2_tr1 = ast2.deepClone();
      trafo1.transform(ast1_tr1, ast2_tr1);
      List<ASTODArtifact> ods_old = CDDiff.computeAlloySemDiff(ast1_tr1, ast2_tr1, 10, 1,
          CDSemantics.STA_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time
      // new method
      long startTime_Syn2SemDiff = System.currentTimeMillis(); // start time
      ReductionTrafo trafo2 = new ReductionTrafo();
      ASTCDCompilationUnit ast1_tr2 = ast1.deepClone();
      ASTCDCompilationUnit ast2_tr2 = ast2.deepClone();
      trafo2.transform(ast1_tr2, ast2_tr2);
      Syn2SemDiff syn2semdiff = new Syn2SemDiff(ast1_tr2, ast2_tr2, 1, 10, true);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
      long endTime_Syn2SemDiff = System.currentTimeMillis(); // end time
      
      System.out.println("Number of witnesses of purely Alloy-based method: " + ods_alloy.size());
      System.out.println("Runtime of of purely Alloy-based method: " + (endTime_alloy
          - startTime_alloy) + "ms");
      System.out.println(
          "Number of witnesses size of Reduction-based method with Alloy-based CDDiff: " + ods_old
              .size());
      System.out.println("Runtime of Reduction-based method with Alloy-based CDDiff: "
          + (endTime_reduction - startTime_reduction) + "ms");
      System.out.println("Number of witnesses size of Reduction-based method with CDSyn2SemDiff: "
          + witnesses.size());
      System.out.println("Reduction of Reduction-based method with CDSyn2SemDiff: "
          + (endTime_Syn2SemDiff - startTime_Syn2SemDiff) + "ms");
    }
  }
  
}
