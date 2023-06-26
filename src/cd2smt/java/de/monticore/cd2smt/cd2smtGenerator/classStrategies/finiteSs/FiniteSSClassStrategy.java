package de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteSs;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort.SSClassStrategy;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.*;
import java.util.function.Function;

public class FiniteSSClassStrategy extends SSClassStrategy {

  protected FuncDecl<Sort> getType;
  Map<ASTCDType, Set<Constructor<Sort>>> constants = new HashMap<>();
  protected static int counter = 0;

  @Override
  public BoolExpr mkForall(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body) {
    BoolExpr res = ctx.mkTrue();
    for (Constructor<Sort> contr : constants.get(type)) {
      Expr<?> var2 = ctx.mkConst(contr.ConstructorDecl());
      res = ctx.mkAnd(res, body.apply(var2));
    }

    return res;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    this.ctx = context;

    this.sort = ctx.mkDatatypeSort("Object", collectObjectsConstructors());

    ast.getCDDefinition().getCDEnumsList().forEach(this::declareEnum);
    ast.getCDDefinition().getCDClassesList().forEach(this::declareCDType);
    ast.getCDDefinition().getCDInterfacesList().forEach(this::declareCDType);

    // declare the datatype which indicate the real type of Each Expr
    types = ctx.mkDatatypeSort("CDType", collectTypeConstructors());

    // declare the function that maps each expression to his Type
    getType = ctx.mkFuncDecl("has_type", new Sort[] {sort}, types);

    classConstraints.add(buildTypeUniquenessConstraint());
  }

  @Override
  protected IdentifiableBoolExpr buildTypeUniquenessConstraint() {

    BoolExpr defConstType = ctx.mkTrue();
    for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {

      for (Constructor<Sort> constant : constants.get(astcdType)) {

        defConstType =
            ctx.mkAnd(defConstType, hasType(ctx.mkConst(constant.ConstructorDecl()), astcdType));
      }
    }

    return IdentifiableBoolExpr.buildIdentifiable(
        defConstType, ast.get_SourcePositionStart(), Optional.of("UniqueType"));
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astCdType) {
    return ctx.mkEq(getType.apply(expr), ctx.mkConst(typeMap.get(astCdType).ConstructorDecl()));
  }

  protected Constructor<Sort>[] collectObjectsConstructors() {
    for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {
      Set<Constructor<Sort>> universe = new HashSet<>();
      for (int i = 0; i < 6; i++) { // TODO: 19.06.2023  fixme
        // create constant
        Constructor<Sort> constant =
            mkconstructor(astcdType.getName() + "_" + FiniteSSClassStrategy.counter);

        // add to  the list
        universe.add(constant);
        FiniteSSClassStrategy.counter++;
      }
      constants.put(astcdType, universe);
    }
    return (Constructor<Sort>[])
        constants.values().stream().flatMap(Set::stream).distinct().toArray(Constructor[]::new);
  }

  protected Constructor<Sort> mkconstructor(String name) {
    return ctx.mkConstructor(name, name, null, null, null);
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    for (Map.Entry<ASTCDType, Set<Constructor<Sort>>> entry : constants.entrySet()) {
      for (Constructor<Sort> constructor : entry.getValue()) {
        Expr<?> smtExpr = ctx.mkConst(constructor.ConstructorDecl());
        if (hasType(smtExpr, entry.getKey(), model)) {

          buildObject(entry.getKey(), smtExpr, model, partial);
        }
      }
    }

    return objectSet;
  }
}
