package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.CDDiffUtil.getAllSuperTypes;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isAttributInSuper;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDTypeDiff;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class CDTypeDiff extends CDPrintDiff implements ICDTypeDiff {
  private final ASTCDType srcElem;
  private final ASTCDType tgtElem;
  private List<CDMemberDiff> changedMembers;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttributes;
  private List<ASTCDAttribute> inheritedAttributes;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> matchedConstants;
  private List<DiffTypes> baseDiff;
  ASTCDCompilationUnit tgtCD;
  int srcLineOfCode, tgtLineOfCode;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  //Print
  private StringBuilder outputSrc,outputTgt, outputAdded, outputDeleted, outputChanged, outputDiff, outputNewlyAdded, outputNewlyDeleted;
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String
    srcModifier, srcType, srcName, srcExtends, srcImplements,
    tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements;
  private String modifierDelete, typeDelete, nameDelete, extendsDelete, implementsDelete;
  //Print end

  public CDTypeDiff(ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit tgtCD) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.tgtCD = tgtCD;
    this.baseDiff = new ArrayList<>();
    this.matchedAttributes = new ArrayList<>();
    this.matchedConstants = new ArrayList<>();
    this.changedMembers = new ArrayList<>();
    this.addedConstants = new ArrayList<>();
    this.inheritedAttributes = new ArrayList<>();
    this.addedAttributes = new ArrayList<>();
    this.deletedAttributes = new ArrayList<>();
    this.deletedConstants = new ArrayList<>();

    //Compare modifier and name of the types
    createDefaultDiffList(srcElem, tgtElem);
    //Load all matching elements from both types
    addAllMatchedElements(srcElem, tgtElem);
    //Find all added, deleted and changed attributes and constants
    loadAllLists(srcElem, tgtElem, tgtCD);
    //Set Strings for printing
    setStrings();
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
  public List<ASTCDAttribute> getDeletedAttributes() {
    return deletedAttributes;
  }
  @Override
  public void setDeletedAttributes(List<ASTCDAttribute> deletedAttribute) {
    this.deletedAttributes = deletedAttribute;
  }
  public List<ASTCDAttribute> getInheritedAttributes() {
    return inheritedAttributes;
  }
  @Override
  public void setInheritedAttributes(List<ASTCDAttribute> inheritedAttributes) { this.inheritedAttributes = inheritedAttributes; }
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
  public void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants) { this.deletedConstants = deletedConstants; }
  @Override
  public List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes() {
    return matchedAttributes;
  }
  @Override
  public List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants() {
    return matchedConstants;
  }
  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }
  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
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
    for (ASTCDAttribute attribute : getDeletedAttributes()){
      if (isDeleted(attribute, helper.getSrcCD())){
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
    if (!isAttributInSuper(attribute, getSrcElem(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())) {
      if (!getSrcElem().getModifier().isAbstract()) {
        return true;
      }
      List<ASTCDClass> classList = Syn2SemDiffHelper.getSpannedInheritance(compilationUnit, (ASTCDClass) getSrcElem());
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
    }
    return false;
  }

  /**
   * Get all attributes with changed types.
   *
   * @param memberDiff pair of attributes
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  @Override
  public Pair<ASTCDClass, ASTCDAttribute> findMemberDiff(CDMemberDiff memberDiff) {
    return new Pair<>((ASTCDClass) getSrcElem(), (ASTCDAttribute) memberDiff.getSrcElem());//add to Diff List new Pair(getElem1(), memberDiff.getElem1()
  }

  @Override
  public ASTCDType isClassNeeded() {
    ASTCDClass srcCLass = (ASTCDClass) getSrcElem();
    if (!srcCLass.getModifier().isAbstract()) {
      return getSrcElem();
    } else {
      //wrong - check if those associations exist in the old CD - done
      if (Syn2SemDiffHelper.getSpannedInheritance(helper.getSrcCD(), (ASTCDClass) getSrcElem()).isEmpty()) {
        Set<ASTCDClass> map = helper.getSrcMap().keySet();
        map.remove((ASTCDClass) getSrcElem());
        for (ASTCDClass astcdClass : map) {
          for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)) {
            if (assocStruct.getSide().equals(ClassSide.Left)
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).b.equals(srcElem)
              && helper.findMatchedClass((ASTCDClass) srcElem) != null
              && helper.classHasAssociationTgt(assocStruct.getAssociation(), helper.findMatchedClass((ASTCDClass) srcElem))) {
              return astcdClass;
            } else if (assocStruct.getSide().equals(ClassSide.Right)
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), helper.getSrcCD()).a.equals(srcElem)
              && helper.findMatchedClass((ASTCDClass) srcElem) != null
              && helper.classHasAssociationTgt(assocStruct.getAssociation(), helper.findMatchedClass((ASTCDClass) srcElem))) {
              return astcdClass;
            }

          }
        }
      }
    }
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
      if (isAdded(attribute, helper.getTgtCD())){
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
    if (isAttributInSuper(attribute, getTgtElem(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      return false;
    }
    if (!getSrcElem().getModifier().isAbstract()){
      return true;
    }
    List<ASTCDClass> classList = Syn2SemDiffHelper.getSpannedInheritance(compilationUnit, (ASTCDClass) getTgtElem());
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

  /*--------------------------------------------------------------------*/
  /**
   * Add all attributes to the list changedMembers which have been changed.   *
   */
  public void loadAllChangedMembers() {
    for(Pair<ASTCDAttribute,ASTCDAttribute> x : matchedAttributes){
      CDMemberDiff diffAttribute = new CDMemberDiff(x.a, x.b);
      if(!diffAttribute.getBaseDiff().isEmpty()) {
        changedMembers.add(diffAttribute);
        baseDiff.addAll(diffAttribute.getBaseDiff());
      }
    }
  }

  List<Pair<ASTCDAttribute, ASTCDAttribute>> removedBcInh = new ArrayList<>();
  private void loadAllInheritedAttributes(ASTCDType srcType, ASTCDType tgtType, ASTCDCompilationUnit tgtCD) {
    Set<ASTCDType> superTypesOfTgtType = getAllSuper(tgtType, (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
    superTypesOfTgtType.remove(tgtType);
    for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
      boolean inheritedFound = false;
      for(ASTCDType x : superTypesOfTgtType) {
        for (ASTCDAttribute tgtAttr : x.getCDAttributeList()) {
          if (tgtAttr.getName().equals(srcAttr.getName())) {
            inheritedFound = true;
            removedBcInh.add(new Pair<>(srcAttr, tgtAttr));
            break;
          }
        }
      }
      if(inheritedFound) {
        inheritedAttributes.add(srcAttr);
        if(!baseDiff.contains(DiffTypes.INHERITED_ATTRIBUTE)) {
          baseDiff.add(DiffTypes.INHERITED_ATTRIBUTE);
        }
      }
    }
  }

  public void loadAllAddedElements(ASTCDType srcType, ASTCDType tgtType){
    if(srcType instanceof ASTCDClass && tgtType instanceof ASTCDClass){
      loadAllAddedAttributes((ASTCDClass) srcType, (ASTCDClass) tgtType);
    }
    if(srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum){
      loadAllAddedConstants((ASTCDEnum) srcType, (ASTCDEnum) tgtType);
    }
  }

  /**
   * Add all attributes to the list addedAttributes which have not been in the tgtType, but are in
   * the srcType
   *
   * @param srcType a class in the new CD
   * @param tgtType a class in the old CD
   */
  public void loadAllAddedAttributes(ASTCDClass srcType, ASTCDClass tgtType) {
    for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
      boolean addedNotFound = true;
      if(inheritedAttributes.contains(srcAttr)) {
        addedNotFound = false;
      }
      for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
        if (srcAttr.getName().equals(tgtAttr.getName())) {
          addedNotFound = false;
          break;
        }
      }
      if (addedNotFound) {
        addedAttributes.add(srcAttr);
        if(!baseDiff.contains(DiffTypes.ADDED_ATTRIBUTE)) {
          baseDiff.add(DiffTypes.ADDED_ATTRIBUTE);
        }
      }
    }
  }

  /**
   * Add all enum constants to the list addedConstants which have not been in the tgtType, but are in
   * the srcType
   *
   * @param srcType an enum in the new CD
   * @param tgtType an enum in the old CD
   */
  public void loadAllAddedConstants(ASTCDEnum srcType, ASTCDEnum tgtType) {
    for (ASTCDEnumConstant srcConst : srcType.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant tgtConst : tgtType.getCDEnumConstantList()) {
        if (srcConst.getName().equals(tgtConst.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(srcConst);
        if(!baseDiff.contains(DiffTypes.ADDED_CONSTANT)) {
          baseDiff.add(DiffTypes.ADDED_CONSTANT);
        }
      }
    }
  }

  public void loadAllDeletedElements(ASTCDType srcType, ASTCDType tgtType){
    if(srcType instanceof ASTCDClass && tgtType instanceof ASTCDClass){
      loadAllDeletedAttributes((ASTCDClass) srcType, (ASTCDClass) tgtType);
    }
    if(srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum){
      loadAllDeletedConstants((ASTCDEnum) srcType, (ASTCDEnum) tgtType);
    }
  }

  /**
   * Add all attributes to the list deletedAttributes which have been in the tgtType, but aren't
   * anymore in the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void loadAllDeletedAttributes(ASTCDClass srcType, ASTCDClass tgtType) {
    for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
      boolean notFound = true;
      for (ASTCDAttribute a : inheritedAttributes){
        if(a.getName().equals(tgtAttr.getName())){
          notFound = false;
          break;
        }
      }
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        if (srcAttr.getName().equals(tgtAttr.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedAttributes.add(tgtAttr);
        if(!baseDiff.contains(DiffTypes.REMOVED_ATTRIBUTE)) {
          baseDiff.add(DiffTypes.REMOVED_ATTRIBUTE);
        }
      }
    }
  }

  /**
   * Add all enum constants to the list deletedConstants which have been in the tgtType, but aren't
   * anymore in the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void loadAllDeletedConstants(ASTCDEnum srcType, ASTCDEnum tgtType) {
    for (ASTCDEnumConstant tgtConst : tgtType.getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant srcConst : srcType.getCDEnumConstantList()) {
        if (tgtConst.getName().equals(srcConst.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedConstants.add(tgtConst);
        if(!baseDiff.contains(DiffTypes.DELETED_CONSTANT)) {
          baseDiff.add(DiffTypes.DELETED_CONSTANT);
        }
      }
    }
  }

  private void createDefaultDiffList(ASTCDType srcType, ASTCDType tgtType) {

    srcLineOfCode = srcElem.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtElem.get_SourcePositionStart().getLine();

    // Modifier
    Optional<ASTModifier> srcModifier = (pp.prettyprint(srcType.getModifier()).length() > 1) ? Optional.of(srcType.getModifier()) : Optional.empty();
    Optional<ASTModifier> tgtModifier = (pp.prettyprint(tgtType.getModifier()).length() > 1) ? Optional.of(tgtType.getModifier()) : Optional.empty();
    CDNodeDiff<ASTModifier, ASTModifier> modifier = new CDNodeDiff<>(srcModifier, tgtModifier);

    srcModifier.ifPresent(initial -> this.srcModifier = getColorCode(modifier) + pp.prettyprint(srcType.getModifier()) + RESET);
    srcModifier.ifPresent(initial -> this.modifierDelete = getColorCode(modifier) + pp.prettyprint(srcType.getModifier()) + RESET);
    tgtModifier.ifPresent(initial -> this.tgtModifier = getColorCode(modifier) + pp.prettyprint(tgtType.getModifier()) + RESET);

    if (modifier.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_CLASS_MODIFIER)) {
        baseDiff.add(DiffTypes.CHANGED_CLASS_MODIFIER);
      }
    }

    if(modifier.findAction() == Actions.REMOVED){
      this.modifierDelete = getColorCode(modifier) + pp.prettyprint(tgtType.getModifier()) + RESET;
    }

    // Name
    Optional<ASTCDType> srcName = Optional.of(srcType);
    Optional<ASTCDType> tgtName = Optional.of(tgtType);
    CDNodeDiff<ASTCDType, ASTCDType> className = new CDNodeDiff<>(null, srcName, tgtName);

    if (!srcName.get().getName().equals(tgtName.get().getName())) {
      className = new CDNodeDiff<>(Actions.CHANGED, srcName, tgtName);
    }

    this.srcName = getColorCode(className) + srcName.get().getName() + RESET;
    this.tgtName = getColorCode(className) + tgtName.get().getName() + RESET;

    if (className.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_CLASS_NAME)) {
        baseDiff.add(DiffTypes.CHANGED_CLASS_NAME);
      }
    }

    if(className.findAction() == Actions.REMOVED){
      this.nameDelete = getColorCode(className) + tgtType.getName() + RESET;
    } else {
      this.nameDelete = getColorCode(className) + srcType.getName() + RESET;
    }

    if ((srcType instanceof ASTCDClass) && (tgtType instanceof ASTCDClass)) {
      this.srcType = "class";
      this.tgtType = "class";
      this.typeDelete = "class";
      createClassDiff((ASTCDClass) srcType, (ASTCDClass) tgtType);
    } else if (srcType instanceof ASTCDInterface && tgtType instanceof ASTCDInterface) {
      this.srcType = "interface";
      this.tgtType = "interface";
      this.typeDelete = "interface";
      createInterfaceDiff((ASTCDInterface) srcType, (ASTCDInterface) tgtType);
    } else if (srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum) {
      this.srcType = "enum";
      this.tgtType = "enum";
      this.typeDelete = "enum";
      createEnumDiff((ASTCDEnum) srcType, (ASTCDEnum) tgtType);
    }
  }

  public void createClassDiff(ASTCDClass srcElem, ASTCDClass tgtElem) {
    createDiffList(srcElem, tgtElem);
  }

  public void createInterfaceDiff(ASTCDInterface srcElem, ASTCDInterface tgtElem) {
    createDiffList(srcElem, tgtElem);
  }

  public void createEnumDiff(ASTCDEnum srcElem, ASTCDEnum tgtElem) {
    createDiffList(srcElem, tgtElem);
  }

  private void createDiffList(ASTCDClass srcElem, ASTCDClass tgtElem) {

    // Extended
    Optional<ASTCDExtendUsage> srcElemExtends = (srcElem.isPresentCDExtendUsage()) ? Optional.of(srcElem.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> tgtElemExtends = (tgtElem.isPresentCDExtendUsage()) ? Optional.of(tgtElem.getCDExtendUsage()) : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedClassDiff = new CDNodeDiff<>(srcElemExtends, tgtElemExtends);

    srcElemExtends.ifPresent(initial -> srcExtends = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);
    srcElemExtends.ifPresent(initial -> extendsDelete = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);
    tgtElemExtends.ifPresent(initial -> tgtExtends = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);

    if (extendedClassDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_EXTENDS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_EXTENDS);
      }
    }

    if(extendedClassDiff.findAction() == Actions.REMOVED){
      this.extendsDelete = getColorCode(extendedClassDiff) + pp.prettyprint(tgtElem.getCDExtendUsage()) + RESET;
    }

    // Implements
    Optional<ASTMCObjectType> srcElemImplements = (srcElem.isPresentCDInterfaceUsage()) ? Optional.of(srcElem.getInterfaceList().get(0)) : Optional.empty();
    Optional<ASTMCObjectType> tgtElemImplements = (tgtElem.isPresentCDInterfaceUsage()) ? Optional.of(tgtElem.getInterfaceList().get(0)) : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> implementedClassDiff = new CDNodeDiff<>(srcElemImplements, tgtElemImplements);

    srcElemImplements.ifPresent(inter -> srcImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    srcElemImplements.ifPresent(inter -> implementsDelete = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> tgtImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);

    if (implementedClassDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_IMPLEMENTS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_IMPLEMENTS);
      }
    }

    if(implementedClassDiff.findAction() == Actions.REMOVED){
      tgtElemImplements.ifPresent(inter -> implementsDelete = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    }
  }

  private void createDiffList(ASTCDInterface srcElem, ASTCDInterface tgtElem) {

    // Extended
    Optional<ASTCDExtendUsage> srcElemExtends = (srcElem.isPresentCDExtendUsage()) ? Optional.of(srcElem.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> tgtElemExtends = (tgtElem.isPresentCDExtendUsage())? Optional.of(tgtElem.getCDExtendUsage()) : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> interfaceDiff = new CDNodeDiff<>(srcElemExtends, tgtElemExtends);

    srcElemExtends.ifPresent(initial -> srcExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    tgtElemExtends.ifPresent(initial -> tgtExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);

    if (interfaceDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_EXTENDS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_EXTENDS);
      }
    }

    if(interfaceDiff.findAction() == Actions.REMOVED){
      tgtElemExtends.ifPresent(initial -> this.extendsDelete = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    } else {
      srcElemExtends.ifPresent(initial -> this.extendsDelete = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    }
  }

  private void createDiffList(ASTCDEnum srcElem, ASTCDEnum tgtElem) {
    // Implements, optional
    Optional<ASTMCObjectType> srcElemImplements = (srcElem.isPresentCDInterfaceUsage()) ? Optional.of(srcElem.getInterfaceList().get(0)) : Optional.empty();
    Optional<ASTMCObjectType> tgtElemImplements = (tgtElem.isPresentCDInterfaceUsage()) ? Optional.of(tgtElem.getInterfaceList().get(0)) : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> enumDiff = new CDNodeDiff<>(srcElemImplements, tgtElemImplements);

    srcElemImplements.ifPresent(inter -> srcImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> tgtImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);

    if (enumDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_IMPLEMENTS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_IMPLEMENTS);
      }
    }

    if(enumDiff.findAction() == Actions.REMOVED){
      tgtElemImplements.ifPresent(inter -> tgtImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    } else {
      srcElemImplements.ifPresent(inter -> srcImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    }
  }

  /**
   * Add all matched elements to the lists
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */

  public void addAllMatchedElements(ASTCDType srcType, ASTCDType tgtType){
    if ((srcType instanceof ASTCDClass) && (tgtType instanceof ASTCDClass)) {
      for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
              matchedAttributes.add(new Pair<>(srcAttr, tgtAttr));
          }
        }
      }
    } else if (srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum) {
      for (ASTCDEnumConstant srcEnumConstant : ((ASTCDEnum) srcType).getCDEnumConstantList()) {
        for (ASTCDEnumConstant tgtEnumConstant : ((ASTCDEnum) tgtType).getCDEnumConstantList()) {
          if (srcEnumConstant.getName().equals(tgtEnumConstant.getName())) {
            matchedConstants.add(new Pair<>(srcEnumConstant, tgtEnumConstant));
          }
        }
      }
    }
  }

  public void loadAllLists(ASTCDType srcType, ASTCDType tgtType, ASTCDCompilationUnit tgtCD) {
    loadAllInheritedAttributes(srcType, tgtType, tgtCD);
    loadAllAddedElements(srcType, tgtType);
    loadAllDeletedElements(srcType, tgtType);
    loadAllChangedMembers();
  }

  private void setStrings() {
    List<Pair<Integer, String>> onlySrcCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyTgtCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyAddedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyDeletedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyChangedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyDiffSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyForNewlyAddedTypes = new ArrayList<>();
    List<Pair<Integer, String>> onlyForNewlyDeletedTypes = new ArrayList<>();

    String signatureSrcCD = insertSpaceBetweenStrings(Arrays.asList(srcModifier, srcType, srcName, srcExtends, srcImplements));
    String signatureSrcCDAddedClass = insertSpaceBetweenStringsAndGreen(Arrays.asList(srcModifier, srcType, srcName, srcExtends, srcImplements));
    String signatureTgtCD = insertSpaceBetweenStrings(Arrays.asList(tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements));
    String signatureTgtCDDeletedClass = insertSpaceBetweenStringsAndRed(Arrays.asList(tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements));
    String signatureDiff = insertSpaceBetweenStrings(Arrays.asList(modifierDelete, typeDelete, nameDelete, extendsDelete, implementsDelete));

    if (!addedAttributes.isEmpty()) {
      for (ASTCDAttribute x : addedAttributes) {
        CDMemberDiff diff = new CDMemberDiff(x, x);
        String comment = "//added attribute, L: " + diff.srcLineOfCode + System.lineSeparator();
        String tmp = comment + diff.printAddedMember() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDiffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedConstants.isEmpty()) {
      for (ASTCDEnumConstant x : addedConstants) {
        CDMemberDiff diff = new CDMemberDiff(x, x);
        String comment = "//added enum constant, L: " + diff.srcLineOfCode + System.lineSeparator();
        String tmp = comment + diff.printAddedMember() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDiffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedAttributes.isEmpty()) {
      for (ASTCDAttribute x : deletedAttributes) {
        CDMemberDiff diff = new CDMemberDiff(x, x);
        String comment = "//deleted attribute, L: " + diff.srcLineOfCode + System.lineSeparator();
        String tmp = comment + diff.printRemovedMember() + RESET;
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDiffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedConstants.isEmpty()) {
      for (ASTCDEnumConstant x : deletedConstants) {
        CDMemberDiff diff = new CDMemberDiff(x, x);
        String comment = "//deleted enum constant, L: " + diff.srcLineOfCode + System.lineSeparator();
        String tmp = comment + diff.printRemovedMember() + RESET;
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDiffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!inheritedAttributes.isEmpty()) {
      for (ASTCDAttribute x : inheritedAttributes) {
        CDMemberDiff diff = new CDMemberDiff(x, x);
        String commentOne = "//inherited attribute, L: " + diff.srcLineOfCode + System.lineSeparator();
        String tmpOne = commentOne + diff.printAddedMember() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmpOne));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmpOne));
        onlyDiffSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmpOne));
      }
    }

    if (!changedMembers.isEmpty()) {
      for (CDMemberDiff x : changedMembers) {
        String tmp = x.printChangedMember() + RESET;
        onlySrcCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
        onlyTgtCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
        onlyDiffSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
      }
    }

    //CDSyntaxDiff makes an CDTypeDiff object for each added class
    //That means that all attributes match because they are the same
    //That is why we can say for all matched attributes, add them in the list
    if (!matchedAttributes.isEmpty()) {
      for (Pair<ASTCDAttribute,ASTCDAttribute> x : matchedAttributes) {
        CDMemberDiff memberDiff = new CDMemberDiff(x.a, x.b);

        String tmp = memberDiff.printAddedMember() + RESET;
        onlyForNewlyAddedTypes.add(new Pair<>(x.a.get_SourcePositionStart().getLine(), tmp));

        String tmpTwo = memberDiff.printRemovedMember() + RESET;
        onlyForNewlyDeletedTypes.add(new Pair<>(x.a.get_SourcePositionStart().getLine(), tmpTwo));
      }
    }

    if (!matchedConstants.isEmpty()) {
      for (Pair<ASTCDEnumConstant,ASTCDEnumConstant> x : matchedConstants) {
        CDMemberDiff memberDiff = new CDMemberDiff(x.a, x.b);

        String tmp = memberDiff.printAddedMember() + RESET;
        onlyForNewlyAddedTypes.add(new Pair<>(x.a.get_SourcePositionStart().getLine(), tmp));

        String tmpTwo = memberDiff.printRemovedMember() + RESET;
        onlyForNewlyDeletedTypes.add(new Pair<>(x.a.get_SourcePositionStart().getLine(), tmpTwo));
      }
    }

    //--print src
    onlySrcCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlySrc = new StringBuilder();
    outputOnlySrc.append(signatureSrcCD).append("{");
    if (!onlySrcCDSort.isEmpty()) {
      for (Pair<Integer, String> x : onlySrcCDSort) {
        outputOnlySrc.append(System.lineSeparator()).append(x.b);
      }
      outputOnlySrc.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outputOnlySrc.append("}").append(System.lineSeparator());
    }
    this.outputSrc = outputOnlySrc;

    //--print tgt
    onlyTgtCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlyTgt = new StringBuilder();
    outputOnlyTgt.append(signatureTgtCD).append("{");
    if (!onlyTgtCDSort.isEmpty()) {
      for (Pair<Integer, String> x : onlyTgtCDSort) {
        outputOnlyTgt.append(System.lineSeparator()).append(x.b);
      }
      outputOnlyTgt.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outputOnlyTgt.append("}").append(System.lineSeparator());
    }
    this.outputTgt = outputOnlyTgt;

    //--print added
    // Two variants
    // 1. We have newly added class with all its attributes
    // 2. We have an already existing class, but we want to show only its added attributes

    String addedComment = "//added type, L: " + srcLineOfCode + System.lineSeparator();
    // This is for 1.
    onlyForNewlyAddedTypes.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyNewlyAddedTypes = new StringBuilder();
    outPutOnlyNewlyAddedTypes.append(COLOR_ADD).append(addedComment).append(COLOR_ADD).append(signatureSrcCDAddedClass).append(COLOR_ADD).append("{");
    if (!onlyForNewlyAddedTypes.isEmpty()) {
      for (Pair<Integer, String> x : onlyForNewlyAddedTypes) {
        outPutOnlyNewlyAddedTypes.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyNewlyAddedTypes.append(System.lineSeparator()).append(COLOR_ADD).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyNewlyAddedTypes.append(COLOR_ADD).append("}").append(System.lineSeparator());
    }
    this.outputNewlyAdded = outPutOnlyNewlyAddedTypes;

    // This is for 2.
    onlyAddedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyAddedAttributes = new StringBuilder();
    outPutOnlyAddedAttributes.append(signatureSrcCD).append("{");
    if (!onlyAddedSort.isEmpty()) {
      for (Pair<Integer, String> x : onlyAddedSort) {
        outPutOnlyAddedAttributes.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyAddedAttributes.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyAddedAttributes.append("}").append(System.lineSeparator());
    }
    this.outputAdded = outPutOnlyAddedAttributes;

    //--print deleted
    // Two variants
    // 1. We have full deleted class with all its attributes
    // 2. We have an already existing class, but we want to show only its deleted attributes

    // This is for 1.
    onlyForNewlyDeletedTypes.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyNewlyDeletedTypes = new StringBuilder();
    outPutOnlyNewlyDeletedTypes.append(COLOR_DELETE).append(signatureTgtCDDeletedClass).append(COLOR_DELETE).append("{");
    if (!onlyForNewlyDeletedTypes.isEmpty()) {
      for (Pair<Integer, String> x : onlyForNewlyDeletedTypes) {
        outPutOnlyNewlyDeletedTypes.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyNewlyDeletedTypes.append(System.lineSeparator()).append(COLOR_DELETE).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyNewlyDeletedTypes.append(COLOR_DELETE).append("}").append(System.lineSeparator());
    }
    this.outputNewlyDeleted = outPutOnlyNewlyDeletedTypes;

    // This is for 2.
    onlyDeletedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyDeletedAttributes = new StringBuilder();
    outPutOnlyDeletedAttributes.append(signatureTgtCD).append("{");
    if (!onlyDeletedSort.isEmpty()) {
      for (Pair<Integer, String> x : onlyDeletedSort) {
        outPutOnlyDeletedAttributes.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyDeletedAttributes.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyDeletedAttributes.append("}").append(System.lineSeparator());
    }
    this.outputDeleted = outPutOnlyDeletedAttributes;

    String comment = "//changed type, L: " + srcLineOfCode + System.lineSeparator();

    //--print changed
    onlyChangedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyChanged = new StringBuilder();

    outPutOnlyChanged.append(comment).append(signatureSrcCD).append("{");
    if (!onlyChangedSort.isEmpty()) {
      for (Pair<Integer, String> x : onlyChangedSort) {
        outPutOnlyChanged.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyChanged.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyChanged.append("}").append(System.lineSeparator());
    }
    this.outputChanged = outPutOnlyChanged;

    //--print diff
    onlyDiffSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyDiff = new StringBuilder();
    outPutOnlyDiff.append(comment).append(signatureDiff).append("{");
    if (!onlyDiffSort.isEmpty()) {
      for (Pair<Integer, String> x : onlyDiffSort) {
        outPutOnlyDiff.append(System.lineSeparator()).append(x.b);
      }
      outPutOnlyDiff.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    } else {
      outPutOnlyDiff.append("}").append(System.lineSeparator());
    }
    this.outputDiff = outPutOnlyDiff;
  }

  //We use this method if we want to show only the added attributes
  public String printIfAddedAttr() { return outputAdded.toString(); }
  //We use this method if we want to show only the removed attributes
  public String printIfRemovedAttr() { return outputDeleted.toString(); }
  //We use this method if we want to print the src class with its added and changed attributes
  public String printSrcCD() { return outputSrc.toString(); }
  //We use this method if we want to print the tgt class with its removed and changed attributes
  public String printTgtCD() { return outputTgt.toString(); }
  //We use this method if we want to print a whole class which is newly added
  public String printAddedType() { return outputNewlyAdded.toString(); }
  //We use this method if we want to print a whole class which is newly removed
  public String printRemovedType() { return outputNewlyDeleted.toString(); }
  //We use this method if we want to print a class with its added, removed, and changed attributes --print diff
  public String printDiffType() { return outputDiff.toString(); }
  //We use this method if we want to print a class only with its added and changed attributes --print changed
  public String printChangedType() { return outputChanged.toString(); }
}
