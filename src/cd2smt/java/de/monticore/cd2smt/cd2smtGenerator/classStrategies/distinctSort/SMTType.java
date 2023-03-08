/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort;

import com.microsoft.z3.Constructor;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import com.microsoft.z3.UninterpretedSort;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.HashMap;
import java.util.Map;

class SMTType {
  private CDHelper.ClassType type;
  private final Map<ASTCDAttribute, FuncDecl<? extends Sort>> attributes = new HashMap<>();
  private final Map<ASTCDEnumConstant, Constructor<Sort>> constantMap = new HashMap<>();
  private Sort sort;

  private ASTCDType astcdType;

  public static SMTType mkClass(ASTCDClass astcdClass) {
    SMTType smtType = new SMTType();
    smtType.type = CDHelper.ClassType.NORMAL_CLASS;
    smtType.astcdType = astcdClass;
    return smtType;
  }

  public static SMTType mkInterface(ASTCDInterface astcdInterface) {
    SMTType smtType = new SMTType();
    smtType.type = CDHelper.ClassType.INTERFACE;
    smtType.astcdType = astcdInterface;
    return smtType;
  }

  public static SMTType mkEnum(ASTCDEnum astcdEnum) {
    SMTType smtType = new SMTType();
    smtType.type = CDHelper.ClassType.ENUMERATION;
    smtType.astcdType = astcdEnum;
    return smtType;
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

  public CDHelper.ClassType getType() {
    return type;
  }

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public Map<ASTCDAttribute, FuncDecl<? extends Sort>> getAttributesMap() {
    return attributes;
  }
}
