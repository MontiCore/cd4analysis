package de.monticore.cddiff.syn2semdiff.odgen;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syn2semdiff.datastructures.ClassSide;
import de.monticore.cddiff.syn2semdiff.helpers.ODGenHelper;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Package {
  private final ASTODObject leftObject;
  private final boolean isProcessedLeft;
  private final ASTODObject rightObject;
  private final boolean isProcessedRight;
  private final ASTODLink association;
  private final ASTCDAssociation astcdAssociation;
  private final ClassSide side;
  private final ODBuilder ODBuilder = new ODBuilder();
  private final ODGenHelper odGenHelper;

  public Package(
      ASTCDClass leftObject,
      String idSrc,
      ASTCDClass rightObject,
      String idTgt,
      ASTCDAssociation association,
      ClassSide side,
      boolean isProcessedLeft,
      boolean isProcessedRight,
      Syn2SemDiffHelper helper,
      ODGenHelper odGenHelper) {
    this.odGenHelper = odGenHelper;
    this.leftObject =
        ODBuilder.buildObj(
            idSrc,
            leftObject.getSymbol().getInternalQualifiedName(),
            odGenHelper.getSuperTypes(leftObject),
            getAttributesOD(leftObject, helper));
    this.rightObject =
        ODBuilder.buildObj(
            idTgt,
            rightObject.getSymbol().getInternalQualifiedName(),
            odGenHelper.getSuperTypes(rightObject),
            getAttributesOD(rightObject, helper));
    this.association =
        ODBuilder.buildLink(
            this.leftObject,
            CDDiffUtil.inferRole(association.getLeft()),
            CDDiffUtil.inferRole(association.getRight()),
            this.rightObject,
            Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(
      ASTODObject leftObject,
      ASTODObject rightObject,
      ASTCDAssociation association,
      ClassSide side,
      boolean isProcessedLeft,
      boolean isProcessedRight,
      ODGenHelper odGenHelper) {
    this.leftObject = leftObject;
    this.rightObject = rightObject;
    this.association =
        ODBuilder.buildLink(
            this.leftObject,
            CDDiffUtil.inferRole(association.getLeft()),
            CDDiffUtil.inferRole(association.getRight()),
            this.rightObject,
            Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
    this.odGenHelper = null;
  }

  public Package(
      ASTCDClass leftObject,
      String idSrc,
      ASTODObject rightObject,
      ASTCDAssociation association,
      ClassSide side,
      boolean isProcessedLeft,
      boolean isProcessedRight,
      Syn2SemDiffHelper helper,
      ODGenHelper odGenHelper) {
    this.odGenHelper = odGenHelper;
    this.leftObject =
        ODBuilder.buildObj(
            idSrc,
            leftObject.getSymbol().getInternalQualifiedName(),
            odGenHelper.getSuperTypes(leftObject),
            getAttributesOD(leftObject, helper));
    this.rightObject = rightObject;
    this.association =
        ODBuilder.buildLink(
            this.leftObject,
            CDDiffUtil.inferRole(association.getLeft()),
            CDDiffUtil.inferRole(association.getRight()),
            this.rightObject,
            Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(
      ASTODObject leftObject,
      ASTCDClass rightObject,
      String idTgt,
      ASTCDAssociation association,
      ClassSide side,
      boolean isProcessedLeft,
      boolean isProcessedRight,
      Syn2SemDiffHelper helper,
      ODGenHelper odGenHelper) {
    this.odGenHelper = odGenHelper;
    this.leftObject = leftObject;
    this.rightObject =
        ODBuilder.buildObj(
            idTgt,
            rightObject.getSymbol().getInternalQualifiedName(),
            odGenHelper.getSuperTypes(rightObject),
            getAttributesOD(rightObject, helper));
    this.association =
        ODBuilder.buildLink(
            this.leftObject,
            CDDiffUtil.inferRole(association.getLeft()),
            CDDiffUtil.inferRole(association.getRight()),
            this.rightObject,
            Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(ASTODObject leftObject, ODGenHelper odGenHelper) {
    this.leftObject = leftObject;
    this.rightObject = null;
    this.association = null;
    this.astcdAssociation = null;
    this.side = ClassSide.Left;
    this.isProcessedLeft = true;
    this.isProcessedRight = false;
    this.odGenHelper = odGenHelper;
  }

  public Package(
      ASTCDClass astcdClass, String id, Syn2SemDiffHelper helper, ODGenHelper odGenHelper) {
    this.odGenHelper = odGenHelper;
    this.leftObject =
        ODBuilder.buildObj(
            id,
            astcdClass.getSymbol().getInternalQualifiedName(),
            odGenHelper.getSuperTypes(astcdClass),
            getAttributesOD(astcdClass, helper));
    this.rightObject = null;
    this.association = null;
    this.astcdAssociation = null;
    this.side = ClassSide.Left;
    this.isProcessedLeft = false;
    this.isProcessedRight = false;
  }

  public ASTODObject getLeftObject() {
    return leftObject;
  }

  public ASTODObject getRightObject() {
    return rightObject;
  }

  public ASTODLink getAssociation() {
    return association;
  }

  public ASTCDAssociation getAstcdAssociation() {
    return astcdAssociation;
  }

  public ClassSide getSide() {
    return side;
  }

  public boolean isProcessedLeft() {
    return isProcessedLeft;
  }

  public boolean isProcessedRight() {
    return isProcessedRight;
  }

  public List<ASTODAttribute> getAttributesOD(ASTCDClass astcdClass, Syn2SemDiffHelper helper) {
    List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      Pair<Boolean, String> attIsEnum = odGenHelper.attIsEnum(attribute);
      if (attIsEnum.a) {
        odAttributes.add(
            ODBuilder.buildAttr(
                attribute.getMCType().printType(), attribute.getName(), attIsEnum.b));
      } else {
        odAttributes.add(
            ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
      }
    }
    return odAttributes;
  }
}
