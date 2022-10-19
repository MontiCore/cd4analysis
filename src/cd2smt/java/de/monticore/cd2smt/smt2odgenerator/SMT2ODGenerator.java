package de.monticore.cd2smt.smt2odgenerator;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.ODHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.CDArtifacts.SMTCDType;
import de.monticore.cd2smt.context.CDArtifacts.SMTInterface;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.context.ODArtifacts.LinkedSMTObject;
import de.monticore.cd2smt.context.ODArtifacts.SMTLink;
import de.monticore.cd2smt.context.ODArtifacts.SMTObject;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.ASTODLink;

import java.util.*;


public class SMT2ODGenerator {
  public Map<Expr<Sort>, SMTObject> objectMap;

  public SMT2ODGenerator() {
    objectMap = new HashMap<>();
  }

  public Map<Expr<Sort>, SMTObject> getObjectMap() {
    return objectMap;
  }


  public Optional<ASTODArtifact> buildOd(CDContext cdContext, String ODName) {
    return buildOd(cdContext, ODName, false);
  }

  public Optional<ASTODArtifact> buildOd(CDContext cdContext) {
    return buildOd(cdContext, "SMTOD");
  }

  public Optional<ASTODArtifact> buildOd(CDContext cdContext, String ODName, boolean partial) {
    //get All Constraints
    List<IdentifiableBoolExpr> constraints = new ArrayList<>();
    constraints.addAll(cdContext.getAssociationConstraints());
    constraints.addAll(cdContext.getInheritanceConstraints());

    //get Model
    Solver solver = CDContext.makeSolver(cdContext.getContext(), constraints);

    if (solver.check() != Status.SATISFIABLE) {
      return Optional.empty();
    }
    return buildOdFromSolver(solver,cdContext,ODName,partial);
  }
  public Optional<ASTODArtifact> buildOdFromSolver( Solver solver,CDContext cdContext, String ODName, boolean partial) {
    //get Model
    Model model = solver.getModel();

    buildObjectMap(cdContext, model, partial);

    List<ASTODElement> elementList = new ArrayList<>();
    //add all Objects
    for (SMTObject obj : objectMap.values()) {
      elementList.add(buildObject(obj));
    }

    // add all links
    for (SMTLink smtLink : buildLinkSet(objectMap, model)) {
      elementList.add(buildLink(smtLink));
    }

    return Optional.of(ODHelper.buildOD(ODName, elementList));
  }


  protected ASTODNamedObject buildObject(SMTObject obj) {
    List<ASTODAttribute> attributeList = new ArrayList<>();
    attributeList = getAllSuperInstanceAttribute(obj, attributeList);

    return ODHelper.buildObject(obj.getSmtExpr().toString().replace("!val!", ""),
      SMTNameHelper.printObjectType(obj),
      attributeList);
  }

  protected List<ASTODAttribute> getAllSuperInstanceAttribute(SMTObject obj, List<ASTODAttribute> attributeList) {
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
    return ODHelper.buildLink(buildObject(smtLink.getLeftObject()).getName(), buildObject(smtLink.getRightObject()).getName(),
      smtLink.getSmtAssociation().getLeftRole(), smtLink.getSmtAssociation().getRightRole());
  }


  protected ASTODAttribute buildAttribute(Map.Entry<FuncDecl<? extends Sort>, Expr<Sort>> smtAttribute) {
    return ODHelper.buildAttribute(smtAttribute.getKey().getName().toString().split("_attrib_")[1],
      CDHelper.sort2MCType(smtAttribute.getValue().getSort()), smtAttribute.getValue().toString());
  }


  protected List<LinkedSMTObject> getSuperInstanceLinks(SMTObject obj, List<LinkedSMTObject> linkedObjects) {
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
    for (Map.Entry<FuncDecl<? extends Sort>, Expr<Sort>> entry : obj.getAttributes().entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }

  protected Set<SMTLink> buildLinkSet(Map<Expr<Sort>, SMTObject> objectMap, Model model) {
    Set<SMTLink> links = new HashSet<>();
    //inherit links of sub instances
    for (SMTObject obj : objectMap.values()) {
      for (LinkedSMTObject linkedObj : getSuperInstanceLinks(obj, new ArrayList<>())) {
        if (objectMap.containsKey(linkedObj.getLinkedObject().getSmtExpr()) && linkedObj.isLeft()) {
          links.add(new SMTLink(linkedObj.getLinkedObject(), obj, linkedObj.getAssociation()));
        }
      }
    }
    //linked class whose superclasses are linked
    for (SMTObject obj1 : objectMap.values()) {
      for (SMTObject obj2 : objectMap.values()) {
        Optional<SMTLink> isLink = haveLinkedSuperInstances(obj1, obj2, model);
        if (isLink.isPresent() && !SMTLink.containsLink(links, isLink.get())) {
          links.add(isLink.get());
        }
      }
    }

    return links;
  }

  protected Optional<SMTLink> haveLinkedSuperInstances(SMTObject leftObj, SMTObject rightObj, Model model) {
    for (LinkedSMTObject left : getSuperInstanceLinks(leftObj, new ArrayList<>())) {
      for (LinkedSMTObject right : getSuperInstanceLinks(rightObj, new ArrayList<>())) {
        if (left.getAssociation().equals(right.getAssociation()) && left.isLeft() && right.isRight()) {
          if (model.evaluate(left.getAssociation().getAssocFunc().apply(left.getLinkedObject().getSmtExpr(),
            right.getLinkedObject().getSmtExpr()), true).getBoolValue() == Z3_lbool.Z3_L_TRUE) {
            return Optional.of(new SMTLink(rightObj, leftObj, right.getAssociation()));
          }
        }
      }
    }
    return Optional.empty();
  }

  protected List<Expr<? extends Sort>> getAllUniverseObject(Model model, CDContext cdContext, boolean partial) {
    //interfaces , abstract and superInstance must be deleted
    List<Expr<? extends Sort>> objToDelete = new ArrayList<>();
    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {
        SMTCDType smtcdType = cdContext.getSMTCDType(mySort.toString().split("_")[0]).get();
        if (smtcdType instanceof SMTInterface) {
          objToDelete.add(smtExpr);
        }
        SMTObject obj = new SMTObject(smtExpr, smtcdType);
        for (FuncDecl<Sort> func : smtcdType.getAttributes()) {
          Expr<Sort> attr = model.eval(func.apply(smtExpr), !partial);
          if (attr.getNumArgs() == 0) {
            obj.addAttribute(func, attr);
          }
        }
        objectMap.put(smtExpr, obj);
      }
    }
    return objToDelete;
  }

  protected void getLinkedObject(Model model, CDContext cdContext) {
    for (SMTAssociation assoc : cdContext.getSMTAssociations().values()) {
      Sort leftSort = assoc.getAssocFunc().getDomain()[0];
      Sort rightSort = assoc.getAssocFunc().getDomain()[1];

      for (SMTObject leftObj : objectMap.values()) {
        for (SMTObject rightObj : objectMap.values()) {
          if ((leftObj.hasSort(leftSort)) && (rightObj.hasSort(rightSort))) {
            if ((model.eval(assoc.getAssocFunc().apply(leftObj.getSmtExpr(), rightObj.getSmtExpr()), true).getBoolValue() == Z3_lbool.Z3_L_TRUE)) {
              leftObj.getLinkedObjects().add(new LinkedSMTObject(rightObj, assoc, false));
              rightObj.getLinkedObjects().add(new LinkedSMTObject(leftObj, assoc, true));
            }
          }
        }
      }
    }
  }

  protected List<Expr<? extends Sort>> getSuperInstances(Model model, CDContext cdContext) {
    List<Expr<? extends Sort>> objToDelete = new ArrayList<>();
    for (SMTObject obj : objectMap.values()) {
      FuncDecl<UninterpretedSort> convertToSuperClass = cdContext.getSMTCDType(obj.getSmtExpr().getSort().toString().split("_")[0]).get().getConvert2Superclass();
      if (convertToSuperClass != null) {
        Expr<? extends Sort> superObj = model.eval(convertToSuperClass.apply(obj.getSmtExpr()), true);
        obj.setSuperClass(objectMap.get(superObj));
        objToDelete.add(superObj);
      }

      Map<ASTCDInterface, FuncDecl<UninterpretedSort>> convert2SuperInterfaceList = cdContext.getSMTCDType(SMTNameHelper.buildClassName(obj)).get().getConvert2SuperInterface();
      for (FuncDecl<UninterpretedSort> convert2SuperInterface : convert2SuperInterfaceList.values()) {
        Expr<? extends Sort> superObj = model.eval(convert2SuperInterface.apply(obj.getSmtExpr()), true);
        obj.addSuperInterfaceList(objectMap.get(superObj));
        objToDelete.add(superObj);
      }
    }
    return objToDelete;
  }

  protected void buildObjectMap(CDContext cdContext, Model model, Boolean partial) {
    //get all objects
    List<Expr<? extends Sort>> objToDelete = getAllUniverseObject(model, cdContext, partial);

    //get link between Objects
    getLinkedObject(model, cdContext);

    //get the superclass instances
    objToDelete.addAll(getSuperInstances(model, cdContext));

    ////remove the subclass instances and their links and Interface  objects
    objToDelete.forEach(o -> objectMap.remove(o));

  }

}





