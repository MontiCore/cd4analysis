package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Package {
  private final ASTODObject srcClass;
  private final boolean isProcessedLeft;
  private final ASTODObject tgtClass;
  private final boolean isProcessedRight;
  private final ASTODLink association;
  private final ASTCDAssociation astcdAssociation;
  private final ClassSide side;
  private final Builder builder = new Builder();
  private final Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  public Package(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.srcClass = builder.buildObj("", srcClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(srcClass), ','), getAttributesOD(srcClass));
    this.tgtClass = builder.buildObj("", tgtClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(tgtClass), ','), getAttributesOD(tgtClass));
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }
  public Package(ASTODObject srcClass, ASTODObject tgtClass, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.srcClass = srcClass;
    this.tgtClass = tgtClass;
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(ASTCDClass srcClass, ASTODObject tgtClass, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.srcClass = builder.buildObj("", srcClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(srcClass), ','), getAttributesOD(srcClass));
    this.tgtClass = tgtClass;
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }

  public Package(ASTODObject srcClass, ASTCDClass tgtClass, ASTCDAssociation association, ClassSide side, boolean isProcessedLeft, boolean isProcessedRight) {
    this.srcClass = srcClass;
    this.tgtClass = builder.buildObj("", tgtClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(tgtClass), ','), getAttributesOD(tgtClass));
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
    this.isProcessedLeft = isProcessedLeft;
    this.isProcessedRight = isProcessedRight;
  }
  public Package(ASTODObject srcClass){
    this.srcClass = srcClass;
    this.tgtClass = null;
    this.association = null;
    this.astcdAssociation = null;
    this.side = ClassSide.Left;
    this.isProcessedLeft = true;
    this.isProcessedRight = false;
  }
  public ASTODObject getSrcClass() {
    return srcClass;
  }
  public ASTODObject getTgtClass() {
    return tgtClass;
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
      odAttributes.add(builder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
    }
    return odAttributes;
  }
}
