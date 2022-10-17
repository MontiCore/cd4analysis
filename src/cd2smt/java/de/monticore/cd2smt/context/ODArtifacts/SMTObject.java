package de.monticore.cd2smt.context.ODArtifacts;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.context.CDArtifacts.SMTCDType;



import java.util.*;

public class SMTObject {

    private final Expr<? extends Sort> smtExpr;
    private final SMTCDType smtcdType ;
    private SMTObject superClass;
    private List<SMTObject> superInterfaceList;
    private final Map<FuncDecl<? extends Sort>, Expr<Sort>>   attributes = new HashMap<>();
    private final List<LinkedSMTObject> linkedObjects;

    public SMTObject(Expr<? extends Sort> smtExpr, SMTCDType smtcdType) {
      this.smtExpr = smtExpr;
      this.smtcdType = smtcdType ;
      linkedObjects = new ArrayList<>();
      superInterfaceList = new ArrayList<>();
    }
  public boolean isPresentSuperclass(){
      return this.superClass != null;
  }
  public boolean isPresentSuperInterface(){
    return this.superInterfaceList.size() > 0;
  }

    public void addAttribute(FuncDecl<? extends  Sort> name, Expr<Sort> value) {
      attributes.put(name, value);
    }

  public Expr<? extends Sort> getSmtExpr() {
    return smtExpr;
  }

  public SMTCDType getSmtcdType() {
    return smtcdType;
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

  public void addSuperInterfaceList(SMTObject superInterfaceList) {
    this.superInterfaceList.add( superInterfaceList);
  }

  public List<SMTObject> getSuperInterfaceList() {
    return superInterfaceList;
  }

  public boolean hasSort(Sort sort){
      return sort.equals(this.getSmtExpr().getSort());
  }

}
