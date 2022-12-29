/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.ODArtifacts;

import com.microsoft.z3.Sort;
import java.util.ArrayList;
import java.util.List;

public class SMTObject extends MinObject {

  private final List<LinkedSMTObject> linkedObjects;
  private final List<SMTObject> superInterfaceList;
  private SMTObject superClass;

  public SMTObject(MinObject minObject) {
    super(minObject.isAbstract(), minObject.getSmtExpr(), minObject.getASTCDType());
    this.attributes = minObject.getAttributes();
    linkedObjects = new ArrayList<>();
    superInterfaceList = new ArrayList<>();
  }

  public boolean isPresentSuperclass() {
    return this.superClass != null;
  }

  public boolean isPresentSuperInterface() {
    return this.superInterfaceList.size() > 0;
  }

  public List<LinkedSMTObject> getLinkedObjects() {
    return linkedObjects;
  }

  public SMTObject getSuperClass() {
    return superClass;
  }

  public void setSuperClass(SMTObject superClass) {
    this.superClass = superClass;
  }

  public void addSuperInterfaceList(SMTObject superInterfaceList) {
    this.superInterfaceList.add(superInterfaceList);
  }

  public List<SMTObject> getSuperInterfaceList() {
    return superInterfaceList;
  }

  public boolean hasSort(Sort sort) {
    return sort.equals(this.getSmtExpr().getSort());
  }
}
