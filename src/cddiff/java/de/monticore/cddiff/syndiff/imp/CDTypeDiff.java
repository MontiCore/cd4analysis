package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.CDDiffUtil.getAllSuperTypes;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

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

public class CDTypeDiff extends CDDiffHelper implements ICDTypeDiff {
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
  ICD4CodeArtifactScope scopeSrcCD;
  ICD4CodeArtifactScope scopeTgtCD;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  //Print
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String
    srcModifier, srcType, srcName, srcExtends, srcImplements,
    tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements,
    srcPrint, tgtPrint;
  //Print end

  public CDTypeDiff(ASTCDType srcElem, ASTCDType tgtElem,
                    ICD4CodeArtifactScope scopeSrcCD,
                    ICD4CodeArtifactScope scopeTgtCD) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.scopeSrcCD = scopeSrcCD;
    this.scopeTgtCD = scopeTgtCD;
    this.baseDiff = new ArrayList<>();
    this.diffList = new ArrayList<>();
    //TODO: Initialize them in the functions!
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
    //Find inherited attributes
    loadAllInheritedAttributes(srcElem, tgtElem, scopeSrcCD, scopeTgtCD);
    //Find all added, deleted and changed attributes and constants
    loadAllLists(srcElem, tgtElem);
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
  public void setInheritedAttributes(List<ASTCDAttribute> inheritedAttributes) {
    this.inheritedAttributes = inheritedAttributes;
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

  /*--------------------------------------------------------------------*/
  /**
   * Add all attributes to the list changedMembers which have been changed.
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void loadAllChangedMembers(ASTCDType srcType, ASTCDType tgtType) {
    boolean changedMember = false;
    for(Pair<ASTCDAttribute,ASTCDAttribute> x : matchedAttributes){
      CDMemberDiff diffAttribute = new CDMemberDiff(x.a, x.b);
      if(diffAttribute.getBaseDiff()!= null) {
        changedMembers.add(diffAttribute);
        changedMember = true;
      }
    }
    if(changedMember) {
      baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE);
    }
  }

  /**
   * Add all attributes to the list addedAttributes which have not been in the tgtType, but are in
   * the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void loadAllAddedAttributes(ASTCDType srcType, ASTCDType tgtType) {
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
  public List<ASTCDType> superTypes = new ArrayList<>();
  public List<ASTCDType> getSuperTypes(){
    return superTypes;
  }
  private void loadAllInheritedAttributes(ASTCDType srcType, ASTCDType tgtType, ICD4CodeArtifactScope scopeSrcCD, ICD4CodeArtifactScope scopeTgtCD) {
    for (ASTCDAttribute srcAttr : srcType.getCDAttributeList()) {
      boolean inheritedFound = false;
      for(ASTCDType x : getAllSuper(tgtType, scopeTgtCD)) {
        superTypes.add(x);
        for (ASTCDAttribute a : x.getCDAttributeList()) {
          if (a.getName().equals(srcAttr.getName())) {
            inheritedFound = true;
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
  /**
   * Add all attributes to the list deletedAttributes which have been in the tgtType, but aren't
   * anymore in the srcType
   *
   * @param srcType a type in the new CD
   * @param tgtType a type in the old CD
   */
  public void loadAllDeletedAttributes(ASTCDType srcType, ASTCDType tgtType) {
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
        if(!baseDiff.contains(DiffTypes.REMOVED_ATTRIBUTE)) {
          baseDiff.add(DiffTypes.REMOVED_ATTRIBUTE);
        }
      }
    }
  }

  /*public void loadAllAddedConstants(ASTCDType srcEnum, ASTCDType tgtEnum) {
    for (ASTCDEnumConstant firstConstant : ((ASTCDEnum)srcEnum).getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : ((ASTCDEnum)tgtEnum).getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedConstants.add(firstConstant);
        if(!baseDiff.contains(DiffTypes.ADDED_CONSTANT)) {
          baseDiff.contains(DiffTypes.ADDED_CONSTANT);
        }
      }
    }
  }*/

  /*public void loadAllDeletedConstants(ASTCDType srcEnum, ASTCDType tgtEnum) {
    for (ASTCDEnumConstant firstConstant : ((ASTCDEnum)tgtEnum).getCDEnumConstantList()) {
      boolean notFound = true;
      for (ASTCDEnumConstant secondConstant : ((ASTCDEnum)srcEnum).getCDEnumConstantList()) {
        if (firstConstant.getName().equals(secondConstant.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedConstants.add(firstConstant);
        if(!baseDiff.contains(DiffTypes.DELETED_CONSTANT)) {
          baseDiff.contains(DiffTypes.DELETED_CONSTANT);
        }
      }
    }

  }*/
  private void createDefaultDiffList(ASTCDType srcElem, ASTCDType tgtElem) {
    List<CDNodeDiff<?,?>> diffs = new ArrayList<>();
    diffType.append("Interpretation: ");

    // Modifier, non-optional
    if (!(pp.prettyprint(srcElem.getModifier()).length() < 1
      && pp.prettyprint(tgtElem.getModifier()).length() < 1)) {
      diffs.add(setModifier(srcElem.getModifier(), tgtElem.getModifier()));
    }

    // Name, non-optional
    Optional<ASTCDType> cd1Name = Optional.of(tgtElem);
    Optional<ASTCDType> cd2Name = Optional.of(srcElem);
    CDNodeDiff<ASTCDType, ASTCDType> className = new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    srcName = getColorCode(className) + cd1Name.get().getName() + RESET;
    tgtName = getColorCode(className) + cd2Name.get().getName() + RESET;

    if (className.checkForAction()) {
      diffs.add(className);
      if (className.getDiff().isPresent()) {
        diffType
          .append("Name")
          .append(": ")
          .append(className.getDiff().get())
          .append(" ");
      }
    }

    if ((srcElem instanceof ASTCDClass) && (tgtElem instanceof ASTCDClass)) {
      srcType = "class";
      tgtType = "class";
      createClassDiff((ASTCDClass) srcElem, (ASTCDClass) tgtElem);
    } else if (srcElem instanceof ASTCDInterface && tgtElem instanceof ASTCDInterface) {
      srcType = "interface";
      tgtType = "interface";
      createInterfaceDiff((ASTCDInterface) srcElem, (ASTCDInterface) tgtElem);
    } else if (srcElem instanceof ASTCDEnum && tgtElem instanceof ASTCDEnum) {
      srcType = "enum";
      tgtType = "enum";
      createEnumDiff((ASTCDEnum) srcElem, (ASTCDEnum) tgtElem);
    }

    this.diffList = diffs;
  }

  boolean isEmpty = true;
  public boolean isEmpty() {
    return isEmpty;
  }

  protected CDNodeDiff<ASTModifier, ASTModifier> setModifier(ASTModifier srcModifier, ASTModifier tgtModifier) {
    CDNodeDiff<ASTModifier, ASTModifier> modifier = new CDNodeDiff<>(Optional.of(srcModifier), Optional.of(tgtModifier));

    if (modifier.getSrcValue().isPresent()) {
      this.srcModifier = getColorCode(modifier) + pp.prettyprint(srcModifier) + RESET;
    }
    if (modifier.getTgtValue().isPresent()) {
      this.tgtModifier = getColorCode(modifier) + pp.prettyprint(tgtModifier) + RESET;
    }

    if (modifier.checkForAction() && modifier.getAction().isPresent()) {
      if (modifier.getDiff().isPresent()) {
        diffType
          .append("Modifier")
          .append(": ")
          .append(modifier.getDiff().get())
          .append(" ");
      }
    }
    return modifier;
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
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
      (srcElem.isPresentCDExtendUsage())
        ? Optional.of(srcElem.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
      (tgtElem.isPresentCDExtendUsage())
        ? Optional.of(tgtElem.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedClassDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> srcExtends = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> tgtExtends = getColorCode(extendedClassDiff) + pp.prettyprint(initial) + RESET);

    if (extendedClassDiff.checkForAction()) {
      diffList.add(extendedClassDiff);
      if (extendedClassDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(extendedClassDiff.getDiff().get())
          .append(" ");
      }
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (srcElem.isPresentCDInterfaceUsage())
        ? Optional.of(srcElem.getInterfaceList().get(0))
        : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (tgtElem.isPresentCDInterfaceUsage())
        ? Optional.of(tgtElem.getInterfaceList().get(0))
        : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> implementedClassDiff = new CDNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(inter -> srcImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(inter -> tgtImplements = getColorCode(implementedClassDiff) + "implements " + pp.prettyprint(inter) + RESET);


    if (implementedClassDiff.checkForAction()) {
      diffList.add(implementedClassDiff);
      if (implementedClassDiff.getDiff().isPresent()) {
        diffType
          .append("Interface")
          .append(": ")
          .append(implementedClassDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface srcElem, ASTCDInterface tgtElem) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (srcElem.isPresentCDExtendUsage())
        ? Optional.of(srcElem.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (tgtElem.isPresentCDExtendUsage())
        ? Optional.of(tgtElem.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> interfaceDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> srcExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> tgtExtends = getColorCode(interfaceDiff) + pp.prettyprint(initial) + RESET);

    if (interfaceDiff.checkForAction()) {
      diffList.add(interfaceDiff);
      if (interfaceDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(interfaceDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDEnum srcElem, ASTCDEnum tgtElem) {
    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (srcElem.isPresentCDInterfaceUsage())
        ? Optional.of(srcElem.getInterfaceList().get(0))
        : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (tgtElem.isPresentCDInterfaceUsage())
        ? Optional.of(tgtElem.getInterfaceList().get(0))
        : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> enumDiff = new CDNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(inter -> srcImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(inter -> tgtImplements = getColorCode(enumDiff) + "implements " + pp.prettyprint(inter) + RESET);

    if (enumDiff.checkForAction()) {
      diffList.add(enumDiff);
      if (enumDiff.getDiff().isPresent()) {
        diffType
          .append("Interface")
          .append(": ")
          .append(enumDiff.getDiff().get())
          .append(" ");
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
              matchedAttributes.add(new Pair(srcAttr, tgtAttr));
          }
        }
      }
    } else if (srcType instanceof ASTCDEnum && tgtType instanceof ASTCDEnum) {
      for (ASTCDEnumConstant srcEnumConstant : ((ASTCDEnum) srcType).getCDEnumConstantList()) {
        for (ASTCDEnumConstant tgtEnumConstant : ((ASTCDEnum) tgtType).getCDEnumConstantList()) {
          if (srcEnumConstant.getName().equals(tgtEnumConstant.getName())) {
            matchedConstants.add(new Pair(srcEnumConstant, tgtEnumConstant));
          }
        }
      }
    }
  }

  public void loadAllLists(ASTCDType srcType, ASTCDType tgtType) {
    loadAllAddedAttributes(srcType, tgtType);
    loadAllDeletedAttributes(srcType, tgtType);
    loadAllChangedMembers(srcType, tgtType);
    //loadAllAddedConstants(srcType, tgtType);
    //loadAllDeletedConstants(srcType, tgtType);
  }

  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();

    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();

    String signatureCD1 = insertSpaceBetweenStrings(Arrays.asList(srcModifier, srcType, srcName, srcExtends, srcImplements));
    String signatureCD2 = insertSpaceBetweenStrings(Arrays.asList(tgtModifier, tgtType, tgtName, tgtExtends, tgtImplements));

    String bodyOffset = "     ";
    String bodyOffsetDel = "-    ";
    String bodyOffsetAdd = "+    ";
    String bodyOffsetChange = "~    ";

    Map<String, Integer> add = new HashMap<>();
    Map<String, Integer> matchDel = new HashMap<>();
    Map<String, Integer> addNC = new HashMap<>();
    Map<String, Integer> matchDelNC = new HashMap<>();

    for (CDMemberDiff x : changedMembers) {
      matchDel.put(
        x.printCD1Element(),
        Integer.valueOf(
          x.getTgtElem().get_SourcePositionStart().getLine()
            + ""
            + x.getTgtElem().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getTgtElem());
      if (!x.getDiffList().isEmpty()) {
        tmp = bodyOffsetChange + "  " + pp.prettyprint(x.getTgtElem());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      matchDelNC.put(
        tmp,
        Integer.valueOf(
          x.getTgtElem().get_SourcePositionStart().getLine()
            + ""
            + x.getTgtElem().get_SourcePositionStart().getColumn()));
    }

    for (CDMemberDiff x : changedMembers) {
      add.put(
        x.printCD2Element(),
        Integer.valueOf(
          x.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + x.getSrcElem().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getSrcElem());
      if (!x.getDiffList().isEmpty()) {
        tmp = bodyOffsetChange + "  " + pp.prettyprint(x.getSrcElem());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      addNC.put(
        tmp,
        Integer.valueOf(
          x.getSrcElem().get_SourcePositionStart().getLine()
            + ""
            + x.getSrcElem().get_SourcePositionStart().getColumn()));
    }

    if(deletedConstants != null) {
      for (ASTCDEnumConstant x : getDeletedConstants()) {
        StringBuilder delEnumConstant = new StringBuilder();
        String deletedEnumConstant = pp.prettyprint(x);
        if (deletedEnumConstant.contains("\n")) {
          deletedEnumConstant = deletedEnumConstant.split("\n")[0];
        }

        delEnumConstant.append(COLOR_DELETE).append(deletedEnumConstant).append(RESET);
        matchDel.put(
          delEnumConstant.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(addedConstants != null) {
      for (ASTCDEnumConstant x : getAddedConstants()) {
        StringBuilder addEnumConst = new StringBuilder();
        String addedEnumConstant = pp.prettyprint(x);
        if (addedEnumConstant.contains("\n")) {
          addedEnumConstant = addedEnumConstant.split("\n")[0];
        }

        addEnumConst.append(COLOR_ADD).append(addedEnumConstant).append(RESET);
        add.put(
          addEnumConst.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(deletedAttributes != null) {
      for (ASTCDAttribute x : getDeletedAttributes()) {
        StringBuilder delAttri = new StringBuilder();
        String deletedAttribute = pp.prettyprint(x);
        if (deletedAttribute.contains("\n")) {
          deletedAttribute = deletedAttribute.split("\n")[0];
        }

        delAttri.append(COLOR_DELETE).append(deletedAttribute).append(RESET);
        matchDel.put(
          delAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(addedAttributes!= null) {
      for (ASTCDAttribute x : addedAttributes) {
        StringBuilder addAttri = new StringBuilder();
        String addedAttribute = pp.prettyprint(x);
        if (addedAttribute.contains("\n")) {
          addedAttribute = addedAttribute.split("\n")[0];
        }
        addNC.put(
          bodyOffsetAdd + addedAttribute,
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));

        addAttri.append(COLOR_ADD).append(addedAttribute).append(RESET);
        add.put(
          addAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    if(inheritedAttributes!= null) {
      for (ASTCDAttribute x : inheritedAttributes) {
        StringBuilder addAttri = new StringBuilder();
        String addedAttribute = pp.prettyprint(x);
        if (addedAttribute.contains("\n")) {
          addedAttribute = addedAttribute.split("\n")[0];
        }
        addNC.put(
          bodyOffsetAdd + addedAttribute,
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));

        addAttri.append(COLOR_INHERITED).append(addedAttribute).append(RESET);
        add.put(
          addAttri.toString(),
          Integer.valueOf(
            x.get_SourcePositionStart().getLine()
              + ""
              + x.get_SourcePositionStart().getColumn()));
      }
    }

    Map<Integer, String> matchAndDelete =
      matchDel.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDelete.forEach(
      (k, v) -> bodyCD1.append(bodyOffset).append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAdd =
      add.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAdd.forEach(
      (k, v) -> bodyCD2.append(bodyOffset).append(v).append(System.lineSeparator()));



    outputCD1.append(signatureCD1);
    if (!bodyCD1.toString().isEmpty()) {
      outputCD1.append("{ ").append(System.lineSeparator()).append(bodyCD1).append("}");
    } else {
      outputCD1.append(";");
    }

    srcPrint = outputCD1.toString();

    outputCD2.append(signatureCD2);
    if (!bodyCD2.toString().isEmpty()) {
      outputCD2.append("{ ").append(System.lineSeparator()).append(bodyCD2).append("}");
    } else {
      outputCD2.append(";");
    }

    tgtPrint = outputCD2.toString();
  }
  public String printSrcCD() { return srcPrint; }

  public String printTgtCD() {
    return tgtPrint;
  }
}
