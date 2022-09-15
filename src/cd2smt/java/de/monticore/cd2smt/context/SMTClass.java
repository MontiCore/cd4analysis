package de.monticore.cd2smt.context;

import com.microsoft.z3.*;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.*;

public class SMTClass {

  private ASTCDClass Class;
  private UninterpretedSort sort;
  private final List<FuncDecl<Sort>> attributes;
  private DatatypeSort<? extends Sort> subclassDatatype;
  private FuncDecl<Sort> subClass;
  private final Map<ASTCDAssociation, SMTAssociation> smtAssociations;
  private final Map<ASTCDClass, Constructor<? extends Sort>> subClassConstrList;
  private FuncDecl<UninterpretedSort> convert2Superclass;


  public DatatypeSort<? extends Sort> getSubclassDatatype() {
    return subclassDatatype;
  }

  public FuncDecl<Sort> getSubClass() {
    return subClass;
  }

  public List<FuncDecl<Sort>> getAttributes() {
    return attributes;
  }

  public Map<ASTCDClass, Constructor<? extends Sort>> getSubClassConstrList() {
    return subClassConstrList;
  }

  public FuncDecl<UninterpretedSort> getConvert2Superclass() {
    return convert2Superclass;
  }

  public UninterpretedSort getSort() {
    return sort;
  }

  public Map<ASTCDAssociation, SMTAssociation> getSMTAssociations() {
    return smtAssociations;
  }


  public ASTCDClass getASTCDClass() {
    return Class;
  }

  public void setSubClass(FuncDecl<Sort> getSubClass) {
    this.subClass = getSubClass;
  }



  public void setClass(ASTCDClass aClass) {
    Class = aClass;
  }

  public void setConvert2Superclass(FuncDecl<UninterpretedSort> convert2Superclass) {
    this.convert2Superclass = convert2Superclass;
  }


  public void setSort(UninterpretedSort sort) {
    this.sort = sort;
  }


  public void setSubclassDatatype(DatatypeSort<? extends Sort> subclassDatatype) {
    this.subclassDatatype = subclassDatatype;
  }

  public SMTClass() {
    smtAssociations = new HashMap<>();
    attributes = new ArrayList<>();
    subClassConstrList = new HashMap<>();
  }
}
