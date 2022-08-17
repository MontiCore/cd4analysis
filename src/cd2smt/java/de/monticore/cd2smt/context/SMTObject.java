package de.monticore.cd2smt.context;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;

import java.util.*;

public class SMTObject {

    private Expr<Sort> smtExpr;
    private Optional<SMTObject> superClass;
    private Map<FuncDecl, Expr<Sort>> attributes = new HashMap<>();
    private List<SMTObject> linkedObjects;

    SMTObject() {
      superClass = Optional.empty();
      linkedObjects = new ArrayList<>();
    }

    public void addAttribute(FuncDecl name, Expr<Sort> value) {
      attributes.put(name, value);
    }

  public Expr<Sort> getSmtExpr() {
    return smtExpr;
  }

  public List<SMTObject> getLinkedObjects() {
    return linkedObjects;
  }

  public void setAttributes(Map<FuncDecl, Expr<Sort>> attributes) {
    this.attributes = attributes;
  }

  public Map<FuncDecl, Expr<Sort>> getAttributes() {
    return attributes;
  }

  public Optional<SMTObject> getSuperClass() {
    return superClass;
  }

  public void setLinkedObjects(List<SMTObject> linkedObjects) {
    this.linkedObjects = linkedObjects;
  }

  public void setSmtExpr(Expr<Sort> smtExpr) {
    this.smtExpr = smtExpr;
  }

  public void setSuperClass(Optional<SMTObject> superClass) {
    this.superClass = superClass;
  }
}
