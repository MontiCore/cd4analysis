package de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort;

import com.microsoft.z3.Constructor;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.HashMap;
import java.util.Map;

class SMTType {
  private final boolean isInterface;
  private final Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributes;
  private Constructor<Sort> type;

  private final ASTCDType astcdType;

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public SMTType(boolean isInterface, ASTCDType astcdType) {
    this.isInterface = isInterface;
    this.astcdType = astcdType;
    attributes = new HashMap<>();
  }

  public FuncDecl<? extends Sort> getAttribute(ASTCDAttribute attribute) {
    return attributes.get(attribute);
  }

  public void addAttribute(ASTCDAttribute attribute, FuncDecl<? extends Sort> attrFunc) {
    attributes.put(attribute, attrFunc);
  }

  public Constructor<? extends Sort> getType() {
    return type;
  }

  public void setType(Constructor<Sort> type) {
    this.type = type;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public boolean isClass() {
    return !isInterface;
  }

  public ASTCDType getAsCdType() {
    return astcdType;
  }

  public Map<ASTCDAttribute, FuncDecl<? extends Sort>> getAttributesMap() {
    return attributes;
  }
}
