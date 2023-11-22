package de.monticore.cddiff.syn2semdiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

/**
 * Data structure for preparing the differences related to changed types - ASTCDClass or ASTCDEnum.
 * The corresponding attributes are set if they lead to a semantic difference. Otherwise, they are
 * null.
 */
public class TypeDiffStruct {
  private ASTCDType astcdType;
  private List<Pair<ASTCDClass, List<ASTCDAttribute>>> deletedAttributes = null;
  private List<Pair<ASTCDClass, List<ASTCDAttribute>>> addedAttributes = null;
  private List<Pair<ASTCDClass, AddedDeletedAtt>> addedDeletedAttributes = null;
  private List<Pair<ASTCDClass, ASTCDAttribute>> memberDiff = null;
  private boolean changedStereotype = false;
  private boolean changedSingleton = false;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes = null;
  private Pair<ASTCDEnum, List<ASTCDEnumConstant>> addedConstants = null;
  private List<DiffTypes> baseDiff;

  public TypeDiffStruct() {}

  public ASTCDType getAstcdType() {
    return astcdType;
  }

  public void setAstcdType(ASTCDType astcdType) {
    this.astcdType = astcdType;
  }

  public List<Pair<ASTCDClass, List<ASTCDAttribute>>> getDeletedAttributes() {
    return deletedAttributes;
  }

  public void setDeletedAttributes(List<Pair<ASTCDClass, List<ASTCDAttribute>>> deletedAttributes) {
    this.deletedAttributes = deletedAttributes;
  }

  public List<Pair<ASTCDClass, List<ASTCDAttribute>>> getAddedAttributes() {
    return addedAttributes;
  }

  public void setAddedAttributes(List<Pair<ASTCDClass, List<ASTCDAttribute>>> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> getMemberDiff() {
    return memberDiff;
  }

  public void setMemberDiff(List<Pair<ASTCDClass, ASTCDAttribute>> memberDiff) {
    this.memberDiff = memberDiff;
  }

  public Pair<ASTCDEnum, List<ASTCDEnumConstant>> getAddedConstants() {
    return addedConstants;
  }

  public void setAddedConstants(Pair<ASTCDEnum, List<ASTCDEnumConstant>> addedConstants) {
    this.addedConstants = addedConstants;
  }

  public boolean getChangedStereotype() {
    return changedStereotype;
  }

  public void setChangedStereotype(boolean changedStereotype) {
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

  public boolean isChangedSingleton() {
    return changedSingleton;
  }

  public void setChangedSingleton(boolean changedSingleton) {
    this.changedSingleton = changedSingleton;
  }

  public boolean isOnlySingletonChanged() {
    return changedSingleton && baseDiff.size() == 1 && !astcdType.getModifier().isAbstract();
  }

  public List<Pair<ASTCDClass, AddedDeletedAtt>> getAddedDeletedAttributes() {
    return addedDeletedAttributes;
  }

  public void setAddedDeletedAttributes(
      List<Pair<ASTCDClass, AddedDeletedAtt>> addedDeletedAttributes) {
    this.addedDeletedAttributes = addedDeletedAttributes;
  }
}
