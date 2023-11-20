/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.ODArtifacts;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.HashMap;
import java.util.Map;

/** this class is used to saved data for a minimal object (object without links and inheritance) */
public class MinObject {
  private final Expr<? extends Sort> smtExpr;
  private final ASTCDType astcdType;
  public Map<ASTCDAttribute, Expr<? extends Sort>> attributes = new HashMap<>();
  private CDHelper.ObjType type;

  /**
   * @param type the type of the object (interface_obj, abstract_obj...)
   * @param smtExpr the SMT-representation of the object
   * @param astcdType the ASTCDType of the object
   */
  public MinObject(CDHelper.ObjType type, Expr<? extends Sort> smtExpr, ASTCDType astcdType) {
    this.type = type;
    this.smtExpr = smtExpr;
    this.astcdType = astcdType;
  }

  public void addAttribute(ASTCDAttribute key, Expr<? extends Sort> value) {
    attributes.put(key, value);
  }

  public Expr<? extends Sort> getSmtExpr() {
    return smtExpr;
  }

  public ASTCDType getASTCDType() {
    return astcdType;
  }

  public Map<ASTCDAttribute, Expr<? extends Sort>> getAttributes() {
    return attributes;
  }

  public CDHelper.ObjType getType() {
    return type;
  }

  public void setType(CDHelper.ObjType type) {
    this.type = type;
  }

  public boolean hasSort(Sort sort) {
    return sort.equals(this.getSmtExpr().getSort());
  }
}
