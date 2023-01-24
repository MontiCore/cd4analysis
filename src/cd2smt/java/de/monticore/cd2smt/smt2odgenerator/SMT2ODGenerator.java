/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.smt2odgenerator;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Sort;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.ODHelper;
import de.monticore.cd2smt.Helper.SMTHelper;
import de.monticore.cd2smt.ODArtifacts.LinkedSMTObject;
import de.monticore.cd2smt.ODArtifacts.SMTLink;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.ASTODLink;
import java.util.*;

public class SMT2ODGenerator {

  public Optional<ASTODArtifact> buildOd(Set<SMTObject> objectSet, String ODName, Model model) {
    List<ASTODElement> elementList = new ArrayList<>();
    // add all Objects
    for (SMTObject obj : objectSet) {
      elementList.add(buildObject(obj));
    }

    // add all links
    for (SMTLink smtLink : buildLinkSet(objectSet, model)) {
      elementList.add(buildLink(smtLink));
    }

    return Optional.of(ODHelper.buildOD(ODName, elementList));
  }

  protected ASTODNamedObject buildObject(SMTObject obj) {
    List<ASTODAttribute> attributeList = new ArrayList<>();
    attributeList = getAllSuperInstanceAttribute(obj, attributeList);

    return ODHelper.buildObject(
        SMTHelper.buildObjectName(obj.getSmtExpr()), obj.getASTCDType().getName(), attributeList);
  }

  protected List<ASTODAttribute> getAllSuperInstanceAttribute(
      SMTObject obj, List<ASTODAttribute> attributeList) {
    attributeList.addAll(buildAttributeList(obj));
    if (obj.isPresentSuperclass()) {
      getAllSuperInstanceAttribute(obj.getSuperClass(), attributeList);
    }
    if (obj.isPresentSuperInterface()) {
      obj.getSuperInterfaceList().forEach(s -> getAllSuperInstanceAttribute(s, attributeList));
    }
    return attributeList;
  }

  protected ASTODLink buildLink(SMTLink smtLink) {
    return ODHelper.buildLink(
        buildObject(smtLink.getLeftObject()).getName(),
        buildObject(smtLink.getRightObject()).getName(),
        smtLink.getAssociation().getLeft().getCDRole().getName(),
        smtLink.getAssociation().getRight().getCDRole().getName());
  }

  protected ASTODAttribute buildAttribute(
      Map.Entry<ASTCDAttribute, Expr<? extends Sort>> smtAttribute) {
    return ODHelper.buildAttribute(
        smtAttribute.getKey().getName(),
        CDHelper.sort2MCType(smtAttribute.getValue().getSort()),
        smtAttribute.getValue().toString());
  }

  protected List<LinkedSMTObject> getSuperInstanceLinks(
      SMTObject obj, List<LinkedSMTObject> linkedObjects) {
    linkedObjects.addAll(obj.getLinkedObjects());
    if (obj.isPresentSuperclass()) {
      getSuperInstanceLinks(obj.getSuperClass(), linkedObjects);
    }
    if (obj.isPresentSuperInterface()) {
      obj.getSuperInterfaceList().forEach(s -> getSuperInstanceLinks(s, linkedObjects));
    }
    return linkedObjects;
  }

  protected List<ASTODAttribute> buildAttributeList(SMTObject obj) {
    List<ASTODAttribute> attributeList = new ArrayList<>();
    for (Map.Entry<ASTCDAttribute, Expr<? extends Sort>> entry : obj.getAttributes().entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }

  protected Set<SMTLink> buildLinkSet(Set<SMTObject> objectMap, Model model) {
    Set<SMTLink> links = new HashSet<>();
    // inherit links of sub instances
    for (SMTObject obj : objectMap) {
      for (LinkedSMTObject linkedObj : getSuperInstanceLinks(obj, new ArrayList<>())) {
        if (objectMap.stream()
                .anyMatch(o -> o.getSmtExpr().equals(linkedObj.getLinkedObject().getSmtExpr()))
            && linkedObj.isLeft()) {
          links.add(new SMTLink(linkedObj.getLinkedObject(), obj, linkedObj.getAssociation()));
        }
      }
    }
    // linked class whose superclasses are linked
    for (SMTObject obj1 : objectMap) {
      for (SMTObject obj2 : objectMap) {
        Optional<SMTLink> isLink = haveLinkedSuperInstances(obj1, obj2, model);
        if (isLink.isPresent() && !SMTLink.containsLink(links, isLink.get())) {
          links.add(isLink.get());
        }
      }
    }

    return links;
  }

  protected Optional<SMTLink> haveLinkedSuperInstances(
      SMTObject leftObj, SMTObject rightObj, Model model) {
    for (LinkedSMTObject left : getSuperInstanceLinks(leftObj, new ArrayList<>())) {
      for (LinkedSMTObject right : getSuperInstanceLinks(rightObj, new ArrayList<>())) {
        if (left.getAssociation().equals(right.getAssociation())
            && left.isLeft()
            && right.isRight()) {
          if (model
                  .evaluate(
                      left.getAssocFunc()
                          .apply(
                              left.getLinkedObject().getSmtExpr(),
                              right.getLinkedObject().getSmtExpr()),
                      true)
                  .getBoolValue()
              == Z3_lbool.Z3_L_TRUE) {
            return Optional.of(new SMTLink(rightObj, leftObj, right.getAssociation()));
          }
        }
      }
    }
    return Optional.empty();
  }
}
