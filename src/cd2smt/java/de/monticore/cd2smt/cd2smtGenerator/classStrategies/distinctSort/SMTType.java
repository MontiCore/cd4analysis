/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.Constructor;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import com.microsoft.z3.UninterpretedSort;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import java.util.HashMap;
import java.util.Map;

class SMTType {

  private final Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributes = new HashMap<>();
  private final Map<ASTCDEnumConstant, Constructor<Sort>> constantMap = new HashMap<>();
  private Sort sort;

  private ASTCDType astcdType;

  public SMTType(ASTCDType astcdType) {
    this.astcdType = astcdType;
  }

  public FuncDecl<? extends Sort> getAttribute(ASTCDAttribute attribute) {
    return attributes.get(attribute);
  }

  public void addAttribute(ASTCDAttribute attribute, FuncDecl<? extends Sort> attrFunc) {
    attributes.put(attribute, attrFunc);
  }

  public void addConstant(ASTCDEnumConstant constant, Constructor<Sort> attrFunc) {
    constantMap.put(constant, attrFunc);
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

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public Map<ASTCDAttribute, FuncDecl<? extends Sort>> getAttributesMap() {
    return attributes;
  }
}
