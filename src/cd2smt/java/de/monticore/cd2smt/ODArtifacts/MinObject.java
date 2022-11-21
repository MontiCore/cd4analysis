package de.monticore.cd2smt.ODArtifacts;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.SMTType;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import java.util.HashMap;
import java.util.Map;

/** this class is used to saved data for a minimal object (object without links and inheritance) */
public class MinObject {
  private final Expr<? extends Sort> smtExpr;
  private final SMTType smtType;
  public Map<ASTCDAttribute, Expr<? extends Sort>> attributes = new HashMap<>();
  private boolean isAbstract;

  /**
   * @param isAbstract is true when the object will be merged with his sub instance e.g
   *     interface-obj
   * @param smtExpr the SMT-representation of the object
   * @param smtcdType the ASTCDType of the object
   */
  public MinObject(boolean isAbstract, Expr<? extends Sort> smtExpr, SMTType smtcdType) {
    this.isAbstract = isAbstract;
    this.smtExpr = smtExpr;
    this.smtType = smtcdType;
  }

  public void addAttribute(ASTCDAttribute key, Expr<? extends Sort> value) {
    attributes.put(key, value);
  }

  public Expr<? extends Sort> getSmtExpr() {
    return smtExpr;
  }

  public SMTType getSmtCdType() {
    return smtType;
  }

  public Map<ASTCDAttribute, Expr<? extends Sort>> getAttributes() {
    return attributes;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract() {
    isAbstract = true;
  }

  public boolean hasSort(Sort sort) {
    return sort.equals(this.getSmtExpr().getSort());
  }
}