package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.matcher.MatchingStrategy;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CDTypeDiff implements ICDTypeDiff {
  private final ASTCDType srcType;
  private final ASTCDType tgtType;
  private List<CDMemberDiff> changedMembers;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttributes;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> matchedConstants;
  private List<DiffTypes> baseDiffs;
  protected MatchingStrategy<ASTCDType> typeMatcher;

  protected CDTypeDiff(ASTCDType srcType, ASTCDType tgtType) {
    this.srcType = srcType;
    this.tgtType = tgtType;
  }

  @Override
  public ASTCDType getSrcType() {
    return srcType;
  }

  @Override
  public ASTCDType getTgtType() {
    return tgtType;
  }

  @Override
  public List<CDMemberDiff> getChangedMembers() {
    return changedMembers;
  }

  @Override
  public void setChangedMembers(List<CDMemberDiff> changedMembers) {
    this.changedMembers = changedMembers;
  }

  @Override
  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  @Override
  public void setAddedAttributes(List<ASTCDAttribute> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  @Override
  public List<ASTCDAttribute> getDeletedAttribute() {
    return deletedAttributes;
  }

  @Override
  public void setDeletedAttribute(List<ASTCDAttribute> deletedAttribute) {
    this.deletedAttributes = deletedAttribute;
  }

  @Override
  public List<ASTCDEnumConstant> getAddedConstants() {
    return addedConstants;
  }

  @Override
  public void setAddedConstants(List<ASTCDEnumConstant> addedConstants) {
    this.addedConstants = addedConstants;
  }

  @Override
  public List<ASTCDEnumConstant> getDeletedConstants() {
    return deletedConstants;
  }

  @Override
  public List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes() {
    return null;
  }

  @Override
  public List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants() {
    return null;
  }

  @Override
  public void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants) {
    this.deletedConstants = deletedConstants;
  }

  @Override
  public List<DiffTypes> getBaseDiffs() {
    return baseDiffs;
  }

  @Override
  public void setBaseDiffs(List<DiffTypes> baseDiffs) {
    this.baseDiffs = baseDiffs;
  }

  @Override
  public String sterDiff() {
    return "Modifier changed from "
        + getTgtType().getModifier()
        + " to "
        + getSrcType().getModifier();
  }

  /**
   * Compute the type difference of the changed attributes.
   *
   * @return old and new type for each changed pair.
   */
  public String attDiff() {
    StringBuilder stringBuilder = new StringBuilder();
    for (CDMemberDiff member : getChangedMembers()) {
      if (member.getSrcElem() instanceof ASTCDAttribute) {
        ASTCDAttribute attribute1 = (ASTCDAttribute) member.getSrcElem();
        ASTCDAttribute attribute2 = (ASTCDAttribute) member.getTgtElem();
        stringBuilder
            .append("Attribute type changed from ")
            .append(attribute2.getMCType().printType())
            .append(" to ")
            .append(attribute1.getMCType().printType());
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Check for each attribute in the list deletedAttribute if it has been really deleted and add it
   * to a list.
   *
   * @param compilationUnit class diagram
   * @return list of pairs of the class with a deleted attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes(
      ASTCDCompilationUnit compilationUnit) {
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getDeletedAttribute()) {
      if (!isDeleted(attribute, compilationUnit)) {
        pairList.add(new Pair<>((ASTCDClass) getSrcType(), attribute));
      }
    }
    return pairList;
  }

  /**
   * Check if an attribute is really deleted.
   *
   * @param attribute from list deletedAttributes.
   * @return false if not found in inheritance hierarchy.
   */
  public boolean isDeleted(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit) {
    return isAttributInSuper(
        attribute, getSrcType(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
  }

  /**
   * Check for each attribute in the list addedAttributes if it has been really added and add it to
   * a list.
   *
   * @param compilationUnit class diagram
   * @return list of pairs of the class with an added (new) attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes(
      ASTCDCompilationUnit compilationUnit) {
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getAddedAttributes()) {
      if (!isAdded(attribute, compilationUnit)) {
        pairList.add(new Pair<>((ASTCDClass) getSrcType(), attribute));
      }
    }
    return pairList;
  }
  /**
   * Check if an attribute is really added.
   *
   * @param attribute from addedList
   * @param compilationUnit for diagram (trg)
   * @return false if not found in all subclasses
   */
  public boolean isAdded(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit) {
    Set<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getTgtType(), compilationUnit);
    boolean conditionSatisfied = false; // Track if the condition is satisfied
    for (ASTCDClass astcdClass : classList) {
      if (!astcdClass.getCDAttributeList().contains(attribute)) {
        Set<ASTCDType> astcdClassList =
            getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
        astcdClassList.remove(getTgtType());
        for (ASTCDType type : astcdClassList) {
          if (type instanceof ASTCDClass && type.getCDAttributeList().contains(attribute)) {
            conditionSatisfied = true; // Set the flag to true if the condition holds
            break;
          }
        }
      }
      if (!conditionSatisfied) { // found a subclass that doesn't have this attribute
        return false; // Break out of the first loop if the condition is satisfied
      } else {
        conditionSatisfied = false;
      }
    }
    return true;
  }

  /**
   * Compute the spanned inheritance of a given class. That is we get all classes that are extending
   * (not only direct) a class
   *
   * @param astcdClass compute subclasses of this class
   * @param compilationUnit class diagram
   * @return set of extending classes. The implementation is not efficient (no way to go from
   *     subclasses to superclasses).
   */
  @Override
  public Set<ASTCDClass> getSpannedInheritance(
      ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit) {
    Set<ASTCDClass> subclasses = new HashSet<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if ((getAllSuper(
              childClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit)))
          .contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    return subclasses;
  }

  /**
   * Get all added constants to an enum
   *
   * @return list of added constants
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDEnumConstant>> newConstants() {
    List<Pair<ASTCDClass, ASTCDEnumConstant>> pairList = new ArrayList<>();
    if (!getAddedConstants().isEmpty()) {
      for (ASTCDEnumConstant constant : getAddedConstants()) {
        pairList.add(new Pair<>((ASTCDClass) getSrcType(), constant));
      }
    }
    return pairList;
  }

  /**
   * Compute all changed attributes in all classes.
   *
   * @param compilationUnit class diagram
   * @return list of pairs of classes and changed attributes.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> changedAttribute(
      ASTCDCompilationUnit compilationUnit) {
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (CDMemberDiff memberDiff : getChangedMembers()) {
      if (findMemberDiff(memberDiff, compilationUnit) != null) {
        pairList.addAll(findMemberDiff(memberDiff, compilationUnit));
      }
    }
    return pairList;
  }

  /**
   * Get all attributes with changed types.
   *
   * @param memberDiff pair of attributes
   * @param compilationUnit class diagram
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> findMemberDiff(
      CDMemberDiff memberDiff, ASTCDCompilationUnit compilationUnit) {
    if (!getSrcType().getModifier().isAbstract()) {
      List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
      for (DiffTypes type : memberDiff.getBaseDiff()) {
        switch (type) {
          case CHANGED_ATTRIBUTE:
            list.add(
                new Pair<>(
                    (ASTCDClass) getSrcType(),
                    (ASTCDAttribute)
                        memberDiff.getSrcElem())); // add to Diff List new Pair(getElem1(),
            // memberDiff.getElem1()
          case CHANGED_VISIBILITY: // give as output to user - no semDiff
            // other cases
        }
      }
      return list;
    } else { // class is abstract and can't be instantiated - get a subclass
      for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
        if (getDirectSuperClasses(
                astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit))
            .contains(getSrcType())) { // can be made to contain ONLY - extends as in java or C++?
          List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
          for (DiffTypes type : memberDiff.getBaseDiff()) {
            switch (type) {
              case CHANGED_ATTRIBUTE:
                list.add(
                    new Pair<>(
                        astcdClass,
                        (ASTCDAttribute)
                            memberDiff.getSrcElem())); // add to Diff List new Pair(astcdClass,
                // memberDiff.getElem1())
              case CHANGED_VISIBILITY: // give as output to user - no semDiff
                // other cases?
            }
          }
          return list;
        }
      }
    }
    return null;
  }

  @Override
  public List<ASTCDClass> getClassesForEnum(ASTCDCompilationUnit compilationUnit) {
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (attribute.getMCType().printType().equals(getSrcType().toString())) {
          classList.add(astcdClass);
        }
      }
    }
    return classList;
  }

  /**
   * Add all attributes to the list changedMembers which have been changed.
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  // Function for adding to the list changedMembers all attributes
  // which have been changed in the source class diagram
  public void addAllChangedMembers(ASTCDType srcType, ASTCDType tgtType) {
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          CDMemberDiff diffMember = new CDMemberDiff(srcAttr, tgtAttr);
          if (diffMember.compareMember(srcAttr, tgtAttr) != null) {
            changedMembers.add(diffMember);
            baseDiffs.add(DiffTypes.CHANGED_ATTRIBUTE);
          }
        }
      }
    }
  }

  /**
   * Add all attributes to the list addedAttributes which have not been in the tgtType, but are in
   * the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void addAllAddedAttributes(ASTCDType srcType, ASTCDType tgtType) {
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        boolean notFound = true;
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            notFound = false;
            break;
          }
        }
        if (notFound) {
          addedAttributes.add(srcAttr);
          baseDiffs.add(DiffTypes.ADDED_ATTRIBUTE);
        }
      }
    }
  }

  /**
   * Add all attributes to the list deletedAttributes which have been in the tgtType, but aren't
   * anymore in the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void addAllDeletedAttributes(ASTCDType srcType, ASTCDType tgtType) {
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
        boolean notFound = true;
        for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            notFound = false;
            break;
          }
        }
        if (notFound) {
          deletedAttributes.add(tgtAttr);
          baseDiffs.add(DiffTypes.REMOVED_ATTRIBUTE);
        }
      }
    }
  }

  /**
   * Add all enum constants to the list addedConstants which are in the srcType, but haven't been in
   * the tgtType
   *
   * @param srcEnum an enum in the new CD
   * @param tgtEnum an enum in the old CD
   */
  public void addAllAddedConstants(ASTCDEnum srcEnum, ASTCDEnum tgtEnum) {
    for (ASTCDEnumConstant firstConstant : srcEnum.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : tgtEnum.getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(firstConstant);
        baseDiffs.add(DiffTypes.ADDED_CONSTANTS);
      }
    }
  }

  /**
   * Add all enum constants to the list deletedConstants which have been in the tgtType, but aren'T
   * in the srcType
   *
   * @param srcEnum an enum constant in the new CD
   * @param tgtEnum an enum constant in the old CD
   */
  public void addAllDeletedConstants(ASTCDEnum srcEnum, ASTCDEnum tgtEnum) {
    for (ASTCDEnumConstant firstConstant : tgtEnum.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : srcEnum.getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(firstConstant);
        baseDiffs.add(DiffTypes.REMOVED_CONSTANTS);
      }
    }
  }

  /**
   * Add all matched attributes to the list matchedAttributes
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void addAllMatchedAttributes(ASTCDType srcType, ASTCDType tgtType) {
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            matchedAttributes.add(new Pair(srcAttr, tgtAttr));
          }
        }
      }
    }
  }

  /**
   * Add all matched enum constants to the list matchedConstants
   *
   * @param srcEnum an enum in the new CD
   * @param tgtEnum an enum in the old CD
   */
  public void addAllMatchedConstants(ASTCDEnum srcEnum, ASTCDEnum tgtEnum) {
    for (ASTCDEnumConstant srcEnumConstant : srcEnum.getCDEnumConstantList()) {
      for (ASTCDEnumConstant tgtEnumConstant : tgtEnum.getCDEnumConstantList()) {
        if (srcEnumConstant.getName().equals(tgtEnumConstant.getName())) {
          matchedConstants.add(new Pair(srcEnumConstant, tgtEnumConstant));
        }
      }
    }
  }
}
