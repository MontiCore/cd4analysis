package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDTypeDiff;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;
import java.util.stream.Collectors;

// TODO: Write comments
public class CDTypeDiff extends CDDiffHelper implements ICDTypeDiff {
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

  //Print
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String ppModifier1,
    ppModifier1NC,
    ppName1,
    ppName1NC,
    ppExtended1,
    ppExtended1NC,
    ppInter1,
    ppInter1NC,
    ppModifier2,
    ppModifier2NC,
    ppName2,
    ppName2NC,
    ppExtended2,
    ppExtended2NC,
    ppInter2,
    ppInter2NC,
    cd1Print,
    cd2Print,
    cd1PrintNC,
    cd2PrintNC,
    keywordCD1,
    keywordCD2,
    keywordCD1NC,
    keywordCD2NC;

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
  public List<ASTCDAttribute> getDeletedAttributes() {
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

//  public int calculateClassDepth(ASTCDClass rootClass, ASTCDClass targetClass) {
//    // Check if the target class is the root class
//    if (rootClass.getSymbol().getInternalQualifiedName().equals(targetClass.getSymbol().getInternalQualifiedName())) {
//      return 0;
//    }
//
//    // Get the direct superclasses of the target class
//    Set<ASTCDClass> superClasses = CDDiffUtil.getAllSuperclasses(targetClass, helper.getSrcCD().getCDDefinition().getCDClassesList());
//
//    // If the target class has no superclasses, it is not in the hierarchy
//    if (superClasses.isEmpty()) {
//      return -1;
//    }
//
//    // Recursively calculate the depth for each direct superclass
//    List<Integer> depths = new ArrayList<>();
//    for (ASTCDClass superClass : superClasses) {
//      int depth = calculateClassDepth(rootClass, superClass);
//      if (depth >= 0) {
//        depths.add(depth + 1);
//      }
//    }
//
//    // Return the maximum depth from the direct superclasses
//    if (depths.isEmpty()) {
//      return -1;
//    } else {
//      return depths.stream().max(Integer::compare).get();
//    }
//  }

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

  // NEW
  private void createDefaultDiffList(ASTCDType tgtElem, ASTCDType srcElem) {
    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    diffType.append("Interpretation: ");

    // Modifier, non-optional
    if (!(pp.prettyprint(tgtElem.getModifier()).length() < 1
      && pp.prettyprint(srcElem.getModifier()).length() < 1)) {
      diffs.add(setModifier(tgtElem.getModifier(), srcElem.getModifier()));
    }

    // Name, non-optional
    Optional<ASTCDType> cd1Name = Optional.of(tgtElem);
    Optional<ASTCDType> cd2Name = Optional.of(srcElem);
    CDNodeDiff<ASTCDType, ASTCDType> className = new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    ppName1NC = cd1Name.get().getName();
    ppName2NC = cd2Name.get().getName();

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

    if (cd1Name.get().getName().equals(cd2Name.get().getName())
      && !cd1Name
      .get()
      .getSymbol()
      .getInternalQualifiedName()
      .equals(cd2Name.get().getSymbol().getInternalQualifiedName())) {
      diffType
        .append("Package")
        .append(": ")
        .append(DiffTypes.RELOCATION)
        .append(" ");
    }

    this.diffList = diffs;
  }

  protected CDNodeDiff<ASTModifier, ASTModifier> setModifier(ASTModifier cd1Modi, ASTModifier cd2Modi) {
    CDNodeDiff<ASTModifier, ASTModifier> modifier = new CDNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

    if (!(pp.prettyprint(cd1Modi).length() < 1)) {
      ppModifier1 = getColorCode(modifier) + pp.prettyprint(cd1Modi) + RESET;
      ppModifier1NC = pp.prettyprint(cd1Modi);
    }
    if (!(pp.prettyprint(cd2Modi).length() < 1)) {
      ppModifier2 = getColorCode(modifier) + pp.prettyprint(cd2Modi) + RESET;
      ppModifier2NC = pp.prettyprint(cd2Modi);
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

  public void createClassDiff(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createInterfaceDiff(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createEnumDiff(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createInterfaceClassDiff(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createClassInterfaceDiff(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  // Class -- class
  // Interface -- interface
  // Enum -- enum
  // Interface -- class
  // Class -- interface
  private void createDiffList(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
      (cd1Element.isPresentCDExtendUsage())
        ? Optional.of(cd1Element.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
      (cd2Element.isPresentCDExtendUsage())
        ? Optional.of(cd2Element.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.checkForAction()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(extendedDiff.getDiff().get())
          .append(" ");
      }
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (cd1Element.isPresentCDInterfaceUsage())
        ? Optional.of(cd1Element.getInterfaceList().get(0))
        : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (cd2Element.isPresentCDInterfaceUsage())
        ? Optional.of(cd2Element.getInterfaceList().get(0))
        : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> interfaceDiff = new CDNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(
      inter -> ppInter1 = getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(
      inter -> ppInter2 = getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);

    cd1Imple.ifPresent(inter -> ppInter1NC = " implements " + pp.prettyprint(inter));
    cd2Imple.ifPresent(inter -> ppInter2NC = " implements " + pp.prettyprint(inter));

    if (interfaceDiff.checkForAction()) {
      diffList.add(interfaceDiff);
      if (interfaceDiff.getDiff().isPresent()) {
        diffType
          .append("Interface")
          .append(": ")
          .append(interfaceDiff.getDiff().get())
          .append(" ");
      }
    }
  }


  private void createDiffList(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage())
        ? Optional.of(cd1Element.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage())
        ? Optional.of(cd2Element.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.checkForAction()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(extendedDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (cd1Element.isPresentCDInterfaceUsage())
        ? Optional.of(cd1Element.getInterfaceList().get(0))
        : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (cd2Element.isPresentCDInterfaceUsage())
        ? Optional.of(cd2Element.getInterfaceList().get(0))
        : Optional.empty();
    CDNodeDiff<ASTMCObjectType, ASTMCObjectType> interfaceDiff = new CDNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(inter -> ppInter1 = getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(inter -> ppInter2 = getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd1Imple.ifPresent(inter -> ppInter1NC = " implements " + pp.prettyprint(inter));
    cd2Imple.ifPresent(inter -> ppInter2NC = " implements " + pp.prettyprint(inter));

    if (interfaceDiff.checkForAction()) {
      diffList.add(interfaceDiff);
      if (interfaceDiff.getDiff().isPresent()) {
        diffType
          .append("Interface")
          .append(": ")
          .append(interfaceDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage())
        ? Optional.of(cd1Element.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage())
        ? Optional.of(cd2Element.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.checkForAction()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(extendedDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage())
        ? Optional.of(cd1Element.getCDExtendUsage())
        : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage())
        ? Optional.of(cd2Element.getCDExtendUsage())
        : Optional.empty();
    CDNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff = new CDNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.checkForAction()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getDiff().isPresent()) {
        diffType
          .append("Extended")
          .append(": ")
          .append(extendedDiff.getDiff().get())
          .append(" ");
      }
    }
  }

  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();
    StringBuilder outputCD1NC = new StringBuilder();
    StringBuilder outputCD2NC = new StringBuilder();

    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();
    StringBuilder bodyCD1NC = new StringBuilder();
    StringBuilder bodyCD2NC = new StringBuilder();

    String signatureCD1 = insertSpaceBetweenStrings(Arrays.asList(ppModifier1, keywordCD1, ppName1, ppExtended1, ppInter1));
    String signatureCD2 = insertSpaceBetweenStrings(Arrays.asList(ppModifier2, keywordCD2, ppName2, ppExtended2, ppInter2));
    String signatureCD1NC = insertSpaceBetweenStrings(Arrays.asList(ppModifier1NC, keywordCD1NC, ppName1NC, ppExtended1NC, ppInter1NC));
    String signatureCD2NC = insertSpaceBetweenStrings(Arrays.asList(ppModifier2NC, keywordCD2NC, ppName2NC, ppExtended2NC, ppInter2NC));

    String bodyOffset = "     ";
    String bodyOffsetDel = "-    ";
    String bodyOffsetAdd = "+    ";
    String bodyOffsetChange = "~    ";

    Map<String, Integer> add = new HashMap<>();
    Map<String, Integer> matchDel = new HashMap<>();
    Map<String, Integer> addNC = new HashMap<>();
    Map<String, Integer> matchDelNC = new HashMap<>();

    /*for (de.monticore.cddiff.syntaxdiff.CDMemberDiff<ASTCDAttribute> x : matchedAttributesList) {
      matchDel.put(
        x.printCD1Element(),
        Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd1Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getCd1Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint(x.getCd1Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      matchDelNC.put(
        tmp,
        Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd1Element().get_SourcePositionStart().getColumn()));
    }

    for (de.monticore.cddiff.syntaxdiff.CDMemberDiff<ASTCDEnumConstant> x : matchedEnumConstantList) {
      matchDel.put(
        x.printCD1Element(),
        Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd1Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getCd1Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint(x.getCd1Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      matchDelNC.put(
        tmp,
        Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd1Element().get_SourcePositionStart().getColumn()));
    }*/

    for (ASTCDEnumConstant x : getDeletedConstants()) {
      StringBuilder delEnumConstant = new StringBuilder();
      String deletedEnumConstant = pp.prettyprint(x);
      if (deletedEnumConstant.contains("\n")) {
        deletedEnumConstant = deletedEnumConstant.split("\n")[0];
      }
      matchDelNC.put(
        bodyOffsetDel + deletedEnumConstant,
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));

      delEnumConstant.append(COLOR_DELETE).append(deletedEnumConstant).append(RESET);
      matchDel.put(
        delEnumConstant.toString(),
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));
    }

    for (ASTCDEnumConstant x : getAddedConstants()) {
      StringBuilder addEnumConst = new StringBuilder();
      String addedEnumConstant = pp.prettyprint(x);
      if (addedEnumConstant.contains("\n")) {
        addedEnumConstant = addedEnumConstant.split("\n")[0];
      }
      addNC.put(
        bodyOffsetAdd + addedEnumConstant,
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));

      addEnumConst.append(COLOR_ADD).append(addedEnumConstant).append(RESET);
      add.put(
        addEnumConst.toString(),
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));
    }


    /*for (de.monticore.cddiff.syntaxdiff.CDMemberDiff<ASTCDAttribute> x : matchedAttributesList) {
      add.put(
        x.printCD2Element(),
        Integer.valueOf(
          x.getCd2Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd2Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getCd2Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint(x.getCd2Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      addNC.put(
        tmp,
        Integer.valueOf(
          x.getCd2Element().get_SourcePositionStart().getLine()
            + ""
            + x.getCd2Element().get_SourcePositionStart().getColumn()));
    }*/

    for (ASTCDAttribute x : getDeletedAttributes()) {
      StringBuilder delAttri = new StringBuilder();
      String deletedAttribute = pp.prettyprint(x);
      if (deletedAttribute.contains("\n")) {
        deletedAttribute = deletedAttribute.split("\n")[0];
      }
      matchDelNC.put(
        bodyOffsetDel + deletedAttribute,
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));

      delAttri.append(COLOR_DELETE).append(deletedAttribute).append(RESET);
      matchDel.put(
        delAttri.toString(),
        Integer.valueOf(
          x.get_SourcePositionStart().getLine()
            + ""
            + x.get_SourcePositionStart().getColumn()));
    }

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

    Map<Integer, String> matchAndDelete =
      matchDel.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDelete.forEach(
      (k, v) -> bodyCD1.append(bodyOffset).append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndDeleteNC =
      matchDelNC.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDeleteNC.forEach((k, v) -> bodyCD1NC.append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAdd =
      add.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAdd.forEach(
      (k, v) -> bodyCD2.append(bodyOffset).append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAddNC =
      addNC.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(
          Collectors.toMap(
            Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAddNC.forEach((k, v) -> bodyCD2NC.append(v).append(System.lineSeparator()));

    outputCD1.append(signatureCD1);
    if (bodyCD1.toString().length() > 0) {
      outputCD1.append("{ ").append(System.lineSeparator()).append(bodyCD1).append("}");
    } else {
      outputCD1.append(";");
    }

    outputCD1NC.append(signatureCD1NC);
    if (bodyCD1NC.toString().length() > 0) {
      outputCD1NC.append("{ ").append(System.lineSeparator()).append(bodyCD1NC).append("}");
    } else {
      outputCD1NC.append(";");
    }

    cd1Print = outputCD1.toString();
    cd1PrintNC = outputCD1NC.toString();

    outputCD2.append(signatureCD2);
    if (bodyCD2.toString().length() > 0) {
      outputCD2.append("{ ").append(System.lineSeparator()).append(bodyCD2).append("}");
    } else {
      outputCD2.append(";");
    }

    outputCD2NC.append(signatureCD2NC);
    if (bodyCD2NC.toString().length() > 0) {
      outputCD2NC.append("{ ").append(System.lineSeparator()).append(bodyCD2NC).append("}");
    } else {
      outputCD2NC.append(";");
    }

    cd2Print = outputCD2.toString();
    cd2PrintNC = outputCD2NC.toString();
  }

  /** Print function for the CDTypeDiff, used to output the diffs appropriately formated */
  public String printCD1() {
    return cd1Print;
  }

  public String printCD1NC() {
    return cd1PrintNC;
  }

  public String printCD2() {
    return cd2Print;
  }

  public String printCD2NC() {
    return cd2PrintNC;
  }





}
