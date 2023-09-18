package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.CDDiffUtil.getAllSuperTypes;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isAttributInSuper;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
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
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String
    srcModifier, srcType, srcName, srcExtends, srcImplements, srcModifierNC, srcTypeNC, srcNameNC, srcExtendsNC, srcImplementsNC,
    tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements, tgtModifierNC, tgtTypeNC, tgtNameNC, tgtExtendsNC, tgtImplementsNC,
    srcPrint, tgtPrint, srcPrintOnlyAdded, tgtPrintOnlyRemoved, classPrint, addedType, removedType,
    typeDiff;
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
    return null;
  }
  @Override
  public List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants() {
    return null;
  }
  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }
  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
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
  public Set<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit) {
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

  /*--------------------------------------------------------------------*/
  /**
   * Add all attributes to the list changedMembers which have been changed.   *
   */
  public void loadAllChangedMembers() {
    for(Pair<ASTCDAttribute,ASTCDAttribute> x : matchedAttributes){
      CDMemberDiff diffAttribute = new CDMemberDiff(x.a, x.b);
      if(diffAttribute.getBaseDiff() != null) {
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
    tgtModifier.ifPresent(initial -> this.tgtModifier = getColorCode(modifier) + pp.prettyprint(tgtType.getModifier()) + RESET);

    srcModifier.ifPresent(initial -> this.srcModifierNC = pp.prettyprint(srcType.getModifier()));
    tgtModifier.ifPresent(initial -> this.tgtModifierNC = pp.prettyprint(tgtType.getModifier()));

    if (modifier.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_CLASS_MODIFIER)) {
        baseDiff.add(DiffTypes.CHANGED_CLASS_MODIFIER);
      }
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
    this.srcNameNC = srcName.get().getName() + RESET;
    this.tgtNameNC = tgtName.get().getName() + RESET;

    if (className.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_CLASS_NAME)) {
        baseDiff.add(DiffTypes.CHANGED_CLASS_NAME);
      }
    }

    if ((srcType instanceof ASTCDClass) && (tgtType instanceof ASTCDClass)) {
      this.srcType = this.srcTypeNC = "class";
      this.tgtType = this.tgtTypeNC = "class";
      createClassDiff((ASTCDClass) srcType, (ASTCDClass) tgtType);
    } else if (srcType instanceof ASTCDInterface && tgtType instanceof ASTCDInterface) {
      this.srcType = this.srcTypeNC = "interface";
      this.tgtType = this.tgtTypeNC = "interface";
      createInterfaceDiff((ASTCDInterface) srcType, (ASTCDInterface) tgtType);
    } else if (srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum) {
      this.srcType = this.srcTypeNC = "enum";
      this.tgtType = this.tgtTypeNC = "enum";
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
    tgtElemExtends.ifPresent(initial -> tgtExtends = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);
    srcElemExtends.ifPresent(initial -> this.srcExtendsNC = pp.prettyprint(initial));
    tgtElemExtends.ifPresent(initial -> this.tgtExtendsNC = pp.prettyprint(initial));

    if (extendedClassDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_EXTENDS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_EXTENDS);
      }
    }

    // Implements
    Optional<ASTMCObjectType> srcElemImplements = (srcElem.isPresentCDInterfaceUsage()) ? Optional.of(srcElem.getInterfaceList().get(0)) : Optional.empty();
    Optional<ASTMCObjectType> tgtElemImplements = (tgtElem.isPresentCDInterfaceUsage()) ? Optional.of(tgtElem.getInterfaceList().get(0)) : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> implementedClassDiff = new CDNodeDiff<>(srcElemImplements, tgtElemImplements);

    srcElemImplements.ifPresent(inter -> srcImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> tgtImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    srcElemImplements.ifPresent(inter -> this.srcImplementsNC = "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> this.tgtImplementsNC = "implements " + pp.prettyprint(inter) + RESET);


    if (implementedClassDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_IMPLEMENTS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_IMPLEMENTS);
      }
    }
  }

  private void createDiffList(ASTCDInterface srcElem, ASTCDInterface tgtElem) {

    // Extended
    Optional<ASTCDExtendUsage> srcElemExtends = (srcElem.isPresentCDExtendUsage()) ? Optional.of(srcElem.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> tgtElemExtends = (tgtElem.isPresentCDExtendUsage())? Optional.of(tgtElem.getCDExtendUsage()) : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> interfaceDiff = new CDNodeDiff<>(srcElemExtends, tgtElemExtends);

    srcElemExtends.ifPresent(initial -> srcExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    tgtElemExtends.ifPresent(initial -> tgtExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    srcElemExtends.ifPresent(initial -> this.srcExtendsNC = pp.prettyprint(initial));
    tgtElemExtends.ifPresent(initial -> this.tgtExtendsNC = pp.prettyprint(initial));

    if (interfaceDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_EXTENDS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_EXTENDS);
      }
    }
  }

  private void createDiffList(ASTCDEnum srcElem, ASTCDEnum tgtElem) {
    // Implements, optional
    Optional<ASTMCObjectType> srcElemImplements = (srcElem.isPresentCDInterfaceUsage()) ? Optional.of(srcElem.getInterfaceList().get(0)) : Optional.empty();
    Optional<ASTMCObjectType> tgtElemImplements = (tgtElem.isPresentCDInterfaceUsage()) ? Optional.of(tgtElem.getInterfaceList().get(0)) : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> enumDiff = new CDNodeDiff<>(srcElemImplements, tgtElemImplements);

    srcElemImplements.ifPresent(inter -> srcImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> tgtImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    srcElemImplements.ifPresent(inter -> this.srcImplementsNC = "implements " + pp.prettyprint(inter) + RESET);
    tgtElemImplements.ifPresent(inter -> this.tgtImplementsNC = "implements " + pp.prettyprint(inter) + RESET);

    if (enumDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_TYPE_IMPLEMENTS)) {
        baseDiff.add(DiffTypes.CHANGED_TYPE_IMPLEMENTS);
      }
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
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputTgtCD = new StringBuilder();
    StringBuilder outputSrcCD = new StringBuilder();
    StringBuilder onlyAddedOutputSrcCD = new StringBuilder();
    StringBuilder onlyDeletedOutputTgtCD = new StringBuilder();
    StringBuilder onlyAddedTypeOutput = new StringBuilder();
    StringBuilder onlyDeletedTypeOutput = new StringBuilder();
    StringBuilder outputChangedClass = new StringBuilder();

    StringBuilder bodyTgtCD = new StringBuilder();
    StringBuilder bodySrcCD = new StringBuilder();
    StringBuilder onlyAddedBody = new StringBuilder();
    StringBuilder onlyDeletedBody = new StringBuilder();
    StringBuilder onlyAddedTypeBody = new StringBuilder();
    StringBuilder onlyDeletedTypeBody = new StringBuilder();
    StringBuilder onlyChangedTypeBody = new StringBuilder();

    String signatureSrcCD = insertSpaceBetweenStrings(Arrays.asList(srcModifier, srcType, srcName, srcExtends, srcImplements));
    String signatureSrcCDNC = insertSpaceBetweenStrings(Arrays.asList(srcModifierNC, srcTypeNC, srcNameNC, srcExtendsNC, srcImplementsNC));
    String signatureTgtCD = insertSpaceBetweenStrings(Arrays.asList(tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements));
    String signatureTgtCDNC = insertSpaceBetweenStrings(Arrays.asList(tgtModifierNC, tgtTypeNC, tgtNameNC, tgtExtendsNC, tgtImplementsNC));

    Map<String, Integer> forSrc = new HashMap<>();
    Map<String, Integer> forTgt = new HashMap<>();
    Map<String, Integer> onlyAdded = new HashMap<>();
    Map<String, Integer> onlyDeleted = new HashMap<>();
    Map<String, Integer> onlyAddedType = new HashMap<>();
    Map<String, Integer> onlyDeletedType = new HashMap<>();
    Map<String, Integer> onlyChanged = new HashMap<>();

    for (CDMemberDiff x : changedMembers) {
      forSrc.put(
        x.printChangedMember(),
        Integer.valueOf(
          x.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + x.getSrcElem().get_SourcePositionStart().getColumn()));
      forTgt.put(
        x.printChangedMember(),
        Integer.valueOf(
          x.getTgtElem().get_SourcePositionStart().getLine()
            + ""
            + x.getTgtElem().get_SourcePositionStart().getColumn()));
      onlyChanged.put(
        x.printChangedMember(),
        Integer.valueOf(
          x.getTgtElem().get_SourcePositionStart().getLine()
            + ""
            + x.getTgtElem().get_SourcePositionStart().getColumn()));
    }

    for (Pair<ASTCDAttribute,ASTCDAttribute> x : matchedAttributes) {
      CDMemberDiff a = new CDMemberDiff(x.a, x.b);
      onlyAddedType.put(
        a.printAddedMember(),
        Integer.valueOf(
          a.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + a.getSrcElem().get_SourcePositionStart().getColumn()));
      onlyDeletedType.put(
        a.printRemovedMember(),
        Integer.valueOf(
          a.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + a.getSrcElem().get_SourcePositionStart().getColumn()));
    }

    for (Pair<ASTCDEnumConstant,ASTCDEnumConstant> x : matchedConstants) {
      CDMemberDiff a = new CDMemberDiff(x.a, x.b);
      onlyAddedType.put(
        a.printAddedMember(),
        Integer.valueOf(
          a.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + a.getSrcElem().get_SourcePositionStart().getColumn()));
      onlyDeletedType.put(
        a.printRemovedMember(),
        Integer.valueOf(
          a.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + a.getSrcElem().get_SourcePositionStart().getColumn()));
    }

    if(addedAttributes!= null) {
      for (ASTCDAttribute x : addedAttributes) {
        CDMemberDiff a = new CDMemberDiff(x, x);
        StringBuilder addAttri = new StringBuilder();
        String comment = "//added attribute, L: " + a.srcLineOfCode + System.lineSeparator();
        String addedAttribute = a.printAddedMember();
        addAttri.append(comment).append(COLOR_ADD).append(addedAttribute).append(RESET);

        forSrc.put(
          addAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));

        onlyAdded.put(
          addAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));

        onlyChanged.put(
          addAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(deletedAttributes != null) {
      for (ASTCDAttribute x : deletedAttributes) {
        CDMemberDiff a = new CDMemberDiff(x, x);
        StringBuilder delAttri = new StringBuilder();
        String comment = "\t" + "//deleted attribute, L: " + a.tgtLineOfCode + System.lineSeparator();
        String deletedAttribute = a.printRemovedMember();
        delAttri.append(comment).append(COLOR_DELETE).append(deletedAttribute).append(RESET);

        forTgt.put(delAttri.toString(),Integer.valueOf(x.get_SourcePositionStart().getLine()
              + "" + x.get_SourcePositionStart().getColumn()));

        onlyDeleted.put(delAttri.toString(),Integer.valueOf(x.get_SourcePositionStart().getLine()
              + "" + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(addedConstants != null) {
      for (ASTCDEnumConstant x : addedConstants) {
        CDMemberDiff a = new CDMemberDiff(x, x);
        StringBuilder addEnumConst = new StringBuilder();
        String comment = "//added enum constant, L: " + a.srcLineOfCode + System.lineSeparator();
        String addedEnumConstant = a.printAddedMember();

        addEnumConst.append(comment).append(COLOR_ADD).append(addedEnumConstant).append(RESET);
        forSrc.put(
          addEnumConst.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
        onlyAdded.put(
          addEnumConst.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
        onlyChanged.put(
          addEnumConst.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(deletedConstants != null) {
      for (ASTCDEnumConstant x : getDeletedConstants()) {
        CDMemberDiff a = new CDMemberDiff(x, x);
        StringBuilder delEnumConstant = new StringBuilder();
        String comment = "//removed enum constant, L: " + a.tgtLineOfCode + System.lineSeparator();
        String deletedEnumConstant = a.printRemovedMember();

        delEnumConstant.append(comment).append(COLOR_DELETE).append(deletedEnumConstant).append(RESET);

        forTgt.put(
          delEnumConstant.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));

        onlyDeleted.put(
          delEnumConstant.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(inheritedAttributes != null) {
      for (ASTCDAttribute x : inheritedAttributes) {
        for(Pair<ASTCDAttribute, ASTCDAttribute> pair : removedBcInh) {
          if(pair.a.getName().equals(x.getName())) {

            StringBuilder inhAttr = new StringBuilder();
            String commentOne = "//inherited, L: " + x.get_SourcePositionStart().getLine() + System.lineSeparator();
            String inheritedAttribute = "\t" + pp.prettyprint(x);
            inhAttr.append(commentOne).append(COLOR_ADD).append(inheritedAttribute).append(RESET);

            forSrc.put(
              inhAttr.toString(),
              Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
            onlyAdded.put(
              inhAttr.toString(),
              Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
          }
        }
      }
    }

    Map<Integer, String> forSrcMap = forSrc.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    forSrcMap.forEach((k, v) -> bodySrcCD.append(v).append(System.lineSeparator()));

    Map<Integer, String> forTgtMap = forTgt.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    forTgtMap.forEach((k, v) -> bodyTgtCD.append(v).append(System.lineSeparator()));

    Map<Integer, String> onlyAddedMap = onlyAdded.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    onlyAddedMap.forEach((k, v) -> onlyAddedBody.append(v).append(System.lineSeparator()));

    Map<Integer, String> onlyDeletedMap = onlyDeleted.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    onlyDeletedMap.forEach((k, v) -> onlyDeletedBody.append(v).append(System.lineSeparator()));

    Map<Integer, String> onlyAddedTypeMap = onlyAddedType.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    onlyAddedTypeMap.forEach((k, v) -> onlyAddedTypeBody.append(v).append(System.lineSeparator()));

    Map<Integer, String> onlyDeletedTypeMap = onlyDeletedType.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    onlyDeletedTypeMap.forEach((k, v) -> onlyDeletedTypeBody.append(v).append(System.lineSeparator()));

    Map<Integer, String> onlyChangedTypeMap = onlyChanged.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    onlyChangedTypeMap.forEach((k, v) -> onlyChangedTypeBody.append(v).append(System.lineSeparator()));

    String newTypeComment = "//changed type, L:" + srcLineOfCode + System.lineSeparator();
    outputSrcCD.append(newTypeComment).append(signatureSrcCD);
    if (!bodySrcCD.toString().isEmpty()) {
      outputSrcCD.append("{ ").append(System.lineSeparator()).append(bodySrcCD).append("}");
    } else {
      outputSrcCD.append("{}");
    }
    srcPrint = outputSrcCD.toString();

    outputTgtCD.append(signatureTgtCD);
    if (!bodyTgtCD.toString().isEmpty()) {
      outputTgtCD.append("{ ").append(System.lineSeparator()).append(bodyTgtCD).append("}");
    } else {
      outputTgtCD.append("{}");
    }
    tgtPrint = outputTgtCD.toString();

    onlyAddedOutputSrcCD.append(signatureSrcCDNC);
    if (!onlyAddedBody.toString().isEmpty()) {
      onlyAddedOutputSrcCD.append("{ ").append(System.lineSeparator()).append(onlyAddedBody).append("}");
    }
    srcPrintOnlyAdded = onlyAddedOutputSrcCD.toString();

    onlyDeletedOutputTgtCD.append(signatureTgtCDNC);
    if (!onlyDeletedBody.toString().isEmpty()) {
      onlyDeletedOutputTgtCD.append("{ ").append(System.lineSeparator()).append(onlyDeletedBody).append("}");
    }
    tgtPrintOnlyRemoved = onlyDeletedOutputTgtCD.toString();

    String addedComment = "//added type, L:" + srcLineOfCode + System.lineSeparator();
    onlyAddedTypeOutput.append(addedComment).append(COLOR_ADD).append(signatureSrcCD);
    if (!onlyAddedTypeBody.toString().isEmpty()) {
      onlyAddedTypeOutput.append(COLOR_ADD).append("{ ").append(System.lineSeparator()).append(onlyAddedTypeBody).append(COLOR_ADD).append("}").append(System.lineSeparator());
    } else {
      onlyAddedTypeOutput.append(COLOR_ADD).append("{}").append(System.lineSeparator());
    }
    addedType = onlyAddedTypeOutput.toString();

    String removedComment = "//removed type, L:" + tgtLineOfCode + System.lineSeparator();
    onlyDeletedTypeOutput.append(removedComment).append(COLOR_DELETE).append(signatureSrcCD);
    if (!onlyDeletedTypeBody.toString().isEmpty()) {
      onlyDeletedTypeOutput.append(COLOR_DELETE).append("{ ").append(System.lineSeparator()).append(onlyDeletedTypeBody).append(COLOR_DELETE).append("}").append(System.lineSeparator());
    } else {
      onlyDeletedTypeOutput.append(COLOR_DELETE).append("{}").append(System.lineSeparator());
    }
    removedType = onlyDeletedTypeOutput.toString();

    String changedTypeComment = "//changed type, L:" + srcLineOfCode + System.lineSeparator();
    outputChangedClass.append(changedTypeComment).append(signatureSrcCD);
    if (!onlyChangedTypeBody.toString().isEmpty()) {
      outputChangedClass.append("{ ").append(System.lineSeparator()).append(onlyChangedTypeBody).append("}").append(System.lineSeparator());
    } else {
      outputChangedClass.append("{}").append(System.lineSeparator());
    }
    classPrint = outputChangedClass.toString();
  }

  //We use this method if we want to show only the added attributes
  public String printIfAddedAttr() { return srcPrintOnlyAdded; }
  //We use this method if we want to show only the removed attributes
  public String printIfRemovedAttr() { return tgtPrintOnlyRemoved; }
  //We use this method if we want to print the src class with its added and changed attributes
  public String printSrcCD() { return srcPrint; }
  //We use this method if we want to print the tgt class with its removed and changed attributes
  public String printTgtCD() { return tgtPrint; }
  //We use this method if we want to print a whole class which is newly added
  public String printAddedType() { return addedType; }
  //We use this method if we want to print a whole class which is newly removed
  public String printRemovedType() { return removedType; }
  //We use this method if we want to print a class with its added, removed, and changed attributes
  public String printChangedType() { return classPrint; }
}
