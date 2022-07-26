package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;

/**
 * The association in AST will be converted to the corresponding CDAssociationWrapper
 * The kind of CDAssociationWrapper are CDWRAPPER_ASC, CDWRAPPER_INHERIT_ASC
 *
 * @attribute originalElement:
 *    store the original AST Association
 * @attribute cDWrapperKind:
 *    if this CDAssociationWrapper is created by inherited situation,
 *      then the cDWrapperKind is CDWRAPPER_INHERIT_ASC
 *    otherwise CDWRAPPER_ASC
 * @attribute cDWrapperLeftClass:
 *    linked left CDTypeWrapper
 * @attribute cDWrapperRightClass:
 *    linked right CDTypeWrapper
 */
public class CDAssociationWrapper implements Cloneable {
  protected final ASTCDAssociation originalElement;
  protected ASTCDAssociation editedElement;
  protected CDWrapper.CDAssociationWrapperKind cDWrapperKind;
  protected CDTypeWrapper cDWrapperLeftClass;
  protected CDTypeWrapper cDWrapperRightClass;

  public CDAssociationWrapper(ASTCDAssociation originalElement, boolean isInherited) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
    this.cDWrapperKind = isInherited ?
      CDWrapper.CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC : CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC;
  }

  public String getName() {
    return "CDAssociationWrapper_"
      + getCDWrapperLeftClass().getOriginalClassName() + "_"
      + getCDWrapperLeftClassRoleName() + "_"
      + formatDirection(getCDAssociationWrapperDirection()) + "_"
      + getCDWrapperRightClassRoleName() + "_"
      + getCDWrapperRightClass().getOriginalClassName();
  }

  public CDWrapper.CDAssociationWrapperKind getCDWrapperKind() {
    return cDWrapperKind;
  }

  public void setCDWrapperKind(CDWrapper.CDAssociationWrapperKind cDWrapperKind) {
    this.cDWrapperKind = cDWrapperKind;
  }

  public CDWrapper.CDAssociationWrapperDirection getCDAssociationWrapperDirection() {
    return distinguishAssociationDirectionHelper(this.editedElement);
  }

  public CDTypeWrapper getCDWrapperLeftClass() {
    return cDWrapperLeftClass;
  }

  public void setCDWrapperLeftClass(CDTypeWrapper cDWrapperLeftClass) {
    this.cDWrapperLeftClass = cDWrapperLeftClass;
  }

  public CDTypeWrapper getCDWrapperRightClass() {
    return cDWrapperRightClass;
  }

  public void setCDWrapperRightClass(CDTypeWrapper cDWrapperRightClass) {
    this.cDWrapperRightClass = cDWrapperRightClass;
  }

  public String getLeftOriginalClassName() {
    return this.originalElement.getLeftQualifiedName().getQName();
  }

  public String getRightOriginalClassName() {
    return this.originalElement.getRightQualifiedName().getQName();
  }

  public CDWrapper.CDAssociationWrapperCardinality getCDWrapperLeftClassCardinality() {
    return distinguishLeftAssociationCardinalityHelper(this.editedElement);
  }

  public void setCDWrapperLeftClassCardinality(
      CDWrapper.CDAssociationWrapperCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case ZERO_TO_ONE:
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

  public CDWrapper.CDAssociationWrapperCardinality getCDWrapperRightClassCardinality() {
    return distinguishRightAssociationCardinalityHelper(this.editedElement);
  }

  public void setCDWrapperRightClassCardinality(
      CDWrapper.CDAssociationWrapperCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case ZERO_TO_ONE:
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

  public String getCDWrapperLeftClassRoleName() {
    return getLeftClassRoleNameHelper(this.editedElement);
  }

  public String getCDWrapperRightClassRoleName() {
    return getRightClassRoleNameHelper(this.editedElement);
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  public ASTCDAssociation getEditedElement() {
    return editedElement;
  }

  public void setEditedElement(ASTCDAssociation editedElement) {
    this.editedElement = editedElement;
  }

  @Override
  public CDAssociationWrapper clone() throws CloneNotSupportedException {
    CDAssociationWrapper cloned = (CDAssociationWrapper) super.clone();
    cloned.editedElement = editedElement.deepClone();
    cloned.cDWrapperLeftClass = cDWrapperLeftClass.clone();
    cloned.cDWrapperRightClass = cDWrapperRightClass.clone();
    return cloned;
  }

}
