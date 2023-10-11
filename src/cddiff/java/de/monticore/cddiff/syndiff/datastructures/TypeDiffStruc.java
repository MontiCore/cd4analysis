package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.imp.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

public class TypeDiffStruc {
  private ASTCDType astcdType;
  private Pair<ASTCDClass, List<ASTCDAttribute>> deletedAttributes;
  private Pair<ASTCDClass, List<ASTCDAttribute>> addedAttributes;
  private Pair<ASTCDClass, List<ASTCDAttribute>> memberDiff;
  private ASTCDType changedStereotype;

  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private Pair<ASTCDEnum, List<ASTCDEnumConstant>> addedConstants;

  private List<DiffTypes> baseDiff;

  public TypeDiffStruc() {}

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public void setAstcdType(ASTCDType astcdType) {
    this.astcdType = astcdType;
  }

  public Pair<ASTCDClass, List<ASTCDAttribute>> getDeletedAttributes() {
    return deletedAttributes;
  }

  public void setDeletedAttributes(Pair<ASTCDClass, List<ASTCDAttribute>> deletedAttributes) {
    this.deletedAttributes = deletedAttributes;
  }

  public Pair<ASTCDClass, List<ASTCDAttribute>> getAddedAttributes() {
    return addedAttributes;
  }

  public void setAddedAttributes(Pair<ASTCDClass, List<ASTCDAttribute>> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  public Pair<ASTCDClass, List<ASTCDAttribute>> getMemberDiff() {
    return memberDiff;
  }

  public void setMemberDiff(Pair<ASTCDClass, List<ASTCDAttribute>> memberDiff) {
    this.memberDiff = memberDiff;
  }

  public Pair<ASTCDEnum, List<ASTCDEnumConstant>> getAddedConstants() {
    return addedConstants;
  }

  public void setAddedConstants(Pair<ASTCDEnum, List<ASTCDEnumConstant>> addedConstants) {
    this.addedConstants = addedConstants;
  }

  public ASTCDType getChangedStereotype() {
    return changedStereotype;
  }

  public void setChangedStereotype(ASTCDType changedStereotype) {
    this.changedStereotype = changedStereotype;
  }

  public List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes() {
    return matchedAttributes;
  }

  public void setMatchedAttributes(List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes) {
    this.matchedAttributes = matchedAttributes;
  }

  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }
}
