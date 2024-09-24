/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import static org.junit.Assert.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.MergeParameter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CDMergeTest extends BaseTest {
  @Test
  public void testMerge() {

    final String srcDir = "src/test/resources/class_diagrams/CDMergeTest/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "A.cd"));
      inputSet.add(loadModel(srcDir + "B.cd"));
      inputSet.add(loadModel(srcDir + "C.cd"));
    } catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "ABC", new HashSet<>());

    Assert.assertNotNull(mergedCD);
    System.out.println(CD4CodeMill.prettyPrint(mergedCD, true));
  }

  @Test
  public void testMotivatingExample() {
    final String srcDir = "src/test/resources/class_diagrams/CDMergeTest/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "Teaching.cd"));
      inputSet.add(loadModel(srcDir + "Management.cd"));
    } catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }

    HashSet<MergeParameter> params = new HashSet<>();

    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "UniversitySystem", params);

    Assert.assertNotNull(mergedCD);
    System.out.println(CD4CodeMill.prettyPrint(mergedCD, true));
  }

  @Test
  public void testUMLPExample() {
    final String srcDir = "src/test/resources/class_diagrams/umlp/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    ASTCDCompilationUnit expected = null;
    try {
      expected = loadModel(srcDir + "MergeDriveAndEmployment.umlp");
      inputSet.add(loadModel(srcDir + "Driver.umlp"));
      inputSet.add(loadModel(srcDir + "Employment.umlp"));
    } catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }

    HashSet<MergeParameter> params = new HashSet<>();

    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "MergeDriveAndEmployment", params);

    Assert.assertNotNull(mergedCD);
    System.out.println(CD4CodeMill.prettyPrint(mergedCD, true));
    Assert.assertTrue(mergedCD.deepEquals(expected, false));
  }
}
