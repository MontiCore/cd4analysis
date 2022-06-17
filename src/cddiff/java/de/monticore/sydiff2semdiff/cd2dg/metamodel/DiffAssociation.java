package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;

/**
 * The association in AST will be converted to the corresponding DiffAssociation
 * The kind of DiffAssociation are DIFF_ASC, DIFF_INHERIT_ASC
 *
 * @attribute originalElement:
 *    store the original AST Association
 * @attribute diffKind:
 *    if this DiffAssociation is created by inherited situation,
 *      then the diffKind is DIFF_INHERIT_ASC
 *    otherwise DIFF_ASC
 * @attribute diffLeftClass:
 *    linked left DiffClass
 * @attribute diffRightClass:
 *    linked right DiffClass
 */
public class DiffAssociation implements Cloneable {
  protected final ASTCDAssociation originalElement;
  protected ASTCDAssociation editedElement;
  protected final DifferentGroup.DiffAssociationKind diffKind;
  protected DiffClass diffLeftClass;
  protected DiffClass diffRightClass;

  public DiffAssociation(ASTCDAssociation originalElement, boolean isInherited) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
    this.diffKind = isInherited ? DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC : DifferentGroup.DiffAssociationKind.DIFF_ASC;
  }

  public String getName() {
    return "DiffAssociation_"
      + getDiffLeftClass().getOriginalClassName() + "_"
      + getDiffLeftClassRoleName() + "_"
      + formatDirection(getDiffDirection()) + "_"
      + getDiffRightClassRoleName() + "_"
      + getDiffRightClass().getOriginalClassName();
  }

  public DifferentGroup.DiffAssociationKind getDiffKind() {
    return diffKind;
  }

  public DifferentGroup.DiffAssociationDirection getDiffDirection() {
    return distinguishAssociationDirectionHelper(this.editedElement);
  }

  public DiffClass getDiffLeftClass() {
    return diffLeftClass;
  }

  public void setDiffLeftClass(DiffClass diffLeftClass) {
    this.diffLeftClass = diffLeftClass;
  }

  public DiffClass getDiffRightClass() {
    return diffRightClass;
  }

  public void setDiffRightClass(DiffClass diffRightClass) {
    this.diffRightClass = diffRightClass;
  }

  public String getLeftOriginalClassName() {
    return this.originalElement.getLeftQualifiedName().getQName();
  }

  public String getRightOriginalClassName() {
    return this.originalElement.getRightQualifiedName().getQName();
  }

  public DifferentGroup.DiffAssociationCardinality getDiffLeftClassCardinality() {
    return distinguishLeftAssociationCardinalityHelper(this.editedElement);
  }

  public void setDiffLeftClassCardinality(DifferentGroup.DiffAssociationCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case ZORE_TO_ONE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardOptBuilder().build());
        break;
      case ONE_TO_MORE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardAtLeastOneBuilder().build());
        break;
      case MORE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
        break;
    }
  }

  public DifferentGroup.DiffAssociationCardinality getDiffRightClassCardinality() {
    return distinguishRightAssociationCardinalityHelper(this.editedElement);
  }

  public void setDiffRightClassCardinality(DifferentGroup.DiffAssociationCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case ZORE_TO_ONE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardOptBuilder().build());
        break;
      case ONE_TO_MORE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardAtLeastOneBuilder().build());
        break;
      case MORE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
        break;
    }
  }

  public String getDiffLeftClassRoleName() {
    return getLeftClassRoleNameHelper(this.editedElement);
  }

  public String getDiffRightClassRoleName() {
    return getRightClassRoleNameHelper(this.editedElement);
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  public ASTCDAssociation getEditedElement() {
    return editedElement;
  }

  @Override
  public DiffAssociation clone() throws CloneNotSupportedException {
    DiffAssociation cloned = (DiffAssociation) super.clone();
    cloned.diffLeftClass = diffLeftClass.clone();
    cloned.diffRightClass = diffRightClass.clone();
    return cloned;
  }

}
