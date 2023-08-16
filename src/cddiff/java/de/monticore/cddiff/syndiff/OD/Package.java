package de.monticore.cddiff.syndiff.OD;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Package {
  private final ASTODObject srcClass;
  private final ASTODObject tgtClass;
  private final ASTODLink association;
  private final ASTCDAssociation astcdAssociation;
  private final ClassSide side;
  private final Builder builder = new Builder();
  private final Syn2SemDiffHelper helper = new Syn2SemDiffHelper();
  public Package(ASTCDClass srcClass, ASTCDClass tgtClass, ASTCDAssociation association, ClassSide side) {
    this.srcClass = builder.buildObj("", srcClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(srcClass), ','), getAttributesOD(srcClass));
    this.tgtClass = builder.buildObj("", tgtClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(tgtClass), ','), getAttributesOD(tgtClass));
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
  }
  public Package(ASTODObject srcClass, ASTODObject tgtClass, ASTCDAssociation association, ClassSide side) {
    this.srcClass = srcClass;
    this.tgtClass = tgtClass;
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
  }

  public Package(ASTCDClass srcClass, ASTODObject tgtClass, ASTCDAssociation association, ClassSide side){
    this.srcClass = builder.buildObj("", srcClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(srcClass), ','), getAttributesOD(srcClass));
    this.tgtClass = tgtClass;
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
  }

  public Package(ASTODObject srcClass, ASTCDClass tgtClass, ASTCDAssociation association, ClassSide side){
    this.srcClass = srcClass;
    this.tgtClass = builder.buildObj("", tgtClass.getName(), Syn2SemDiffHelper.splitStringByCharacter(helper.getSuperClasses(tgtClass), ','), getAttributesOD(tgtClass));
    this.association = builder.buildLink(this.srcClass, association.getLeft().getCDRole().getName(), association.getRight().getCDRole().getName(), this.tgtClass, Objects.requireNonNull(Syn2SemDiffHelper.getDirection(association)).toString());
    this.astcdAssociation = association;
    this.side = side;
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
  public List<ASTODAttribute> getAttributesOD(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = helper.getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      odAttributes.add(builder.buildAttr(attribute.printType(), attribute.getName(), null));
    }
    return odAttributes;
  }
}
