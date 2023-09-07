package de.monticore.cddiff.syndiff.OD;

import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odattribute._ast.ASTODList;
import de.monticore.odattribute._ast.ASTODMap;
import de.monticore.odbasis.ODBasisMill;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink.ODLinkMill;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkBuilder;
import de.monticore.odlink._ast.ASTODLinkLeftSideBuilder;
import de.monticore.odlink._ast.ASTODLinkRightSideBuilder;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class ODBuilder implements IODBuilder{
  @Override
  public ASTODAttribute buildAttr(String type, String name, String value) {
    Optional<ASTODAttribute> attribute = Optional.empty();
    try{
      attribute = OD4ReportMill.parser().parse_StringODAttribute(type+ " " + name + "=" + value +";");
    } catch (Exception exception){
      Log.error("Attributes couldn't be created");
    }
    assert Objects.requireNonNull(attribute).isPresent();
    return attribute.get();
  }
  @Override
  public ASTODAttribute buildAttr(String type, String name) {
    Optional<ASTODAttribute> attribute = Optional.empty();
    try{
      attribute = OD4ReportMill.parser().parse_StringODAttribute(type+ " " + name + ";");
    } catch (Exception exception){
      Log.error("Attributes couldn't be created");
    }
    assert Objects.requireNonNull(attribute).isPresent();
    return attribute.get();
  }

  private ASTODAttribute createListAttribute(ASTModifier modifier, String name, String value){
    ASTODList astodList = OD4ReportMill.oDListBuilder()
      .addODValue(OD4ReportMill.oDSimpleAttributeValueBuilder()
        .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
        .build())
      .addODValue(OD4ReportMill.oDAbsentBuilder().build())
      .build();

    ASTODAttributeBuilder listAttribute = ODBasisMill.oDAttributeBuilder().setModifier(modifier).setName(name);

    listAttribute.setMCType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(OD4ReportMill.mCQualifiedNameBuilder()
          .addParts("List")
          .build())
        .build())
      .setComplete("=")
      .setODValue(astodList)
      .build();

    return listAttribute.build();
  }

  private ASTODAttribute createSetAttribute(ASTModifier modifier, String name, String value){
    ASTODSimpleAttributeValue attributeValue =
      ODBasisMill.oDSimpleAttributeValueBuilder()
        .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
        .build();
    ASTODAttributeBuilder setAttribute = ODBasisMill.oDAttributeBuilder().setModifier(modifier).setName(name);

    setAttribute.setMCType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(OD4ReportMill.mCQualifiedNameBuilder()
          .addParts("List")
          .build())
        .build())
      .setComplete("=")
      .setODValue(attributeValue)
      .build();

    return setAttribute.build();
  }

  private ASTODAttribute createOptionalAttribute(ASTModifier modifier, String name, String value){
    ASTODSimpleAttributeValue attributeValue = ODBasisMill.oDSimpleAttributeValueBuilder()
      .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
      .build();

    ASTODAttributeBuilder optAttribute = ODBasisMill.oDAttributeBuilder().setModifier(modifier).setName(name);

    optAttribute.setMCType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(OD4ReportMill.mCQualifiedNameBuilder()
          .addParts("List")
          .build())
        .build())
      .setComplete("=")
      .setODValue(attributeValue)
      .build();

    return optAttribute.build();
  }

  private ASTODAttribute createMapAttribute(ASTModifier modifier, String name, String value){
    String[] parts = value.split(",", 2);
    ASTODMap astodMap = OD4ReportMill.oDMapBuilder()
      .addODMapElement(OD4ReportMill.oDMapElementBuilder()
        .setKey(OD4ReportMill.oDSimpleAttributeValueBuilder()
          .setExpression(OD4ReportMill.nameExpressionBuilder().setName(parts[0]).build())
          .build())
        .setVal(OD4ReportMill.oDSimpleAttributeValueBuilder()
          .setExpression(OD4ReportMill.nameExpressionBuilder().setName(parts[1]).build())
          .build())
        .build())
      .addODMapElement(OD4ReportMill.oDMapElementBuilder()
        .setKey(OD4ReportMill.oDAbsentBuilder().build())
        .setVal(OD4ReportMill.oDAbsentBuilder().build())
        .build())
      .build();
    ASTODAttributeBuilder mapAttribute = ODBasisMill.oDAttributeBuilder().setModifier(modifier).setName(name);
    mapAttribute.setMCType(OD4ReportMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(OD4ReportMill.mCQualifiedNameBuilder()
          .addParts("Map")
          .build())
        .build())
      .setComplete("=")
      .setODValue(astodMap)
      .build();
    return mapAttribute.build();
  }
  private ASTODAttribute createEnumAttribute(ASTModifier modifier, String name, String value){
    ASTODAttributeBuilder attributeBuilder = ODBasisMill.oDAttributeBuilder().setModifier(modifier).setName(name).setComplete("=");
    attributeBuilder.setName(name);
    attributeBuilder.setMCType(ODBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ODBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Collections.singletonList(name))
        .build())
      .build());

    ASTLiteralExpression expression = ODBasisMill.literalExpressionBuilder()
      .setLiteral(ODBasisMill.stringLiteralBuilder().setSource(value).build())
      .build();
    attributeBuilder.setODValue(
      ODBasisMill.oDSimpleAttributeValueBuilder().setExpression(expression).build());

    return attributeBuilder.build();
  }

  @Override
  public ASTODObject buildObj(String id, String type, Collection<String> types, Collection<ASTODAttribute> attrs) {
    ASTODNamedObjectBuilder objectBuilder = ODBasisMill.oDNamedObjectBuilder().setModifier(ODBasisMill.modifierBuilder().build()).setName(id);

    objectBuilder.setName(id);

    objectBuilder.setModifier(OD4ReportMill.modifierBuilder()
      .setStereotype(OD4ReportMill.stereotypeBuilder().addValues(OD4ReportMill.stereoValueBuilder()
        .setName("instanceof")
        .setContent(String.join(", ", types))
        .setText(OD4ReportMill.stringLiteralBuilder()
          .setSource(String.join(", ", types))
          .build()).build()).build()).build());

    objectBuilder.setMCObjectType(ODBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ODBasisMill.mCQualifiedNameBuilder()
        .setPartsList(Collections.singletonList(type))
        .build())
      .build());

    objectBuilder.setODAttributesList(new ArrayList<>(attrs));
    return objectBuilder.build();
  }
  @Override
  public ASTODLink buildLink(ASTODObject srcObj, String roleNameSrc, String roleNameTgt, ASTODObject trgObj, AssocDirection direction) {
    ASTODLinkBuilder linkBuilder = ODLinkMill.oDLinkBuilder();

    ASTODLinkLeftSideBuilder leftSideBuilder = ODLinkMill.oDLinkLeftSideBuilder().setModifier(ODBasisMill.modifierBuilder().build()).setODLinkQualifierAbsent().setRole(roleNameSrc);
    ASTODLinkRightSideBuilder rightSideBuilder = ODLinkMill.oDLinkRightSideBuilder().setModifier(ODBasisMill.modifierBuilder().build()).setODLinkQualifierAbsent().setRole(roleNameTgt);

    ASTODNameBuilder nameBuilder = ODBasisMill.oDNameBuilder().setName(srcObj.getName());
    ASTODNameBuilder nameBuilder1 = ODBasisMill.oDNameBuilder().setName(trgObj.getName());

    leftSideBuilder.setReferenceNamesList(Collections.singletonList(nameBuilder.build()));
    rightSideBuilder.setReferenceNamesList(Collections.singletonList(nameBuilder1.build()));

    linkBuilder.setODLinkLeftSide(leftSideBuilder.build());
    linkBuilder.setODLinkRightSide(rightSideBuilder.build());

    if(direction == AssocDirection.BiDirectional)
      linkBuilder.setODLinkDirection(ODLinkMill.oDBiDirBuilder().build());//bidirektional
    else if (direction == AssocDirection.LeftToRight){
      linkBuilder.setODLinkDirection(ODLinkMill.oDLeftToRightDirBuilder().build());//links nach rechts
    } else if (direction == AssocDirection.RightToLeft){
      linkBuilder.setODLinkDirection(ODLinkMill.oDRightToLeftDirBuilder().build());//rechts nach links
    }

    linkBuilder.setLink(true);//nur links

    return linkBuilder.build();
  }
}
