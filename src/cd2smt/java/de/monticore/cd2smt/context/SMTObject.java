package de.monticore.cd2smt.context;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;


import java.util.*;

public class SMTObject {

    private final Expr<? extends Sort> smtExpr;

    private SMTObject superClass;
    private final Map<FuncDecl<? extends Sort>, Expr<Sort>>   attributes = new HashMap<>();
    private final List< LinkedSMTObject> linkedObjects;

    SMTObject(Expr<? extends  Sort> smtExpr) {
      this.smtExpr = smtExpr;
      linkedObjects = new ArrayList<>();
    }
  public boolean isPresentSuperclass(){
      return  this.superClass != null;
  }
    public void addAttribute(FuncDecl<? extends  Sort> name, Expr<Sort> value) {
      attributes.put(name, value);
    }

  public Expr<? extends Sort> getSmtExpr() {
    return smtExpr;
  }

  public List<LinkedSMTObject> getLinkedObjects() {
    return linkedObjects;
  }

  public Map<FuncDecl<? extends  Sort>, Expr<Sort>> getAttributes() {
    return attributes;
  }

  public SMTObject getSuperClass() {
    return superClass;
  }

  public void setSuperClass(SMTObject superClass) {
    this.superClass = superClass;
  }

  public boolean hasSort(Sort sort){
      return sort.equals(this.getSmtExpr().getSort());
  }

}
