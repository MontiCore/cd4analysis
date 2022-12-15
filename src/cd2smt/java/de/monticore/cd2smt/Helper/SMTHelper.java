package de.monticore.cd2smt.Helper;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Set;

public class SMTHelper {
  public static String fCharToLowerCase(String str) {
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  public static String printSMTAssociationName(ASTCDAssociation myAssociation) {
    StringBuilder myString = new StringBuilder();
    myString.append(myAssociation.getLeft().getCDRole().getName());
    if (myAssociation.isPresentName()) {
      myString.append(myAssociation.getName());
    }
    myString.append(myAssociation.getRight().getCDRole().getName());
    return myString.toString();
  }

  public static String printAttributeNameSMT(ASTCDType astcdType, ASTCDAttribute myAttribute) {
    return SMTHelper.fCharToLowerCase(astcdType.getName()) + "_attrib_" + myAttribute.getName();
  }

  public static String printSubclassFuncName(ASTCDType astcdType) {
    return SMTHelper.fCharToLowerCase(astcdType.getName()) + "_get_subclass";
  }

  public static String printSMTCDTypeName(ASTCDType myClass) {
    return myClass.getName() + "_obj";
  }

  public static BoolExpr mkForAll(
      Context ctx,
      Set<Pair<ASTCDType, Expr<? extends Sort>>> vars,
      BoolExpr body,
      ClassData classData) {
    Expr[] res = vars.stream().map(Pair::getRight).toArray(Expr[]::new);
    return ctx.mkForall(
        res,
        ctx.mkImplies(buildTypeConstraint(vars, ctx, classData), body),
        0,
        null,
        null,
        null,
        null);
  }

  public static BoolExpr mkExists(
      Context ctx,
      Set<Pair<ASTCDType, Expr<? extends Sort>>> vars,
      BoolExpr body,
      ClassData classData) {
    return ctx.mkExists(
        vars.stream().map(Pair::getRight).toArray(Expr[]::new),
        ctx.mkAnd(buildTypeConstraint(vars, ctx, classData), body),
        0,
        null,
        null,
        null,
        null);
  }

  public static BoolExpr buildTypeConstraint(
      Set<Pair<ASTCDType, Expr<? extends Sort>>> vars, Context ctx, ClassData classData) {
    BoolExpr constraint = ctx.mkTrue();
    for (Map.Entry<ASTCDType, Expr<? extends Sort>> entry : vars) {
      constraint = ctx.mkAnd(constraint, classData.isInstanceOf(entry.getValue(), entry.getKey()));
    }
    return constraint;
  }
}
