package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDTypeDiff;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.prettyprint.IndentPrinter;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;

public class CDTypeDiff implements ICDTypeDiff {
  private final ASTCDType srcElem;
  private final ASTCDType tgtElem;
  private List<CDMemberDiff> changedMembers;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttributes;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> matchedConstants;
  private List<DiffTypes> baseDiffs;
  protected MatchingStrategy<ASTCDType> typeMatcher;

  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  //Print help functions and strings
  CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());


  public CDTypeDiff(ASTCDType srcElem, ASTCDType tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
  }

  @Override
  public ASTCDType getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTCDType getTgtElem() {
    return tgtElem;
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
        + getTgtElem().getModifier()
        + " to "
        + getSrcElem().getModifier();
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

  public ASTCDAttribute getOldAttribute(ASTCDAttribute attribute){
    for (Pair<ASTCDAttribute, ASTCDAttribute> pair : matchedAttributes){
      if (attribute.equals(pair.a)){
        return pair.b;
      }
    }
    return null;
  }

  /**
   * Check for each attribute in the list deletedAttribute if it
   * has been really deleted and add it to a list.
   *
   * @return list of pairs of the class with a deleted attribute.
   */
  @Override
  public Pair<ASTCDClass, List<ASTCDAttribute>> deletedAttributes(){
    List<ASTCDAttribute> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getDeletedAttribute()){
      if (!helper.getNotInstanClassesSrc().contains((ASTCDClass) srcElem)
        && isDeleted(attribute, helper.getSrcCD())){
        pairList.add(attribute);
      }
    }
    return new Pair<>( (ASTCDClass) getSrcElem(), pairList);
  }

  /**
   * Check if an attribute is really deleted.
   * @param attribute from list deletedAttributes.
   * @param compilationUnit srcCD
   * @return false if found in inheritance hierarchy or the class is now abstract and the structure is refactored
   */
  public boolean isDeleted(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit) {
    if (isAttributInSuper(attribute, getSrcElem(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())) {
      return false;
    } else {
      if (!getSrcElem().getModifier().isAbstract()) {
        return true;
      }
      Set<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getSrcElem(), compilationUnit);
      classList.remove(getSrcElem());
      boolean conditionSatisfied = false; // Track if the condition is satisfied
      for (ASTCDClass astcdClass : classList) {
        if (!helper.getNotInstanClassesSrc().contains(astcdClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, astcdClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
          astcdClassList.remove(getSrcElem());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
              && !helper.getNotInstanClassesSrc().contains((ASTCDClass) type)) {
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
                conditionSatisfied = true; // Set the flag to true if the condition holds
                break;
              }
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
   * Get all attributes with changed types.
   *
   * @param memberDiff pair of attributes
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  @Override
  public Pair<ASTCDClass, ASTCDAttribute> findMemberDiff(CDMemberDiff memberDiff) {
    if (!getSrcElem().getModifier().isAbstract()) {
      return new Pair<>((ASTCDClass) getSrcElem(), (ASTCDAttribute) memberDiff.getSrcElem());//add to Diff List new Pair(getElem1(), memberDiff.getElem1()
    } else { //class is abstract and can't be instantiated - get a subclass
      return new Pair<>(minDiffWitness((ASTCDClass) getSrcElem()), (ASTCDAttribute) memberDiff.getSrcElem());//add to Diff List new Pair(astcdClass, memberDiff.getElem1())
    }
  }

  public ASTCDClass minDiffWitness(ASTCDClass astcdClass){
    assert helper.getSrcCD() !=null;
    Set<ASTCDClass> set = getSpannedInheritance(astcdClass, helper.getSrcCD());

    ASTCDClass closestClass = null;
    int closestDepth = Integer.MAX_VALUE;

    for (ASTCDClass cdClass : set) {
      if (!cdClass.getModifier().isAbstract()
        && !helper.getNotInstanClassesSrc().contains(astcdClass)) {
        int depth = getDepthOfClass(cdClass, astcdClass);
        if (depth < closestDepth) {
          closestClass = cdClass;
          closestDepth = depth;
        }
      }
    }

    return closestClass;
  }

  public int getDepthOfClass(ASTCDClass classNode, ASTCDClass rootClass) {
    if (classNode == null || rootClass == null) {
      return -1; // or any other suitable value to indicate an invalid depth
    }

    if (classNode == rootClass) {
      return 0; // base case: classNode is the root class
    }

    int maxDepth = -1;
    for (ASTCDType directSuperClass : CDInheritanceHelper.getDirectSuperClasses(classNode, (ICD4CodeArtifactScope) helper.getSrcCD().getEnclosingScope())) {
      if (directSuperClass instanceof ASTCDClass) {
        int depth = getDepthOfClass((ASTCDClass) directSuperClass, rootClass);
        if (depth >= 0 && depth > maxDepth) {
          maxDepth = depth;
        }
      }
    }

    return maxDepth >= 0 ? maxDepth + 1 : -1; // add 1 to the maximum depth found
  }
  public int calculateClassDepth(ASTCDClass rootClass, ASTCDClass targetClass) {
    // Check if the target class is the root class
    if (rootClass.getName().equals(targetClass.getName())) {
      return 0;
    }

    // Get the direct superclasses of the target class
    Set<ASTCDClass> superClasses = CDDiffUtil.getAllSuperclasses(targetClass, helper.getSrcCD().getCDDefinition().getCDClassesList());

    // If the target class has no superclasses, it is not in the hierarchy
    if (superClasses.isEmpty()) {
      return -1;
    }

    // Recursively calculate the depth for each direct superclass
    List<Integer> depths = new ArrayList<>();
    for (ASTCDClass superClass : superClasses) {
      int depth = calculateClassDepth(rootClass, superClass);
      if (depth >= 0) {
        depths.add(depth + 1);
      }
    }

    // Return the maximum depth from the direct superclasses
    if (depths.isEmpty()) {
      return -1;
    } else {
      return depths.stream().max(Integer::compare).get();
    }
  }

  //this doesn't belong here
  @Override
  public List<ASTCDClass> getClassesForEnum(ASTCDCompilationUnit compilationUnit) {
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()){
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()){
        if (attribute.getMCType().printType().equals(getSrcElem().toString())){
          classList.add(astcdClass);
        }
      }
    }
    return classList;
  }

  @Override
  public ASTCDType isClassNeeded() {
    ASTCDClass srcCLass = (ASTCDClass) getSrcElem();
    if (!srcCLass.getModifier().isAbstract()){
      return getSrcElem();
    }
    else{
      //do we check if assocs make sense - assoc to abstract class
      if (Syn2SemDiffHelper.getSpannedInheritance(helper.getSrcCD(), (ASTCDClass) getSrcElem()).isEmpty()) {
        Set<ASTCDClass> map = helper.getSrcMap().keySet();
        map.remove((ASTCDClass) getSrcElem());
        for (ASTCDClass astcdClass : map) {
          for (AssocStruct mapPair : helper.getSrcMap().get(astcdClass)) {//Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>
            if (Objects.equals(mapPair.getDirection(), AssocDirection.LeftToRight)
              && Syn2SemDiffHelper.getConnectedClasses(mapPair.getAssociation(), helper.getSrcCD()).b.equals(getSrcElem())
              && mapPair.getAssociation().getRight().getCDCardinality().isAtLeastOne()) {
              //add to Diff List - class can be instantiated without the abstract class
              return astcdClass;
            } else if (Objects.equals(mapPair.getDirection(), AssocDirection.RightToLeft)
              && Syn2SemDiffHelper.getConnectedClasses(mapPair.getAssociation(), helper.getSrcCD()).a.equals(getSrcElem())
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

  /**
   * Check for each attribute in the list addedAttributes if it
   * has been really added and add it to a list.
   * @return list of pairs of the class with an added (new) attribute.
   */
  @Override
  public Pair<ASTCDClass, List<ASTCDAttribute>> addedAttributes(){
    List<ASTCDAttribute> pairList = new ArrayList<>();
    for (ASTCDAttribute attribute : getAddedAttributes()){
      if (!helper.getNotInstanClassesSrc().contains((ASTCDClass) srcElem)
        &&isAdded(attribute, helper.getTgtCD())){
        pairList.add(attribute);
      }
    }
    return new Pair<>( (ASTCDClass) getSrcElem(), pairList);
  }

  /**
   * Check if an attribute is really added.
   * @param attribute from addedList
   * @param compilationUnit for diagram (trg)
   * @return false if found in all 'old' subclasses or in some 'old' superClass
   */
  public boolean isAdded(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit){
    if (CDInheritanceHelper.isAttributInSuper(attribute, getTgtElem(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      return false;
    }
    if (!getSrcElem().getModifier().isAbstract()){
      return true;
    }
    Set<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getTgtElem(), compilationUnit);
    classList.remove(tgtElem);
    boolean conditionSatisfied = false; // Track if the condition is satisfied
    for (ASTCDClass astcdClass : classList) {
      if (!helper.getNotInstanClassesTgt().contains(astcdClass)
        &&!Syn2SemDiffHelper.isAttContainedInClass(attribute, astcdClass)) {
        Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
        astcdClassList.remove(getTgtElem());
        for (ASTCDType type : astcdClassList) {
          if (type instanceof ASTCDClass
            && helper.getNotInstanClassesSrc().contains((ASTCDClass) type)
            && Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
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
  public Pair<ASTCDEnum, List<ASTCDEnumConstant>> newConstants() {
    List<ASTCDEnumConstant> pairList = new ArrayList<>();
    if (!getAddedConstants().isEmpty()) {
      pairList.addAll(getAddedConstants());
    }
    return new Pair<>((ASTCDEnum) getSrcElem(), pairList);
  }

  /**
   * Compute all changed attributes in all classes.
   * @return list of pairs of classes and changed attributes.
   */
  @Override
  public Pair<ASTCDClass, List<ASTCDAttribute>> changedAttribute(){
    List<ASTCDAttribute> pairList = new ArrayList<>();
    for (CDMemberDiff memberDiff : getChangedMembers()){
      if (findMemberDiff(memberDiff) != null){
        pairList.add(findMemberDiff(memberDiff).b);
      }
    }
    return new Pair<>((ASTCDClass) getSrcElem(), pairList);
  }

  /**
   * Add all attributes to the list changedMembers which have been changed.
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void addAllChangedMembers(ASTCDType srcType, ASTCDType tgtType) {
    boolean changedMember = false;
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          CDMemberDiff diffMember = new CDMemberDiff(srcAttr, tgtAttr);
          if (diffMember.compareMember(srcAttr, tgtAttr) != null) {
            changedMembers.add(diffMember);
            changedMember = true;
          }
        }
      }
    }
    if (changedMember) {
      baseDiffs.add(DiffTypes.CHANGED_ATTRIBUTE);
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
    boolean addedAttribute = false;
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        boolean notFound = true;
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            notFound = false;
            addedAttribute = true;
            break;
          }
        }
        if (notFound) {
          addedAttributes.add(srcAttr);
        }
      }
    }
    if (addedAttribute) {
      baseDiffs.add(DiffTypes.ADDED_ATTRIBUTE);
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
    boolean removedAttribute = false;
    if (typeMatcher.isMatched(srcType, tgtType)) {
      for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
        boolean notFound = true;
        for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            notFound = false;
            removedAttribute = true;
            break;
          }
        }
        if (notFound) {
          deletedAttributes.add(tgtAttr);
        }
      }
    }
    if (removedAttribute) {
      baseDiffs.add(DiffTypes.REMOVED_ATTRIBUTE);
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
    boolean addedConstant = false;
    for (ASTCDEnumConstant firstConstant : srcEnum.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : tgtEnum.getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          addedConstant = true;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(firstConstant);
      }
    }
    if (addedConstant) {
      baseDiffs.add(DiffTypes.ADDED_CONSTANTS);
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
    boolean deletedConstant = false;
    for (ASTCDEnumConstant firstConstant : tgtEnum.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : srcEnum.getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          deletedConstant = true;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(firstConstant);
      }
    }
    if (deletedConstant) {
      baseDiffs.add(DiffTypes.REMOVED_CONSTANTS);
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
