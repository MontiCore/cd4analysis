package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces.ICDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;

import java.util.List;

public abstract class ACDTypeDiff implements ICDTypeDiff {
  private final ASTCDType elem1;
  private final ASTCDType elem2;
  private List<ACDMemberDiff> changedMembers;
  private List<ACDMemberDiff> changedModifier;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttribute;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  protected ACDTypeDiff(ASTCDType elem1, ASTCDType elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }
}
