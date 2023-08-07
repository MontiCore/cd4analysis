package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public class TypeDiffStruc {
  private ASTCDType astcdType;
  private List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes;
  private List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes;
  private List<Pair<ASTCDClass, ASTCDAttribute>> memberDiff;
  private List<Pair<ASTCDClass, ASTCDEnumConstant>> addedConstants;

  public TypeDiffStruc() {
  }

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public void setAstcdType(ASTCDType astcdType) {
    this.astcdType = astcdType;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> getDeletedAttributes() {
    return deletedAttributes;
  }

  public void setDeletedAttributes(List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes) {
    this.deletedAttributes = deletedAttributes;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> getAddedAttributes() {
    return addedAttributes;
  }

  public void setAddedAttributes(List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> getMemberDiff() {
    return memberDiff;
  }

  public void setMemberDiff(List<Pair<ASTCDClass, ASTCDAttribute>> memberDiff) {
    this.memberDiff = memberDiff;
  }

  public List<Pair<ASTCDClass, ASTCDEnumConstant>> getAddedConstants() {
    return addedConstants;
  }

  public void setAddedConstants(List<Pair<ASTCDClass, ASTCDEnumConstant>> addedConstants) {
    this.addedConstants = addedConstants;
  }
}
