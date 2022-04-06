package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import de.monticore.ow2cw.ReductionTrafo;
import net.sourceforge.plantuml.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ReductionTrafoTest extends AbstractTest {

  @Test
  public void testResolve(){

    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd");
    ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast,m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1= pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2= pprinter.getPrinter().getContent();

    // Set Output Path
    String outputPath = "target/generated/cddiff-test/reduction/";
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
