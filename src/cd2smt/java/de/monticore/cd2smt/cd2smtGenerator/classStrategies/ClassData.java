/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.Set;

public interface ClassData {
  /**
   * @param astCdType the ASTCDType (class or Interface)
   * @return the Sort declared for the ASTCDType
   */
  Sort getSort(ASTCDType astCdType);

  /**
   * @param expr SMT Expression
   * @param astCdType the ASTCDType (class or interface)
   * @return true, iff expr represent an expression with the type astCdType
   */
  BoolExpr isInstanceOf(Expr<? extends Sort> expr, ASTCDType astCdType);

  /**
   * this function calculate the value of an Expr that represent a ASTCDType Object .
   *
   * @param astCdType the ASTCDType (class or interface)
   * @param attributeName an name of the Attribute of the ASTCDType
   * @param cDTypeExpr the SMT Expr of the object
   * @return the Attribute as SMT Expr
   */
  Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr);

  /**
   * this function calculate the value of an Expr that represent a ASTCDType Object .
   *
   * @param astCdType the ASTCDType (class or interface)
   * @param attribute ASTCDAttribute of the ASTCDType
   * @param cDTypeExpr the SMT Expr of the object
   * @return the Attribute as SMT Expr
   */
  default Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, ASTCDAttribute attribute, Expr<? extends Sort> cDTypeExpr) {
    return getAttribute(astCdType, attribute.getName(), cDTypeExpr);
  }

  ASTCDCompilationUnit getClassDiagram();

  /** @return the class Constraints as Set of Bool-Expressions */
  Set<IdentifiableBoolExpr> getClassConstraints();
}
