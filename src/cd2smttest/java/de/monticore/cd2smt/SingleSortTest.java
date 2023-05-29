package de.monticore.cd2smt;

import static de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy.Strategy.DEFAULT;
import static de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy.Strategy.SS;
import static de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData.Strategy.SE;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SingleSortTest extends CD2SMTAbstractTest {

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.init();

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    ctx = new Context(cfg);
  }

  @Disabled("enum and abstract class conversion not yet implemented ")
  @ParameterizedTest
  @MethodSource("modelTarget")
  public void CheckTypeUnicity(String cdfile) {

    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator(SS, SE, DEFAULT);
    ASTCDCompilationUnit ast = parseModel(cdfile);
    cd2SMTGenerator.cd2smt(ast, ctx);
    List<BoolExpr> constraints = new ArrayList<>();
    Expr<? extends Sort> obj =
        ctx.mkConst(
            "Obj", cd2SMTGenerator.getSort(ast.getCDDefinition().getCDClassesList().get(0)));
    ast.getCDDefinition()
        .getCDClassesList()
        .forEach(astcdclass -> constraints.add(cd2SMTGenerator.hasType(obj, astcdclass)));

    ast.getCDDefinition()
        .getCDInterfacesList()
        .forEach(astcdclass -> constraints.add(cd2SMTGenerator.hasType(obj, astcdclass)));

    Set<IdentifiableBoolExpr> UNSATConstrList = new HashSet<>();

    for (int i = 0; i < constraints.size(); i++) {
      for (int j = 0; j < constraints.size(); j++) {
        if (i != j) {
          UNSATConstrList.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  ctx.mkAnd(constraints.get(i), constraints.get(j)),
                  null,
                  Optional.of("[NONAME]")));
        }
      }
    }
    for (IdentifiableBoolExpr UNSATConstr : UNSATConstrList) {
      Solver solver = cd2SMTGenerator.makeSolver(List.of(UNSATConstr));
      Assertions.assertEquals(solver.check(), Status.UNSATISFIABLE);
    }
  }
}
