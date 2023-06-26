/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import java.util.Set;
import java.util.function.Function;

public interface ClassData {
  /**
   * @param astCdType the ASTCDType (class or Interface).
   * @return the Sort declared for the ASTCDType.
   */
  Sort getSort(ASTCDType astCdType);

  /***
   * return a BoolExpr that check if the object "expr" has the type astcdtype (without taking inheritance into account).
   *
   * @param expr the object
   * @param astcdType  the type
   * @return a Boolexpr
   */
  BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astcdType);

  /**
   * this function calculates the value of an Expr that represent a ASTCDType Object.
   *
   * @param astCdType the ASTCDType (class or interface).
   * @param attributeName a name of the Attribute of the ASTCDType.
   * @param cDTypeExpr the SMT Expr of the object.
   * @return the Attribute as SMT Expr.
   */
  Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr);

  /**
   * this function calculates the value of an Expr that represent a ASTCDType Object.
   *
   * @param astCdType the ASTCDType (class or interface).
   * @param attribute ASTCDAttribute of the ASTCDType.
   * @param cDTypeExpr the SMT Expr of the object.
   * @return the Attribute as SMT Expr.
   */
  default Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, ASTCDAttribute attribute, Expr<? extends Sort> cDTypeExpr) {
    return getAttribute(astCdType, attribute.getName(), cDTypeExpr);
  }

  ASTCDCompilationUnit getClassDiagram();

  /** @return the class Constraints as Set of Bool-Expressions. */
  Set<IdentifiableBoolExpr> getClassConstraints();

  /** @return context where the class diagram elements are transformed. */
  Context getContext();

  /***
   * get an enm Constant form the class declaration as SMTExpression
   */
  Expr<? extends Sort> getEnumConstant(ASTCDEnum enumeration, ASTCDEnumConstant enumConstant);

  /***
   * assert that the constraint "body" must hold for all elements with the type "type".
   * @param type the type.
   * @param var  one Variable of this type.
   * @param body the body.
   * @return the assertion as boolExpr.
   */
  BoolExpr mkForall(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body);

  /***
   * assert that the constraint "body" must hold for at least one element with the type "type".
   * @param type the type.
   * @param var  one Variable of this type.
   * @param body the body.
   * @return the assertion as boolExpr.
   */

  BoolExpr mkExists(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body);
}
