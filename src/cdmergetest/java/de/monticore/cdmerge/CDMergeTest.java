package de.monticore.cdmerge;

import static org.junit.Assert.fail;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class CDMergeTest extends BaseTest {
  @Test
  public void testMerge() {

    final String srcDir = "src/cdmergetest/resources/class_diagrams/CDMergeTest/";
    Set<ASTCDCompilationUnit> inputSet = new HashSet<>();
    try {
      inputSet.add(loadModel(srcDir + "A.cd"));
      inputSet.add(loadModel(srcDir + "B.cd"));
      inputSet.add(loadModel(srcDir + "C.cd"));
    } catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet);

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    Assert.assertNotNull(mergedCD);
    mergedCD.accept(pp.getTraverser());
    System.out.println(pp.prettyprint(mergedCD));
  }
}
