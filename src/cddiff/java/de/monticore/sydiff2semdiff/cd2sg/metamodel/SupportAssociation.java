package de.monticore.sydiff2semdiff.cd2sg.metamodel;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.*;

/**
 * The association in AST will be converted to the corresponding SupportAssociation
 * The kind of SupportAssociation are SUPPORT_ASC, SUPPORT_INHERIT_ASC
 *
 * @attribute originalElement:
 *    store the original AST Association
 * @attribute supportKind:
 *    if this SupportAssociation is created by inherited situation,
 *      then the supportKind is SUPPORT_INHERIT_ASC
 *    otherwise SUPPORT_ASC
 * @attribute supportLeftClass:
 *    linked left SupportClass
 * @attribute supportRightClass:
 *    linked right SupportClass
 */
public class SupportAssociation implements Cloneable {
  protected final ASTCDAssociation originalElement;
  protected ASTCDAssociation editedElement;
  protected final SupportGroup.SupportAssociationKind supportKind;
  protected SupportClass supportLeftClass;
  protected SupportClass supportRightClass;

  public SupportAssociation(ASTCDAssociation originalElement, boolean isInherited) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
    this.supportKind = isInherited ? SupportGroup.SupportAssociationKind.SUPPORT_INHERIT_ASC : SupportGroup.SupportAssociationKind.SUPPORT_ASC;
  }

  public String getName() {
    return "SupportAssociation_"
      + getSupportLeftClass().getOriginalClassName() + "_"
      + getSupportLeftClassRoleName() + "_"
      + formatDirection(getSupportDirection()) + "_"
      + getSupportRightClassRoleName() + "_"
      + getSupportRightClass().getOriginalClassName();
  }

  public SupportGroup.SupportAssociationKind getSupportKind() {
    return supportKind;
  }

  public SupportGroup.SupportAssociationDirection getSupportDirection() {
    return distinguishAssociationDirectionHelper(this.editedElement);
  }

  public SupportClass getSupportLeftClass() {
    return supportLeftClass;
  }

  public void setSupportLeftClass(SupportClass supportLeftClass) {
    this.supportLeftClass = supportLeftClass;
  }

  public SupportClass getSupportRightClass() {
    return supportRightClass;
  }

  public void setSupportRightClass(SupportClass supportRightClass) {
    this.supportRightClass = supportRightClass;
  }

  public String getLeftOriginalClassName() {
    return this.originalElement.getLeftQualifiedName().getQName();
  }

  public String getRightOriginalClassName() {
    return this.originalElement.getRightQualifiedName().getQName();
  }

  public SupportGroup.SupportAssociationCardinality getSupportLeftClassCardinality() {
    return distinguishLeftAssociationCardinalityHelper(this.editedElement);
  }

  public void setSupportLeftClassCardinality(SupportGroup.SupportAssociationCardinality cardinalityResult) {
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

  public SupportGroup.SupportAssociationCardinality getSupportRightClassCardinality() {
    return distinguishRightAssociationCardinalityHelper(this.editedElement);
  }

  public void setSupportRightClassCardinality(SupportGroup.SupportAssociationCardinality cardinalityResult) {
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

  public String getSupportLeftClassRoleName() {
    return getLeftClassRoleNameHelper(this.editedElement);
  }

  public String getSupportRightClassRoleName() {
    return getRightClassRoleNameHelper(this.editedElement);
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  public ASTCDAssociation getEditedElement() {
    return editedElement;
  }

  @Override
  public SupportAssociation clone() throws CloneNotSupportedException {
    SupportAssociation cloned = (SupportAssociation) super.clone();
    cloned.editedElement = editedElement.deepClone();
    cloned.supportLeftClass = supportLeftClass.clone();
    cloned.supportRightClass = supportRightClass.clone();
    return cloned;
  }

}
