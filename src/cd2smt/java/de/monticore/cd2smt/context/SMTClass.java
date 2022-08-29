package de.monticore.cd2smt.context;

import com.microsoft.z3.*;
import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.*;

public class SMTClass {

   private ASTCDClass Class;
   private UninterpretedSort sort;
   private List<FuncDecl<Sort>> attributes;
   private DatatypeSort subclassDatatype;
   private FuncDecl<Sort> subClass;
   private Map<ASTCDClass, Constructor> subClassConstrList;
   private Optional<FuncDecl<UninterpretedSort>> convert2Superclass;

  public DatatypeSort getSubclassDatatype() {
    return subclassDatatype;
  }

  public FuncDecl<Sort> getSubClass() {
    return subClass;
  }

  public List<FuncDecl<Sort>> getAttributes() {
    return attributes;
  }

  public Map<ASTCDClass, Constructor> getSubClassConstrList() {
    return subClassConstrList;
  }

  public Optional<FuncDecl<UninterpretedSort>> getConvert2Superclass() {
    return convert2Superclass;
  }

  public UninterpretedSort getSort() {
    return sort;
  }


  public ASTCDClass getASTCDClass() {
    return Class;
  }

  public void setSubClass(FuncDecl<Sort> getSubClass) {
    this.subClass = getSubClass;
  }

  public void setAttributes(List<FuncDecl<Sort>> attributes) {
    this.attributes = attributes;
  }

  public void setClass(ASTCDClass aClass) {
    Class = aClass;
  }

  public void setConvert2Superclass(Optional<FuncDecl<UninterpretedSort>> convert2Superclass) {
    this.convert2Superclass = convert2Superclass;
  }




  public void setSort(UninterpretedSort sort) {
    this.sort = sort;
  }

  public void setSubClassConstrList(Map<ASTCDClass, Constructor> subClassConstrList) {
    this.subClassConstrList = subClassConstrList;
  }

  public void setSubclassDatatype(DatatypeSort subclassDatatype) {
    this.subclassDatatype = subclassDatatype;
  }

  public SMTClass() {
      attributes = new ArrayList<>();
      subClassConstrList = new HashMap<>();
      convert2Superclass = Optional.empty();
    }
}
