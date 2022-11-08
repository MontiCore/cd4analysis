package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapperKind;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.ASTODClassStackPack;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.CDWrapperObjectPack;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.od4report.prettyprinter.OD4ReportFullPrettyPrinter;
import de.monticore.odattribute._ast.ASTODList;
import de.monticore.odattribute._ast.ASTODMap;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink.ODLinkMill;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkDirection;
import de.monticore.prettyprint.IndentPrinter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.formatDirection;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;

public class CDSyntax2SemDiff4ASTODHelper {

  /**
   * create value for attribute
   */
  public static String createValue(CDWrapper cdw,
      Optional<CDTypeWrapperDiff> cDTypeDiff,
      String type,
      Boolean isEnumClass) {
    String result;
    String cDTypeWrapperKey = "CDWrapperEnum_" + type;
    if (cdw.getCDTypeWrapperGroup().containsKey(cDTypeWrapperKey)) {
      if (isEnumClass) {
        result = cDTypeDiff.get().getWhichAttributesDiff().get().get(0);
      }
      else {
        result = ((ASTCDEnum) cdw.getCDTypeWrapperGroup().get(cDTypeWrapperKey).getEditedElement())
            .getCDEnumConstantList().get(0).getName();
      }
    }
    else {
      result = "some_type_" + type;
    }
    return result;
  }

  /**
   * create an attribute
   */
  public static ASTODAttribute createASTODAttribute(ASTCDAttribute astcdAttribute,
      ASTODValue oDValue) {
    return OD4ReportMill.oDAttributeBuilder()
        .setName(astcdAttribute.getName())
        .setModifier(OD4ReportMill.modifierBuilder().build())
        .setMCType(OD4ReportMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(
                OD4ReportMill.mCQualifiedNameBuilder().addParts(astcdAttribute.printType()).build())
            .build())
        .setComplete("=")
        .setODValue(oDValue)
        .build();
  }

  /**
   * create all attributes for created object distinguish the type of object, simple class or
   * collection
   */
  public static List<ASTODAttribute> createASTODAttributeList(CDWrapper cdw,
      Optional<CDTypeWrapperDiff> cDTypeDiff, CDTypeWrapper cDTypeWrapper) {
    List<ASTODAttribute> astODAttributeList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : cdw.getCDTypeWrapperGroup()
        .get(cDTypeWrapper.getName())
        .getEditedElement()
        .getCDAttributeList()) {
      // set attribute
      if (Pattern.matches("List<(.*)>", astcdAttribute.printType())) {
        // List<> attribute
        Matcher listMatcher = Pattern.compile("List<(.*)>").matcher(astcdAttribute.printType());
        if (listMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, listMatcher.group(1), false);
          ASTODList oDValue = OD4ReportMill.oDListBuilder()
              .addODValue(OD4ReportMill.oDSimpleAttributeValueBuilder()
                  .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
                  .build())
              .addODValue(OD4ReportMill.oDAbsentBuilder().build())
              .build();
          astODAttributeList.add(createASTODAttribute(astcdAttribute, oDValue));
        }

      }
      else if (Pattern.matches("Set<(.*)>", astcdAttribute.printType())) {
        // Set<> attribute
        Matcher setMatcher = Pattern.compile("Set<(.*)>").matcher(astcdAttribute.printType());
        if (setMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, "Set_" + setMatcher.group(1), false);
          ASTODSimpleAttributeValue oDValue = OD4ReportMill.oDSimpleAttributeValueBuilder()
              .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
              .build();
          astODAttributeList.add(createASTODAttribute(astcdAttribute, oDValue));
        }

      }
      else if (Pattern.matches("Optional<(.*)>", astcdAttribute.printType())) {
        // Optional<> attribute
        Matcher optMatcher = Pattern.compile("Optional<(.*)>").matcher(astcdAttribute.printType());
        if (optMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, optMatcher.group(1), false);
          ASTODSimpleAttributeValue oDValue = OD4ReportMill.oDSimpleAttributeValueBuilder()
              .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
              .build();
          astODAttributeList.add(createASTODAttribute(astcdAttribute, oDValue));
        }

      }
      else if (Pattern.matches("Map<(.*),(.*)>", astcdAttribute.printType())) {
        // Map<,> attribute
        Matcher mapMatcher = Pattern.compile("Map<(.*),(.*)>").matcher(astcdAttribute.printType());
        if (mapMatcher.find()) {
          String kValue = createValue(cdw, cDTypeDiff, mapMatcher.group(1), false);
          String vValue = createValue(cdw, cDTypeDiff, mapMatcher.group(2), false);
          ASTODMap oDValue = OD4ReportMill.oDMapBuilder()
              .addODMapElement(OD4ReportMill.oDMapElementBuilder()
                  .setKey(OD4ReportMill.oDSimpleAttributeValueBuilder()
                      .setExpression(OD4ReportMill.nameExpressionBuilder().setName(kValue).build())
                      .build())
                  .setVal(OD4ReportMill.oDSimpleAttributeValueBuilder()
                      .setExpression(OD4ReportMill.nameExpressionBuilder().setName(vValue).build())
                      .build())
                  .build())
              .addODMapElement(OD4ReportMill.oDMapElementBuilder()
                  .setKey(OD4ReportMill.oDAbsentBuilder().build())
                  .setVal(OD4ReportMill.oDAbsentBuilder().build())
                  .build())
              .build();
          astODAttributeList.add(createASTODAttribute(astcdAttribute, oDValue));
        }

      }
      else {
        // normal attribute
        String value;
        if (cDTypeDiff.isPresent()) {
          if (cDTypeDiff.get().getCDDiffKind() == CDTypeDiffKind.CDDIFF_ENUM) {
            value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), true);
          }
          else {
            value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), false);
          }
        }
        else {
          value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), false);
        }
        ASTODSimpleAttributeValue oDValue = OD4ReportMill.oDSimpleAttributeValueBuilder()
            .setExpression(OD4ReportMill.nameExpressionBuilder().setName(value).build())
            .build();
        astODAttributeList.add(createASTODAttribute(astcdAttribute, oDValue));
      }
    }

    return astODAttributeList;
  }

  /**
   * create an object
   */
  public static CDWrapperObjectPack createObject(CDWrapper cdw,
      Optional<CDTypeWrapperDiff> cDTypeDiff,
      CDTypeWrapper cDTypeWrapper,
      int index,
      Optional<CDTypeWrapper> instanceClass,
      CDSemantics cdSemantics) {

    // if this CDTypeWrapper is interface or abstract class, then find a simple class on
    // inheritancePath
    CDTypeWrapper newCDTypeWrapper;
    if (instanceClass.isPresent()) {
      newCDTypeWrapper = instanceClass.get();
    }
    else {
      if (cDTypeWrapper.getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE ||
          cDTypeWrapper.getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
        List<CDTypeWrapper> cdTypeWrapperList =
            getAllSimpleSubClasses4CDTypeWrapperWithStatusOpen(cDTypeWrapper, cdw.getCDTypeWrapperGroup());

        // Guaranteed CDTypeWrapper status is OPEN
        if (cdTypeWrapperList.isEmpty()) {
          return new CDWrapperObjectPack();
        }

        newCDTypeWrapper = cdTypeWrapperList.get(0);

      }
      else {
        // Guaranteed CDTypeWrapper status is OPEN
        if (!cDTypeWrapper.isOpen()) {
          return new CDWrapperObjectPack();
        }

        newCDTypeWrapper = cDTypeWrapper;
      }
    }

    // set attributes
    List<ASTODAttribute> astODAttributeList = createASTODAttributeList(cdw, cDTypeDiff,
        newCDTypeWrapper);

    // set objects
    ASTODNamedObject astodNamedObject = null;

    if (cdSemantics == CDSemantics.SIMPLE_CLOSED_WORLD) {
      astodNamedObject = OD4ReportMill.oDNamedObjectBuilder()
          .setName(
              CDDiffUtil.processQName2RoleName(newCDTypeWrapper.getOriginalClassName().replaceAll(
                  "\\.","_")) + "__" + index)
          .setModifier(OD4ReportMill.modifierBuilder().build())
          .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(
                  newCDTypeWrapper.getOriginalClassName()))
              .build())
          .setODAttributesList(astODAttributeList)
          .build();
    }
    else if (cdSemantics == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD) {
      List<String> classList = new ArrayList<>();
      newCDTypeWrapper.getSuperclasses().forEach(e -> classList.add(e.split("_")[1]));
      String multiLabel = "instanceof";

      astodNamedObject = OD4ReportMill.oDNamedObjectBuilder()
          .setName(
              CDDiffUtil.processQName2RoleName(newCDTypeWrapper.getOriginalClassName().replaceAll(
                  "\\.","_")) + "__" + index)
          .setModifier(OD4ReportMill.modifierBuilder()
              .setStereotype(OD4ReportMill.stereotypeBuilder()
                  .addValues(OD4ReportMill.stereoValueBuilder()
                      .setName(multiLabel)
                      .setContent(String.join(", ", classList))
                      .setText(OD4ReportMill.stringLiteralBuilder()
                          .setSource(String.join(", ", classList))
                          .build())
                      .build())
                  .build())
              .build())
          .setMCObjectType(OD4ReportMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(
                  newCDTypeWrapper.getOriginalClassName()))
              .build())
          .setODAttributesList(astODAttributeList)
          .build();
    }

    return new CDWrapperObjectPack(astodNamedObject, newCDTypeWrapper);
  }

  /**
   * create all objects that should be used in an OD
   */
  public static List<ASTODNamedObject> createObjectList(CDWrapper cdw,
      Optional<CDTypeWrapperDiff> cdTypeWrapperDiff,
      CDTypeWrapper offerCDTypeWrapper,
      int cardinalityCount,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass,
      Optional<CDTypeWrapper> instanceClass,
      CDSemantics cdSemantics) {

    List<ASTODNamedObject> astODNamedObjectList = new LinkedList<>();
    List<ASTODNamedObject> tempList = new LinkedList<>();
    CDTypeWrapper actualCDTypeWrapper = offerCDTypeWrapper;
    for (int i = 0; i < cardinalityCount; i++) {
      // set objects
      CDWrapperObjectPack objectPack = createObject(cdw, cdTypeWrapperDiff, offerCDTypeWrapper, i,
          instanceClass, cdSemantics);

      // Guaranteed CDTypeWrapper status is OPEN
      if (objectPack.isEmpty()) {
        return new LinkedList<>();
      }

      actualCDTypeWrapper = objectPack.getCDTypeWrapper();
      astODNamedObjectList.add(objectPack.getNamedObject());
      tempList.add(objectPack.getNamedObject());
    }
    classStack4TargetClass.push(new ASTODClassStackPack(tempList, actualCDTypeWrapper));
    classStack4SourceClass.push(new ASTODClassStackPack(tempList, actualCDTypeWrapper));
    return astODNamedObjectList;
  }

  /**
   * create a link between two created objects
   */
  public static ASTODLink createLink(ASTODNamedObject left, ASTODNamedObject right,
      String leftRoleName, String rightRoleName, ASTODLinkDirection astodLinkDirection) {
    return OD4ReportMill.oDLinkBuilder()
        .setLink(true)
        .setODLinkLeftSide(OD4ReportMill.oDLinkLeftSideBuilder()
            .setModifier(OD4ReportMill.modifierBuilder().build())
            .addReferenceNames(0, OD4ReportMill.oDNameBuilder().setName(left.getName()).build())
            .setRole(leftRoleName)
            .build())
        .setODLinkDirection(astodLinkDirection)
        .setODLinkRightSide(OD4ReportMill.oDLinkRightSideBuilder()
            .setModifier(OD4ReportMill.modifierBuilder().build())
            .addReferenceNames(0, OD4ReportMill.oDNameBuilder().setName(right.getName()).build())
            .setRole(rightRoleName)
            .build())
        .build();
  }

  /**
   * create link for one object to two objects
   */
  public static List<ASTODLink> createLinkList(List<ASTODNamedObject> leftElementList,
      List<ASTODNamedObject> rightElementList,
      String leftRoleName,
      String rightRoleName,
      int directionType) {
    List<ASTODLink> linkElementList = new LinkedList<>();
    if (leftElementList.size() != 0 && rightElementList.size() != 0) {
      switch (directionType) {
        case 1:
          leftElementList.forEach(left -> rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(left, right, leftRoleName, rightRoleName,
                ODLinkMill.oDLeftToRightDirBuilder().build()));
          }));
          break;
        case 2:
          leftElementList.forEach(left -> rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(right, left, rightRoleName, leftRoleName,
                ODLinkMill.oDLeftToRightDirBuilder().build()));
          }));
          break;
        case 3:
          leftElementList.forEach(left -> rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(left, right, leftRoleName, rightRoleName,
                ODLinkMill.oDLeftToRightDirBuilder().build()));
            linkElementList.add(createLink(right, left, rightRoleName, leftRoleName,
                ODLinkMill.oDLeftToRightDirBuilder().build()));
          }));
          break;
      }
    }
    return linkElementList;
  }

  /**
   * create link for one object to one object
   */
  public static List<ASTODLink> createLinkList(ASTODNamedObject left, ASTODNamedObject right,
      String leftRoleName, String rightRoleName, int directionType) {
    return createLinkList(List.of(left), List.of(right), leftRoleName, rightRoleName,
        directionType);
  }

  /**
   * generate OD title for semantic difference with association
   */
  public static String generateODTitle(CDAssocWrapperDiff association, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Diff_").append(index).append("__").append("association").append("__");

    // set assoc name
    stringBuilder.append(association.getBaseElement().getCDWrapperLeftClass().getOriginalClassName())
        .append("__");
    stringBuilder.append("$").append(association.getBaseElement().getCDWrapperLeftClassRoleName()).append("$")
        .append("__");
    stringBuilder.append(formatDirection(association.getBaseElement().getCDAssociationWrapperDirection()))
        .append("__");
    stringBuilder.append("$").append(association.getBaseElement().getCDWrapperRightClassRoleName()).append("$")
        .append("__");
    stringBuilder.append(association.getBaseElement().getCDWrapperRightClass().getOriginalClassName());

    return stringBuilder.toString();
  }

  /**
   * generate OD title for semantic difference with class
   */
  public static String generateODTitle(CDTypeWrapperDiff cDTypeWrapperDiff, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Diff_").append(index).append("__");

    if (cDTypeWrapperDiff.getCDDiffCategory() == CDTypeDiffCategory.EDITED) {
      stringBuilder.append("attribute").append("__");
    } else {
      stringBuilder.append("class").append("__");
    }
    // set assoc name
    stringBuilder.append(cDTypeWrapperDiff.getName(true).split("_")[1]);

    // set which part
    if (cDTypeWrapperDiff.getCDDiffCategory() == CDTypeDiffCategory.EDITED) {
      if (cDTypeWrapperDiff.getWhichAttributesDiff().isPresent()) {
        stringBuilder.append("_");
        stringBuilder.append(cDTypeWrapperDiff.getWhichAttributesDiff()
            .get()
            .toString()
            .replace(" ", "")
            .replace(",", "_")
            .replace("[", "")
            .replace("]", ""));
      }
    }

    return stringBuilder.toString();
  }

  /**
   * generate the label of DiffCategory
   */
  public static String generateODDiffLabel(CDTypeWrapperDiff cdTypeWrapperDiff) {
    return cdTypeWrapperDiff.getCDDiffCategory().toString().toLowerCase();
  }

  /**
   * generate the label of DiffCategory
   */
  public static String generateODDiffLabel(CDAssocWrapperDiff cdAssocWrapperDiff) {
    StringBuilder stringBuilder = new StringBuilder();
    // set assoc type
    stringBuilder.append(cdAssocWrapperDiff.getCDDiffCategory().toString().toLowerCase());

    // set which part
    if (cdAssocWrapperDiff.getWhichPartDiff().isPresent()) {
      if (cdAssocWrapperDiff.getWhichPartDiff().get() != WhichPartDiff.DIRECTION) {
        stringBuilder.append("__");
        stringBuilder.append(cdAssocWrapperDiff.getWhichPartDiff().get().toString().toLowerCase());
      }
    }

    return stringBuilder.toString();
  }

  /**
   * generate SourcePosition for baseCD and compareCD
   */
  public static String generateODSourcePosition(CDTypeWrapperDiff cdTypeWrapperDiff) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<");
    stringBuilder.append(cdTypeWrapperDiff.getBaseSourcePositionStr());
    stringBuilder.append(">");
    stringBuilder.append("__");
    stringBuilder.append("<");
    stringBuilder.append(cdTypeWrapperDiff.getCompareSourcePositionStr());
    stringBuilder.append(">");
    return stringBuilder.toString();
  }

  /**
   * generate SourcePosition for baseCD and compareCD
   */
  public static String generateODSourcePosition(CDAssocWrapperDiff cdAssocWrapperDiff) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<");
    stringBuilder.append(cdAssocWrapperDiff.getBaseSourcePositionStr());
    stringBuilder.append(">");
    stringBuilder.append("__");
    stringBuilder.append("<");
    stringBuilder.append(cdAssocWrapperDiff.getCompareSourcePositionStr());
    stringBuilder.append(">");
    return stringBuilder.toString();
  }

  /**
   * generate ASTODArtifact
   */
  public static ASTODArtifact generateASTODArtifact(List<ASTODElement> astODElementList,
      String odTitle, String odSourcePosition, String odDiffLable) {
    String baseCDSrcPos = odSourcePosition.split("__")[0];
    String compareCDSrcPos = odSourcePosition.split("__")[1];
    // set ASTObjectDiagram
    ASTObjectDiagram objectDiagram = OD4ReportMill.objectDiagramBuilder()
        .setName(odTitle.replaceAll("\\.","_"))
        .setODElementsList(astODElementList)
        .setStereotype(OD4ReportMill.stereotypeBuilder()
            .addValues(OD4ReportMill.stereoValueBuilder()
                .setName("baseCD")
                .setContent(baseCDSrcPos)
                .setText(OD4ReportMill.stringLiteralBuilder()
                    .setSource(baseCDSrcPos)
                    .build())
                .build())
            .addValues(OD4ReportMill.stereoValueBuilder()
                .setName("compareCD")
                .setContent(compareCDSrcPos)
                .setText(OD4ReportMill.stringLiteralBuilder()
                    .setSource(compareCDSrcPos)
                    .build())
                .build())
            .addValues(OD4ReportMill.stereoValueBuilder()
                .setName("syntaxDiffCategory")
                .setContent(odDiffLable)
                .setText(OD4ReportMill.stringLiteralBuilder()
                    .setSource(odDiffLable)
                    .build())
                .build())
            .build())
        .build();

    return OD4ReportMill.oDArtifactBuilder()
        .setObjectDiagram(objectDiagram)
        .build();
  }

  /**
   * using pretty printer to print OD
   */
  public static String printOD(ASTODArtifact astodArtifact) {
    // pretty print the AST
    return new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(astodArtifact);
  }

  /**
   * using pretty printer to print OD
   */
  public static List<String> printODs(List<ASTODArtifact> astODArtifacts) {
    // pretty print the AST
    List<String> result = new ArrayList<>();
    for (ASTODArtifact od : astODArtifacts) {
      result.add(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    }
    return result;
  }
}
