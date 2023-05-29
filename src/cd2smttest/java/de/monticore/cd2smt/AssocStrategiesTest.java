/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class AssocStrategiesTest extends CDDiffTestBasis {
  protected final String RELATIVE_MODEL_PATH = "src/cd2smttest/resources/de/monticore/cd2smt";

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();
  }

  @Test
  public void testOptionalCard() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    Context ctx = new Context(cfg);
    ASTCDCompilationUnit ast = parseModel(RELATIVE_MODEL_PATH + "/assocStrategies/Optional.cd");
    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();
    cd2SMTGenerator.initDefaultStrategies();
    cd2SMTGenerator.cd2smt(ast, ctx);
    ASTCDType ClassCar = CDHelper.getClass("Car", ast.getCDDefinition());
    ASTCDType MotorClass = CDHelper.getClass("Motor", ast.getCDDefinition());
    Sort Motor = cd2SMTGenerator.getSort(MotorClass);
    Sort Car = cd2SMTGenerator.getSort(ClassCar);
    Expr<? extends Sort> motor1 = ctx.mkConst("motor1", Motor);
    Expr<? extends Sort> motor2 = ctx.mkConst("motor2", Motor);
    Expr<? extends Sort> car = ctx.mkConst("car", Car);

    BoolExpr two =
        ctx.mkAnd(
            ctx.mkNot(ctx.mkEq(motor1, motor2)),
            cd2SMTGenerator.evaluateLink(
                ast.getCDDefinition().getCDAssociationsList().get(0),
                ClassCar,
                MotorClass,
                car,
                motor2),
            cd2SMTGenerator.evaluateLink(
                ast.getCDDefinition().getCDAssociationsList().get(0),
                ClassCar,
                MotorClass,
                car,
                motor1));

    Solver solver =
        cd2SMTGenerator.makeSolver(
            List.of(IdentifiableBoolExpr.buildIdentifiable(two, null, Optional.of("Two_Motor"))));
    Assert.assertEquals(Status.UNSATISFIABLE, solver.check());
  }
}
