package de.monticore.cd2smt.smt2odgenerator;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.LinkedSMTObject;
import de.monticore.cd2smt.context.ODContext;
import de.monticore.cd2smt.context.SMTLink;
import de.monticore.cd2smt.context.SMTObject;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;

import java.util.*;

public class SMT2ODGenerator {
  public SMT2ODGenerator() {
  }

  public ASTODArtifact buildOd(ODContext odContext) {
    OD4ReportMill.init();
    List<ASTODElement> elementList = new ArrayList<>();

    //add all Objects
    for (SMTObject obj : odContext.getObjectMap().values()) {
      elementList.add(buildObject(obj));
    }

    // add all links
    for (SMTLink smtLink : buildLinkSet(odContext)) {
      elementList.add(buildLink(smtLink));
    }

    return OD4ReportMill.oDArtifactBuilder()
      .setObjectDiagram(OD4ReportMill.objectDiagramBuilder()
        .setName("SMTOD")
        .setStereotypeAbsent()
        .setODElementsList(elementList)
        .build())
      .build();
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
    int type = Sort2Type(smtAttribute.getValue().getSort());
    ASTODAttribute attribute;
    if (type >= 7) {
      attribute = OD4ReportMill.oDAttributeBuilder()
        .setName(smtAttribute.getKey().getName().toString().split("_attrib_")[1])
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setStereotypeAbsent()
        .setComplete("")
        .setODValue(OD4ReportMill.oDNameBuilder().setName(smtAttribute.getValue().toString()).build())
        .setMCType(OD4ReportMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("java.lang.String")).build())
        .build();
    } else {
      attribute = OD4ReportMill.oDAttributeBuilder()
        .setName(smtAttribute.getKey().getName().toString().split("_attrib_")[1])
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setStereotypeAbsent()
        .setComplete("")
        .setODValue(OD4ReportMill.oDNameBuilder().setName(smtAttribute.getValue().toString()).build()) //type 1 = bool,  3 = char 4 = double 5=float 6= int 7=long
        .setMCType(OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(type).build())
        .build();
    }

    return attribute;
  }

  public List<LinkedSMTObject> buildLinkSet(SMTObject obj, List<LinkedSMTObject> linkedObjects) {
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

  List<ASTODAttribute> buildAttributeList(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = new ArrayList<>();
    for (Map.Entry<FuncDecl<? extends Sort>, Expr<Sort>> entry : obj.getAttributes().entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }

  protected Set<SMTLink> buildLinkSet(ODContext odContext) {
    Set<SMTLink> links = new HashSet<>();
    for (SMTObject obj : odContext.getObjectMap().values()) {
      for (LinkedSMTObject linkedObj : buildLinkSet(obj, new ArrayList<>())) {
        if (odContext.getObjectMap().containsKey(linkedObj.getLinkedObject().getSmtExpr()) && linkedObj.isLeft()) {
          links.add(new SMTLink(linkedObj.getLinkedObject(), obj, linkedObj.getAssociation()));
        }
        if (odContext.getObjectMap().containsKey(linkedObj.getLinkedObject().getSmtExpr()) && linkedObj.isRight()) {
          links.add(new SMTLink(obj, linkedObj.getLinkedObject(), linkedObj.getAssociation()));
        }
      }
    }
    return links;
  }


  static int Sort2Type(Sort mySort) {
    switch (mySort.toString()) {
      case "Int":
        return 6;
      case "Real":
        return 4;
      case "Bool":
        return 1;
      case "String":
        return 7;
      default:
        System.out.println("type not support");
        return 0;
    }
  }


}



