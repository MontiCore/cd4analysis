package de.monticore.cd2smt.smt2odgenerator;

import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2smt.context.ODContext;
import de.monticore.cd2smt.context.SMTObject;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;

public class SMT2ODGenerator {
  public SMT2ODGenerator() {}

  public  List<SMTObject> getSuperClassLinks(SMTObject obj, List<SMTObject> linkedObjects){
    linkedObjects.addAll(obj.getLinkedObjects()) ;
    if (!obj.getSuperClass().isPresent()){
      return linkedObjects ;
    }
    return getSuperClassLinks(obj.getSuperClass().get(),linkedObjects);
  }


  protected List<ASTODAttribute> getSubclassesAttributeList(SMTObject obj, List<ASTODAttribute> attribList){
    attribList.addAll(buildAttributeList(obj));
    if (!obj.getSuperClass().isPresent()){
      return attribList ;
    }
    return getSubclassesAttributeList(obj.getSuperClass().get(),attribList);
  }
  List<ASTODAttribute> buildAttributeList(SMTObject obj) {
    OD4ReportMill.init();
    List<ASTODAttribute> attributeList = new ArrayList<>();
    for (Map.Entry<FuncDecl, Expr<Sort>> entry : obj.getAttributes().entrySet()) {
      attributeList.add(buildAttribute(entry));
    }
    return attributeList;
  }



 /**
  * build an ASTCDAttribute from his representation in SMT
  * @param
  * */
  protected ASTODAttribute buildAttribute(Map.Entry<FuncDecl, Expr<Sort>> smtAttribute) {
    int type = Sort2Type(smtAttribute.getValue().getSort());
    ASTODAttribute attribute;
    if (type >= 7) {
      attribute = OD4ReportMill.oDAttributeBuilder()
        .setName(smtAttribute.getKey().getName().toString().split("_attrib_")[1])
        .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
        .setStereotypeAbsent()
        .setComplete("")
        .setODValue(OD4ReportMill.oDNameBuilder().setName("\"hello_world\"").build())
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
    List<ASTODAttribute> attributeList = new ArrayList<>() ;
    if (obj.getSuperClass().isPresent()){
      attributeList = getSubclassesAttributeList(obj.getSuperClass().get(),attributeList) ;
    }
    attributeList.addAll( buildAttributeList(obj));
    ASTODNamedObject object1 = OD4ReportMill.oDNamedObjectBuilder()
      .setName(obj.getSmtExpr().toString().replace("!val!",""))
      .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(obj.getSmtExpr().toString().split("_")[0]))
        .build())
      .setModifier(OD4ReportMill.modifierBuilder().setStereotypeAbsent().build())
      .setODAttributesList(attributeList)
      .build();
    return object1;
  }

  protected Set<Pair<SMTObject, SMTObject>> getLinks(ODContext odContext) {
    Set<Pair<SMTObject, SMTObject>> links = new HashSet<>();
    for (Map.Entry<Expr<Sort>, SMTObject> obj : odContext.getObjectMap().entrySet()) {
      for (SMTObject obj2 : getSuperClassLinks(obj.getValue(), new ArrayList<>())) {
        links.add(new Pair<>(obj.getValue(), obj2));
      }
    }


    Set<Pair<SMTObject, SMTObject>> links2 = new HashSet<>() ;
    links2.addAll(links) ;
    for (Pair<SMTObject, SMTObject> pair1: links2)
      for (Pair<SMTObject, SMTObject> pair2 :links2)
        if ((
          pair1.a.getSmtExpr().toString().equals(pair2.b.getSmtExpr().toString()) &&
            pair1.b.getSmtExpr().toString().equals(pair2.a.getSmtExpr().toString()))){
          links.remove(pair1) ;
        }
    return links ;
  }
  public ASTODArtifact buildOd(ODContext odContext) {
    OD4ReportMill.init();
    List<ASTODElement> elementList = new ArrayList<>();
    //add all Objects
    for (Map.Entry<Expr<Sort>, SMTObject> obj : odContext.getObjectMap().entrySet()) {
      elementList.add(buildObject(obj.getValue()));
    }
    // add all
    for (Pair<SMTObject, SMTObject> entry : getLinks(odContext)){
      elementList.add(buildLink(buildObject(entry.a),buildObject(entry.b))) ;
    }

    ASTODArtifact od = OD4ReportMill.oDArtifactBuilder()
      .setObjectDiagram(OD4ReportMill.objectDiagramBuilder()
        .setName("SMTOD")
        .setStereotypeAbsent()
        .setODElementsList(elementList)
        .build())
      .build();
    return od;
  }





}



