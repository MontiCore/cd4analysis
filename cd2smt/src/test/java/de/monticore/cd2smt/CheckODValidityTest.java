package de.monticore.cd2smt;

import static de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData.Strategy.SE;

import com.microsoft.z3.*;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
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
import de.se_rwth.commons.logging.LogStub;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CheckODValidityTest extends CD2SMTAbstractTest {

  protected final OD2CDMatcher matcher = new OD2CDMatcher();

  @BeforeEach
  public void setup() {
    LogStub.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());

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

  @Disabled
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

  @Disabled
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestSECOMB_O2O(String fileName) {
    // TODO: 03.09.23  figure out why  we get a timeout
    checkODValidity(
        fileName,
        "seComb/one2one",
        ClassStrategy.Strategy.SSCOMB,
        AssociationStrategy.Strategy.ONE2ONE,
        SE);
  }
  /****************************finite strategies*******************************************/
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestFiniteDS_O2O(String CDFileName) {

    checkODValidityFinite(
        CDFileName,
        "finiteDs/one2one/",
        ClassStrategy.Strategy.FINITEDS,
        AssociationStrategy.Strategy.ONE2ONE,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestFiniteDS_DEFAULT(String CDFileName) {
    checkODValidityFinite(
        CDFileName,
        "ds/default/",
        ClassStrategy.Strategy.FINITEDS,
        AssociationStrategy.Strategy.DEFAULT,
        InheritanceData.Strategy.ME);
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestFiniteSS_O2O(String CDFileName) {

    checkODValidityFinite(
        CDFileName,
        "finiteSs/one2one/",
        ClassStrategy.Strategy.FINITESS,
        AssociationStrategy.Strategy.ONE2ONE,
        InheritanceData.Strategy.ME);
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void checkODValidityTestFiniteSS_DEFAULT(String CDFileName) {

    checkODValidityFinite(
        CDFileName,
        "finiteSs/default/",
        ClassStrategy.Strategy.FINITESS,
        AssociationStrategy.Strategy.DEFAULT,
        InheritanceData.Strategy.ME);
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void CDInitializerTestFiniteDS(String fileName) {
    cd2smt(fileName, ClassStrategy.Strategy.FINITEDS, "CDInitializer/DS/default");
  }

  @ParameterizedTest
  @MethodSource("modelTarget")
  public void CDInitializerTestFiniteSS(String fileName) {
    cd2smt(fileName, ClassStrategy.Strategy.FINITESS, "CDInitializer/DS/default");
  }

  public void cd2smt(String fileName, ClassStrategy.Strategy cs, String outputDir) {
    ASTCDCompilationUnit ast = parseModel(fileName);

    Stream<Map<ASTCDType, Integer>> res = CDTypeInitializer.initialize(ast, 10, true).limit(10);

    AtomicInteger i = new AtomicInteger();
    res.forEach(
        cardinalities -> {
          i.getAndIncrement();
          CD2SMTMill.setCardinalities(cardinalities);
          checkODValidityFinite(
              fileName,
              outputDir + i + "/",
              cs,
              AssociationStrategy.Strategy.DEFAULT,
              InheritanceData.Strategy.ME);
        });
  }

  public void checkODValidityFinite(
      String cdFileName,
      String targetName,
      ClassStrategy.Strategy cs,
      AssociationStrategy.Strategy as,
      InheritanceData.Strategy is) {

    ASTCDCompilationUnit ast = parseModel(cdFileName);

    Stream<Map<ASTCDType, Integer>> res = CDTypeInitializer.initialize(ast, 10, true).limit(10);
    for (Map<ASTCDType, Integer> cardinalities : res.collect(Collectors.toSet())) {
      CD2SMTMill.setCardinalities(cardinalities);
      checkODValidity(ast, targetName, cdFileName, cs, as, is);
    }
  }

  public void checkODValidity(
      String cdFileName,
      String targetName,
      ClassStrategy.Strategy cs,
      AssociationStrategy.Strategy as,
      InheritanceData.Strategy is) {
    ASTCDCompilationUnit ast = parseModel(cdFileName);
    checkODValidity(ast, targetName, cdFileName.split("\\.")[0] + targetName, cs, as, is);
  }

  public void checkODValidity(
      ASTCDCompilationUnit ast,
      String targetName,
      String odName,
      ClassStrategy.Strategy cs,
      AssociationStrategy.Strategy as,
      InheritanceData.Strategy is) {

    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator(cs, is, as);

    cd2SMTGenerator.cd2smt(ast, ctx);
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();

    if (cs == ClassStrategy.Strategy.SS || cs == ClassStrategy.Strategy.SSCOMB) {
      constraints.add(typeNotEmpty(cd2SMTGenerator, ast.getCDDefinition()));
    }

    Solver solver = cd2SMTGenerator.makeSolver(constraints);

    Assertions.assertEquals(Status.SATISFIABLE, solver.check());

    Model model = solver.getModel();
    Optional<ASTODArtifact> optOd = cd2SMTGenerator.smt2od(model, false, odName);
    Assertions.assertTrue(optOd.isPresent());

    printOD(optOd.get(), targetName);
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
