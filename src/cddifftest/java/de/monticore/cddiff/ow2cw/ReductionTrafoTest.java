package de.monticore.cddiff.ow2cw;

import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import de.monticore.ow2cw.ReductionTrafo;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReductionTrafoTest extends AbstractTest {

  @Test
  public void testTrafo(){

    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/RManager"
        + "/Employees5.cd");
    ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/RManager"
        + "/Employees6.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast,m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1= pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2= pprinter.getPrinter().getContent();

    // Set Output Path
    String outputPath = "target/generated/cddiff-test/trafo/";
    Path outputFile1 = Paths.get(outputPath, m1Ast.getCDDefinition().getName() + ".cd");
    Path outputFile2 = Paths.get(outputPath, m2Ast.getCDDefinition().getName() + ".cd");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
      FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testReduction(){
    CD4CodeMill.globalScope().clear();

    String outputPath = "target/generated/cddiff-test/reduction/";

    ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager"
        + "/Employees2.cd");
    ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager"
        + "/Employees1.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast,m2Ast);


    // compute semDiff(m1AST,m2AST)
    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(m1Ast,m2Ast,20,true, outputPath);

    // test if solution is present
    if (!optS.isPresent()) {
      Log.error("0xCDD12: Could not compute semdiff.");
      fail();
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(20);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));

    //no corresponding .od files are generated
    File[] odFiles = Paths.get(outputPath).toFile().listFiles();
    assertNotNull(odFiles);

    List<String> odFilePaths = new LinkedList<>();
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        odFilePaths.add(odFile.toPath().toString());
      }
    }
    assertTrue(odFilePaths.isEmpty());

    // clean-up
    try {
      FileUtils.forceDelete(Paths.get(outputPath).toFile());
    }
    catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", outputPath, e.getMessage()));
    }

  }

  @Test
  public void testReductionWithPackages(){
    CD4CodeMill.globalScope().clear();

    String outputPath = "target/generated/cddiff-test/trafo-with-packages/";

    ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/RQManager"
        + "/Employees8.cd");
    ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/RQManager"
        + "/Employees7.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast,m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1= pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2= pprinter.getPrinter().getContent();

    Path outputFile1 = Paths.get(outputPath, m1Ast.getCDDefinition().getName() + ".cd");
    Path outputFile2 = Paths.get(outputPath, m2Ast.getCDDefinition().getName() + ".cd");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
      FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    outputPath = "target/generated/cddiff-test/reduction-with-packages/";

    // compute semDiff(m1AST,m2AST)
    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(m1Ast,m2Ast,20,true, outputPath);

    // test if solution is present
    if (!optS.isPresent()) {
      Log.error("0xCDD13: Could not compute semdiff.");
      fail();
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(20);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));

    //no corresponding .od files are generated
    File[] odFiles = Paths.get(outputPath).toFile().listFiles();
    assertNotNull(odFiles);

    List<String> odFilePaths = new LinkedList<>();
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        odFilePaths.add(odFile.toPath().toString());
      }
    }
    assertTrue(odFilePaths.isEmpty());

    // clean-up
    try {
      FileUtils.forceDelete(Paths.get(outputPath).toFile());
    }
    catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", outputPath, e.getMessage()));
    }

  }

}
