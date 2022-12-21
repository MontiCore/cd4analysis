package de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy;

import static de.monticore.cd2smt.Helper.SMTHelper.mkExists;
import static de.monticore.cd2smt.Helper.SMTHelper.mkForAll;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.ODArtifacts.LinkedSMTObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.SourcePosition;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultAssocStrategy implements AssociationStrategy {
  ClassData classData;
  private final Map<ASTCDAssociation, FuncDecl<BoolSort>> associationsFuncMap;
  private final Set<IdentifiableBoolExpr> associationConstraints;

  public DefaultAssocStrategy() {
    associationsFuncMap = new HashMap<>();
    associationConstraints = new HashSet<>();
  }

  @Override
  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType type1,
      ASTCDType type2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {
    FuncDecl<BoolSort> assocFunc = associationsFuncMap.get(association);
    return association.getLeftQualifiedName().getQName().equals(type1.getName())
        ? (BoolExpr) assocFunc.apply(expr1, expr2)
        : (BoolExpr) assocFunc.apply(expr2, expr1);
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return associationConstraints;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context ctx, ClassData classData) {
    this.classData = classData;
    ast.getCDDefinition()
        .getCDAssociationsList()
        .forEach(assoc -> declareAssociation(ast.getCDDefinition(), assoc, classData, ctx));
    associationConstraints.addAll(buildAssocConstraints(ast.getCDDefinition(), classData, ctx));
  }

  protected void declareAssociation(
      ASTCDDefinition cd, ASTCDAssociation myAssociation, ClassData classData, Context ctx) {
    String assocName = SMTHelper.printSMTAssociationName(myAssociation);
    ASTCDType leftClass =
        CDHelper.getASTCDType(myAssociation.getLeftQualifiedName().getQName(), cd);
    ASTCDType rightClass =
        CDHelper.getASTCDType(myAssociation.getRightQualifiedName().getQName(), cd);

    // set the Association function
    Sort rightSort = classData.getSort(rightClass);
    Sort leftSort = classData.getSort(leftClass);

    associationsFuncMap.put(
        myAssociation,
        ctx.mkFuncDecl(assocName, new Sort[] {leftSort, rightSort}, ctx.getBoolSort()));
  }

  List<IdentifiableBoolExpr> buildAssocConstraints(
      ASTCDDefinition cd, ClassData classData, Context ctx) {
    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
      // get the sort for the left and right objects
      ASTCDType rightClass = CDHelper.getASTCDType(myAssoc.getRightQualifiedName().getQName(), cd);
      ASTCDType leftClass = CDHelper.getASTCDType(myAssoc.getLeftQualifiedName().getQName(), cd);
      assert rightClass != null;
      assert leftClass != null;
      Sort leftSort = classData.getSort(leftClass);
      Sort rightSort = classData.getSort(rightClass);
      FuncDecl<BoolSort> assocFunc = associationsFuncMap.get(myAssoc);

      // build constants for quantifiers scope
      Pair<ASTCDType, Expr<? extends Sort>> r1 =
          new ImmutablePair<>(rightClass, ctx.mkConst(rightClass.getName() + "r1", rightSort));
      Pair<ASTCDType, Expr<? extends Sort>> l1 =
          new ImmutablePair<>(leftClass, ctx.mkConst(leftClass.getName() + "l1", leftSort));
      Pair<ASTCDType, Expr<? extends Sort>> l2 =
          new ImmutablePair<>(leftClass, ctx.mkConst(leftClass.getName() + "l2", leftSort));
      Pair<ASTCDType, Expr<? extends Sort>> r2 =
          new ImmutablePair<>(rightClass, ctx.mkConst(rightClass.getName() + "r2", rightSort));

      // position
      SourcePosition srcPos = myAssoc.get_SourcePositionStart();
      assert srcPos.getFileName().isPresent();

      // Cardinality on the right side
      if (myAssoc.getRight().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(assocFunc, l1, r1, false, ctx);
        BoolExpr optional = buildOptionalConstraint(assocFunc, l1, r1, r2, false, ctx);

        // get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getRight().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getRight().getCDCardinality().isAtLeastOne()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  atLeastOne, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOpt()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  optional, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOne()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  ctx.mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_right")));
        }
      }

      // Cardinality on the left side
      if (myAssoc.getLeft().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(assocFunc, r1, l1, true, ctx);
        BoolExpr optional = buildOptionalConstraint(assocFunc, r1, l1, l2, true, ctx);

        // get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getLeft().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getLeft().getCDCardinality().isAtLeastOne()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  atLeastOne, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOpt()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  optional, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOne()) {
          constraints.add(
              IdentifiableBoolExpr.buildIdentifiable(
                  ctx.mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_left")));
        }
      }
    }
    return constraints;
  }

  protected BoolExpr buildAtLeastOneConstraint(
      FuncDecl<BoolSort> assocFunc,
      Pair<ASTCDType, Expr<? extends Sort>> obj,
      Pair<ASTCDType, Expr<? extends Sort>> otherObj,
      boolean isLeft,
      Context ctx) {

    Pair<ASTCDType, Expr<? extends Sort>> left = !isLeft ? obj : otherObj;
    Pair<ASTCDType, Expr<? extends Sort>> right = isLeft ? obj : otherObj;

    return mkForAll(
        ctx,
        Set.of(obj),
        mkExists(
            ctx,
            Set.of(otherObj),
            (BoolExpr) ctx.mkApp(assocFunc, left.getRight(), right.getRight()),
            classData),
        classData);
  }

  protected BoolExpr buildOptionalConstraint(
      FuncDecl<BoolSort> assocFunc,
      Pair<ASTCDType, Expr<? extends Sort>> obj1,
      Pair<ASTCDType, Expr<? extends Sort>> otherObj1,
      Pair<ASTCDType, Expr<? extends Sort>> otherObj2,
      boolean isLeft,
      Context ctx) {
    Pair<ASTCDType, Expr<? extends Sort>> left1 = !isLeft ? obj1 : otherObj1;
    Pair<ASTCDType, Expr<? extends Sort>> left2 = !isLeft ? obj1 : otherObj2;
    Pair<ASTCDType, Expr<? extends Sort>> right1 = isLeft ? obj1 : otherObj1;
    Pair<ASTCDType, Expr<? extends Sort>> right2 = isLeft ? obj1 : otherObj1;
    return mkForAll(
        ctx,
        Set.of(obj1, otherObj1, otherObj2),
        ctx.mkImplies(
            ctx.mkAnd(
                ctx.mkApp(assocFunc, left1.getRight(), right1.getRight()),
                ctx.mkApp(assocFunc, left2.getRight(), right2.getRight())),
            ctx.mkEq(otherObj1.getRight(), otherObj2.getRight())),
        classData);
  }

  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {

    for (Map.Entry<ASTCDAssociation, FuncDecl<BoolSort>> assoc : associationsFuncMap.entrySet()) {
      Sort leftSort = assoc.getValue().getDomain()[0];
      Sort rightSort = assoc.getValue().getDomain()[1];

      for (SMTObject leftObj : objectSet) {
        for (SMTObject rightObj : objectSet) {
          if ((leftObj.hasSort(leftSort)) && (rightObj.hasSort(rightSort))) {
            if ((model
                    .eval(assoc.getValue().apply(leftObj.getSmtExpr(), rightObj.getSmtExpr()), true)
                    .getBoolValue()
                == Z3_lbool.Z3_L_TRUE)) {
              leftObj
                  .getLinkedObjects()
                  .add(new LinkedSMTObject(assoc.getKey(), rightObj, assoc.getValue(), false));
              rightObj
                  .getLinkedObjects()
                  .add(new LinkedSMTObject(assoc.getKey(), leftObj, assoc.getValue(), true));
            }
          }
        }
      }
    }
    return objectSet;
  }
}
