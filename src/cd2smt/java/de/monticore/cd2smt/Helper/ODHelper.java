package de.monticore.cd2smt.Helper;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;


public class ODHelper {

  public static ASTODArtifact buildOD(String name, List<ASTODElement> elementList) {
    return OD4ReportMill.oDArtifactBuilder()
      .setObjectDiagram(OD4ReportMill.objectDiagramBuilder()
        .setName(name)
        .setStereotypeAbsent()
        .setODElementsList(elementList)
        .build())
      .build();
  }

  public static ASTODNamedObject buildObject(String objName, String type, List<ASTODAttribute> attributeList) {
    return OD4ReportMill.oDNamedObjectBuilder()
      .setName(objName)
      .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(type))
        .build())
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setODAttributesList(attributeList)
      .build();
  }

  public static ASTODAttribute buildAttribute(String name, ASTMCType type, String value) {
    return OD4ReportMill.oDAttributeBuilder()
      .setName(name)
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setStereotypeAbsent()
      .setComplete("")
      .setODValue(OD4ReportMill.oDNameBuilder().setName(value).build())
      .setMCType(type).build();
  }

  public static ASTODLink buildLink(String leftObjName, String rightObjName, String leftRole, String rightRole) {
    ASTODLink link = buildLink(leftObjName, rightObjName, rightRole);
    link.getODLinkLeftSide().setRole(leftRole);
    return link;
  }

  public static ASTODLink buildLink(String leftObjName, String rightObjName, String rightRole) {
    List<ASTODName> leftRefList = new ArrayList<>();

    List<ASTODName> rightRefList = new ArrayList<>();

    leftRefList.add(OD4ReportMill.oDNameBuilder().setName(leftObjName).build());

    rightRefList.add(OD4ReportMill.oDNameBuilder().setName(rightObjName).build());

    return OD4ReportMill.oDLinkBuilder()
      .setLink(true)
      .setODLinkDirection(OD4ReportMill.oDUnspecifiedDirBuilder().build())
      .setODLinkLeftSide(OD4ReportMill.oDLinkLeftSideBuilder()
        .setODLinkQualifierAbsent()
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setReferenceNamesList(leftRefList)
        .build())
      .setODLinkRightSide(OD4ReportMill.oDLinkRightSideBuilder()
        .setODLinkQualifierAbsent()
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setRole(rightRole)
        .setReferenceNamesList(rightRefList)
        .build())
      .setStereotypeAbsent()
      .setNameAbsent()
      .build();
  }
}
