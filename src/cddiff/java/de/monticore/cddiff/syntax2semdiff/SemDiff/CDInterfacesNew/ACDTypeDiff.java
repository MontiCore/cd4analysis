package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces.ICDTypeDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;

import java.util.List;

public abstract class ACDTypeDiff implements ICDTypeDiff {
  private final ASTCDType elem1;
  private final ASTCDType elem2;
  private List<ACDMemberDiff> changedMembers;
  private List<ACDMemberDiff> changedModifier;

  public ASTCDType getElem1() {
    return elem1;
  }

  public ASTCDType getElem2() {
    return elem2;
  }

  @Override
  public List<ACDMemberDiff> getChangedMembers() {
    return changedMembers;
  }

  public void setChangedMembers(List<ACDMemberDiff> changedMembers) {
    this.changedMembers = changedMembers;
  }

  @Override
  public List<ACDMemberDiff> getChangedModifier() {
    return changedModifier;
  }

  public void setChangedModifier(List<ACDMemberDiff> changedModifier) {
    this.changedModifier = changedModifier;
  }

  @Override
  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public void setAddedAttributes(List<ASTCDAttribute> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  @Override
  public List<ASTCDAttribute> getDeletedAttribute() {
    return deletedAttribute;
  }

  public void setDeletedAttribute(List<ASTCDAttribute> deletedAttribute) {
    this.deletedAttribute = deletedAttribute;
  }

  @Override
  public List<ASTCDEnumConstant> getAddedConstants() {
    return addedConstants;
  }

  public void setAddedConstants(List<ASTCDEnumConstant> addedConstants) {
    this.addedConstants = addedConstants;
  }

  @Override
  public List<ASTCDEnumConstant> getDeletedConstants() {
    return deletedConstants;
  }

  public void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants) {
    this.deletedConstants = deletedConstants;
  }

  public List<DiffTypes> getBaseDiffs() {
    return baseDiffs;
  }

  public void setBaseDiffs(List<DiffTypes> baseDiffs) {
    this.baseDiffs = baseDiffs;
  }

  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttribute;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;

  private List<DiffTypes> baseDiffs;
  protected ACDTypeDiff(ASTCDType elem1, ASTCDType elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }
}
