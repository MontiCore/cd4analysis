package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CheckODValidityTest extends CD2SMTAbstractTest {

  protected final OD2CDMatcher matcher = new OD2CDMatcher();

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    ctx = new Context(cfg);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestDS(String CDFileName, String targetNumber) {
    checkODValidity(CDFileName, "DS" + targetNumber, ClassStrategy.Strategy.DS);
  }

  @Disabled("Test is flaky: Sometimes fails or takes too much time.")
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSS(String CDFileName, String targetNumber) {
    Assumptions.assumeFalse(CDFileName.equals("car3.cd")); // too much time
    Assumptions.assumeFalse(CDFileName.equals("car10.cd")); // too much time
    Assumptions.assumeFalse(CDFileName.equals("car19.cd")); // too much time
    Assumptions.assumeFalse(CDFileName.equals("car16.cd")); // too much time
    Assumptions.assumeFalse(CDFileName.equals("car20.cd")); // don't support enum yet

    checkODValidity(CDFileName, "SS" + targetNumber, ClassStrategy.Strategy.SS);
  }

  @Test
  public void testAbstract() {
    checkODValidity("car3.cd", "300000", ClassStrategy.Strategy.DS);
  }

  public void checkODValidity(
      String CDFileName, String targetNumber, ClassStrategy.Strategy strategy) {

    ASTCDCompilationUnit ast = parseModel(CDFileName);
    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();

    cd2SMTGenerator.setClassStrategy(strategy);
    cd2SMTGenerator.cd2smt(ast, ctx);

    Solver solver = cd2SMTGenerator.makeSolver(new ArrayList<>());
    Assertions.assertEquals(Status.SATISFIABLE, solver.check());

    Optional<ASTODArtifact> optOd = cd2SMTGenerator.smt2od(solver.getModel(), false, "MyOD");
    Assertions.assertTrue(optOd.isPresent());

    printOD(optOd.get(), targetNumber);
    Assertions.assertTrue(
        matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, optOd.get(), ast));
  }
}
