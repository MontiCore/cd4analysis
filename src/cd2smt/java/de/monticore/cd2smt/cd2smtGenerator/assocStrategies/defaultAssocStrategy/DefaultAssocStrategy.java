/* (c) https://github.com/MontiCore/monticore */
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
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.SourcePosition;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/***
 * this class transform associations relation's between classes and interfaces
 * For each association relation Type1 -- Type2:
 * 1-A Function (Type => Type2 => Bool) is defined
 * 2-cardinalities of the association are defined as boolean formulas
 */
public class DefaultAssocStrategy implements AssociationStrategy {
  protected InheritanceData inheritanceData;
  protected ClassData classData;
  protected final Map<ASTCDAssociation, FuncDecl<? extends Sort>> assocFuncMap;
  protected final Set<IdentifiableBoolExpr> assocConstraints;

  public DefaultAssocStrategy() {
    assocFuncMap = new HashMap<>();
    assocConstraints = new HashSet<>();
  }

  @Override
  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType type1,
      ASTCDType type2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {
    FuncDecl<? extends Sort> assocFunc = assocFuncMap.get(association);
    return (BoolExpr) assocFunc.apply(expr1, expr2);
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return assocConstraints;
  }

  @Override
  public void cd2smt(
      ASTCDCompilationUnit ast, Context ctx, ClassData classData, InheritanceData inheritanceData) {
    this.inheritanceData = inheritanceData;
    this.classData = classData;
    ASTCDDefinition cd = ast.getCDDefinition();
    cd.getCDAssociationsList().forEach(assoc -> declareAssociation(cd, assoc, ctx));
  }
  /** declare the association function of the association */
  protected void declareAssociation(ASTCDDefinition cd, ASTCDAssociation assoc, Context ctx) {

    String assocName = SMTHelper.printSMTAssociationName(assoc);
    ASTCDType leftClass = CDHelper.getASTCDType(assoc.getLeftQualifiedName().getQName(), cd);
    ASTCDType rightClass = CDHelper.getASTCDType(assoc.getRightQualifiedName().getQName(), cd);

    // set the Association function
    Sort rightSort = classData.getSort(rightClass);
    Sort leftSort = classData.getSort(leftClass);

    assocFuncMap.put(
        assoc, ctx.mkFuncDecl(assocName, new Sort[] {leftSort, rightSort}, ctx.getBoolSort()));

    assocConstraints.addAll(buildAssocConstraints(assoc, ctx));
  }
  /***build cardinality constraints of the association*/
  protected List<IdentifiableBoolExpr> buildAssocConstraints(
      ASTCDAssociation myAssoc, Context ctx) {
    List<IdentifiableBoolExpr> constraints = new LinkedList<>();
    ASTCDDefinition cd = classData.getClassDiagram().getCDDefinition();
    // get the sort for the left and right objects
    ASTCDType rightClass = CDHelper.getASTCDType(myAssoc.getRightQualifiedName().getQName(), cd);
    ASTCDType leftClass = CDHelper.getASTCDType(myAssoc.getLeftQualifiedName().getQName(), cd);
    assert rightClass != null;
    assert leftClass != null;
    Sort leftSort = classData.getSort(leftClass);
    Sort rightSort = classData.getSort(rightClass);
    FuncDecl<? extends Sort> assocFunc = assocFuncMap.get(myAssoc);

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

      // get the source Position for the cardinality
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

      // get the source Position for the cardinality
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

    return constraints;
  }

  protected BoolExpr buildAtLeastOneConstraint(
      FuncDecl<? extends Sort> assocFunc,
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
            inheritanceData),
        inheritanceData);
  }

  protected BoolExpr buildOptionalConstraint(
      FuncDecl<? extends Sort> assocFunc,
      Pair<ASTCDType, Expr<? extends Sort>> obj1,
      Pair<ASTCDType, Expr<? extends Sort>> otherObj1,
      Pair<ASTCDType, Expr<? extends Sort>> otherObj2,
      boolean isLeft,
      Context ctx) {
    BoolExpr res;
    if (isLeft) {
      res =
          mkForAll(
              ctx,
              Set.of(obj1, otherObj1, otherObj2),
              ctx.mkImplies(
                  ctx.mkAnd(
                      (BoolExpr) ctx.mkApp(assocFunc, otherObj1.getRight(), obj1.getRight()),
                      (BoolExpr) ctx.mkApp(assocFunc, otherObj2.getRight(), obj1.getRight())),
                  ctx.mkEq(otherObj1.getRight(), otherObj2.getRight())),
              inheritanceData);
    } else {
      res =
          mkForAll(
              ctx,
              Set.of(obj1, otherObj1, otherObj2),
              ctx.mkImplies(
                  ctx.mkAnd(
                      (BoolExpr) ctx.mkApp(assocFunc, obj1.getRight(), otherObj1.getRight()),
                      (BoolExpr) ctx.mkApp(assocFunc, obj1.getRight(), otherObj2.getRight())),
                  ctx.mkEq(otherObj1.getRight(), otherObj2.getRight())),
              inheritanceData);
    }
    return res;
  }
  /***evaluate association function to get links between objects*/
  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {
    for (ASTCDAssociation assoc : assocFuncMap.keySet()) {
      for (SMTObject leftObj : objectSet) {
        for (SMTObject rightObj : objectSet) {

          if (hasLink(leftObj, rightObj, assoc, model)) {
            leftObj
                .getLinkedObjects()
                .add(new LinkedSMTObject(assoc, rightObj, assocFuncMap.get(assoc), false));

            rightObj
                .getLinkedObjects()
                .add(new LinkedSMTObject(assoc, leftObj, assocFuncMap.get(assoc), true));
          }
        }
      }
    }
    return objectSet;
  }
  /***check if two object ar linked by an association by evaluation the model*/
  protected boolean hasLink(
      SMTObject leftObj, SMTObject rightObj, ASTCDAssociation assoc, Model model) {

    if (checkTypeConformance(assoc, leftObj, rightObj, model)) {
      return model
              .eval(
                  evaluateLink(
                      assoc,
                      leftObj.getASTCDType(),
                      rightObj.getASTCDType(),
                      leftObj.getSmtExpr(),
                      rightObj.getSmtExpr()),
                  true)
              .getBoolValue()
          == Z3_lbool.Z3_L_TRUE;
    } else {
      return false;
    }
  }

  protected boolean checkTypeConformance(
      ASTCDAssociation assoc, SMTObject leftObj, SMTObject rightObj, Model model) {
    ASTCDDefinition cd = classData.getClassDiagram().getCDDefinition();
    ASTCDType leftType = CDHelper.getLeftType(assoc, cd);
    ASTCDType rightType = CDHelper.getRightType(assoc, cd);

    BoolExpr checkLeft =
        (BoolExpr)
            model.evaluate(inheritanceData.filterObject(leftObj.getSmtExpr(), leftType), true);
    BoolExpr checkRight =
        (BoolExpr)
            model.evaluate(inheritanceData.filterObject(rightObj.getSmtExpr(), rightType), true);

    return checkLeft.getBoolValue() == Z3_lbool.Z3_L_TRUE
        && checkRight.getBoolValue() == Z3_lbool.Z3_L_TRUE;
  }
}
