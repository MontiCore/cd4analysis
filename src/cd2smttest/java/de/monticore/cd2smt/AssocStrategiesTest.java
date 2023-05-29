/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AssocStrategiesTest extends CD2SMTAbstractTest {

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
    ASTCDCompilationUnit ast = parseModel("/assocStrategies/Optional.cd");
    CD2SMTMill.initDefault();
    CD2SMTGenerator cd2SMTGenerator = CD2SMTMill.cd2SMTGenerator();
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
    Assertions.assertEquals(Status.UNSATISFIABLE, solver.check());
  }
}
