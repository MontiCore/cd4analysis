package de.monticore.cd2smt;

import static de.monticore.cd2smt.Helper.CDHelper.getASTCDTypes;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class CDTypeInitializer {
  /****
   * This method analyzes class diagrams structure and computes possible universes size for each type which satisfies
   * the cardinalities of associations.
   * @param ast the class diagram.
   * @param max the maximal number of objects.
   * @param miWorld this parameter wenn Inheritance relation is defined in a Multi instance way
   *                           that means they ara a main object and sub-object of each subtype.
   * @return the set of examples with cardinalities
   */
  public static Stream<Map<ASTCDType, Integer>> initialize(
      ASTCDCompilationUnit ast, long max, boolean miWorld) {

    // init
    Map<ASTCDType, IntExpr> vars = new HashMap<>();
    Context ctx = new Context();
    Solver solver = ctx.mkSolver();

    // create it const for each type
    getASTCDTypes(ast.getCDDefinition()).forEach(t -> vars.put(t, ctx.mkIntConst(t.getName())));

    // add assoc constraints
    mkAssocConstraints(ctx, ast, max, miWorld, vars).forEach(solver::add);

    // generate all maps as lazy stream
    return Stream.generate(() -> generateNext(solver, ctx, vars)).takeWhile(Objects::nonNull);
  }

  public static Map<ASTCDType, Integer> generateNext(
      Solver solver, Context ctx, Map<ASTCDType, IntExpr> oldExample) {

    if (solver.check().equals(Status.SATISFIABLE)) {
      Map<ASTCDType, Integer> example = evaluateModel(solver.getModel(), oldExample);
      solver.add(negateExample(ctx, oldExample, example));
      return example;
    } else {
      return null;
    }
  }

  static BoolExpr negateExample(
      Context ctx, Map<ASTCDType, IntExpr> vars, Map<ASTCDType, Integer> example) {
    BoolExpr exampleExpr = ctx.mkTrue();

    for (ASTCDType astcdType : example.keySet()) {
      exampleExpr =
          ctx.mkAnd(exampleExpr, ctx.mkEq(vars.get(astcdType), ctx.mkInt(example.get(astcdType))));
    }
    return ctx.mkNot(exampleExpr);
  }

  static Map<ASTCDType, Integer> evaluateModel(Model model, Map<ASTCDType, IntExpr> vars) {
    Map<ASTCDType, Integer> res = new HashMap<>();

    // evaluate model
    for (Map.Entry<ASTCDType, IntExpr> val : vars.entrySet()) {
      String valString = model.evaluate(val.getValue(), true).toString();
      res.put(val.getKey(), Integer.valueOf(valString));
    }
    return res;
  }

  static Set<BoolExpr> mkAssocConstraints(
      Context ctx,
      ASTCDCompilationUnit ast,
      long max,
      boolean multiInstanceWorld,
      Map<ASTCDType, IntExpr> vars) {

    Set<BoolExpr> constraints = new HashSet<>();
    IntExpr sum = ctx.mkInt(0);

    // the size of each type universe must positive
    for (ASTCDType type : getASTCDTypes(ast.getCDDefinition())) {
      IntExpr expr = vars.get(type);
      constraints.add(ctx.mkGt(expr, ctx.mkInt(0)));
      sum = (IntExpr) ctx.mkAdd(sum, expr);
    }

    // add constraints on the number of elements
    IntExpr maximum = ctx.mkInt(max * getMaxFactor(ast) / 2); // TODO find a better approximation
    constraints.add(ctx.mkLe(sum, maximum));

    // define constraints for association multiplicity
    for (ASTCDAssociation assoc : ast.getCDDefinition().getCDAssociationsList()) {
      Set<BiFunction<IntExpr, IntExpr, BoolExpr>> constrs = getAssocConstraint(assoc, ctx);
      constrs.forEach(
          constr ->
              constraints.add(
                  constr.apply(vars.get(leftType(assoc, ast)), vars.get(rightType(assoc, ast)))));
    }

    // case multi-instance-world update the cardinalities constraint.
    if (multiInstanceWorld) {
      for (ASTCDType astcdType : getASTCDTypes(ast.getCDDefinition())) {
        IntExpr number = ctx.mkInt(0);
        for (ASTCDType subType : CDHelper.getSubTypeList(ast.getCDDefinition(), astcdType)) {
          number = (IntExpr) ctx.mkAdd(number, vars.get(subType));
        }
        if (astcdType instanceof ASTCDInterface || astcdType.getModifier().isAbstract()) {
          constraints.add(ctx.mkEq(number, vars.get(astcdType)));
        } else {
          constraints.add(ctx.mkGe(vars.get(astcdType), number));
        }
      }
    }

    return constraints;
  }

  private static Set<BiFunction<IntExpr, IntExpr, BoolExpr>> getAssocConstraint(
      ASTCDAssociation assoc, Context ctx) {
    Set<BiFunction<IntExpr, IntExpr, BoolExpr>> constraints = new HashSet<>();
    if (leftCard(assoc).isOne() && rightCard(assoc).isOne()) {
      constraints.add(ctx::mkEq);
    } else if (leftCard(assoc).isOne() && rightCard(assoc).isOpt()) {
      constraints.add(ctx::mkGe);
    } else if (leftCard(assoc).isOne() && rightCard(assoc).isAtLeastOne()) {
      constraints.add(ctx::mkLe);
      constraints.add(
          (l, r) -> ctx.mkImplies(ctx.mkGt(r, ctx.mkInt(0)), ctx.mkGt(l, ctx.mkInt(0))));
    } else if (leftCard(assoc).isOpt() && rightCard(assoc).isOne()) {
      constraints.add(ctx::mkLe);
    } else if (leftCard(assoc).isOpt() && rightCard(assoc).isAtLeastOne()) {
      constraints.add(ctx::mkLe);
    } else if (leftCard(assoc).isOpt() && rightCard(assoc).isAtLeastOne()) {
      constraints.add(ctx::mkLe);
    } else if (leftCard(assoc).isAtLeastOne() && rightCard(assoc).isOne()) {
      constraints.add(
          (l, r) -> ctx.mkImplies(ctx.mkGt(l, ctx.mkInt(0)), ctx.mkGt(r, ctx.mkInt(0))));
      constraints.add(ctx::mkGe);
    } else if (leftCard(assoc).isAtLeastOne() && rightCard(assoc).isOpt()) {
      constraints.add(ctx::mkGe);
    }

    return constraints;
  }

  public static long getMaxFactor(ASTCDCompilationUnit ast) {
    long maxFactor = 1;
    for (ASTCDType astcdType : getASTCDTypes(ast.getCDDefinition())) {
      long size = CDHelper.getSubTypeAllDeep(astcdType, ast).size();
      if (size > maxFactor) {
        maxFactor = size;
      }
    }
    return maxFactor;
  }

  private static ASTCDCardinality leftCard(ASTCDAssociation assoc) {
    return assoc.getLeft().getCDCardinality();
  }

  private static ASTCDType leftType(ASTCDAssociation assoc, ASTCDCompilationUnit ast) {
    return CDHelper.getASTCDType(assoc.getLeftQualifiedName().getQName(), ast.getCDDefinition());
  }

  public static ASTCDType rightType(ASTCDAssociation assoc, ASTCDCompilationUnit ast) {
    return CDHelper.getASTCDType(assoc.getRightQualifiedName().getQName(), ast.getCDDefinition());
  }

  public static ASTCDCardinality rightCard(ASTCDAssociation assoc) {
    return assoc.getRight().getCDCardinality();
  }
}
