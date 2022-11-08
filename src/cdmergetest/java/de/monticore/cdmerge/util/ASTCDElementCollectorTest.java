/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ASTCDElementCollectorTest extends BaseTest {

  public static final String INPUT_MODEL_FILE = "General/university/Staff.cd";

  public final ASTCDCompilationUnit cd;

  public final ASTCDElementCollector testant;

  public final ASTCDHelper helper;

  public ASTCDElementCollectorTest() throws IOException, MergingException {
    this.cd = loadModel(Paths.get(MODEL_PATH, INPUT_MODEL_FILE).toString());
    this.helper = new ASTCDHelper(this.cd);
    this.testant = new ASTCDElementCollector(helper);
  }

  @Test
  public void test() {

    // The Element collector does not change the result

    // Get PrettyPrinter
    IndentPrinter printer = new IndentPrinter();
    CD4CodeFullPrettyPrinter visitor = new CD4CodeFullPrettyPrinter(printer);

    String oldCD = visitor.prettyprint(cd);

    testant.collect(cd);

    assertTrue(oldCD.equals(visitor.prettyprint(cd)));
  }

}
