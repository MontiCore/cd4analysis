/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.AddMethodToEnum;
import de.monticore.tf.RefactorCDs;
import java.io.IOException;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by
 *
 * @author KH
 */
public class RefactorCDsTest {

  @BeforeClass
  public static void init() {
    CD4CodeMill.init();
  }

  @Test
  public void testRefactorCDs() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/RefactorCDsValid.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    RefactorCDs refactorCDs = new RefactorCDs(ast.get());

    assertTrue(refactorCDs.doPatternMatching());
    refactorCDs.doReplacement();

    //    System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(ast.get()));

  }

  @Test
  public void testRefactorCDs_Invalid() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/RefactorCDsInvalid.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    RefactorCDs refactorCDs = new RefactorCDs(ast.get());

    assertFalse(refactorCDs.doPatternMatching());

    //    System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(ast.get()));

  }
  // TODO: Fix me
  //  @Test
  //  public void testTranslation() throws IOException {
  //    Optional<ASTTFRule> ast =
  // CD4CodeTRMill.parser().parseTFRule("src/main/transformations/RefactorCDs.mtr");
  //    if(ast.isPresent()){
  //      ASTTFRule rule = ast.get();
  //
  //      ModelTraversal mt = new ModelTraversal();
  //      mt.handle(rule);
  //      Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), mt.getParents());
  //      CD4AnalysisRuleCollectVariablesVisitor variablesVisitor = new
  // CD4AnalysisRuleCollectVariablesVisitor(state);
  //      variablesVisitor.handle(rule);
  //      CD4AnalysisRule2ODVisitor rule2ODVisitor = new CD4AnalysisRule2ODVisitor(state);
  //      rule2ODVisitor.handle(rule);
  //      ASTODRule astod = rule2ODVisitor.getOD();
  //
  //      ODRulesPrettyPrinter pp = new ODRulesPrettyPrinter();
  //      pp.handle(astod);
  //      System.out.println(pp.getPrintedAST());
  //    }
  //  }

  @Test
  public void testAdMethodToEnum() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/Enum.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    AddMethodToEnum refactorCDs = new AddMethodToEnum(ast.get());

    assertTrue(refactorCDs.doPatternMatching());

    refactorCDs.doReplacement();

    //    System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(ast.get()));
  }
}
