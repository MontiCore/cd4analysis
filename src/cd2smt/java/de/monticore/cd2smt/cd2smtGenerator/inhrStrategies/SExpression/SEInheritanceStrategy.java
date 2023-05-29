package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.SExpression;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort.SSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.*;

/***
 * this class  convert inheritance relations in a Single expression way.
 * 1-define a function sub_type (CDType => CDType => Bool). the function is evaluated to true,
 * when the first type a subtype of the second.
 *
 * 2-define a function instance_of (Object => CDType => Bool). the function is evaluated to true
 * when the object is an instance of the CDType.
 *
 * 3-add constraints to forbid object with abstract/interface CDTypes
 */
public class SEInheritanceStrategy extends SSClassStrategy implements InheritanceStrategy {
  protected Set<IdentifiableBoolExpr> inheritanceConstraints;
  protected FuncDecl<BoolSort> subTypeFunc;
  protected FuncDecl<BoolSort> instanceOf;

  private boolean partial = false;

  public SEInheritanceStrategy() {
    classConstraints = new HashSet<>();
    inheritanceConstraints = new HashSet<>();
    typeMap = new HashMap<>();
  }

  @Override
  public Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    return objExpr;
  }

  @Override
  public BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType subType) {
    return (BoolExpr)
        ctx.mkApp(instanceOf, obj, ctx.mkConst(typeMap.get(subType).ConstructorDecl()));
  }

  @Override
  public BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type) {
    return instanceOf(obj, type);
  }

  @Override
  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceConstraints;
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    this.partial = partial;
    return super.smt2od(model, partial);
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context ctx, ClassData classData) {
    inheritanceConstraints.add(defineSubTypeFunc());

    inheritanceConstraints.add(defineInstanceOfFunc());

    inheritanceConstraints.add(noAbstractsObjects());
  }


  IdentifiableBoolExpr defineInstanceOfFunc() {
    Expr<? extends Sort> object = ctx.mkConst("object", sort);
    Expr<? extends Sort> type = ctx.mkConst("type", types);
    Expr<? extends Sort> type1 = ctx.mkConst("type1", types);

    instanceOf = ctx.mkFuncDecl("instance_of", new Sort[] {sort, types}, ctx.mkBoolSort());
    BoolExpr res =
        ctx.mkOr(
            ctx.mkApp(hasTypeFunc, object, type),
            mkExists(
                new Expr[] {type1},
                ctx.mkAnd(
                    ctx.mkApp(subTypeFunc, type1, type), ctx.mkApp(hasTypeFunc, object, type1))));

    BoolExpr defineInstanceOf =
        mkForall(new Expr[] {object, type}, ctx.mkEq(ctx.mkApp(instanceOf, object, type), res));

    return IdentifiableBoolExpr.buildIdentifiable(
        defineInstanceOf, ast.get_SourcePositionStart(), Optional.of("defineInstanceOf"));
  }

  IdentifiableBoolExpr defineSubTypeFunc() {
    ASTCDDefinition cd = ast.getCDDefinition();
    List<ASTCDType> allTypes = CDHelper.getASTCDTypes(cd);

    subTypeFunc = ctx.mkFuncDecl("sub_type", new Sort[] {types, types}, ctx.mkBoolSort());

    BoolExpr body = ctx.mkTrue();
    for (ASTCDType subType : allTypes) {
      Expr<? extends Sort> subExpr = ctx.mkConst(typeMap.get(subType).ConstructorDecl());

      for (ASTCDType superTye : allTypes) {
        Expr<? extends Sort> superExpr = ctx.mkConst(typeMap.get(superTye).ConstructorDecl());

        if (CDHelper.getSuperTypeAllDeep(subType, cd).contains(superTye)
            || superTye.equals(subType)) {
          body = ctx.mkAnd(body, ctx.mkApp(this.subTypeFunc, subExpr, superExpr));
        } else {
          body = ctx.mkAnd(body, ctx.mkNot(ctx.mkApp(this.subTypeFunc, subExpr, superExpr)));
        }
      }
    }

    return IdentifiableBoolExpr.buildIdentifiable(
        body, ast.get_SourcePositionStart(), Optional.of("defineSubType"));
  }

  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {

    for (SMTObject object : objectSet) {

      for (ASTCDType superType :
          CDHelper.getSuperTypeAllDeep(object.getASTCDType(), ast.getCDDefinition())) {
        for (ASTCDAttribute attribute : superType.getCDAttributeList()) {

          Expr<? extends Sort> attrExpr =
              model.eval(attributeMap.get(attribute).apply(object.getSmtExpr()), !partial);
          if (attrExpr.getNumArgs() == 0) {
            object.addAttribute(attribute, attrExpr);
          }
        }
      }

      objectSet.add(object);
    }

    return objectSet;
  }

  protected IdentifiableBoolExpr noAbstractsObjects() {
    Expr<? extends Sort> object = ctx.mkConst("object", sort);
    BoolExpr body = ctx.mkTrue();
    for (ASTCDType astCDType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {
      if (astCDType instanceof ASTCDInterface || astCDType.getModifier().isAbstract()) {
        Expr<? extends Sort> type = ctx.mkConst(typeMap.get(astCDType).ConstructorDecl());

        body = ctx.mkAnd(body, ctx.mkNot(ctx.mkApp(hasTypeFunc, object, type)));
      }
    }
    BoolExpr noAbstractObject = mkForall(new Expr[] {object}, body);

    return
        IdentifiableBoolExpr.buildIdentifiable(
            noAbstractObject, ast.get_SourcePositionStart(), Optional.of("No_abstract_objects"));
  }

  @Override
  protected boolean hasType(Expr<? extends Sort> expr, ASTCDType astcdType, Model model) {

    return model
            .evaluate(
                ctx.mkApp(hasTypeFunc, expr, ctx.mkConst(typeMap.get(astcdType).ConstructorDecl())),
                true)
            .getBoolValue()
        == Z3_lbool.Z3_L_TRUE;
  }

  private BoolExpr mkForall(Expr<?>[] expr, BoolExpr body) {
    return ctx.mkForall(expr, body, 0, null, null, null, null);
  }

  private BoolExpr mkExists(Expr<?>[] expr, BoolExpr body) {
    return ctx.mkExists(expr, body, 0, null, null, null, null);
  }
}
