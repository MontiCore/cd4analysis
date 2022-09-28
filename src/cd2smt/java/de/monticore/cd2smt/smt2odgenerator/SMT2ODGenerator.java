package de.monticore.cd2smt.smt2odgenerator;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.*;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.ODArtifacts.LinkedSMTObject;
import de.monticore.cd2smt.context.ODArtifacts.SMTLink;
import de.monticore.cd2smt.context.ODArtifacts.SMTObject;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;



public class SMT2ODGenerator {
  public Map<Expr<Sort>, SMTObject> objectMap;

  public SMT2ODGenerator() {
    objectMap = new HashMap<>();
  }

  public Map<Expr<Sort>, SMTObject> getObjectMap() {
    return objectMap;
  }

  public Optional <ASTODArtifact> buildOd(CDContext cdContext, ASTCDDefinition cd, String ODName) {
    return buildOd(cdContext, cd, ODName, false);
  }

  public Optional< ASTODArtifact> buildOd(CDContext cdContext, ASTCDDefinition cd) {
    return buildOd(cdContext, cd, "SMTOD");
  }

  public Optional <ASTODArtifact> buildOd(CDContext cdContext, ASTCDDefinition cd, String ODName, boolean partial) {
    OD4ReportMill.init();

    //get All Constraints
    List<Pair<String, BoolExpr>> constraints = new ArrayList<>();
    constraints.addAll(cdContext.getOclConstraints());
    constraints.addAll(cdContext.getAssociationConstraints());
    constraints.addAll(cdContext.getInheritanceConstraints());

    //get Model
    Optional<Model> modelOpt = cdContext.getModel(cdContext.getContext(), constraints);
    if (modelOpt.isEmpty()){
      return  Optional.empty();
    }

    Map<Expr<Sort>, SMTObject> objectMap = buildObjectMap(cdContext, modelOpt.get(),cd, partial);

    List<ASTODElement> elementList = new ArrayList<>();
    //add all Objects
    for (SMTObject obj : objectMap.values()) {
      elementList.add(buildObject(obj));
    }

    // add all links
    for (SMTLink smtLink : buildLinkSet(objectMap)) {
      elementList.add(buildLink(smtLink));
    }

    return Optional.of( OD4ReportMill.oDArtifactBuilder()
      .setObjectDiagram(OD4ReportMill.objectDiagramBuilder()
        .setName(ODName)
        .setStereotypeAbsent()
        .setODElementsList(elementList)
        .build())
      .build());
  }


  ASTODNamedObject buildObject(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = new ArrayList<>();
    if (obj.isPresentSuperclass()) {
      attributeList = getAllObjectAttribute(obj.getSuperClass(), attributeList);
    }
    attributeList.addAll(buildAttributeList(obj));

    return OD4ReportMill.oDNamedObjectBuilder()
      .setName(obj.getSmtExpr().toString().replace("!val!", ""))
      .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(SMTNameHelper.printObjectName(obj)))
        .build())
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setODAttributesList(attributeList)
      .build();
  }

  protected ASTODLink buildLink(SMTLink smtLink) {
    OD4ReportMill.init();

    List<ASTODName> leftRefList = new ArrayList<>();

    List<ASTODName> rightRefList = new ArrayList<>();

    leftRefList.add(OD4ReportMill.oDNameBuilder().setName(buildObject(smtLink.getLeftObject()).getName()).build());

    rightRefList.add(OD4ReportMill.oDNameBuilder().setName(buildObject(smtLink.getRightObject()).getName()).build());

    return OD4ReportMill.oDLinkBuilder()
      .setLink(true)
      .setODLinkDirection(OD4ReportMill.oDUnspecifiedDirBuilder().build())
      .setODLinkLeftSide(OD4ReportMill.oDLinkLeftSideBuilder()
        .setODLinkQualifierAbsent()
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setRole(smtLink.getSmtAssociation().getLeftRole())
        .setReferenceNamesList(leftRefList)
        .build())
      .setODLinkRightSide(OD4ReportMill.oDLinkRightSideBuilder()
        .setODLinkQualifierAbsent()
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setRole(smtLink.getSmtAssociation().getRightRole())
        .setReferenceNamesList(rightRefList)
        .build())
      .setStereotypeAbsent()
      .setNameAbsent()
      .build();
  }

  protected ASTODAttribute buildAttribute(Map.Entry<FuncDecl<? extends Sort>, Expr<Sort>> smtAttribute) {
    return  OD4ReportMill.oDAttributeBuilder()
      .setName(smtAttribute.getKey().getName().toString().split("_attrib_")[1])
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setStereotypeAbsent()
      .setComplete("")
      .setODValue(OD4ReportMill.oDNameBuilder().setName(smtAttribute.getValue().toString()).build())
      .setMCType(CDHelper.sort2MCType(smtAttribute.getValue().getSort())).build();
  }

  protected List<LinkedSMTObject> buildLinkSet(SMTObject obj, List<LinkedSMTObject> linkedObjects) {
    linkedObjects.addAll(obj.getLinkedObjects());
    if (!obj.isPresentSuperclass()) {
      return linkedObjects;
    }
    return buildLinkSet(obj.getSuperClass(), linkedObjects);
  }


  protected List<ASTODAttribute> getAllObjectAttribute(SMTObject obj, List<ASTODAttribute> attribList) {
    attribList.addAll(buildAttributeList(obj));
    if (!obj.isPresentSuperclass()) {
      return attribList;
    }
    return getAllObjectAttribute(obj.getSuperClass(), attribList);
  }

  protected List<ASTODAttribute> buildAttributeList(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = new ArrayList<>();
    for (Map.Entry<FuncDecl<? extends Sort>, Expr<Sort>> entry : obj.getAttributes().entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }

  protected Set<SMTLink> buildLinkSet(Map<Expr<Sort>, SMTObject> objectMap) {
    Set<SMTLink> links = new HashSet<>();
    for (SMTObject obj : objectMap.values()) {
      for (LinkedSMTObject linkedObj : buildLinkSet(obj, new ArrayList<>())) {
        if (objectMap.containsKey(linkedObj.getLinkedObject().getSmtExpr()) && linkedObj.isLeft()) {
          links.add(new SMTLink(linkedObj.getLinkedObject(), obj, linkedObj.getAssociation()));
        }
        if (objectMap.containsKey(linkedObj.getLinkedObject().getSmtExpr()) && linkedObj.isRight()) {
          links.add(new SMTLink(obj, linkedObj.getLinkedObject(), linkedObj.getAssociation()));
        }
      }
    }
    return links;
  }


  protected void getAllUniverseObject(Model model, CDContext cdContext, ASTCDDefinition cd, boolean partial) {
    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {
        SMTObject obj = new SMTObject(smtExpr);
        for (FuncDecl<Sort> func : cdContext.getSmtClasses().get(CDHelper.getClass(mySort.toString().split("_")[0], cd)).getAttributes()) {
          Expr<Sort> attr = model.eval(func.apply(smtExpr), !partial);
          if (attr.getNumArgs() == 0) {
            obj.addAttribute(func, attr);
          }
        }
        objectMap.put(smtExpr, obj);
      }
    }
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
            }
          }
        }
      }
    }
  }

  protected List<Expr<? extends Sort>> getSuperInstances(Model model, CDContext cdContext, ASTCDDefinition cd) {
    List<Expr<? extends Sort>> objToDelete = new ArrayList<>();
    for (SMTObject obj : objectMap.values()) {
      FuncDecl<UninterpretedSort> convertTo = cdContext.getSmtClasses().
        get(CDHelper.getClass(obj.getSmtExpr().getSort().toString().
          split("_")[0], cd)).getConvert2Superclass();
      if (convertTo != null) {
        Expr<? extends Sort> subObj = model.eval(convertTo.apply(obj.getSmtExpr()), true);
        obj.setSuperClass(objectMap.get(subObj));
        objToDelete.add(subObj);
      }
    }
    return objToDelete;
  }

  protected  Map<Expr<Sort>, SMTObject> buildObjectMap(CDContext cdContext,Model model, ASTCDDefinition cd, Boolean partial) {
    //get all objects
    getAllUniverseObject(model, cdContext, cd, partial);

    //get link between Objects
    getLinkedObject(model, cdContext);

    //get the superclass instances
    List<Expr<? extends Sort>> objToDelete = getSuperInstances(model, cdContext, cd);

    ////remove the subclass instances and their links
    objToDelete.forEach(o -> objectMap.remove(o));
    return objectMap;
  }

}





