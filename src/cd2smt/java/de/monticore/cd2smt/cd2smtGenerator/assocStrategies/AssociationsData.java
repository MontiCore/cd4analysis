package de.monticore.cd2smt.cd2smtGenerator.assocStrategies;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import java.util.Set;

public interface AssociationsData {
  /**
   * this function evaluate an association function and return a BoolExpr. the BoolExpr is true when
   * the two Expr left and right are linked by the ASTCDAssociation
   *
   * @param association the ASTCDAssociation
   * @param left the left SMT Expr
   * @param right the right SMT Expr
   * @return the result of the evaluation as BoolExpr
   */
  BoolExpr evaluateLink(
      ASTCDAssociation association, Expr<? extends Sort> left, Expr<? extends Sort> right);

  /** @return the associations Constraints as Set of Bool-Expressions */
  Set<IdentifiableBoolExpr> getAssociationsConstraints();
}
