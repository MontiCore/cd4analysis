package de.monticore.cd2smt.context.CDArtifacts;

import com.microsoft.z3.*;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SMTCDType {

  private final List<FuncDecl<Sort>> attributes;
  private final Map<ASTCDType, Constructor<? extends Sort>> subClassConstructorList;
  private final Map<ASTCDAssociation, SMTAssociation> smtAssociations;
  private final Map<ASTCDInterface, FuncDecl<UninterpretedSort>> convert2SuperInterface;
  private UninterpretedSort sort;
  private DatatypeSort<? extends Sort> subclassDatatype;
  private FuncDecl<Sort> getSubClass;
  private FuncDecl<UninterpretedSort> convert2Superclass;

  public SMTCDType() {
    smtAssociations = new HashMap<>();
    attributes = new ArrayList<>();
    subClassConstructorList = new HashMap<>();
    convert2SuperInterface = new HashMap<>();
  }

  public DatatypeSort<? extends Sort> getSubclassDatatype() {
    return subclassDatatype;
  }

  public void setSubclassDatatype(DatatypeSort<? extends Sort> subclassDatatype) {
    this.subclassDatatype = subclassDatatype;
  }
  public ASTCDType getASTCDType() {
    return null;
  }
  public FuncDecl<UninterpretedSort> getConvert2Superclass() {
    return convert2Superclass;
  }

  public void setConvert2Superclass(FuncDecl<UninterpretedSort> convert2Superclass) {
    this.convert2Superclass = convert2Superclass;
  }

  public Map<ASTCDInterface, FuncDecl<UninterpretedSort>> getConvert2SuperInterface() {
    return convert2SuperInterface;
  }

  public void setCDType(ASTCDInterface anInterface) {
  }

  public void setCDType(ASTCDClass anInterface) {
  }

  public List<FuncDecl<Sort>> getAttributes() {
    return attributes;
  }

  public Map<ASTCDType, Constructor<? extends Sort>> getSubClassConstructorList() {
    return subClassConstructorList;
  }

  public UninterpretedSort getSort() {
    return sort;
  }

  public void setSort(UninterpretedSort sort) {
    this.sort = sort;
  }

  public void addSubclassConstructor(ASTCDType astcdClass, Constructor<Sort> constructor) {
    subClassConstructorList.put(astcdClass, constructor);
  }

  public void addConvert2SuperInterfFunc(ASTCDInterface astcdType, FuncDecl<UninterpretedSort> convert2SuperInterface) {
    this.convert2SuperInterface.put(astcdType, convert2SuperInterface);
  }

  public Map<ASTCDAssociation, SMTAssociation> getSMTAssociations() {
    return smtAssociations;
  }

  public void setGetSubClass(FuncDecl<Sort> getSubClass) {
    this.getSubClass = getSubClass;
  }

  public FuncDecl<Sort> getSubClass() {
    return getSubClass;
  }

}
