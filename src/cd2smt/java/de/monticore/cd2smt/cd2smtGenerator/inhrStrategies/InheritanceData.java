/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.Set;

public interface InheritanceData {
  /**
   * this Function convert an object to one of his super instances
   *
   * @param objType the type of the object
   * @param superType the supertype into which the object have to be converted
   * @param objExpr the SMT Expr of the object
   * @return the Super instance as SMT Expr
   */
  Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr);

  /**
   * this function check if the object obj is an instance of subType
   *
   * @param obj the object ins SMT
   * @param objType the ASTCDType of the Object
   * @param subType the subtype
   * @return a BoolExpr tha will be evaluated to true if the object is an instance of the subType
   */
  BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType objType, ASTCDType subType);

  /** @return the inheritances' constraint as set identifiable Bool-expressions */
  Set<IdentifiableBoolExpr> getInheritanceConstraints();
}
