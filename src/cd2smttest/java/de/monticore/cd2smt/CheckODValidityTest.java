package de.monticore.cd2smt;

import static de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData.Strategy.SE;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
  public void checkODValidityTestDS_O2O(String CDFileName) {
    checkODValidity(
        CDFileName,
        "ds/one2one/",
        ClassStrategy.Strategy.DS,
        AssociationStrategy.Strategy.ONE2ONE,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestDS_DEFAULT(String CDFileName) {
    checkODValidity(
        CDFileName,
        "ds/default/",
        ClassStrategy.Strategy.DS,
        AssociationStrategy.Strategy.DEFAULT,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSS_DEFAULT(String fileName) {
    checkODValidity(
        fileName,
        "ss/default",
        ClassStrategy.Strategy.SS,
        AssociationStrategy.Strategy.DEFAULT,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSS_O2O(String fileName) {
    checkODValidity(
        fileName,
        "ss/one2one",
        ClassStrategy.Strategy.SS,
        AssociationStrategy.Strategy.ONE2ONE,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSECOMB_DEFAULT(String fileName) {

    checkODValidity(
        fileName,
        "seComb/default",
        ClassStrategy.Strategy.SSCOMB,
        AssociationStrategy.Strategy.DEFAULT,
        SE);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSECOMB_O2O(String fileName) {

    checkODValidity(
        fileName,
        "seComb/one2one",
        ClassStrategy.Strategy.SSCOMB,
        AssociationStrategy.Strategy.ONE2ONE,
        SE);
  }

  public void checkODValidity(
      String CDFileName,
      String targetNumber,
      ClassStrategy.Strategy cs,
      AssociationStrategy.Strategy as,
      InheritanceData.Strategy is) {

    ASTCDCompilationUnit ast = parseModel(CDFileName);
    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();

    cd2SMTGenerator.setClassStrategy(cs);
    cd2SMTGenerator.setAssociationStrategy(as);
    cd2SMTGenerator.setInheritanceStrategy(is);
    cd2SMTGenerator.cd2smt(ast, ctx);
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();

    if (cs == ClassStrategy.Strategy.SS || cs == ClassStrategy.Strategy.SSCOMB) {
      constraints.add(typeNotEmpty(cd2SMTGenerator, ast.getCDDefinition()));
    }

    Solver solver = cd2SMTGenerator.makeSolver(constraints);
    Assertions.assertEquals(Status.SATISFIABLE, solver.check());
    String odName = CDFileName.split("\\.")[0];
    Model model = solver.getModel();
    Optional<ASTODArtifact> optOd = cd2SMTGenerator.smt2od(model, false, odName);
    Assertions.assertTrue(optOd.isPresent());

    printOD(optOd.get(), targetNumber);
    Assertions.assertTrue(
        matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, optOd.get(), ast));
  }

  public IdentifiableBoolExpr typeNotEmpty(CD2SMTGenerator cd2SMTGenerator, ASTCDDefinition cd) {
    Sort Object = cd2SMTGenerator.getSort(cd.getCDClassesList().get(0));
    Expr<? extends Sort> expr = ctx.mkConst("obj", Object);
    BoolExpr constr = ctx.mkTrue();
    for (ASTCDType astcdType : CDHelper.getASTCDTypes(cd)) {
      BoolExpr subRes =
          ctx.mkExists(
              new Expr[] {expr},
              cd2SMTGenerator.filterObject(expr, astcdType),
              0,
              null,
              null,
              null,
              null);
      constr = ctx.mkAnd(subRes, constr);
    }
    return IdentifiableBoolExpr.buildIdentifiable(
        constr, cd.get_SourcePositionStart(), Optional.of("Type_not_empty"));
  }
}
