package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cddiff.syndiff.interfaces.ICDSyntaxDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getDirectSuperClasses;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class CDSyntaxDiff extends SyntaxDiffHelper implements ICDSyntaxDiff {
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
  List<ASTCDType> srcCDTypes;
  ICD4CodeArtifactScope scopeSrcCD, scopeTgtCD;

  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  public Syn2SemDiffHelper helper;

  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDDiffUtil.refreshSymbolTable(srcCD);
    CDDiffUtil.refreshSymbolTable(tgtCD);
    helper = new Syn2SemDiffHelper();
    helper.setNotInstClassesSrc(new HashSet<>());
    helper.setNotInstClassesTgt(new HashSet<>());
    helper.setSrcCD(srcCD);
    helper.setTgtCD(tgtCD);
    helper.setSubMaps();
    helper.setMaps();
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

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(srcCD);
    new CD4CodeDirectCompositionTrafo().transform(tgtCD);

    loadAllLists(srcCD, tgtCD, scopeSrcCD, scopeTgtCD);
    helper.setMatchedClasses(matchedClasses);
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

  @Override
  public ASTCDClass isSupClass(ASTCDClass astcdClass) {
    if (astcdClass.getModifier().isAbstract()) {
      List<ASTCDClass> classesToCheck = helper.getSrcSubMap().get(astcdClass);
      List<ASTCDAttribute> attributes = astcdClass.getCDAttributeList();
      for (ASTCDClass classToCheck : classesToCheck) {
        for (ASTCDAttribute attribute : attributes) {
          if (!Syn2SemDiffHelper.isAttContainedInClass(attribute, classToCheck)) {
            Set<ASTCDClass> classes =
                CDDiffUtil.getAllSuperclasses(
                    classToCheck, helper.getSrcCD().getCDDefinition().getCDClassesList());
            classes.remove(astcdClass);
            boolean isContained = false;
            for (ASTCDClass superOfSub : classes) {
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, superOfSub)) {
                isContained = true;
                break;
              }
            }
            if (!isContained) {
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

  // CHECKED
  @Override
  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> deletedInheritance() {
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDType>> struc : deletedInheritance) {
      List<ASTCDType> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDType superClass : superClasses) {
        if (!helper.getNotInstClassesSrc().contains(helper.findMatchedSrc(struc.a))
            && !helper.getNotInstClassesTgt().contains(struc.a)
            && !helper.getNotInstClassesTgt().contains(superClass)
            && isInheritanceDeleted((ASTCDClass) superClass, struc.a)) {
          currentDiff.add((ASTCDClass) superClass);
        }
      }
      if (!currentDiff.isEmpty()) {
        diff.add(new Pair<>(helper.findMatchedSrc(struc.a), currentDiff));
      }
    }
    return diff;
  }

  // CHECKED
  @Override
  public boolean isInheritanceDeleted(ASTCDClass astcdClass, ASTCDClass subClassTgt) {
    // check if a deleted class brings a semantic difference
    // check if all subclasses have the attributes from this class from tgt tgtCD
    // check if there were outgoing(or bidirectional) associations that weren't zero assocs and
    // check if all subclasses have them
    ASTCDClass subClass = helper.findMatchedSrc(subClassTgt);
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstClassesSrc().contains(subClass)
            && !Syn2SemDiffHelper.isAttContainedInClass(attribute, subClass)) {
          Set<ASTCDType> astcdClassList =
              getAllSuper(subClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
                && !helper.getNotInstClassesSrc().contains((ASTCDClass) type)) {
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
                conditionSatisfied = true; // Set the flag to true if the condition holds
                break;
              }
            }
          }
        } else {
          conditionSatisfied = true;
        }
        if (!conditionSatisfied) { // found a subclass that doesn't have this attribute
          return true; // Break out of the first loop if the condition is satisfied
        }
      }
    }
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getTgtMap().get(astcdClass)) {
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
    // check if there were outgoing(or bidirectional) associations that weren't zero assocs and
    // check if all subclasses have them - done (only outgoing are saved to the values of a key)
    return false;
  }

  // CHECKED
  @Override
  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> addedInheritance() {
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDType>> struc : addedInheritance) {
      List<ASTCDType> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDType superClass : superClasses) {
        if (!helper.getNotInstClassesSrc().contains(struc.a)
            && !helper.getNotInstClassesTgt().contains(helper.findMatchedClass(struc.a))
            && !helper.getNotInstClassesSrc().contains(superClass)
            && isInheritanceAdded((ASTCDClass) superClass, struc.a)) {
          currentDiff.add((ASTCDClass) superClass);
        }
      }
      if (!currentDiff.isEmpty()) {
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  // CHECKED
  @Override
  public Set<InheritanceDiff> mergeInheritanceDiffs() {
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> added = addedInheritance();
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> deleted = deletedInheritance();
    Set<InheritanceDiff> set = new HashSet<>();
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : added) {
      InheritanceDiff diff =
          new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedClass(pair.a)));
      diff.setNewDirectSuper(new ArrayList<>(pair.b));
      set.add(diff);
    }
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : deleted) {
      boolean holds = false;
      for (InheritanceDiff diff : set) {
        if (pair.a.equals(diff.getAstcdClasses().b)) {
          diff.setOldDirectSuper(new ArrayList<>(pair.b));
          holds = true;
          break;
        }
      }
      if (!holds) {
        InheritanceDiff diff =
            new InheritanceDiff(new Pair<>(helper.findMatchedSrc(pair.a), pair.a));
        diff.setOldDirectSuper(new ArrayList<>(pair.b));
        set.add(diff);
      }
    }
    return set;
  }

  // CHECKED
  @Override
  public boolean isInheritanceAdded(ASTCDClass astcdClass, ASTCDClass subClass) {
    // reversed case
    // check if new attributes existed in the given subclass - use function from CDTypeDiff
    // check if the associations also existed(are subtypes of the associations) in the tgtMap - same
    // subfunction from isClassDeleted
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstClassesSrc().contains(astcdClass)
            && !Syn2SemDiffHelper.isAttContainedInClass(
                attribute, helper.findMatchedClass(subClass))) {
          Set<ASTCDType> astcdClassList =
              getAllSuper(
                  helper.findMatchedClass(subClass),
                  (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
                && !helper.getNotInstClassesTgt().contains((ASTCDClass) type)
                && Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
              conditionSatisfied = true; // Set the flag to true if the condition holds
              break;
            }
          }
        } else {
          conditionSatisfied = true;
        }
        if (!conditionSatisfied) { // found a subclass that doesn't have this attribute
          return true; // Break out of the first loop if the condition is satisfied
        }
      }
    }
    // check if there were outgoing(or bidirectional) associations that weren't zero assocs and
    // check if all subclasses have them - only outgoing are saved in the map
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getSrcMap().get(astcdClass)) {
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getTgtMap().get(helper.findMatchedClass(subClass))) {
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
    for (AssocStruct otherStruct : helper.getAllOtherAssocsSrc(astcdClass)) {
      boolean isContained1 = false;
      for (AssocStruct srcStruct : helper.getAllOtherAssocsTgt(helper.findMatchedClass(subClass))) {
        if (helper.sameAssociationTypeSrcTgt(srcStruct, otherStruct)) {
          isContained1 = true;
        }
      }
      if (!isContained1) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  @Override
  public List<ASTCDClass> isAssocDeleted(ASTCDAssociation association, ASTCDClass astcdClass) {
    ASTCDClass isDeletedSrc = null;
    ASTCDClass isDeletedTgt = null;
    AssocStruct assocStruct = helper.getAssocStructByUnmodTgt(astcdClass, association);
    if (assocStruct
        != null) { // if assocStruc is null, then the association is deleted because of overlapping
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (!(assocStruct.getAssociation().getRight().getCDCardinality().isMult()
            || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())) {
          ASTCDClass matched = helper.findMatchedSrc(astcdClass);
          ASTCDClass sub = helper.allSubclassesHaveIt(assocStruct, astcdClass);
          if (!astcdClass.getModifier().isAbstract()
              && matched != null
              && !helper.getNotInstClassesTgt().contains(astcdClass)
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper.classHasAssociationTgtSrc(assocStruct, matched)) {
            isDeletedSrc = matched;
          } else if (!helper.getNotInstClassesTgt().contains(astcdClass) && sub != null) {
            isDeletedSrc = helper.findMatchedSrc(sub);
          }
        }
        if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isOpt()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isMult())) {
          ASTCDClass right = getConnectedClasses(assocStruct.getAssociation(), helper.getTgtCD()).b;
          ASTCDClass matched = helper.findMatchedSrc(right);
          ASTCDClass sub = helper.allSubClassesAreTgtTgtSrc(assocStruct, right);
          if (!right.getModifier().isAbstract()
              && matched != null
              && !helper.getNotInstClassesTgt().contains(right)
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper.classIsTargetTgtSrc(assocStruct, matched)) {
            isDeletedTgt = matched;
          } else if (!helper.getNotInstClassesTgt().contains(right) && sub != null) {
            isDeletedTgt = helper.findMatchedSrc(sub);
          }
        }
      } else {
        if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())) {
          ASTCDClass matched = helper.findMatchedSrc(astcdClass);
          ASTCDClass sub = helper.allSubclassesHaveIt(assocStruct, astcdClass);
          if (!astcdClass.getModifier().isAbstract()
              && matched != null
              && !helper.getNotInstClassesTgt().contains(astcdClass)
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper.classHasAssociationTgtSrc(assocStruct, matched)) {
            isDeletedSrc = helper.findMatchedSrc(astcdClass);
          } else if (!helper.getNotInstClassesTgt().contains(astcdClass) && sub != null) {
            isDeletedSrc = helper.findMatchedSrc(sub);
          }
        }
        if (!(assocStruct.getAssociation().getRight().getCDCardinality().isOpt()
            || assocStruct.getAssociation().getRight().getCDCardinality().isMult())) {
          ASTCDClass left = getConnectedClasses(assocStruct.getAssociation(), helper.getTgtCD()).a;
          ASTCDClass matched = helper.findMatchedSrc(left);
          ASTCDClass sub = helper.allSubClassesAreTgtTgtSrc(assocStruct, left);
          if (!left.getModifier().isAbstract()
              && matched != null
              && !helper.getNotInstClassesTgt().contains(left)
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper.classIsTargetTgtSrc(assocStruct, matched)) {
            isDeletedTgt = matched;
          } else if (!helper.getNotInstClassesTgt().contains(left) && sub != null) {
            isDeletedTgt = helper.findMatchedSrc(sub);
          }
        }
      }
    }
    List<ASTCDClass> list = new ArrayList<>();
    if (isDeletedSrc != null) {
      list.add(isDeletedSrc);
    }
    if (isDeletedTgt != null) {
      list.add(isDeletedTgt);
    }
    return list;
  }

  // CHECKED
  @Override
  public List<ASTCDClass> isAssocAdded(ASTCDAssociation association) {
    ASTCDClass isAddedSrc = null;
    ASTCDClass isAddedTgt = null;
    ASTCDClass classToUse;
    ASTCDClass otherSide;
    Pair<ASTCDClass, ASTCDClass> pair =
        Syn2SemDiffHelper.getConnectedClasses(association, helper.getSrcCD());
    AssocStruct assocStruct = helper.getAssocStructByUnmod(pair.a, association);
    classToUse = pair.a;
    otherSide = pair.b;
    if (assocStruct == null) {
      assocStruct = helper.getAssocStructByUnmod(pair.b, association);
      classToUse = pair.b;
      otherSide = pair.a;
    }
    if (assocStruct != null) {
      ASTCDClass matched = helper.findMatchedClass(classToUse);
      ASTCDClass sub = helper.allSubClassesHaveItTgt(assocStruct, classToUse);
      if (!classToUse.getModifier().isAbstract()
          && matched != null
          && !helper.getNotInstClassesTgt().contains(matched)
          && !helper.getNotInstClassesSrc().contains(classToUse)
          && !helper.classHasAssociationSrcTgt(assocStruct, matched)) {
        isAddedSrc = classToUse;
      } else if (!helper.getNotInstClassesSrc().contains(classToUse) && sub != null) {
        isAddedSrc = sub;
      }
      ASTCDClass matched2 = helper.findMatchedClass(otherSide);
      ASTCDClass sub2 = helper.allSubClassesAreTgtSrcTgt(assocStruct, otherSide);
      if (!otherSide.getModifier().isAbstract()
          && matched2 != null
          && !helper.getNotInstClassesTgt().contains(matched2)
          && !helper.getNotInstClassesSrc().contains(otherSide)
          && !helper.classIsTgtSrcTgt(assocStruct, matched2)) {
        isAddedTgt = otherSide;
      } else if (!helper.getNotInstClassesSrc().contains(otherSide) && sub2 != null) {
        isAddedTgt = sub2;
      }
    }
    List<ASTCDClass> list = new ArrayList<>();
    if (isAddedSrc != null) {
      list.add(isAddedSrc);
    }
    if (isAddedTgt != null) {
      list.add(isAddedTgt);
    }
    return list;
  }

  /**
   * Find all overlapping and all duplicated associations. When comparing associations, we
   * distinguish two cases: 1) association and superAssociation 2) two associations with the same
   * source For the first case we do the following: If the two associations are in conflict(they
   * have the same role name in target direction) and the target classes are in an inheritance
   * relation(B extends C or C extends B), the subAssociation needs to be merged with the
   * superAssociation. If the associations are in a conflict, but aren't in an inheritance relation,
   * then the subAssociation can't exist(A.r would lead to classes with different types). For the
   * last, we also consider the cardinalities of the associations. If they are additionally at least
   * 1, then the subclass(and its subclasses) can't exist (A.r always has to lead to different
   * classes, which is not allowed). The second case is handled the same way. We distinguish the
   * cases, because in the first one additional delete operation for the used datastructure must be
   * executed. The implementation can be changed o work without the cases.
   */
  @Override
  public void findOverlappingAssocs() {
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
              if (isInConflict(association, superAssoc)
                  && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNamesSrc(association, superAssoc)
                    && !(association.getDirection().equals(AssocDirection.BiDirectional)
                        || superAssoc.getDirection().equals(AssocDirection.BiDirectional))) {
                  Log.error("Bad overlapping found");
                }
                // same target role names and target classes are in inheritance relation
                // associations need to be merged
                srcAssocsToMergeWithDelete.add(
                    new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc)
                  && !helper.inInheritanceRelation(association, superAssoc)) {
                // two associations with same target role names, but target classes are not in
                // inheritance relation
                // if trg cardinality on one of them is 0..1 or 0..* then such association can't
                // exist
                // if trg cardinality on one of them is 1 or 1..* then such association can't exist
                // and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  // such association can't exist
                  // delete
                  srcAssocsToDelete.add(
                      new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  // such class can't exist
                  // delete
                  System.out.println("class " + astcdClass.getName() + " cannot be instantiated because of");
                  System.out.println("association " + association.getAssociation().getLeftQualifiedName() + " " + association.getAssociation().getRightQualifiedName());
                  System.out.println("superassociation " + superAssoc.getAssociation().getLeftQualifiedName() + " " + superAssoc.getAssociation().getRightQualifiedName());
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)
                && !superAssoc.isSuperAssoc()
                && !association.isSuperAssoc()) {
              // comparison between direct associations
              if ((helper.sameAssocStruct(association, superAssoc)
                      || helper.sameAssocStructInReverse(association, superAssoc))
                  && !helper.isAdded(
                      association, superAssoc, astcdClass, srcAssocsToMergeWithDelete)) {
                srcAssocsToMergeWithDelete.add(
                    new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc)
                  && helper.inInheritanceRelation(association, superAssoc)) {
                srcAssocsToMerge.add(new Pair<>(association, superAssoc));
              } else if (isInConflict(association, superAssoc)
                  && !helper.inInheritanceRelation(association, superAssoc)
                  && !getConnectedClasses(association.getAssociation(), srcCD)
                      .equals(getConnectedClasses(superAssoc.getAssociation(), srcCD))) {
                if (areZeroAssocs(association, superAssoc)) {
                  srcAssocsToDelete.add(
                      new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  System.out.println("class " + astcdClass.getName() + " cannot be instantiated because of");
                  System.out.println("association " + association.getAssociation().getLeftQualifiedName() + " " + association.getAssociation().getRightQualifiedName());
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : helper.getTgtMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc)
                  && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNamesSrc(association, superAssoc)
                    && !(association.getDirection().equals(AssocDirection.BiDirectional)
                        || superAssoc.getDirection().equals(AssocDirection.BiDirectional))) {
                  Log.error("Bad overlapping found");
                }
                tgtAssocsToMergeWithDelete.add(
                    new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc)
                  && !helper.inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  tgtAssocsToDelete.add(
                      new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  System.out.println("class " + astcdClass.getName() + " cannot be instantiated because of");
                  System.out.println("association " + association.getAssociation().getLeftQualifiedName() + " " + association.getAssociation().getRightQualifiedName());
                  System.out.println("superassociation " + superAssoc.getAssociation().getLeftQualifiedName() + " " + superAssoc.getAssociation().getRightQualifiedName());
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc)
                  && helper.inInheritanceRelation(association, superAssoc)) {
                tgtAssocsToMerge.add(new Pair<>(association, superAssoc));
              } else if (isInConflict(association, superAssoc)
                  && !helper.inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  tgtAssocsToDelete.add(
                      new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  System.out.println("class " + astcdClass.getName() + " cannot be instantiated because of");
                  System.out.println("association " + association.getAssociation().getLeftQualifiedName() + " " + association.getAssociation().getRightQualifiedName());
                  System.out.println("superassociation " + superAssoc.getAssociation().getLeftQualifiedName() + " " + superAssoc.getAssociation().getRightQualifiedName());
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              } else if (helper.sameAssocStruct(association, superAssoc)
                  || helper.sameAssocStructInReverse(association, superAssoc)) {
                tgtAssocsToMergeWithDelete.add(
                    new DeleteStruc(association, superAssoc, astcdClass));
              }
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass : srcToDelete) {
      helper.getSrcMap().removeAll(astcdClass);
      helper.deleteOtherSideSrc(astcdClass);
      for (ASTCDClass subClass : helper.getSrcSubMap().get(astcdClass)) {
        helper.getSrcMap().removeAll(subClass);
        helper.deleteOtherSideSrc(subClass);
      }
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete) {
      if (!helper.getNotInstClassesSrc().contains(pair.getAstcdClass())) {
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
      helper.getTgtMap().removeAll(astcdClass);
      helper.deleteOtherSideTgt(astcdClass);
      for (ASTCDClass subClass : helper.getTgtSubMap().get(astcdClass)) {
        helper.deleteOtherSideTgt(subClass);
        helper.getTgtMap().removeAll(subClass);
      }
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      if (!helper.getNotInstClassesTgt().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      helper.getTgtMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
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

  // CHECKED
  public void deleteCompositions() {
    for (ASTCDAssociation association :
        helper.getSrcCD().getCDDefinition().getCDAssociationsList()) {
      Pair<ASTCDClass, ASTCDClass> pair =
          Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
      AssocStruct assocStruct = helper.getAssocStructByUnmod(pair.a, association);
      if (association.getCDAssocType().isComposition() && assocStruct != null) {
        if (helper.getNotInstClassesSrc().contains(pair.b)) {
          helper.updateSrc(pair.a);
          for (ASTCDClass subClass : helper.getSrcSubMap().get(pair.a)) {
            helper.getSrcMap().removeAll(subClass);
            helper.updateSrc(subClass);
          }
        }
      }
    }

    for (ASTCDAssociation association :
        helper.getTgtCD().getCDDefinition().getCDAssociationsList()) {
      Pair<ASTCDClass, ASTCDClass> pair =
          Syn2SemDiffHelper.getConnectedClasses(association, getTgtCD());
      AssocStruct assocStruct = helper.getAssocStructByUnmod(pair.a, association);
      if (association.getCDAssocType().isComposition() && assocStruct != null) {
        if (helper.getNotInstClassesTgt().contains(pair.b)) {
          helper.updateTgt(pair.a);
          for (ASTCDClass subClass : helper.getTgtSubMap().get(pair.a)) {
            helper.getTgtMap().removeAll(subClass);
            helper.updateTgt(subClass);
          }
        }
      }
    }
  }

  // CHECKED
  /**
   * Delete associations from srcMap with a specific role name
   *
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromSrc(ASTCDClass astcdClass, ASTCDRole role) {
    Iterator<AssocStruct> iterator = helper.getSrcMap().get(astcdClass).iterator();
    while (iterator.hasNext()) {
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(role.getName())) {
        helper.deleteAssocOtherSideSrc(assocStruct);
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        helper.deleteAssocOtherSideSrc(assocStruct);
        iterator.remove();
      }
    }
  }

  // CHECKED
  /**
   * Delete associations from trgMap with a specific role name
   *
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromTgt(ASTCDClass astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = helper.getTgtMap().get(astcdClass).iterator();
    while (iterator.hasNext()){
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(role.getName())) {
        helper.deleteAssocOtherSideTgt(assocStruct);
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        helper.deleteAssocOtherSideTgt(assocStruct);
        iterator.remove();
      }
    }
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDAssociation, List<ASTCDClass>>> addedAssocList() {
    List<Pair<ASTCDAssociation, List<ASTCDClass>>> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      List<ASTCDClass> list = isAssocAdded(association);
      if (!list.isEmpty()) {
        associationList.add(new Pair<>(association, list));
      }
    }
    return associationList;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDAssociation, List<ASTCDClass>>> deletedAssocList() {
    List<Pair<ASTCDAssociation, List<ASTCDClass>>> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, tgtCD);
      if (association.getCDAssocDir().isBidirectional()) {
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.a);
        List<ASTCDClass> astcdClass1 = isAssocDeleted(association, pair.b);
        if (helper.findMatchedSrc(pair.a) != null && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
        if (helper.findMatchedSrc(pair.a) != null && !astcdClass1.isEmpty()) {
          list.add(new Pair<>(association, astcdClass1));
        }
      } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.b);
        if (helper.findMatchedSrc(pair.b) != null && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
      } else {
        List<ASTCDClass> astcdClass = isAssocDeleted(association, pair.a);
        if (helper.findMatchedSrc(pair.a) != null && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
      }
    }
    return list;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDClass, ASTCDClass>> addedClassList() {
    List<Pair<ASTCDClass, ASTCDClass>> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClasses) {
      ASTCDClass result = isSupClass(astcdClass);
      if (!helper.getNotInstClassesSrc().contains(astcdClass) && result != null) {
        classList.add(new Pair<>(astcdClass, result));
      }
    }
    return classList;
  }

  // CHECKED
  @Override
  public List<TypeDiffStruct> changedTypes() {
    List<TypeDiffStruct> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedTypes) {
      boolean changed = false;
      TypeDiffStruct diff = new TypeDiffStruct();
      diff.setAstcdType(typeDiff.getSrcElem());
      diff.setBaseDiff(typeDiff.getBaseDiff());
      if (typeDiff.getSrcElem() instanceof ASTCDEnum) {
        diff.setAddedConstants(typeDiff.newConstants());
        list.add(diff);
      } else if (!helper.getNotInstClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())) {
        if ((typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE)
            || typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER)
            ) && !typeDiff.changedAttribute().b.isEmpty()) {
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
        if (typeDiff.getBaseDiff().contains(DiffTypes.DELETED_ATTRIBUTE)) {
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

  // CHECKED
  @Override
  public List<AssocDiffStruc> changedAssoc() {
    List<AssocDiffStruc> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs) {
      Pair<AssocStruct, AssocStruct> matchedPairs =
          helper.getStructsForAssocDiff(assocDiff.getSrcElem(), assocDiff.getTgtElem());
      if (matchedPairs.a == null || matchedPairs.b == null) {
        continue;
      }
      Pair<ASTCDClass, ASTCDClass> pairDef =
          Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
      Pair<ASTCDClass, ASTCDClass> pair;
      if (pairDef.a.getModifier().isAbstract() || pairDef.b.getModifier().isAbstract()) {
        pair = helper.getClassesForAssoc(pairDef);
      } else {
        pair = pairDef;
      }
      if (pair != null
          && pair.a != null
          && pair.b != null
          && !helper.getNotInstClassesSrc().contains(pair.a)
          && !helper.getNotInstClassesSrc().contains(pair.b)) {
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
        // Tsveti - done
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

  // CHECKED
  @Override
  public List<ASTCDClass> srcExistsTgtNot() {
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstClassesTgt()) {
      ASTCDClass matched = helper.findMatchedSrc(astcdClass);
      if (matched != null && !helper.getNotInstClassesSrc().contains(matched)) {
        list.add(matched);
      }
    }
    return list;
  }

  // CHECKED
  // TODO: check again
  @Override
  public List<ASTCDClass> tgtExistsSrcNot() {
    // for each class, and each AssocStrcut in the tgtMap if the cardinality on the same side is 1
    // or 1..*
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstClassesSrc()) {
      if (helper.findMatchedClass(astcdClass) != null
          && !helper.getNotInstClassesTgt().contains(helper.findMatchedClass(astcdClass))) {
        list.add(helper.findMatchedClass(astcdClass));
      }
    }
    Set<ASTCDClass> set = new HashSet<>();
    for (ASTCDClass astcdClass : list){
      for (AssocStruct assocStruct : helper.getTgtMap().get(astcdClass)){
        if (assocStruct.getSide().equals(ClassSide.Left)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
                || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
          ASTCDClass matched =
              helper.findMatchedSrc(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b);
          if (matched != null
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper
                  .getNotInstClassesTgt()
                  .contains(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)) {
            set.add(matched);
          }
        }
        if (assocStruct.getSide().equals(ClassSide.Right)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
                || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
          ASTCDClass matched =
              helper.findMatchedSrc(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a);
          if (matched != null
              && !helper.getNotInstClassesSrc().contains(matched)
              && !helper
                  .getNotInstClassesTgt()
                  .contains(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)) {
            set.add(matched);
          }
        }
        for (AssocStruct assocStruct1 : helper.getAllOtherAssocsTgt(astcdClass)) {
          if (assocStruct1.getSide().equals(ClassSide.Left)) {
            ASTCDClass matched =
                helper.findMatchedSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a);
            if (matched != null
                && !helper.getNotInstClassesSrc().contains(matched)
                && !helper
                    .getNotInstClassesTgt()
                    .contains(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a)) {
              set.add(matched);
            }
          } else {
            ASTCDClass matched =
                helper.findMatchedSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b);
            if (matched != null
                && !helper.getNotInstClassesSrc().contains(matched)
                && !helper
                    .getNotInstClassesTgt()
                    .contains(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b)) {
              set.add(matched);
            }
          }
        }
      }
    }
    return new ArrayList<>(set);
  }

  // CHECKED
  // TODO: check again
  @Override
  public List<ASTCDClass> srcAssocExistsTgtNot() {
    // get List of AssocStructs from srcMap
    // for each class remove added assocs
    // for the remaining find matches in the tgtMap
    // if no matches found add to list
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()) {
      ASTCDClass matched = helper.findMatchedClass(astcdClass);
      if (matched != null && !helper.getNotInstClassesTgt().contains(matched)) {
        List<AssocStruct> assocStructs = helper.getSrcMap().get(astcdClass);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = addedAssocsForClass(astcdClass);
        copy.removeAll(added);
        if (srcAssocsExist(copy, matched)) {
          classes.add(astcdClass);
        }
      }
    }
    return new ArrayList<>(classes);
  }

  // CHECKED
  public List<AssocStruct> addedAssocsForClass(ASTCDClass astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      AssocStruct matched = helper.getAssocStructByUnmod(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  // CHECKED
  public List<AssocStruct> deletedAssocsForClass(ASTCDClass astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      AssocStruct matched = helper.getAssocStructByUnmodTgt(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  // CHECKED
  public boolean srcAssocsExist(List<AssocStruct> assocStructs, ASTCDClass astcdClass) {
    for (AssocStruct assocStruct : assocStructs) {
      ASTCDAssociation matched = findMatchedAssociation(assocStruct.getAssociation());
      if (matched != null && helper.getAssocStructByUnmodTgt(astcdClass, matched) == null) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean tgtAssocsExist(List<AssocStruct> assocStructs, ASTCDClass astcdClass) {
    for (AssocStruct assocStruct : assocStructs) {
      ASTCDAssociation matched = findMatchedAssociationSrc(assocStruct.getAssociation());
      if (matched != null && helper.getAssocStructByUnmod(astcdClass, matched) == null) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public ASTCDAssociation findMatchedAssociation(ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs) {
      if (pair.a.equals(association)) {
        return pair.b;
      }
    }
    return null;
  }

  // CHECKED
  public ASTCDAssociation findMatchedAssociationSrc(ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs) {
      if (pair.b.equals(association)) {
        return pair.a;
      }
    }
    return null;
  }

  // CHECKED
  public List<ASTCDClass> tgtAssocsExistsSrcNot() {
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getTgtMap().keySet()){
      ASTCDClass matched = helper.findMatchedSrc(astcdClass);
      if (matched != null
        && !helper.getNotInstClassesSrc().contains(matched)){
        List<AssocStruct> assocStructs = helper.getTgtMap().get(astcdClass);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = deletedAssocsForClass(astcdClass);
        copy.removeAll(added);
        if (tgtAssocsExist(copy, matched)) {
          classes.add(matched);
        }
      }
    }
    return new ArrayList<>(classes);
  }

  // CHECKED
  @Override
  public AssocDiffs getAssocDiffs() {
    List<ASTCDClass> srcAssocExistsTgtNot = srcAssocExistsTgtNot();
    List<ASTCDClass> tgtAssocExistsSrcNot = tgtAssocsExistsSrcNot();
    List<ASTCDClass> mixed = new ArrayList<>();
    for (ASTCDClass astcdClass : srcAssocExistsTgtNot) {
      if (tgtAssocExistsSrcNot.contains(astcdClass)) {
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

  // CHECKED
  @Override
  public List<ASTCDClass> hasDiffSuper() {
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()) {
      ASTCDClass matchedClass = helper.findMatchedClass(astcdClass);
      if (matchedClass != null && !helper.getNotInstClassesTgt().contains(matchedClass)) {
        if (hasDiffSuper(astcdClass)) {
          list.add(astcdClass);
        }
      }
    }
    return list;
  }

  // CHECKED
  public boolean hasDiffSuper(ASTCDClass astcdClass) {
    ASTCDClass oldClass = helper.findMatchedClass(astcdClass);
    List<ASTCDClass> oldCLasses = getSuperClasses(tgtCD, oldClass);
    List<ASTCDClass> newClasses = getSuperClasses(srcCD, astcdClass);
    for (ASTCDClass class1 : oldCLasses) {
      boolean foundMatch = false;
      for (ASTCDClass class2 : newClasses) {
        if (helper.findMatchedSrc(class1) == class2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return true;
      }
    }
    for (ASTCDClass class1 : newClasses) {
      boolean foundMatch = false;
      for (ASTCDClass class2 : oldCLasses) {
        if (helper.findMatchedClass(class1) == class2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return true;
      }
    }
    return false;
  }

  /*--------------------------------------------------------------------*/

  /**
   * Adds matched CD types from the computed matching map to their respective type lists (classes,
   * enums, or interfaces). It iterates through the keys (source CD types) of the computed matching
   * map and creates pairs with their corresponding target CD types. These pairs are then added to
   * the respective type lists.
   *
   * @param computedMatchingMapTypes A map containing matched CD types between source and target
   *     CDs.
   */
  public void addAllMatchedTypes(Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    for (ASTCDType x : computedMatchingMapTypes.keySet()) {
      if (x instanceof ASTCDClass) {
        matchedClasses.add(
            new Pair<>((ASTCDClass) x, (ASTCDClass) computedMatchingMapTypes.get(x)));
      }
      if (x instanceof ASTCDEnum) {
        matchedEnums.add(new Pair<>((ASTCDEnum) x, (ASTCDEnum) computedMatchingMapTypes.get(x)));
      }
      if (x instanceof ASTCDInterface) {
        matchedInterfaces.add(
            new Pair<>((ASTCDInterface) x, (ASTCDInterface) computedMatchingMapTypes.get(x)));
      }
    }
  }

  /**
   * Adds matched associations from the computed matching map to the 'matchedAssocs' list. It
   * iterates through the keys (source associations) of the computed matching map and creates pairs
   * with their corresponding target associations. These pairs are then added to the 'matchedAssocs'
   * list.
   *
   * @param computedMatchingMapAssocs A map containing matched associations between source and
   *     target CDs.
   */
  public void addAllMatchedAssocs(
      Map<ASTCDAssociation, ASTCDAssociation> computedMatchingMapAssocs) {
    for (ASTCDAssociation x : computedMatchingMapAssocs.keySet()) {
      matchedAssocs.add(new Pair<>(x, computedMatchingMapAssocs.get(x)));
    }
  }

  /**
   * Adds changed type differences from the matched classes and enums in the source and target CDs
   * to the 'changedTypes' list. It iterates through the matched classes and enums, calculates the
   * differences between them, and adds the differences to the 'baseDiff' list. If differences are
   * found, the type differences are added to the 'changedTypes' list.
   */
  public void addAllChangedTypes() {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD, helper);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
    for(Pair<ASTCDEnum, ASTCDEnum> pair : matchedEnums){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD, helper);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
  }

  /**
   * Adds changed association differences from the matched associations in the source and target CDs
   * to the 'changedAssocs' list. It iterates through the matched associations and calculates the
   * differences between them, adding the differences to the 'baseDiff' list. If differences are
   * found, the association differences are added to the 'changedAssocs' list.
   */
  public void addAllChangedAssocs() {
    for(Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs){
      CDAssocDiff assocDiff = new CDAssocDiff(pair.a, pair.b, srcCD, tgtCD, helper);
      if(!assocDiff.getBaseDiff().isEmpty()){
        changedAssocs.add(assocDiff);
        baseDiff.addAll(assocDiff.getBaseDiff());
      }
    }
  }

  /**
   * Adds added class types from the source CD to the 'addedClasses' list based on matching maps
   * between source and target CD types. It identifies the class types in the source CD that do not
   * have a match in the target CD.
   *
   * @param srcCD The source CD.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CDs.
   */
  public void addAllAddedClasses(
      ASTCDCompilationUnit srcCD, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    List<ASTCDClass> tmp = new ArrayList<>(srcCD.getCDDefinition().getCDClassesList());
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      if (computedMatchingMapTypes.containsKey(srcClass)) {
        tmp.remove(srcClass);
      }
    }
    addedClasses.addAll(tmp);
    if (!tmp.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_CLASS)) {
      baseDiff.add(DiffTypes.ADDED_CLASS);
    }
  }

  /**
   * Adds deleted class types in the target CD to the 'deletedClasses' list based on matching maps
   * between source and target CD types. It identifies the class types in the target CD that do not
   * have a match in the source CD.
   *
   * @param tgtCD The target CD.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CDs.
   */
  public void addAllDeletedClasses(
      ASTCDCompilationUnit tgtCD, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    List<ASTCDClass> tmp = new ArrayList<>(tgtCD.getCDDefinition().getCDClassesList());
    for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
      if (computedMatchingMapTypes.containsValue(tgtClass)) {
        tmp.remove(tgtClass);
      }
    }
    deletedClasses.addAll(tmp);
    if (!tmp.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_CLASS)) {
      baseDiff.add(DiffTypes.DELETED_CLASS);
    }
  }

  /**
   * Adds added enumeration types in the source CD to the 'addedEnums' list based on matching maps
   * between source and target CD types. It identifies the enumeration types in the source CD that
   * do not have a match in the target CD.
   *
   * @param srcCD The source CD.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CDs.
   */
  public void addAllAddedEnums(
      ASTCDCompilationUnit srcCD, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    List<ASTCDEnum> tmp = new ArrayList<>(srcCD.getCDDefinition().getCDEnumsList());
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      if (computedMatchingMapTypes.containsKey(srcEnum)) {
        tmp.remove(srcEnum);
      }
    }
    addedEnums.addAll(tmp);
    if (!tmp.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_ENUM)) {
      baseDiff.add(DiffTypes.ADDED_ENUM);
    }
  }

  /**
   * Adds deleted enumeration types in the target CD to the 'deletedEnums' list based on matching
   * maps between source and target CD types. It identifies the enumeration types in the target CD
   * that do not have a match in the source CD.
   *
   * @param tgtCD The target CD.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CDs.
   */
  public void addAllDeletedEnums(
      ASTCDCompilationUnit tgtCD, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    List<ASTCDEnum> tmp = new ArrayList<>(tgtCD.getCDDefinition().getCDEnumsList());
    for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
      if (computedMatchingMapTypes.containsValue(tgtEnum)) {
        tmp.remove(tgtEnum);
      }
    }
    deletedEnums.addAll(tmp);
    if (!tmp.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_ENUM)) {
      baseDiff.add(DiffTypes.DELETED_ENUM);
    }
  }

  /**
   * Adds added association relationships in the source CD to the 'addedAssocs' list based on
   * matching maps between source and target CD associations. It identifies the associations in the
   * source CD that do not have a match in the target CD.
   *
   * @param srcCD The source CD.
   * @param computedMatchingMapAssocs A map of matched CD associations between source and target
   *     CDs.
   */
  public void addAllAddedAssocs(
      ASTCDCompilationUnit srcCD,
      Map<ASTCDAssociation, ASTCDAssociation> computedMatchingMapAssocs) {
    List<ASTCDAssociation> addedSrcAssocs =
        new ArrayList<>(srcCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      if (computedMatchingMapAssocs.containsKey(srcAssoc)) {
        addedSrcAssocs.remove(srcAssoc);
      }
    }
    addedAssocs.addAll(addedSrcAssocs);
    if (!addedSrcAssocs.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_ASSOCIATION)) {
      baseDiff.add(DiffTypes.ADDED_ASSOCIATION);
    }
  }

  /**
   * Adds deleted association relationships in the target CD to the 'deletedAssocs' list based on
   * matching maps between source and target CD associations. It identifies the associations in the
   * target CD that do not have a match in the source CD.
   *
   * @param tgtCD The target CD.
   * @param computedMatchingMapAssocs A map of matched CD associations between source and target
   *     CDs.
   */
  public void addAllDeletedAssocs(
      ASTCDCompilationUnit tgtCD,
      Map<ASTCDAssociation, ASTCDAssociation> computedMatchingMapAssocs) {
    List<ASTCDAssociation> deletedTgtAssocs =
        new ArrayList<>(tgtCD.getCDDefinition().getCDAssociationsList());
    for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
      if (computedMatchingMapAssocs.containsValue(tgtAssoc)) {
        deletedTgtAssocs.remove(tgtAssoc);
      }
    }
    deletedAssocs.addAll(deletedTgtAssocs);
    if (!deletedTgtAssocs.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_ASSOCIATION)) {
      baseDiff.add(DiffTypes.DELETED_ASSOCIATION);
    }
  }

  /**
   * Adds added inheritance relationships to the 'addedInheritance' list based on matching maps
   * between source and target CD classes. It identifies the added superclasses for each matched
   * source class.
   *
   * @param srcCDScope The scope of the source CD.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CDs.
   */
  public void addAllAddedInheritance(
      ICD4CodeArtifactScope srcCDScope, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      List<ASTCDType> addedInh = new ArrayList<>(getDirectSuperClasses(pair.a, srcCDScope));
      for (ASTCDType srcType : getDirectSuperClasses(pair.a, srcCDScope)) {
        if (computedMatchingMapTypes.containsKey(srcType)) {
          addedInh.remove(srcType);
        }
      }
      addedInheritance.add(new Pair<>(pair.a, addedInh));
      if (!addedInh.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_INHERITANCE)) {
        baseDiff.add(DiffTypes.ADDED_INHERITANCE);
      }
    }
  }

  /**
   * Adds deleted inheritance relationships to the 'deletedInheritance' list based on matching maps
   * between source and target CD classes. It identifies the deleted superclasses for each matched
   * target class.
   *
   * @param tgtCDScope The target CD scope.
   * @param computedMatchingMapTypes A map of matched CD types between source and target CD.
   */
  public void addAllDeletedInheritance(
      ICD4CodeArtifactScope tgtCDScope, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      List<ASTCDType> deletedInh = new ArrayList<>(getDirectSuperClasses(pair.b, tgtCDScope));
      for (ASTCDType tgtType : getDirectSuperClasses(pair.b, tgtCDScope)) {
        if (computedMatchingMapTypes.containsValue(tgtType)) {
          deletedInh.remove(tgtType);
        }
      }
      deletedInheritance.add(new Pair<>(pair.a, deletedInh));
      if (!deletedInh.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_INHERITANCE)) {
        baseDiff.add(DiffTypes.DELETED_INHERITANCE);
      }
    }
  }

  /**
   * Loads various lists of differences between source and target CD and scopes. This method
   * populates lists for matched types, associations, changed types, changed associations, added
   * classes, deleted classes, added enums, deleted enums, added associations, deleted associations,
   * added inheritance relationships, and deleted inheritance relationships.
   *
   * @param srcCD The source CD.
   * @param tgtCD The target CD.
   * @param srcCDScope The source CD scope.
   * @param tgtCDScope The target CD scope.
   */
  private void loadAllLists(
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      ICD4CodeArtifactScope srcCDScope,
      ICD4CodeArtifactScope tgtCDScope) {
    addAllMatchedTypes(computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllMatchedAssocs(
        computeMatchingMapAssocs(srcCD.getCDDefinition().getCDAssociationsList(), srcCD, tgtCD));
    addAllChangedTypes();
    addAllChangedAssocs();
    addAllAddedClasses(srcCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllDeletedClasses(tgtCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllAddedEnums(srcCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllDeletedEnums(tgtCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllAddedAssocs(
        srcCD,
        computeMatchingMapAssocs(srcCD.getCDDefinition().getCDAssociationsList(), srcCD, tgtCD));
    addAllDeletedAssocs(
        tgtCD,
        computeMatchingMapAssocs(tgtCD.getCDDefinition().getCDAssociationsList(), srcCD, tgtCD));
    addAllAddedInheritance(srcCDScope, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllDeletedInheritance(tgtCDScope, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
  }
}
