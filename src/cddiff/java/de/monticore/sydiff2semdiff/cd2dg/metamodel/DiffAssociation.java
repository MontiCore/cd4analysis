package de.monticore.sydiff2semdiff.cd2dg.metamodel;

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
 * @attribute isLeftRightExchange:
 *    this attribute is to solve the next problem in compare stage:
 *      in CD1: [*] A <- B [*]
 *      in CD2: [*] B -> A [*]
 *      There is no semantic difference.
 *    For the association in CD2, the system will create a corresponding DiffAssociation that
 *    the position of left and right DiffClass is exchanged
 *    and the role name of left and right DiffClass is also exchanged.
 *    But this created DiffAssociation is only suitable for compare stage and it will not be added into DiffAssociation Map.
 */
public class DiffAssociation implements Cloneable{
  protected final ASTCDAssociation originalElement;
  protected final DifferentGroup.DiffAssociationKind diffKind;
  protected DiffClass diffLeftClass;
  protected DiffClass diffRightClass;
  protected final boolean isLeftRightExchange;

  public DiffAssociation(ASTCDAssociation originalElement, boolean isInherited, boolean isLeftRightExchange) {
    this.originalElement = originalElement;
    this.isLeftRightExchange = isLeftRightExchange;
    this.diffKind = isInherited ? DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC : DifferentGroup.DiffAssociationKind.DIFF_ASC;
  }

  public String getName() {
    if (!isLeftRightExchange) {
      return "DiffAssociation_"
        + getDiffLeftClass().getOriginalClassName() + "_"
        + getDiffLeftClassRoleName() + "_"
        + getDiffRightClassRoleName() + "_"
        + getDiffRightClass().getOriginalClassName();
    } else {
      return "DiffAssociation_"
        + getDiffRightClass().getOriginalClassName() + "_"
        + getDiffRightClassRoleName() + "_"
        + getDiffLeftClassRoleName() + "_"
        + getDiffLeftClass().getOriginalClassName();
    }
  }

  public DifferentGroup.DiffAssociationKind getDiffKind() {
    return diffKind;
  }

  public DifferentGroup.DiffAssociationDirection getDiffDirection() {
    DifferentGroup.DiffAssociationDirection kind = distinguishAssociationDirectionHelper(this.originalElement);
    if (!isLeftRightExchange) {
      return kind;
    } else {
      switch (kind) {
        case LEFT_TO_RIGHT:
          return DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT;
        case RIGHT_TO_LEFT:
          return DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT;
        default:
          return kind;
      }
    }
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
    if (!isLeftRightExchange) {
      return distinguishLeftAssociationCardinalityHelper(this.originalElement);
    } else {
      return distinguishRightAssociationCardinalityHelper(this.originalElement);
    }
  }

  public DifferentGroup.DiffAssociationCardinality getDiffRightClassCardinality() {
    if (!isLeftRightExchange) {
      return distinguishRightAssociationCardinalityHelper(this.originalElement);
    } else {
      return distinguishLeftAssociationCardinalityHelper(this.originalElement);
    }
  }

  public String getDiffLeftClassRoleName() {
    return !isLeftRightExchange ? getLeftClassRoleNameHelper(this.originalElement) : getRightClassRoleNameHelper(this.originalElement);
  }

  public String getDiffRightClassRoleName() {
    return !isLeftRightExchange ? getRightClassRoleNameHelper(this.originalElement) : getLeftClassRoleNameHelper(this.originalElement);
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  @Override
  public DiffAssociation clone() throws CloneNotSupportedException {
    DiffAssociation cloned = (DiffAssociation) super.clone();
    cloned.diffLeftClass = diffLeftClass.clone();
    cloned.diffRightClass = diffRightClass.clone();
    return cloned;
  }

}
