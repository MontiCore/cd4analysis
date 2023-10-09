package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;

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
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  public Package(ASTCDClass leftObject, String idSrc, ASTCDClass rightObject, String idTgt, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.leftObject = ODBuilder.buildObj(idSrc, leftObject.getSymbol().getInternalQualifiedName(), helper.getSuperClasses(leftObject), getAttributesOD(leftObject));
    this.rightObject = ODBuilder.buildObj(idTgt, rightObject.getSymbol().getInternalQualifiedName(), helper.getSuperClasses(rightObject), getAttributesOD(rightObject));
    this.association = ODBuilder.buildLink(this.leftObject, CDDiffUtil.inferRole(association.getLeft()), CDDiffUtil.inferRole(association.getRight()), this.rightObject, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }
  public Package(ASTODObject leftObject, ASTODObject rightObject, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.leftObject = leftObject;
    this.rightObject = rightObject;
    this.association = ODBuilder.buildLink(this.leftObject, CDDiffUtil.inferRole(association.getLeft()), CDDiffUtil.inferRole(association.getRight()), this.rightObject, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(ASTCDClass leftObject, String idSrc, ASTODObject rightObject, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.leftObject = ODBuilder.buildObj(idSrc, leftObject.getSymbol().getInternalQualifiedName(), helper.getSuperClasses(leftObject), getAttributesOD(leftObject));
    this.rightObject = rightObject;
    this.association = ODBuilder.buildLink(this.leftObject, CDDiffUtil.inferRole(association.getLeft()), CDDiffUtil.inferRole(association.getRight()), this.rightObject, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(ASTODObject leftObject, ASTCDClass rightObject, String idTgt, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.leftObject = leftObject;
    this.rightObject = ODBuilder.buildObj(idTgt, rightObject.getSymbol().getInternalQualifiedName(), helper.getSuperClasses(rightObject), getAttributesOD(rightObject));
    this.association = ODBuilder.buildLink(this.leftObject, CDDiffUtil.inferRole(association.getLeft()), CDDiffUtil.inferRole(association.getRight()), this.rightObject, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)));
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }
  public Package(ASTODObject leftObject){
    this.leftObject = leftObject;
    this.rightObject = null;
    this.association = null;
    this.astcdAssociation = null;
    this.side = ClassSide.Left;
    this.isProcessedLeft = true;
    this.isProcessedRight = false;
  }

  public Package(ASTCDClass astcdClass, String id){
    this.leftObject = ODBuilder.buildObj(id, astcdClass.getSymbol().getInternalQualifiedName(), helper.getSuperClasses(astcdClass), getAttributesOD(astcdClass));
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
  public boolean isProcessedRight(){
    return isProcessedRight;
  }

  //add regex for special cases in ODBuilder and add values to primitive type here - done
  //Check how Integer, Long, Float, Double, Boolean are written(make a diagram and print type) - done
  public List<ASTODAttribute> getAttributesOD(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      if (helper.attIsEnum(attribute)) {
        odAttributes.add(ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName(), "''"));
      }
      odAttributes.add(ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
    }
    return odAttributes;
  }
}
