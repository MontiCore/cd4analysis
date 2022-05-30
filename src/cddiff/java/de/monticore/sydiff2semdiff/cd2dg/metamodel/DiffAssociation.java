package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

public class DiffAssociation implements Cloneable{
  protected String name;
  protected DifferentGroup.DiffAssociationKind diffKind;
  protected DifferentGroup.DiffAssociationDirection diffDirection;
  protected DiffClass diffLeftClass;
  protected DiffClass diffRightClass;
  protected DifferentGroup.DiffAssociationCardinality diffLeftClassCardinality;
  protected DifferentGroup.DiffAssociationCardinality diffRightClassCardinality;
  protected String diffLeftClassRoleName;
  protected String diffRightClassRoleName;
  protected ASTCDAssociation originalElement;
  protected ASTCDAssociation editedElement;

  public DiffAssociation() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DifferentGroup.DiffAssociationKind getDiffKind() {
    return diffKind;
  }

  public void setDiffKind(DifferentGroup.DiffAssociationKind diffKind) {
    this.diffKind = diffKind;
  }

  public DifferentGroup.DiffAssociationDirection getDiffDirection() {
    return diffDirection;
  }

  public void setDiffDirection(DifferentGroup.DiffAssociationDirection diffDirection) {
    this.diffDirection = diffDirection;
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

  public DifferentGroup.DiffAssociationCardinality getDiffLeftClassCardinality() {
    return diffLeftClassCardinality;
  }

  public void setDiffLeftClassCardinality(DifferentGroup.DiffAssociationCardinality diffLeftClassCardinality) {
    this.diffLeftClassCardinality = diffLeftClassCardinality;
  }

  public DifferentGroup.DiffAssociationCardinality getDiffRightClassCardinality() {
    return diffRightClassCardinality;
  }

  public void setDiffRightClassCardinality(DifferentGroup.DiffAssociationCardinality diffRightClassCardinality) {
    this.diffRightClassCardinality = diffRightClassCardinality;
  }

  public String getDiffLeftClassRoleName() {
    return diffLeftClassRoleName;
  }

  public void setDiffLeftClassRoleName(String diffLeftClassRoleName) {
    this.diffLeftClassRoleName = diffLeftClassRoleName;
    if (!this.editedElement.getLeft().isPresentCDRole()) {
      ASTCDRole astcdRole = CD4AnalysisMill.cDRoleBuilder().uncheckedBuild();
      astcdRole.setName(diffLeftClassRoleName);
      this.editedElement.getLeft().setCDRole(astcdRole);
    }
  }

  public String getDiffRightClassRoleName() {
    return diffRightClassRoleName;
  }

  public void setDiffRightClassRoleName(String diffRightClassRoleName) {
    this.diffRightClassRoleName = diffRightClassRoleName;
    if (!this.editedElement.getRight().isPresentCDRole()) {
      ASTCDRole astcdRole = CD4AnalysisMill.cDRoleBuilder().uncheckedBuild();
      astcdRole.setName(diffRightClassRoleName);
      this.editedElement.getRight().setCDRole(astcdRole);
    }
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  public void setOriginalElement(ASTCDAssociation originalElement) {
    this.originalElement = originalElement;
    this.editedElement = originalElement.deepClone();
  }

  public ASTCDAssociation getEditedElement() {
    return editedElement;
  }

  public void setEditedElement(ASTCDAssociation editedElement) {
    this.editedElement = editedElement;
  }

  @Override
  public DiffAssociation clone() throws CloneNotSupportedException {
    DiffAssociation cloned = (DiffAssociation) super.clone();
    cloned.diffLeftClass = diffLeftClass.clone();
    cloned.diffRightClass = diffRightClass.clone();
    return cloned;
  }

}
