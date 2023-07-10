/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.Set;
import java.util.function.Function;

public interface InheritanceData {
  /**
   * this Function converts an object to one of his super instances
   *
   * @param objType the type of the object
   * @param superType the supertype into which the object has to be converted
   * @param objExpr the SMT Expr of the object
   * @return the Super instance as SMT Expr
   */
  Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr);

  /**
   * this function check if the object obj is an instance of subType
   *
   * @param obj the object ins SMT
   * @param type the ASTCDType of the Object
   * @return a BoolExpr tha will be evaluated to true if the object is an instance of the subType
   */
  BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType type);

  /***
   * This function filter object for the association strategy according to the inheritance Strategy.
   * - For ME, the function is "has_type" function which recognizes the type of object without taking inheritance into account.
   * - For SEComb, the function is an "instance_of" function which recognizes the type of object taking into account inheritance
   */
  BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type);

  /** @return the inheritances' constraint as set identifiable Bool-expressions */
  Set<IdentifiableBoolExpr> getInheritanceConstraints();

  /***
   * assert that the constraint "body" must hold for all elements with the type "type".
   *
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

  enum Strategy {
    ME,
    SE
  }
}
