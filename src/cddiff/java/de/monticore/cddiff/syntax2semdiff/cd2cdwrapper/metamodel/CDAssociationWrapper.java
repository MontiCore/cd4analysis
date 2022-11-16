package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.CDDiffUtil;
import de.se_rwth.commons.SourcePosition;

/**
 * The association in AST will be converted to the corresponding CDAssociationWrapper The kind of
 * CDAssociationWrapper are CDWRAPPER_ASC, CDWRAPPER_INHERIT_ASC, CDWRAPPER_INHERIT_DISPLAY_ASC
 *
 * @attribute originalElement: store the original AST Association
 * @attribute cDWrapperKind: - if this CDAssociationWrapper is created by inherited situation, then
 *     the cDWrapperKind is 'CDWRAPPER_INHERIT_ASC' otherwise 'CDWRAPPER_ASC' - Special situation of
 *     inherited association: In comparing process, if this inherited association should be
 *     displayed and generate a witness, the CDWrapperKind of this current inherited association
 *     will be changed into 'CDWRAPPER_INHERIT_DISPLAY_ASC'
 * @attribute cDWrapperLeftClass: linked left CDTypeWrapper
 * @attribute cDWrapperRightClass: linked right CDTypeWrapper
 * @attribute status: OPEN, CONFLICTING
 */
public class CDAssociationWrapper implements Cloneable {
  protected final ASTCDAssociation originalElement;

  protected ASTCDAssociation editedElement;

  protected CDAssociationWrapperKind cDWrapperKind;

  protected CDTypeWrapper cDWrapperLeftClass;

  protected CDTypeWrapper cDWrapperRightClass;

  protected CDStatus status;

  public CDAssociationWrapper(ASTCDAssociation originalElement, boolean isInherited) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
    this.cDWrapperKind =
        isInherited
            ? CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC
            : CDAssociationWrapperKind.CDWRAPPER_ASC;
    this.status = CDStatus.OPEN;
  }

  public String getName() {
    return "CDAssociationWrapper_"
        + getCDWrapperLeftClass().getOriginalClassName()
        + "_"
        + getCDWrapperLeftClassRoleName()
        + "_"
        + formatDirection(getCDAssociationWrapperDirection())
        + "_"
        + getCDWrapperRightClassRoleName()
        + "_"
        + getCDWrapperRightClass().getOriginalClassName();
  }

  public CDAssociationWrapperKind getCDWrapperKind() {
    return cDWrapperKind;
  }

  public void setCDWrapperKind(CDAssociationWrapperKind cDWrapperKind) {
    this.cDWrapperKind = cDWrapperKind;
  }

  public CDAssociationWrapperDirection getCDAssociationWrapperDirection() {
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

  public CDAssociationWrapperCardinality getCDWrapperLeftClassCardinality() {
    return distinguishLeftAssociationCardinalityHelper(this.editedElement);
  }

  public void setCDWrapperLeftClassCardinality(CDAssociationWrapperCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case OPTIONAL:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardOptBuilder().build());
        break;
      case AT_LEAST_ONE:
        this.editedElement
            .getLeft()
            .setCDCardinality(CD4AnalysisMill.cDCardAtLeastOneBuilder().build());
        break;
      case MULTIPLE:
        this.editedElement.getLeft().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
        break;
    }
  }

  public CDAssociationWrapperCardinality getCDWrapperRightClassCardinality() {
    return distinguishRightAssociationCardinalityHelper(this.editedElement);
  }

  public void setCDWrapperRightClassCardinality(CDAssociationWrapperCardinality cardinalityResult) {
    switch (cardinalityResult) {
      case ONE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardOneBuilder().build());
        break;
      case OPTIONAL:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardOptBuilder().build());
        break;
      case AT_LEAST_ONE:
        this.editedElement
            .getRight()
            .setCDCardinality(CD4AnalysisMill.cDCardAtLeastOneBuilder().build());
        break;
      case MULTIPLE:
        this.editedElement.getRight().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
        break;
    }
  }

  public String getCDWrapperLeftClassRoleName() {
    return CDDiffUtil.inferRole(this.editedElement.getLeft());
  }

  public String getCDWrapperRightClassRoleName() {
    return CDDiffUtil.inferRole(this.editedElement.getRight());
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

  public CDStatus getStatus() {
    return status;
  }

  public void setStatus(CDStatus status) {
    this.status = status;
  }

  public SourcePosition getSourcePosition() {
    return this.originalElement.get_SourcePositionStart();
  }

  public boolean isOpen() {
    return status == CDStatus.OPEN;
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
