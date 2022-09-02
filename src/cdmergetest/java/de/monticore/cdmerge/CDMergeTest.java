package de.monticore.cdmerge;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CDMergeTest extends BaseTest{
  @Test
  public void testMerge(){

    final String srcDir = "src/cdmergetest/resources/class_diagrams/General/four_classdiagrams/";

    Set<ASTCDCompilationUnit> inputSet = new HashSet<>();
    inputSet.add(parseModel(srcDir + "A.cd"));
    inputSet.add(parseModel(srcDir + "B.cd"));
    inputSet.add(parseModel(srcDir + "C.cd"));
    inputSet.add(parseModel(srcDir + "D.cd"));

    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet);

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    Assert.assertNotNull(mergedCD);
    mergedCD.accept(pp.getTraverser());
    System.out.println(pp.prettyprint(mergedCD));


  }

  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      //assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

}
