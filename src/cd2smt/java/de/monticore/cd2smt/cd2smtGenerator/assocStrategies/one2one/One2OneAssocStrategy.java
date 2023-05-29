package de.monticore.cd2smt.cd2smtGenerator.assocStrategies.one2one;

import static de.monticore.cd2smt.Helper.SMTHelper.mkExists;
import static de.monticore.cd2smt.Helper.SMTHelper.mkForAll;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy.DefaultAssocStrategy;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.SourcePosition;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

/***
 * extend the DefaultAssocStrategy by handling One2One ([1]A--B[1]) cardinality constraints differently.
 * for ([1]A--B[1]):
 * declare a function (A => B).
 * */
public class One2OneAssocStrategy extends DefaultAssocStrategy {

  public One2OneAssocStrategy() {
    super();
  }

  @Override
  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType leftType,
      ASTCDType rightType,
      Expr<? extends Sort> leftExpr,
      Expr<? extends Sort> rightExpr) {

    if (CDHelper.isCardinalityOne2One(association)) {
      FuncDecl<? extends Sort> assocFunc = assocFuncMap.get(association);
      return classData.getContext().mkEq(rightExpr, assocFunc.apply(leftExpr));
    } else {
      return super.evaluateLink(association, leftType, rightType, leftExpr, rightExpr);
    }
  }

  @Override
  protected void declareAssociation(ASTCDDefinition cd, ASTCDAssociation assoc, Context ctx) {

    if (CDHelper.isCardinalityOne2One(assoc)) {

      ASTCDType leftType = CDHelper.getASTCDType(assoc.getLeftQualifiedName().getQName(), cd);

      ASTCDType rightType = CDHelper.getASTCDType(assoc.getRightQualifiedName().getQName(), cd);

      String assocName = SMTHelper.printSMTAssociationName(assoc);

      Sort leftSort = classData.getSort(leftType);
      Sort rightSort = classData.getSort(rightType);

      assocFuncMap.put(assoc, ctx.mkFuncDecl(assocName, new Sort[] {leftSort}, rightSort));
      assocConstraints.addAll(buildAssocConstraints(assoc, ctx));
    } else {
      super.declareAssociation(cd, assoc, ctx);
    }
  }

  @Override
  protected List<IdentifiableBoolExpr> buildAssocConstraints(ASTCDAssociation assoc, Context ctx) {

    ASTCDDefinition cd = classData.getClassDiagram().getCDDefinition();
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();

    if (CDHelper.isCardinalityOne2One(assoc)) {

      ASTCDType left = CDHelper.getLeftType(assoc, cd);
      ASTCDType right = CDHelper.getRightType(assoc, cd);

      Expr<? extends Sort> leftExpr1 = ctx.mkConst("left1", classData.getSort(left));
      Expr<? extends Sort> leftExpr2 = ctx.mkConst("left2", classData.getSort(left));
      Expr<? extends Sort> rightExpr = ctx.mkConst("right", classData.getSort(right));

      FuncDecl<? extends Sort> assocFunc = assocFuncMap.get(assoc);
      BoolExpr surjectiv =
          mkForAll(
              ctx,
              Set.of(new ImmutablePair<>(right, rightExpr)),
              mkExists(
                  ctx,
                  Set.of(new ImmutablePair<>(left, leftExpr1)),
                  ctx.mkEq(rightExpr, assocFunc.apply(leftExpr1)),
                  inheritanceData),
              inheritanceData);

      BoolExpr injective =
          mkForAll(
              ctx,
              Set.of(new ImmutablePair<>(left, leftExpr1), new ImmutablePair<>(left, leftExpr2)),
              ctx.mkImplies(
                  ctx.mkEq(assocFunc.apply(leftExpr1), assocFunc.apply(leftExpr2)),
                  ctx.mkEq(leftExpr2, leftExpr1)),
              inheritanceData);

      // result must have the correct type
      BoolExpr resultType =
          mkForAll(
              ctx,
              Set.of(new ImmutablePair<>(left, leftExpr1)),
              inheritanceData.filterObject(assocFunc.apply(leftExpr1), right),
              inheritanceData);

      SourcePosition srcPos = assoc.get_SourcePositionStart();
      assert srcPos.getFileName().isPresent();
      IdentifiableBoolExpr constraint =
          IdentifiableBoolExpr.buildIdentifiable(
              ctx.mkAnd(injective, surjectiv, resultType),
              srcPos,
              Optional.of("cardinality-constraint"));

      constraints.add(constraint);

    } else {
      constraints.addAll(super.buildAssocConstraints(assoc, ctx));
    }

    return constraints;
  }
}
