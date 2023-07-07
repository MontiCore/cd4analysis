package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

public class CDTypeDiff implements ICDTypeDiff {
  private final ASTCDType elem1;
  private final ASTCDType elem2;
  private List<CDMemberDiff> changedMembers;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttributes;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> matchedConstants;
  private List<DiffTypes> baseDiffs;

  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  public CDTypeDiff(ASTCDType elem1, ASTCDType elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }

  @Override
  public ASTCDType getElem1() {
    return elem1;
  }

  @Override
  public ASTCDType getElem2() {
    return elem2;
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
    return "Modifier changed from " + getElem2().getModifier() + " to " + getElem1().getModifier();
  }

  /**
   * Compute the type difference of the changed attributes.
   * @return old and new type for each changed pair.
   */
  public String attDiff() {
    StringBuilder stringBuilder = new StringBuilder();
    for (CDMemberDiff member : getChangedMembers()) {
      if (member.getSrcElem() instanceof ASTCDAttribute) {
        ASTCDAttribute attribute1 = (ASTCDAttribute) member.getSrcElem();
        ASTCDAttribute attribute2 = (ASTCDAttribute) member.getTgtElem();
        stringBuilder.append("Attribute type changed from ")
          .append(attribute2.getMCType().printType())
          .append(" to ")
          .append(attribute1.getMCType().printType());
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Check for each attribute in the list deletedAttribute if it
   * has been really deleted and add it to a list.
   * @param compilationUnit class diagram
   * @return list of pairs of the class with a deleted attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes(ASTCDCompilationUnit compilationUnit){
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getDeletedAttribute()){
      if (isDeleted(attribute, compilationUnit)){
        pairList.add(new Pair<>((ASTCDClass) getElem1(), attribute));
      }
    }
    return pairList;
  }

  /**
   * Check if an attribute is really deleted.
   * @param attribute from list deletedAttributes.
   * @param compilationUnit srcCD
   * @return false if found in inheritance hierarchy or the class is now abstract and the structure is refactored
   */
  public boolean isDeleted(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit){
    if (isAttributInSuper(attribute, getElem1(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      return false;
    } else {
      if (!getElem1().getModifier().isAbstract()){
        return true;
      }
      Set<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getElem1(), compilationUnit);
      boolean conditionSatisfied = false; // Track if the condition is satisfied
      for (ASTCDClass astcdClass : classList) {
        if (!astcdClass.getCDAttributeList().contains(attribute)) {
          Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
          astcdClassList.remove(getElem1());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass && type.getCDAttributeList().contains(attribute)) {
              conditionSatisfied = true; // Set the flag to true if the condition holds
              break;
            }
          }
        } else {
          conditionSatisfied = true;
        }
        if (!conditionSatisfied) {//found a subclass that doesn't have this attribute
          return true;// Break out of the first loop if the condition is satisfied
        } else {
          conditionSatisfied = false;
        }
      }
      return false;
    }
  }

  /**
   * Check for each attribute in the list addedAttributes if it
   * has been really added and add it to a list.
   * @param compilationUnit trgCD
   * @return list of pairs of the class with an added (new) attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes(ASTCDCompilationUnit compilationUnit){
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getAddedAttributes()){
      if (isAdded(attribute, compilationUnit)){
        pairList.add(new Pair<>((ASTCDClass) getElem1(), attribute));
      }
    }
    return pairList;
  }
  /**
   * Check if an attribute is really added.
   * @param attribute from addedList
   * @param compilationUnit for diagram (trg)
   * @return false if found in all 'old' subclasses or in some 'old' superClass
   */
  public boolean isAdded(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit){
    if (CDInheritanceHelper.isAttributInSuper(attribute, getElem2(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      return false;
    }
    if (!getElem1().getModifier().isAbstract()){
      return true;
    }
    Set<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getElem2(), compilationUnit);
    boolean conditionSatisfied = false; // Track if the condition is satisfied
    for (ASTCDClass astcdClass : classList) {
      if (!astcdClass.getCDAttributeList().contains(attribute)) {
        Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
        astcdClassList.remove(getElem2());
        for (ASTCDType type : astcdClassList) {
          if (type instanceof ASTCDClass && type.getCDAttributeList().contains(attribute)) {
            conditionSatisfied = true; // Set the flag to true if the condition holds
            break;
          }
        }
      } else {
        conditionSatisfied = true;
      }
      if (!conditionSatisfied) {//found a subclass that doesn't have this attribute
        return true;// Break out of the first loop if the condition is satisfied
      } else {
        conditionSatisfied = false;
      }
    }
    return false;
  }

  /**
   * Compute the spanned inheritance of a given class.
   * That is we get all classes that are extending (not only direct) a class
   * @param astcdClass compute subclasses of this class
   * @param compilationUnit class diagram
   * @return set of extending classes.
   * The implementation is not efficient (no way to go from subclasses to superclasses).
   */
  @Override
  public Set<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit){
    Set<ASTCDClass> subclasses = new HashSet<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if ((getAllSuper(childClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit))).contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    return subclasses;
  }

  /**
   * Get all added constants to an enum
   * @return list of added constants
   */
  //TODO: get the attribute that uses this Enum
  @Override
  public List<Pair<ASTCDClass, ASTCDEnumConstant>> newConstants(){
    List<Pair<ASTCDClass, ASTCDEnumConstant>> pairList = new ArrayList<>();
    if (!getAddedConstants().isEmpty()){
      for (ASTCDEnumConstant constant : getAddedConstants()){
        pairList.add(new Pair<>((ASTCDClass) getElem1(), constant));
      }
    }
    return pairList;
  }

  /**
   * Compute all changed attributes in all classes.
   * @param compilationUnit class diagram
   * @return list of pairs of classes and changed attributes.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> changedAttribute(ASTCDCompilationUnit compilationUnit){
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    for (CDMemberDiff memberDiff : getChangedMembers()){
      if (findMemberDiff(memberDiff, compilationUnit) != null){
        pairList.addAll(findMemberDiff(memberDiff, compilationUnit));
      }
    }
    return pairList;
  }

  /**
   * Get all attributes with changed types.
   * @param memberDiff pair of attributes
   * @param compilationUnit class diagram
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  @Override
  public List<Pair<ASTCDClass, ASTCDAttribute>> findMemberDiff(CDMemberDiff memberDiff, ASTCDCompilationUnit compilationUnit){
    if (!getElem1().getModifier().isAbstract()) {
      List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
      for (DiffTypes type : memberDiff.getBaseDiff()) {
        switch (type) {
          case CHANGED_ATTRIBUTE: list.add(new Pair<>((ASTCDClass)getElem1(),(ASTCDAttribute) memberDiff.getSrcElem()));//add to Diff List new Pair(getElem1(), memberDiff.getElem1()
          case CHANGED_VISIBILITY: //give as output to user - no semDiff
            //other cases
        }
      }
      return list;
    }
    else { //class is abstract and can't be instantiated - get a subclass
      //TODO: direct sublclasses might also be abstract
      for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()){
        if (getDirectSuperClasses(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit)).contains(getElem1())){// can be made to contain ONLY - extends as in java or C++?
          List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
          for (DiffTypes type : memberDiff.getBaseDiff()) {
            switch (type) {
              case CHANGED_ATTRIBUTE: list.add(new Pair<>(astcdClass, (ASTCDAttribute) memberDiff.getSrcElem()));//add to Diff List new Pair(astcdClass, memberDiff.getElem1())
              case CHANGED_VISIBILITY: //give as output to user - no semDiff
                //other cases?
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
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()){
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()){
        if (attribute.getMCType().printType().equals(getElem1().toString())){
          classList.add(astcdClass);
        }
      }
    }
    return classList;
  }

  @Override
  public ASTCDType isClassNeeded(CDSyntaxDiff cdSyntaxDiff) {
    //TODO: check
    ASTCDClass srcCLass = (ASTCDClass) getElem1();
    if (!srcCLass.getModifier().isAbstract()){
      return getElem1();
    }
    else{
      //do we check if assocs make sense - assoc to abstract class
      //TODO: ask Max if this case is allowed
      //it is needed
      //TODO:
      if (Syn2SemDiffHelper.getSpannedInheritance(cdSyntaxDiff.getSrcCD(), (ASTCDClass) getElem1()).isEmpty()) {
        Set<ASTCDClass> map = helper.getSrcMap().keySet();
        map.remove((ASTCDClass) getElem1());
        for (ASTCDClass astcdClass : map) {
          for (AssocStruct mapPair : helper.getSrcMap().get(astcdClass)) {//Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>
            if (Objects.equals(mapPair.getDirection(), AssocDirection.LeftToRight)
              && Syn2SemDiffHelper.getConnectedClasses(mapPair.getAssociation(), cdSyntaxDiff.getSrcCD()).b.equals(getElem1())
              && mapPair.getAssociation().getRight().getCDCardinality().isAtLeastOne()) {
              //add to Diff List - class can be instantiated without the abstract class
              return astcdClass;
            } else if (Objects.equals(mapPair.getDirection(), AssocDirection.RightToLeft)
              && Syn2SemDiffHelper.getConnectedClasses(mapPair.getAssociation(), cdSyntaxDiff.getSrcCD()).a.equals(getElem1())
              && mapPair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
              //add to Diff List - class can be instantiated without the abstract class
              return astcdClass;
            } else if (Objects.equals(mapPair.getDirection(), AssocDirection.BiDirectional)) {
              if (Objects.equals(mapPair.getSide(), ClassSide.Left)
                && mapPair.getAssociation().getRight().getCDCardinality().isAtLeastOne()) {
                //add to Diff List - class can be instantiated without the abstract class
                return astcdClass;
              } else if (mapPair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
                //add to Diff List - class can be instantiated without the abstract class
                return astcdClass;
              }
            }
          }
        }
      }
    }
    //not implemented
    return null;
  }
}
