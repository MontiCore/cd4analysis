package de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.ODArtifacts.LinkedSMTObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.SourcePosition;

import java.util.*;

public class DefaultAssocStrategy implements AssociationStrategy {
  private final Map<ASTCDAssociation, FuncDecl<BoolSort>> associationsFuncMap;
  private final Set<IdentifiableBoolExpr> associationConstraints;
  ASTCDCompilationUnit astCD;

  public DefaultAssocStrategy() {
    associationsFuncMap = new HashMap<>();
    associationConstraints = new HashSet<>();
  }


  @Override
  public BoolExpr evaluateLink(ASTCDAssociation association, Expr<? extends Sort> left, Expr<? extends Sort> right) {
    FuncDecl<BoolSort> assocFunc = associationsFuncMap.get(association);
      return left.getSort().equals(assocFunc.getDomain()[0])  ?(BoolExpr) assocFunc.apply(left,right): (BoolExpr) assocFunc.apply(right,left)  ;
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return associationConstraints;
  }

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context ctx, ClassStrategy classStrategy) {
    this.astCD = ast;
    ast.getCDDefinition().getCDAssociationsList().forEach(assoc -> declareAssociation(ast.getCDDefinition(), assoc, classStrategy, ctx));
    associationConstraints.addAll(buildAssocConstraints(ast.getCDDefinition(), classStrategy, ctx));
  }

  protected void declareAssociation(ASTCDDefinition cd, ASTCDAssociation myAssociation, ClassStrategy classStrategy, Context ctx) {
    String assocName = SMTNameHelper.printSMTAssociationName(myAssociation);
    ASTCDType leftClass = CDHelper.getASTCDType(myAssociation.getLeftQualifiedName().getQName(), cd);
    ASTCDType rightClass = CDHelper.getASTCDType(myAssociation.getRightQualifiedName().getQName(), cd);

    //set the Association function
    Sort rightSort = classStrategy.getSort(rightClass);
    Sort leftSort = classStrategy.getSort(leftClass);

    associationsFuncMap.put(myAssociation, ctx.mkFuncDecl(assocName, new Sort[]{leftSort, rightSort}, ctx.getBoolSort()));
  }

  List<IdentifiableBoolExpr> buildAssocConstraints(ASTCDDefinition cd, ClassStrategy classStrategy, Context ctx) {
    List<IdentifiableBoolExpr> constraints = new LinkedList<>();

    for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
      //get the sort for the left and right objects
      Sort rightSortSMT = classStrategy.getSort(CDHelper.getASTCDType(myAssoc.getRightQualifiedName().getQName(), cd));
      Sort leftSortSMT = classStrategy.getSort(CDHelper.getASTCDType(myAssoc.getLeftQualifiedName().getQName(), cd));
      String assocName = SMTNameHelper.printSMTAssociationName(myAssoc);
      FuncDecl<BoolSort> assocFunc = associationsFuncMap.get(myAssoc);
      //build constants for quantifiers scope
      Expr<Sort> r1 = ctx.mkConst(assocName + "r1", rightSortSMT);
      Expr<Sort> l1 = ctx.mkConst(assocName + "l1", leftSortSMT);
      Expr<Sort> l2 = ctx.mkConst(assocName + "l2", leftSortSMT);
      Expr<Sort> r2 = ctx.mkConst(assocName + "r2", rightSortSMT);

      //position
      SourcePosition srcPos = myAssoc.get_SourcePositionStart();
      assert srcPos.getFileName().isPresent();

      //Cardinality on the right side
      if (myAssoc.getRight().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(assocFunc, l1, r1, false, ctx);
        BoolExpr optional = buildOptionalConstraint(assocFunc, l1, r1, r2, false, ctx);

        //get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getRight().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getRight().getCDCardinality().isAtLeastOne()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(atLeastOne, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOpt()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(optional, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOne()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(ctx.mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_right")));
        }
      }

      //Cardinality on the left side
      if (myAssoc.getLeft().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(assocFunc, r1, l1, true, ctx);
        BoolExpr optional = buildOptionalConstraint(assocFunc, r1, l1, l2, true, ctx);

        //get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getLeft().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getLeft().getCDCardinality().isAtLeastOne()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(atLeastOne, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOpt()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(optional, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOne()) {
          constraints.add(IdentifiableBoolExpr.buildIdentifiable(ctx.mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_left")));
        }

      }
    }
    return constraints;
  }

  protected BoolExpr buildAtLeastOneConstraint(FuncDecl<BoolSort> assocFunc, Expr<? extends Sort> obj, Expr<? extends Sort> otherObj, boolean isLeft, Context ctx) {
    Expr<? extends Sort> left = !isLeft ? obj : otherObj;
    Expr<? extends Sort> right = isLeft ? obj : otherObj;
    return ctx.mkForall(new Expr[]{obj}, ctx.mkExists(new Expr[]{otherObj}, ctx.mkApp(assocFunc, left, right), 0,
      null, null, null, null), 0, null, null, null, null);
  }

  protected BoolExpr buildOptionalConstraint(FuncDecl<BoolSort> assocFunc, Expr<? extends Sort> obj1, Expr<? extends Sort> otherObj1, Expr<? extends Sort> otherObj2, boolean isLeft, Context ctx) {
    Expr<? extends Sort> left1 = !isLeft ? obj1 : otherObj1;
    Expr<? extends Sort> left2 = !isLeft ? obj1 : otherObj2;
    Expr<? extends Sort> right1 = isLeft ? obj1 : otherObj1;
    Expr<? extends Sort> right2 = isLeft ? obj1 : otherObj1;
    return ctx.mkForall(new Expr[]{obj1, otherObj1, otherObj2}, ctx.mkImplies(ctx.mkAnd(ctx.mkApp(assocFunc, left1, right1),
      ctx.mkApp(assocFunc, left2, right2)), ctx.mkEq(otherObj1, otherObj2)), 0, null, null, null, null);
  }

  @Override
  public Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet) {

    for (Map.Entry<ASTCDAssociation, FuncDecl<BoolSort>> assoc : associationsFuncMap.entrySet()) {
      Sort leftSort = assoc.getValue().getDomain()[0];
      Sort rightSort = assoc.getValue().getDomain()[1];

      for (SMTObject leftObj : objectSet) {
        for (SMTObject rightObj : objectSet) {
          if ((leftObj.hasSort(leftSort)) && (rightObj.hasSort(rightSort))) {
            if ((model.eval(assoc.getValue().apply(leftObj.getSmtExpr(), rightObj.getSmtExpr()), true).getBoolValue() == Z3_lbool.Z3_L_TRUE)) {
              leftObj.getLinkedObjects().add(new LinkedSMTObject(assoc.getKey(), rightObj, assoc.getValue(), false));
              rightObj.getLinkedObjects().add(new LinkedSMTObject(assoc.getKey(), leftObj, assoc.getValue(), true));
            }
          }
        }
      }
    }
    return objectSet;
  }


}
