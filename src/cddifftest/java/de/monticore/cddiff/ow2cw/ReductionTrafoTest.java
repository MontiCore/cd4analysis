package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import de.monticore.ow2cw.ReductionTrafo;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReductionTrafoTest extends AbstractTest {

  @Test
  public void testTrafo() {

    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit m1Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees5.cd");
    ASTCDCompilationUnit m2Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees6.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast, m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

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
  public void testTrafoWithPackages() {
    CD4CodeMill.globalScope().clear();

    String outputPath = "target/generated/cddiff-test/trafo-with-packages/";

    ASTCDCompilationUnit m1Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");
    ASTCDCompilationUnit m2Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast, m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

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

}
