package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import com.microsoft.z3.UninterpretedSort;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import java.util.HashMap;
import java.util.Map;

public class SMTType {
  private final boolean isInterface;
  private final Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributes;
  private Sort sort;

  public SMTType(boolean isInterface) {
    this.isInterface = isInterface;
    attributes = new HashMap<>();
  }

  public FuncDecl<? extends Sort> getAttribute(ASTCDAttribute attribute) {
    return attributes.get(attribute);
  }

  public void addAttribute(ASTCDAttribute attribute, FuncDecl<? extends Sort> attrFunc) {
    attributes.put(attribute, attrFunc);
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(UninterpretedSort sort) {
    this.sort = sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public boolean isClass() {
    return !isInterface;
  }

  public Map<ASTCDAttribute, FuncDecl<? extends Sort>> getAttributesMap() {
    return attributes;
  }
}
