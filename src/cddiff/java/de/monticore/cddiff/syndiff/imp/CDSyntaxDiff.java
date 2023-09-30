package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getDirectSuperClasses;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getSpannedInheritance;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDSyntaxDiff;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.*;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociation;
import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociationInReverse;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class CDSyntaxDiff extends CDPrintDiff implements ICDSyntaxDiff {
  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit tgtCD;
  private List<CDTypeDiff> changedTypes;
  private List<CDAssocDiff> changedAssocs;
  private List<ASTCDClass> addedClasses;
  private List<ASTCDClass> deletedClasses;
  private List<ASTCDEnum> addedEnums;
  private List<ASTCDEnum> deletedEnums;
  private List<ASTCDAssociation> addedAssocs;
  private List<ASTCDAssociation> deletedAssocs;
  private List<Pair<ASTCDClass, List<ASTCDType>>> addedInheritance;
  private List<Pair<ASTCDClass, List<ASTCDType>>> deletedInheritance;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  private List<DiffTypes> baseDiff;
  List<MatchingStrategy<ASTCDType>> typeMatchers;
  List<ASTCDType> srcCDTypes;
  List<ASTCDType> tgtCDTypes;
  ICD4CodeArtifactScope scopeSrcCD, scopeTgtCD;
  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  public Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDDiffUtil.refreshSymbolTable(srcCD);
    CDDiffUtil.refreshSymbolTable(tgtCD);
    helper.setSrcCD(srcCD);
    helper.setTgtCD(tgtCD);
    this.baseDiff = new ArrayList<>();
    this.matchedClasses = new ArrayList<>();
    this.matchedEnums = new ArrayList<>();
    this.matchedInterfaces = new ArrayList<>();
    this.matchedAssocs = new ArrayList<>();
    this.changedTypes = new ArrayList<>();
    this.changedAssocs = new ArrayList<>();
    this.addedClasses = new ArrayList<>();
    this.deletedClasses = new ArrayList<>();
    this.addedEnums = new ArrayList<>();
    this.deletedEnums = new ArrayList<>();
    this.deletedAssocs = new ArrayList<>();
    this.addedAssocs = new ArrayList<>();
    this.addedInheritance = new ArrayList<>();
    this.deletedInheritance = new ArrayList<>();
    scopeSrcCD = (ICD4CodeArtifactScope) srcCD.getEnclosingScope();
    scopeTgtCD = (ICD4CodeArtifactScope) tgtCD.getEnclosingScope();
    srcCDTypes = new ArrayList<>();
    srcCDTypes.addAll(srcCD.getCDDefinition().getCDClassesList());
    srcCDTypes.addAll(srcCD.getCDDefinition().getCDEnumsList());
    srcCDTypes.addAll(srcCD.getCDDefinition().getCDInterfacesList());
    tgtCDTypes = new ArrayList<>();
    tgtCDTypes.addAll(tgtCD.getCDDefinition().getCDClassesList());
    tgtCDTypes.addAll(tgtCD.getCDDefinition().getCDInterfacesList());
    tgtCDTypes.addAll(tgtCD.getCDDefinition().getCDEnumsList());

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(srcCD);
    new CD4CodeDirectCompositionTrafo().transform(tgtCD);

    loadAllLists(srcCD, tgtCD, scopeSrcCD, scopeTgtCD);
    helper.setMatchedClasses(matchedClasses);
    helper.setMaps();
  }
  @Override
  public ASTCDCompilationUnit getSrcCD() {
    return srcCD;
  }
  @Override
  public void setSrcCD(ASTCDCompilationUnit srcCD) {
    this.srcCD = srcCD;
  }
  @Override
  public ASTCDCompilationUnit getTgtCD() {
    return tgtCD;
  }
  @Override
  public void setTgtCD(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
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
  public List<CDTypeDiff> getChangedTypes() {
    return changedTypes;
  }
  @Override
  public void setChangedTypes(List<CDTypeDiff> changedTypes) {
    this.changedTypes = changedTypes;
  }

  @Override
  public List<CDAssocDiff> getChangedAssocs() {
    return changedAssocs;
  }

  @Override
  public void setChangedAssocs(List<CDAssocDiff> changedAssocs) {
    this.changedAssocs = changedAssocs;
  }

  @Override
  public List<ASTCDClass> getAddedClasses() {
    return addedClasses;
  }

  @Override
  public void setAddedClasses(List<ASTCDClass> addedClasses) {
    this.addedClasses = addedClasses;
  }

  @Override
  public List<ASTCDClass> getDeletedClasses() {
    return deletedClasses;
  }

  @Override
  public void setDeletedClasses(List<ASTCDClass> deletedClasses) {
    this.deletedClasses = deletedClasses;
  }

  @Override
  public List<ASTCDEnum> getAddedEnums() {
    return addedEnums;
  }

  @Override
  public void setAddedEnums(List<ASTCDEnum> addedEnums) {
    this.addedEnums = addedEnums;
  }

  @Override
  public List<ASTCDEnum> getDeletedEnums() {
    return deletedEnums;
  }

  @Override
  public void setDeletedEnums(List<ASTCDEnum> deletedEnums) {
    this.deletedEnums = deletedEnums;
  }

  @Override
  public List<ASTCDAssociation> getAddedAssocs() {
    return addedAssocs;
  }

  @Override
  public void setAddedAssocs(List<ASTCDAssociation> addedAssocs) {
    this.addedAssocs = addedAssocs;
  }

  @Override
  public List<ASTCDAssociation> getDeletedAssocs() {
    return deletedAssocs;
  }

  @Override
  public void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs) {
    this.deletedAssocs = deletedAssocs;
  }

  @Override
  public List<Pair<ASTCDClass, ASTCDClass>> getMatchedClasses() {
    return matchedClasses;
  }

  @Override
  public List<Pair<ASTCDEnum, ASTCDEnum>> getMatchedEnums() {
    return matchedEnums;
  }

  @Override
  public List<Pair<ASTCDInterface, ASTCDInterface>> getMatchedInterfaces() {
    return matchedInterfaces;
  }

  @Override
  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  @Override
  public List<Pair<ASTCDAssociation, ASTCDAssociation>> getMatchedAssocs() {
    return matchedAssocs;
  }

  @Override
  public void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs) {
    this.matchedAssocs = matchedAssocs;
  }

  @Override
  public void setMatchedEnums(List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums) {
    this.matchedEnums = matchedEnums;
  }

  @Override
  public void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces) {
    this.matchedInterfaces = matchedInterfaces;
  }

  /**
   * Checks if an added classes refactors the old structure. The class must be abstract,
   * its subclasses in the old CD need to have all of its attributes, and it can't have new ones.
   */
  public ASTCDClass isSupClass(ASTCDClass astcdClass){
    if (astcdClass.getModifier().isAbstract()){
      List<ASTCDClass> classesToCheck = getSpannedInheritance(helper.getSrcCD(), astcdClass);
      List<ASTCDAttribute> attributes = astcdClass.getCDAttributeList();
      for (ASTCDClass classToCheck : classesToCheck){
        for (ASTCDAttribute attribute : attributes){
          if (!Syn2SemDiffHelper.isAttContainedInClass(attribute, classToCheck)){
            Set<ASTCDClass> classes = CDDiffUtil.getAllSuperclasses(classToCheck, helper.getSrcCD().getCDDefinition().getCDClassesList());
            classes.remove(astcdClass);
            boolean isContained = false;
            for (ASTCDClass superOfSub : classes){
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, superOfSub)){
                isContained = true;
                break;
              }
            }
            if (!isContained){
              return classToCheck;
            }
          }
        }
      }
    } else {
      return astcdClass;
    }
    return null;
  }

  //CHECKED
  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> deletedInheritance(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDType>> struc : deletedInheritance){
      List<ASTCDType> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDType superClass : superClasses){
        if (!helper.getNotInstanClassesSrc().contains(helper.findMatchedSrc(struc.a))
          && !helper.getNotInstanClassesTgt().contains(struc.a)
          && !helper.getNotInstanClassesTgt().contains(superClass)
          && isInheritanceDeleted((ASTCDClass) superClass, struc.a)){
          currentDiff.add((ASTCDClass) superClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(helper.findMatchedSrc(struc.a), currentDiff));
      }
    }
    return diff;
  }

  //CHECKED
  public boolean isInheritanceDeleted(ASTCDClass astcdClass, ASTCDClass subClassTgt) {
    //check if a deleted class brings a semantic difference
    //check if all subclasses have the attributes from this class from tgt tgtCD
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them
    ASTCDClass subClass = helper.findMatchedSrc(subClassTgt);
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesSrc().contains(subClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, subClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(subClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
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
        }
      }
    }
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getTrgMap().get(astcdClass)) {
      if (!areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getSrcMap().get(subClass)) {
          if (helper.sameAssociationTypeTgtSrc(baseAssoc, assocStruct)) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
      }
    }
    for (AssocStruct otherStruct : helper.getAllOtherAssocsTgt(astcdClass)) {
      boolean isContained1 = false;
      for (AssocStruct srcStruct : helper.getAllOtherAssocsSrc(subClass)) {
        if (helper.sameAssociationTypeTgtSrc(srcStruct, otherStruct)) {
          isContained1 = true;
        }
      }
      if (!isContained1) {
        return true;
      }
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - done (only outgoing are saved to the values of a key)
    return false;
  }

  //CHECKED
  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> addedInheritance(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDType>> struc : addedInheritance){
      List<ASTCDType> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDType superClass : superClasses){
        if (!helper.getNotInstanClassesSrc().contains(struc.a)
          && !helper.getNotInstanClassesTgt().contains(helper.findMatchedClass(struc.a))
          && !helper.getNotInstanClassesSrc().contains(superClass)
          && isInheritanceAdded((ASTCDClass) superClass, struc.a)){
          currentDiff.add((ASTCDClass) superClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  //CHECKED
  public Set<InheritanceDiff> mergeInheritanceDiffs(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> added = addedInheritance();
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> deleted = deletedInheritance();
    Set<InheritanceDiff> set = new HashSet<>();
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : added){
      InheritanceDiff diff = new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedClass(pair.a)));
      diff.setNewDirectSuper(new ArrayList<>(pair.b));
      set.add(diff);
    }
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : deleted){
      boolean holds = false;
      for (InheritanceDiff diff : set){
        if (pair.a.equals(diff.getAstcdClasses().b)){
          diff.setOldDirectSuper(new ArrayList<>(pair.b));
          holds = true;
          break;
        }
      }
      if (!holds){
        InheritanceDiff diff = new InheritanceDiff(new Pair<>(helper.findMatchedSrc(pair.a), pair.a));
        diff.setOldDirectSuper(new ArrayList<>(pair.b));
        set.add(diff);
      }
    }
    return set;
  }

  //CHECKED
  public boolean isInheritanceAdded(ASTCDClass astcdClass, ASTCDClass subClass) {
    //reversed case
    //check if new attributes existed in the given subclass - use function from CDTypeDiff
    //check if the associations also existed(are subtypes of the associations) in the tgtMap - same subfunction from isClassDeleted
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesSrc().contains(astcdClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, helper.findMatchedClass(subClass))) {
          Set<ASTCDType> astcdClassList = getAllSuper(helper.findMatchedClass(subClass), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
              && !helper.getNotInstanClassesTgt().contains((ASTCDClass) type)
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
        }
      }
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - only outgoing are saved in the map
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getSrcMap().get(astcdClass)) {
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getTrgMap().get(helper.findMatchedClass(subClass))) {
          if (helper.sameAssociationTypeSrcTgt(baseAssoc, assocStruct)) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
      }
    }
    for (AssocStruct otherStruct : helper.getAllOtherAssocsSrc(astcdClass)){
      boolean isContained1 = false;
      for (AssocStruct srcStruct : helper.getAllOtherAssocsTgt(helper.findMatchedClass(subClass))){
        if (helper.sameAssociationTypeSrcTgt(srcStruct, otherStruct)){
          isContained1 = true;
        }
      }
      if (!isContained1){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public List<ASTCDClass> isAssocDeleted(ASTCDAssociation association, ASTCDClass astcdClass) {
    ASTCDClass isDeletedSrc = null;
    ASTCDClass isDeletedTgt = null;
    AssocStruct assocStruct = helper.getAssocStrucForClassTgt(astcdClass, association);
    if (assocStruct != null) {//if assocStruc is null, then the association is deleted because of overlapping
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (!(assocStruct.getAssociation().getRight().getCDCardinality().isMult()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())) {
          if (!astcdClass.getModifier().isAbstract()
            && helper.findMatchedSrc(astcdClass) != null
            && !helper.getNotInstanClassesTgt().contains(astcdClass)
            && !helper.getNotInstanClassesSrc().contains(helper.findMatchedSrc(astcdClass))
            && !helper.classHasAssociationTgtSrc(assocStruct, helper.findMatchedSrc(astcdClass))) {
            isDeletedSrc = helper.findMatchedSrc(astcdClass);
          } else if (!helper.getNotInstanClassesTgt().contains(astcdClass)
            && helper.allSubclassesHaveIt(assocStruct, astcdClass) != null){
            isDeletedSrc = helper.findMatchedSrc(helper.allSubclassesHaveIt(assocStruct, astcdClass));
          }
        }
        if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isOpt()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isMult())){
          ASTCDClass right = getConnectedClasses(assocStruct.getAssociation(), helper.getTgtCD()).b;
          if (!right.getModifier().isAbstract()
            && helper.findMatchedSrc(right) != null
            && !helper.getNotInstanClassesTgt().contains(right)
            && !helper.getNotInstanClassesSrc().contains(helper.findMatchedSrc(right))
            && !helper.classIsTargetTgtSrc(assocStruct, helper.findMatchedSrc(right))) {
            isDeletedTgt = helper.findMatchedSrc(right);
          } else if (!helper.getNotInstanClassesTgt().contains(right)
            && helper.allSubClassesAreTgtTgtSrc(assocStruct, right) != null){
            isDeletedTgt = helper.findMatchedSrc(helper.allSubClassesAreTgtTgtSrc(assocStruct, right));
          }
        }
      } else {
        if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())) {
          if (!astcdClass.getModifier().isAbstract()
            && helper.findMatchedSrc(astcdClass) != null
            && !helper.getNotInstanClassesTgt().contains(astcdClass)
            && !helper.getNotInstanClassesSrc().contains(helper.findMatchedSrc(astcdClass))
            && !helper.classHasAssociationTgtSrc(assocStruct, helper.findMatchedSrc(astcdClass))) {
            isDeletedSrc = helper.findMatchedSrc(astcdClass);
          } else if (!helper.getNotInstanClassesTgt().contains(astcdClass)
            && helper.allSubclassesHaveIt(assocStruct, astcdClass) != null){
            isDeletedSrc = helper.findMatchedSrc(helper.allSubclassesHaveIt(assocStruct, astcdClass));
          }
        }
        if (!(assocStruct.getAssociation().getRight().getCDCardinality().isOpt()
          || assocStruct.getAssociation().getRight().getCDCardinality().isMult())){
          ASTCDClass left = getConnectedClasses(assocStruct.getAssociation(), helper.getTgtCD()).a;
          if (!left.getModifier().isAbstract()
            && helper.findMatchedSrc(left) != null
            && !helper.getNotInstanClassesTgt().contains(left)
            && !helper.getNotInstanClassesSrc().contains(helper.findMatchedSrc(left))
            && !helper.classIsTargetTgtSrc(assocStruct, helper.findMatchedSrc(left))) {
            isDeletedTgt = helper.findMatchedSrc(left);
          } else if (!helper.getNotInstanClassesTgt().contains(left)
            && helper.allSubClassesAreTgtTgtSrc(assocStruct, left) != null){
            isDeletedTgt = helper.findMatchedSrc(helper.allSubClassesAreTgtTgtSrc(assocStruct, left));
          }
        }
      }
    }
    List<ASTCDClass> list = new ArrayList<>();
    if (isDeletedSrc != null){
      list.add(isDeletedSrc);
    }
    if (isDeletedTgt != null){
      list.add(isDeletedTgt);
    }
    return list;
  }

  //CHECKED
  public List<ASTCDClass> isAssocAdded(ASTCDAssociation association) {
    ASTCDClass isAddedSrc = null;
    ASTCDClass isAddedTgt = null;
    ASTCDClass classToUse;
    ASTCDClass otherSide;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
    AssocStruct assocStruct = helper.getAssocStrucForClass(pair.a, association);
    classToUse = pair.a;
    otherSide = pair.b;
    if (assocStruct == null) {
      assocStruct = helper.getAssocStrucForClass(pair.b, association);
      classToUse = pair.b;
      otherSide = pair.a;
    }
    if (assocStruct != null) {
      if (!classToUse.getModifier().isAbstract()
        && helper.findMatchedClass(classToUse) != null
        && !helper.getNotInstanClassesTgt().contains(helper.findMatchedClass(classToUse))
        && !helper.getNotInstanClassesSrc().contains(classToUse)
        && !helper.classHasAssociationSrcTgt(assocStruct, helper.findMatchedClass(classToUse))) {
        isAddedSrc = classToUse;
      } else if (!helper.getNotInstanClassesSrc().contains(classToUse)
        && helper.allSubClassesHaveItTgt(assocStruct, classToUse) != null) {
        isAddedSrc = helper.allSubClassesHaveItTgt(assocStruct, classToUse);
      }
      if (!otherSide.getModifier().isAbstract()
        && helper.findMatchedSrc(otherSide) != null
        && !helper.getNotInstanClassesTgt().contains(helper.findMatchedClass(otherSide))
        && !helper.getNotInstanClassesSrc().contains(otherSide)
        && !helper.classIsTgtSrcTgt(assocStruct, otherSide)) {
        isAddedTgt = otherSide;
      } else if (!helper.getNotInstanClassesSrc().contains(otherSide)
        && helper.allSubClassesAreTgtSrcTgt(assocStruct, otherSide) != null) {
        isAddedTgt = helper.allSubClassesAreTgtSrcTgt(assocStruct, otherSide);
      }
    }
    List<ASTCDClass> list = new ArrayList<>();
    if (isAddedSrc != null){
      list.add(isAddedSrc);
    }
    if (isAddedTgt != null){
      list.add(isAddedTgt);
    }
    return list;
  }

  /**
   * Find all overlapping and all duplicated associations.
   * When comparing associations, we distinguish two cases:
   * 1) association and superAssociation
   * 2) two associations with the same source
   * For the first case we do the following:
   * If the two associations are in conflict(they have the same role name in target direction) and
   * the target classes are in an inheritance relation(B extends C or C extends B), the subAssociation needs to be merged with the superAssociation.
   * If the associations are in a conflict, but aren't in an inheritance relation, then the subAssociation can't exist(A.r would lead to classes with different types).
   * For the last, we also consider the cardinalities of the associations. If they are additionally at least 1, then the subclass(and its subclasses) can't exist
   * (A.r always has to lead to different classes, which is not allowed).
   * The second case is handled the same way. We distinguish the cases, because in the first one additional delete operation for the used datastructure must be executed.
   * The implementation can be changed o work without the cases.
   */

  @Override
  public void findOverlappingAssocs(){
    Set<ASTCDClass> srcToDelete = new HashSet<>();
    Set<Pair<ASTCDClass, ASTCDRole>> srcAssocsToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> srcAssocsToMerge = new HashSet<>();
    Set<DeleteStruc> srcAssocsToMergeWithDelete = new HashSet<>();
    Set<ASTCDClass> tgtToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> tgtAssocsToMerge = new HashSet<>();
    Set<Pair<ASTCDClass, ASTCDRole>> tgtAssocsToDelete = new HashSet<>();
    Set<DeleteStruc> tgtAssocsToMergeWithDelete = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNamesSrc(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)
              && !superAssoc.isSuperAssoc()
              && !association.isSuperAssoc()) {
              //comparison between direct associations
              if ((sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationInReverse(association.getAssociation(), superAssoc.getAssociation()))
                && !helper.isAdded(association, superAssoc, astcdClass, srcAssocsToMergeWithDelete)){
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              }
              else if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                srcAssocsToMerge.add(new Pair<>(association, superAssoc));
              }
              else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)
                && !getConnectedClasses(association.getAssociation(), srcCD).equals(getConnectedClasses(superAssoc.getAssociation(), srcCD))){
                if (areZeroAssocs(association, superAssoc)) {
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : helper.getTrgMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNamesSrc(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                tgtAssocsToMerge.add(new Pair<>(association, superAssoc));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              }
              else if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationInReverse(association.getAssociation(), superAssoc.getAssociation())){
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              }
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass : srcToDelete) {
      helper.getSrcMap().removeAll(astcdClass);
      helper.deleteOtherSideSrc(astcdClass);
      for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)) {
        helper.getSrcMap().removeAll(subClass);
        helper.deleteOtherSideSrc(subClass);
      }
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete) {
      if (!helper.getNotInstanClassesSrc().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete) {
      helper.getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : srcAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : srcAssocsToDelete) {
      deleteAssocsFromSrc(pair.a, pair.b);
    }
    for (ASTCDClass astcdClass : tgtToDelete) {
      helper.getTrgMap().removeAll(astcdClass);
      helper.deleteOtherSideTgt(astcdClass);
      for (ASTCDClass subClass : getSpannedInheritance(tgtCD, astcdClass)) {
        helper.deleteOtherSideTgt(subClass);
        helper.getTrgMap().removeAll(subClass);
      }
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      if (!helper.getNotInstanClassesTgt().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      helper.getTrgMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : tgtAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : tgtAssocsToDelete) {
      deleteAssocsFromTgt(pair.a, pair.b);
    }
    deleteCompositions();
  }

  //CHECKED
  public void deleteCompositions() {
    for (ASTCDAssociation association : helper.getSrcCD().getCDDefinition().getCDAssociationsList()) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
      AssocStruct assocStruct = helper.getAssocStrucForClass(pair.a, association);
      if (assocStruct == null) {
        assocStruct = helper.getAssocStrucForClass(pair.b, association);
      }
      if (association.getCDAssocType().isComposition()
        && assocStruct != null) {
        if (helper.getNotInstanClassesSrc().contains(pair.b)) {
          helper.updateSrc(pair.a);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getSrcCD(), pair.a)) {
            helper.getSrcMap().removeAll(subClass);
            helper.updateSrc(subClass);
          }
        }
      }
    }

    for (ASTCDAssociation association : helper.getTgtCD().getCDDefinition().getCDAssociationsList()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getTgtCD());
      AssocStruct assocStruct = helper.getAssocStrucForClassTgt(pair.a, association);
      if (assocStruct == null) {
        assocStruct = helper.getAssocStrucForClassTgt(pair.b, association);
      }
      if (association.getCDAssocType().isComposition()
        && assocStruct != null){
        if (helper.getNotInstanClassesTgt().contains(pair.b)){
          helper.updateTgt(pair.a);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getTgtCD(), pair.a)) {
            helper.getSrcMap().removeAll(subClass);
            helper.updateTgt(subClass);
          }
        }
      }
    }
  }

  //CHECKED
  /**
   * Delete associations from srcMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromSrc(ASTCDClass astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = helper.getSrcMap().get(astcdClass).iterator();
   while (iterator.hasNext()){
     AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().getName().equals(role.getName())){
        helper.deleteAssocOtherSideSrc(assocStruct);
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        helper.deleteAssocOtherSideSrc(assocStruct);
        iterator.remove();
      }
    }
  }

  //CHECKED
  /**
   * Delete associations from trgMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromTgt(ASTCDClass astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = helper.getTrgMap().get(astcdClass).iterator();
    while (iterator.hasNext()){
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().getName().equals(role.getName())){
        helper.deleteAssocOtherSideTgt(assocStruct);
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        helper.deleteAssocOtherSideTgt(assocStruct);
        iterator.remove();
      }
    }
  }

  //CHECKED
  public List<Pair<ASTCDAssociation, List<ASTCDClass>>> addedAssocList(){
    List<Pair<ASTCDAssociation, List<ASTCDClass>>> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs){
      List<ASTCDClass> list = isAssocAdded(association);
      if (!list.isEmpty()){
        associationList.add(new Pair<>(association, list));
      }
    }
    return associationList;
  }

  public List<Pair<ASTCDAssociation, List<ASTCDClass>>> deletedAssocList() {
    List<Pair<ASTCDAssociation, List<ASTCDClass>>> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, tgtCD);
      if (association.getCDAssocDir().isBidirectional()){
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.a);
        List<ASTCDClass> astcdClass1 = isAssocDeleted(association, pair.b);
        if (helper.findMatchedSrc(pair.a) != null
          && !astcdClass.isEmpty()){
          list.add(new Pair<>(association, astcdClass));
        }
        if (helper.findMatchedSrc(pair.a) != null
          && !astcdClass1.isEmpty()){
          list.add(new Pair<>(association, astcdClass1));
        }
      }
      else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.b);
        if (helper.findMatchedSrc(pair.b) != null
          && !astcdClass.isEmpty()){
          list.add(new Pair<>(association, astcdClass));
        }
      }
      else {
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.a);
        if (helper.findMatchedSrc(pair.a) != null
          && !astcdClass.isEmpty()){
          list.add(new Pair<>(association, astcdClass));
        }
      }
    }
    return list;
  }

  //CHECKED
  public List<ASTCDClass> addedClassList(){
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClasses){
      ASTCDClass result = isSupClass(astcdClass);
      if (!helper.getNotInstanClassesSrc().contains(astcdClass)
        && result != null){
        classList.add(result);
      }
    }
    return classList;
  }

  //CHECKED
  public boolean srcAndTgtExist(CDAssocDiff assocDiff){
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
    if (assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.a) != null
      || assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.b) != null){
      Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getTgtElem(), tgtCD);
      return assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.a) != null
        || assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.b) != null;
    }
    return false;
  }

  //CHECKED
  public List<TypeDiffStruc> changedTypes() {
    List<TypeDiffStruc> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedTypes) {
      boolean changed = false;
      TypeDiffStruc diff = new TypeDiffStruc();
      diff.setAstcdType(typeDiff.getSrcElem());
      diff.setBaseDiff(typeDiff.getBaseDiff());
      if (typeDiff.getSrcElem() instanceof ASTCDEnum) {
        diff.setAddedConstants(typeDiff.newConstants());
        list.add(diff);
      } else if (!helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())) {
        if (typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE) || typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER)) {
          diff.setMemberDiff(typeDiff.changedAttribute());
          changed = true;
          List<Pair<ASTCDAttribute, ASTCDAttribute>> pairs = new ArrayList<>();
          for (ASTCDAttribute attribute : typeDiff.changedAttribute().b) {
            pairs.add(new Pair<>(attribute, typeDiff.getOldAttribute(attribute)));
          }
          diff.setMatchedAttributes(pairs);
        }
        if (typeDiff.getBaseDiff().contains(DiffTypes.ADDED_ATTRIBUTE)) {
          diff.setAddedAttributes(typeDiff.addedAttributes());
          changed = true;
        }
        if (typeDiff.getBaseDiff().contains(DiffTypes.REMOVED_ATTRIBUTE)) {
          diff.setDeletedAttributes(typeDiff.deletedAttributes());
          changed = true;
        }
        if (typeDiff.getBaseDiff().contains(DiffTypes.STEREOTYPE_DIFFERENCE)) {
          diff.setChangedStereotype(typeDiff.isClassNeeded());
          changed = true;
        }
      }
      if (changed) {
        list.add(diff);
      }
    }
    return list;
  }

  //CHECKED
  public List<AssocDiffStruc> changedAssoc() {
    List<AssocDiffStruc> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs) {
      Pair<AssocStruct, AssocStruct> matchedPairs = helper.getStructsForAssocDiff(assocDiff.getSrcElem(), assocDiff.getTgtElem());
      if (matchedPairs.a == null || matchedPairs.b == null) {
        continue;
      }
      Pair<ASTCDClass, ASTCDClass> pairDef = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
      Pair<ASTCDClass, ASTCDClass> pair;
      if (pairDef.a.getModifier().isAbstract() || pairDef.b.getModifier().isAbstract()) {
        pair = helper.getClassesForAssoc(pairDef);
      } else {
        pair = pairDef;
      }
      if (pair != null && pair.a != null && pair.b != null && !helper.getNotInstanClassesSrc().contains(pair.a) && !helper.getNotInstanClassesSrc().contains(pair.b)) {
        AssocDiffStruc diff = new AssocDiffStruc();
        diff.setAssociation(assocDiff.getSrcElem());
        boolean changed = false;
        assocDiff.setStructs();
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {

          diff.setChangedRoleNames(assocDiff.getRoleDiff().b);
          changed = true;
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
          if (assocDiff.isDirectionChanged()) {
            diff.setChangedDir(true);
            changed = true;
          }
        }

        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
          ASTCDClass change = assocDiff.changedTgt();
          if (change != null) {
            diff.setChangedTgt(change);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {

          ASTCDClass changedSrc = assocDiff.changedSrc();
          ASTCDClass changedTgt = assocDiff.changedTgt();
          if (changedSrc != null) {
            diff.setChangedSrc(changedSrc);
            changed = true;
          }
          if (changedTgt != null) {
            diff.setChangedTgt(changedTgt);
            changed = true;
          }
        }
        //Tsveti - done
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {

          ASTCDClass change = assocDiff.changedTgt();
          if (change != null) {
            diff.setChangedTgt(change);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY)) {
          diff.setChangedCard(assocDiff.getCardDiff().b);
          changed = true;
        }
        if (changed) {
          list.add(diff);
        }
      }
    }
    return list;
  }

  //CHECKED
  public List<ASTCDClass> srcExistsTgtNot(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstanClassesTgt()){
      ASTCDClass matched = helper.findMatchedSrc(astcdClass);
      if (matched != null
        && !helper.getNotInstanClassesSrc().contains(matched)){
        list.add(astcdClass);
      }
    }
    return list;
  }

  //CHECKED
  public List<ASTCDClass> tgtExistsSrcNot(){
    //for each class, and each AssocStrcut in the tgtMap if the cardinality on the same side is 1 or 1..*
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstanClassesSrc()){
      if (helper.findMatchedClass(astcdClass) != null
        && !helper.getNotInstanClassesTgt().contains(helper.findMatchedClass(astcdClass))){
        list.add(helper.findMatchedClass(astcdClass));
      }
    }
    Set<ASTCDClass> set = new HashSet<>();
    for (ASTCDClass astcdClass : list){
      for (AssocStruct assocStruct : helper.getTrgMap().get(astcdClass)){
        if (assocStruct.getSide().equals(ClassSide.Left)
          && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())){
          ASTCDClass matched = helper.findMatchedSrc(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b);
          if (matched != null
            && !helper.getNotInstanClassesSrc().contains(matched)
            && !helper.getNotInstanClassesTgt().contains(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)){
            set.add(matched);
          }
        }
        if (assocStruct.getSide().equals(ClassSide.Right)
          && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())){
          ASTCDClass matched = helper.findMatchedSrc(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a);
          if (matched != null
            && !helper.getNotInstanClassesSrc().contains(matched)
            && !helper.getNotInstanClassesTgt().contains(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)){
            set.add(matched);
          }
        }
        for (AssocStruct assocStruct1 : helper.getAllOtherAssocsTgt(astcdClass)){
          if (assocStruct1.getSide().equals(ClassSide.Left)){
            ASTCDClass matched = helper.findMatchedSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a);
            if (matched != null
              && !helper.getNotInstanClassesSrc().contains(matched)
              && !helper.getNotInstanClassesTgt().contains(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a)){
              set.add(matched);
            }
          } else {
            ASTCDClass matched = helper.findMatchedSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b);
            if (matched != null
              && !helper.getNotInstanClassesSrc().contains(matched)
              && !helper.getNotInstanClassesTgt().contains(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b)){
              set.add(matched);
            }
          }
        }
      }
    }
    return new ArrayList<>(set);
  }

  //CHECKED
  public List<ASTCDClass> srcAssocExistsTgtNot(){
    //get List of AssocStructs from srcMap
    //for each class remove added assocs
    //for the remaining find matches in the tgtMap
    //if no matches found add to list
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()){
      ASTCDClass matched = helper.findMatchedClass(astcdClass);
      if (matched != null
        && !helper.getNotInstanClassesTgt().contains(matched)){
        List<AssocStruct> assocStructs = helper.getSrcMap().get(astcdClass);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = addedAssocsForClass(astcdClass);
        copy.removeAll(added);
        if (srcAssocsExist(copy, matched)){
          classes.add(astcdClass);
        }
      }
    }
    return new ArrayList<>(classes);
  }

  //CHECKED
  public List<AssocStruct> addedAssocsForClass(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      AssocStruct matched = helper.getAssocStrucForClass(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  //CHECKED
  public List<AssocStruct> deletedAssocsForClass(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      AssocStruct matched = helper.getAssocStrucForClassTgt(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  //CHECKED
  public boolean srcAssocsExist(List<AssocStruct> assocStructs, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : assocStructs){
      ASTCDAssociation matched = findMatchedAssociation(assocStruct.getAssociation());
      if (matched != null
        && helper.getAssocStrucForClassTgt(astcdClass, matched) == null){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean tgtAssocsExist(List<AssocStruct> assocStructs, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : assocStructs){
      ASTCDAssociation matched = findMatchedAssociationSrc(assocStruct.getAssociation());
      if (matched != null
        && helper.getAssocStrucForClass(astcdClass, matched) == null){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public ASTCDAssociation findMatchedAssociation(ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs) {
      if (pair.a.equals(association)) {
        return pair.b;
      }
    }
    return null;
  }

  //CHECKED
  public ASTCDAssociation findMatchedAssociationSrc(ASTCDAssociation association){
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs){
      if (pair.b.equals(association)){
        return pair.a;
      }
    }
    return null;
  }

  //CHECKED
  public List<ASTCDClass> tgtAssocsExistsSrcNot(){
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getTrgMap().keySet()){
      ASTCDClass matched = helper.findMatchedSrc(astcdClass);
      if (matched != null
        && !helper.getNotInstanClassesSrc().contains(matched)){
        List<AssocStruct> assocStructs = helper.getTrgMap().get(astcdClass);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = deletedAssocsForClass(astcdClass);
        copy.removeAll(added);
        if (tgtAssocsExist(copy, matched)){
          classes.add(matched);
        }
      }
    }
    return new ArrayList<>(classes);
  }

  //CHECKED
  public AssocDiffs getAssocDiffs(){
    List<ASTCDClass> srcAssocExistsTgtNot = srcAssocExistsTgtNot();
    List<ASTCDClass> tgtAssocExistsSrcNot = tgtAssocsExistsSrcNot();
    List<ASTCDClass> mixed = new ArrayList<>();
    for (ASTCDClass astcdClass : srcAssocExistsTgtNot){
      if (tgtAssocExistsSrcNot.contains(astcdClass)){
        mixed.add(astcdClass);
      }
    }
    mixed.removeAll(tgtExistsSrcNot());
    mixed.removeAll(srcExistsTgtNot());
    srcAssocExistsTgtNot.removeAll(mixed);
    srcAssocExistsTgtNot.removeAll(srcExistsTgtNot());
    tgtAssocExistsSrcNot.removeAll(mixed);
    tgtAssocExistsSrcNot.removeAll(tgtExistsSrcNot());
    return new AssocDiffs(srcAssocExistsTgtNot, tgtAssocExistsSrcNot, mixed);
  }

  //CHECKED
  public List<ASTCDClass> hasDiffSuper(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()){
      ASTCDClass matchedClass = helper.findMatchedClass(astcdClass);
      if (matchedClass != null
        && !helper.getNotInstanClassesTgt().contains(matchedClass)){
        if (hasDiffSuper(astcdClass)){
          list.add(astcdClass);
        }
      }
    }
    return list;
  }

  //CHECKED
  public boolean hasDiffSuper(ASTCDClass astcdClass){
    ASTCDClass oldClass = helper.findMatchedClass(astcdClass);
    List<ASTCDClass> oldCLasses = getSuperClasses(tgtCD, oldClass);
    List<ASTCDClass> newClasses = getSuperClasses(srcCD, astcdClass);
    for (ASTCDClass class1 : oldCLasses){
      boolean foundMatch = false;
      for (ASTCDClass class2 : newClasses){
        if (helper.findMatchedSrc(class1) == class2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch){
        return true;
      }
    }
    for (ASTCDClass class1 : newClasses){
      boolean foundMatch = false;
      for (ASTCDClass class2 : oldCLasses){
        if (helper.findMatchedClass(class1) == class2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch){
        return true;
      }
    }
    return false;
  }

  /*--------------------------------------------------------------------*/

  public Map<ASTCDType,ASTCDType> computeMatchingMapTypes(List<ASTCDType> listToMatch, ASTCDCompilationUnit srcCD,
                                                          ASTCDCompilationUnit tgtCD) {
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    StructureTypeMatcher structureTypeMatch = new StructureTypeMatcher(tgtCD);
    SuperTypeMatcher superTypeMatchNameType = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    SuperTypeMatcher superTypeMatchStructureType = new SuperTypeMatcher(structureTypeMatch, srcCD, tgtCD);
    List<MatchingStrategy<ASTCDType>> typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatchNameType);
    typeMatchers.add(superTypeMatchStructureType);

    CombinedMatching<ASTCDType> combinedMatching = new CombinedMatching<>(listToMatch, srcCD,
      tgtCD, typeMatchers);

    return combinedMatching.getFinalMap();
  }

  public Map<ASTCDAssociation,ASTCDAssociation> computeMatchingMapAssocs(List<ASTCDAssociation> listToMatch, ASTCDCompilationUnit srcCD,
                                                                         ASTCDCompilationUnit tgtCD) {
    NameAssocMatcher nameAssocMatch = new NameAssocMatcher(tgtCD);
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    StructureTypeMatcher structureTypeMatch = new StructureTypeMatcher(tgtCD);
    SuperTypeMatcher superTypeMatchNameType = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    SuperTypeMatcher superTypeMatchStructureType = new SuperTypeMatcher(structureTypeMatch, srcCD, tgtCD);
    SrcTgtAssocMatcher associationSrcTgtMatchNameType = new SrcTgtAssocMatcher(superTypeMatchNameType, srcCD, tgtCD);
    SrcTgtAssocMatcher associationSrcTgtMatchStructureType = new SrcTgtAssocMatcher(superTypeMatchStructureType, srcCD, tgtCD);
    List<MatchingStrategy<ASTCDAssociation>> assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatchNameType);
    assocMatchers.add(associationSrcTgtMatchStructureType);

    CombinedMatching<ASTCDAssociation> combinedMatching = new CombinedMatching<>(listToMatch, srcCD,
      tgtCD, assocMatchers);

    return combinedMatching.getFinalMap();
  }

  public void addAllMatchedTypes(Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    for(ASTCDType x : computedMatchingMapTypes.keySet()){
      if(x instanceof ASTCDClass){
        matchedClasses.add(new Pair<>((ASTCDClass) x, (ASTCDClass)computedMatchingMapTypes.get(x)));
      }
      if(x instanceof ASTCDEnum){
        matchedEnums.add(new Pair<>((ASTCDEnum) x, (ASTCDEnum)computedMatchingMapTypes.get(x)));
      }
      if(x instanceof ASTCDInterface){
        matchedInterfaces.add(new Pair<>((ASTCDInterface) x, (ASTCDInterface)computedMatchingMapTypes.get(x)));
      }
    }
  }

  public void addAllMatchedAssocs(Map<ASTCDAssociation,ASTCDAssociation> computedMatchingMapAssocs) {
    for(ASTCDAssociation x : computedMatchingMapAssocs.keySet()){
      matchedAssocs.add(new Pair<>(x, computedMatchingMapAssocs.get(x)));
    }
  }

  public void addAllChangedTypes() {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
    for(Pair<ASTCDEnum, ASTCDEnum> pair : matchedEnums){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
  }

  public void addAllChangedAssocs() {
    for(Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs){
      CDAssocDiff assocDiff = new CDAssocDiff(pair.a, pair.b, srcCD, tgtCD);
      if(!assocDiff.getBaseDiff().isEmpty()){
        changedAssocs.add(assocDiff);
        baseDiff.addAll(assocDiff.getBaseDiff());
      }
    }
  }

  public void addAllAddedClasses(ASTCDCompilationUnit srcCD, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    List<ASTCDClass> tmp = new ArrayList<>(srcCD.getCDDefinition().getCDClassesList());
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      if (computedMatchingMapTypes.containsKey(srcClass)) {
        tmp.remove(srcClass);
      }
    }
    addedClasses.addAll(tmp);
    if(!tmp.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_CLASS)){
      baseDiff.add(DiffTypes.ADDED_CLASS);
    }
  }

  public void addAllDeletedClasses(ASTCDCompilationUnit tgtCD, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    List<ASTCDClass> tmp = new ArrayList<>(tgtCD.getCDDefinition().getCDClassesList());
    for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
      if (computedMatchingMapTypes.containsValue(tgtClass)) {
        tmp.remove(tgtClass);
      }
    }
    deletedClasses.addAll(tmp);
    if(!tmp.isEmpty() && !baseDiff.contains(DiffTypes.REMOVED_CLASS)){
      baseDiff.add(DiffTypes.REMOVED_CLASS);
    }
  }

  public void addAllAddedEnums(ASTCDCompilationUnit srcCD, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    List<ASTCDEnum> tmp = new ArrayList<>(srcCD.getCDDefinition().getCDEnumsList());
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      if (computedMatchingMapTypes.containsKey(srcEnum)) {
        tmp.remove(srcEnum);
      }
    }
    addedEnums.addAll(tmp);
    if(!tmp.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_ENUM)){
      baseDiff.add(DiffTypes.ADDED_ENUM);
    }
  }

  public void addAllDeletedEnums(ASTCDCompilationUnit tgtCD, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    List<ASTCDEnum> tmp = new ArrayList<>(tgtCD.getCDDefinition().getCDEnumsList());
    for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
      if (computedMatchingMapTypes.containsValue(tgtEnum)) {
        tmp.remove(tgtEnum);
      }
    }
    deletedEnums.addAll(tmp);
    if(!tmp.isEmpty() && !baseDiff.contains(DiffTypes.REMOVED_ENUM)){
      baseDiff.add(DiffTypes.REMOVED_ENUM);
    }
  }

  public void addAllAddedAssocs(ASTCDCompilationUnit srcCD, Map<ASTCDAssociation,ASTCDAssociation> computedMatchingMapAssocs) {
    List<ASTCDAssociation> addedSrcAssocs = new ArrayList<>(srcCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      if(computedMatchingMapAssocs.containsKey(srcAssoc)){
        addedSrcAssocs.remove(srcAssoc);
      }
    }
    addedAssocs.addAll(addedSrcAssocs);
    if(!addedSrcAssocs.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_ASSOCIATION)){
      baseDiff.add(DiffTypes.ADDED_ASSOCIATION);
    }
  }

  public void addAllDeletedAssocs(ASTCDCompilationUnit tgtCD, Map<ASTCDAssociation,ASTCDAssociation> computedMatchingMapAssocs) {
    List<ASTCDAssociation> deletedTgtAssocs = new ArrayList<>(tgtCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
      if(computedMatchingMapAssocs.containsValue(tgtAssoc)){
        deletedTgtAssocs.remove(tgtAssoc);
      }
    }
    deletedAssocs.addAll(deletedTgtAssocs);
    if(!deletedTgtAssocs.isEmpty() && !baseDiff.contains(DiffTypes.REMOVED_ASSOCIATION)){
      baseDiff.add(DiffTypes.REMOVED_ASSOCIATION);
    }
  }

  public void addAllAddedInheritance(ICD4CodeArtifactScope srcCDScope, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      List<ASTCDType> addedInh = new ArrayList<>(getDirectSuperClasses(pair.a, srcCDScope));
      for (ASTCDType srcType : getDirectSuperClasses(pair.a, srcCDScope)) {
        if(computedMatchingMapTypes.containsKey(srcType)){
          addedInh.remove(srcType);
        }
      }
      addedInheritance.add(new Pair<>(pair.a, addedInh));
      if(!addedInh.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_INHERITANCE)){
        baseDiff.add(DiffTypes.ADDED_INHERITANCE);
      }
    }
  }

  public void addAllDeletedInheritance(ICD4CodeArtifactScope tgtCDScope, Map<ASTCDType,ASTCDType> computedMatchingMapTypes) {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      List<ASTCDType> deletedInh = new ArrayList<>(getDirectSuperClasses(pair.b, tgtCDScope));
      for (ASTCDType tgtType : getDirectSuperClasses(pair.b, tgtCDScope)) {
        if(computedMatchingMapTypes.containsValue(tgtType)){
          deletedInh.remove(tgtType);
        }
      }
      deletedInheritance.add(new Pair<>(pair.a, deletedInh));
      if(!deletedInh.isEmpty() && !baseDiff.contains(DiffTypes.REMOVED_INHERITANCE)){
        baseDiff.add(DiffTypes.REMOVED_INHERITANCE);
      }
    }
  }

  private void loadAllLists(ASTCDCompilationUnit srcCD,
                            ASTCDCompilationUnit tgtCD,
                            ICD4CodeArtifactScope srcCDScope,
                            ICD4CodeArtifactScope tgtCDScope) {
    addAllMatchedTypes(computeMatchingMapTypes(srcCDTypes,srcCD,tgtCD));
    addAllMatchedAssocs(computeMatchingMapAssocs(srcCD.getCDDefinition().getCDAssociationsList(),srcCD,tgtCD));
    addAllChangedTypes();
    addAllChangedAssocs();
    addAllAddedClasses(srcCD, computeMatchingMapTypes(srcCDTypes,srcCD,tgtCD));
    addAllDeletedClasses(tgtCD, computeMatchingMapTypes(tgtCDTypes,srcCD,tgtCD));
    addAllAddedEnums(srcCD, computeMatchingMapTypes(srcCDTypes,srcCD,tgtCD));
    addAllDeletedEnums(srcCD, computeMatchingMapTypes(tgtCDTypes,srcCD,tgtCD));
    addAllAddedAssocs(srcCD, computeMatchingMapAssocs(srcCD.getCDDefinition().getCDAssociationsList(),srcCD,tgtCD));
    addAllDeletedAssocs(tgtCD, computeMatchingMapAssocs(tgtCD.getCDDefinition().getCDAssociationsList(),srcCD,tgtCD));
    addAllAddedInheritance(srcCDScope, computeMatchingMapTypes(srcCDTypes,srcCD,tgtCD));
    addAllDeletedInheritance(tgtCDScope, computeMatchingMapTypes(tgtCDTypes,srcCD,tgtCD));
  }
}
