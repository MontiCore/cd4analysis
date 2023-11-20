package de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteSs;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort.SSClassStrategy;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteSSClassStrategy extends SSClassStrategy {
  private Map<ASTCDType, Integer> cardMap = new HashMap<>();
  protected FuncDecl<Sort> getType;
  Map<ASTCDType, List<Constructor<Sort>>> constants = new HashMap<>();

  @Override
  public BoolExpr mkForall(ASTCDType type, Expr<?> var, BoolExpr body) {
    BoolExpr res = ctx.mkTrue();

    for (Expr<?> param : generateTypeUniverse(type)) {
      res = ctx.mkAnd(res, body.substitute(var, param));
    }
    return res;
  }

  private List<Expr<?>> generateTypeUniverse(ASTCDType type) {
    return constants.get(type).stream()
        .map(constr -> ctx.mkConst(constr.ConstructorDecl()))
        .collect(Collectors.toList());
  }

  Constructor<Sort> anonymousObject;
  Constructor<Sort> anonymousType;

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    this.ctx = context;

    cardMap = CD2SMTMill.getCardinalities();
    Set<Constructor<Sort>> constructors = collectObjectsConstructors();
    anonymousObject = mkconstructor("NOTYPE");
    constructors.add(anonymousObject);
    this.sort = ctx.mkDatatypeSort("Object", constructors.toArray(new Constructor[0]));

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
    // they always exist and object with no type to handle the case where all types are empty
    defConstType =
        ctx.mkAnd(
            defConstType,
            ctx.mkEq(
                getType.apply(ctx.mkConst(anonymousObject.ConstructorDecl())),
                ctx.mkConst(anonymousType.ConstructorDecl())));

    return IdentifiableBoolExpr.buildIdentifiable(
        defConstType, ast.get_SourcePositionStart(), Optional.of("UniqueType"));
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astCdType) {
    return ctx.mkEq(getType.apply(expr), ctx.mkConst(typeMap.get(astCdType).ConstructorDecl()));
  }

  protected Set<Constructor<Sort>> collectObjectsConstructors() {
    long counter = 0;
    for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {
      List<Constructor<Sort>> universe = new ArrayList<>();
      for (int i = 0; i < cardMap.get(astcdType); i++) { // TODO: 19.06.2023  fixme
        // create constant
        Constructor<Sort> constant = mkconstructor(astcdType.getName() + "_" + counter);

        // add to the list
        universe.add(constant);
        counter++;
      }
      constants.put(astcdType, universe);
    }

    return constants.values().stream().flatMap(List::stream).collect(Collectors.toSet());
  }

  @Override
  protected Constructor<Sort>[] collectTypeConstructors() {
    Set<Constructor<? extends Sort>> constructors = new HashSet<>(typeMap.values());
    anonymousType = ctx.mkConstructor("SS_NO_TYPE", "SS_IS_" + "NO_TYPE", null, null, null);
    constructors.add(anonymousType);

    return (Constructor<Sort>[]) constructors.toArray(new Constructor[0]);
  }

  protected Constructor<Sort> mkconstructor(String name) {
    return ctx.mkConstructor(name, name, null, null, null);
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    for (Map.Entry<ASTCDType, List<Constructor<Sort>>> entry : constants.entrySet()) {
      for (Constructor<Sort> constructor : entry.getValue()) {
        Expr<?> smtExpr = ctx.mkConst(constructor.ConstructorDecl());
        if (hasType(smtExpr, entry.getKey(), model)) {

          objectSet.add(buildObject(entry.getKey(), smtExpr, model, partial));
        }
      }
    }

    return objectSet;
  }
}
