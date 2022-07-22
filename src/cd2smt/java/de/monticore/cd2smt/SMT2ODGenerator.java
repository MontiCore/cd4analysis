package de.monticore.cd2smt;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper;

import java.util.*;

public class SMT2ODGenerator {
  protected Set<SMTObject> objectSet;
  protected Map<SMTObject, SMTObject> linkedObject;

  static class SMTObject {
    protected String name;
    protected Sort type;
    protected Expr<Sort> smtExpr ;
    protected Map<FuncDecl, Expr<Sort>> attributes = new HashMap<>();

    SMTObject() {
    }

    public void addAttribute(FuncDecl name, Expr<Sort> value) {
      attributes.put(name, value);
    }
  }


  public SMT2ODGenerator() {
    objectSet = new HashSet<>();
    linkedObject = new HashMap<>();
  }

  protected void addObject(SMTObject obj) {
    objectSet.add(obj);
  }

  List<ASTODAttribute> buildAttributeList(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = new ArrayList<>();
    for (Map.Entry<FuncDecl, Expr<Sort>> entry : obj.attributes.entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }
  public void addLink(SMTObject o1 , SMTObject o2){
    linkedObject.put(o1,o2) ;
  }
 protected ASTODAttribute buildAttribute(Map.Entry<FuncDecl, Expr<Sort>> entry) {
    int type = Sort2Type(entry.getValue().getSort());
    ASTODAttribute attribute;
    if (type >= 7) {
      attribute = OD4ReportMill.oDAttributeBuilder()
        .setName(entry.getKey().getName().toString().split("_attrib_")[1])
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setStereotypeAbsent()
        .setComplete("")
        .setODValue(OD4ReportMill.oDNameBuilder().setName("\"hello_world\"").build())
        .setMCType(OD4ReportMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("java.lang.String")).build())
        .build();
    } else {
      attribute = OD4ReportMill.oDAttributeBuilder()
        .setName(entry.getKey().getName().toString().split("_attrib_")[1])
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setStereotypeAbsent()
        .setComplete("")
        .setODValue(OD4ReportMill.oDNameBuilder().setName(entry.getValue().toString()).build()) //type 1 = bool,  3 = char 4 = double 5=float 6= int 7=long
        .setMCType(OD4ReportMill.mCPrimitiveTypeBuilder().setPrimitive(type).build())
        //.setMCType(OD4ReportMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("int")).build())
        .build();
    }

    return attribute;
  }

protected ASTODLink buildLink(ASTODObject object1, ASTODObject object2){
  OD4ReportMill.init();

  List<ASTODName> leftRefList = new ArrayList<>();

  List<ASTODName> rightRefList = new ArrayList<>();

  leftRefList.add(OD4ReportMill.oDNameBuilder().setName(object1.getName()).build());

  rightRefList.add(OD4ReportMill.oDNameBuilder().setName(object2.getName()).build());

  //todo: helper-method for link-generation
  ASTODLink link = OD4ReportMill.oDLinkBuilder()
    .setLink(true)
    .setODLinkDirection(OD4ReportMill.oDUnspecifiedDirBuilder().build())
    .setODLinkLeftSide(OD4ReportMill.oDLinkLeftSideBuilder()
      .setODLinkQualifierAbsent()
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setRole("left")
      .setReferenceNamesList(leftRefList)
      .build())
    .setODLinkRightSide(OD4ReportMill.oDLinkRightSideBuilder()
      .setODLinkQualifierAbsent()
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setRole("right")
      .setReferenceNamesList(rightRefList)
      .build())
    .setStereotypeAbsent()
    .setNameAbsent()
    .build();

  return  link ;
}


  //type 1 = bool,  3 = char 4 = double 5=float 6= int 7=long
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

  ASTODNamedObject buildObject(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = buildAttributeList(obj);
    ASTODNamedObject object1 = OD4ReportMill.oDNamedObjectBuilder()
      .setName(obj.name.replace("!val!",""))
      .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(obj.type.toString().split("_")[0]))
        .build())
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setODAttributesList(attributeList)
      .build();
    return object1;
  }

  protected ASTODArtifact buildOd() {
    OD4ReportMill.init();
    List<ASTODElement> elementList = new ArrayList<>();
    //add all Objects
    for (SMTObject obj : objectSet) {
      elementList.add(buildObject(obj));
    }
    // add all attributes
    for (Map.Entry<SMTObject, SMTObject> entry : linkedObject.entrySet()){
      elementList.add(buildLink(buildObject(entry.getKey()),buildObject(entry.getValue()))) ;
    }

    ASTODArtifact od = OD4ReportMill.oDArtifactBuilder()
      .setObjectDiagram(OD4ReportMill.objectDiagramBuilder()
        .setName("SMTOD")
        .setStereotypeAbsent()
        .setODElementsList(elementList)
        .build())
      .build();
    System.out.println(GenerateODHelper.printOD(od));
    return od;
  }


  //elementList.add(link);


}



