package de.monticore.cdmerge;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CDMergeTest extends BaseTest {
  @Test
  public void testMerge() {

    final String srcDir = "src/cdmergetest/resources/class_diagrams/CDMergeTest/";

    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "A.cd"));
      inputSet.add(loadModel(srcDir + "B.cd"));
      inputSet.add(loadModel(srcDir + "C.cd"));
    } catch (IOException e) {
      fail("IO exception whie accessing input models: " + e.getMessage());
    }

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet);

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    Assert.assertNotNull(mergedCD);
    mergedCD.accept(pp.getTraverser());
    System.out.println(pp.prettyprint(mergedCD));


  }


}
