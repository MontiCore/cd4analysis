package de.monticore.cddiff.syndiff;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper;
import de.monticore.cddiff.syn2semdiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.syn2semdiff.odgen.Syn2SemDiffHelper.*;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;

/**
 * This is the core class for semantic differencing. It contains the results of the syntactic analysis as attributes.
 * All changed types and associations are stored in lists. The class is used to find the semantic differences and to
 * merge them into lists of differences. Furthermore, in this class the overlapping and duplicated associations are
 * handled.
 */
public class CDSyntaxDiff extends SyntaxDiffHelper implements ICDSyntaxDiff {
  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit tgtCD;
  private List<CDTypeDiff> changedTypes;
  private List<CDAssocDiff> changedAssocs;
  private List<ASTCDClass> addedClasses;
  private List<ASTCDClass> deletedClasses;
  private List<ASTCDEnum> addedEnums;
  private List<ASTCDEnum> deletedEnums;
  private List<ASTCDClass> addedClassesSem;
  private List<ASTCDClass> deletedClassesSem;
  private List<ASTCDAssociation> addedAssocs;
  private List<ASTCDAssociation> deletedAssocs;
  private List<Pair<ASTCDType, List<ASTCDType>>> addedInheritance;
  private List<Pair<ASTCDType, List<ASTCDType>>> deletedInheritance;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  private List<DiffTypes> baseDiff;
  List<ASTCDType> srcCDTypes;
  ICD4CodeArtifactScope scopeSrcCD, scopeTgtCD;

  public Syn2SemDiffHelper helper;

  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDDiffUtil.refreshSymbolTable(srcCD);
    CDDiffUtil.refreshSymbolTable(tgtCD);
    helper = new Syn2SemDiffHelper();//Don't change the order of the calls!
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
    this.addedClassesSem = new ArrayList<>();
    this.deletedClassesSem = new ArrayList<>();
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
    helper.setMatchedAssocs(matchedAssocs);
    helper.setDeletedAssocs(deletedAssocs);
    helper.setAddedAssocs(addedAssocs);
    helper.setMatchedInterfaces(matchedInterfaces);
    helper.filterMatched();
    helper.setDiffs(changedAssocs);
    helper.setMatcher();
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

  public Syn2SemDiffHelper getHelper() {
    return helper;
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
      List<ASTCDClass> classesToCheck = new ArrayList<>();
      for (ASTCDClass value : helper.getSrcSubMap().get(astcdClass)) {
        classesToCheck.add(value);
      }
      List<ASTCDAttribute> attributes = astcdClass.getCDAttributeList();
      for (ASTCDClass classToCheck : classesToCheck) {
        for (ASTCDAttribute attribute : attributes) {
          if (!helper.isAttContainedInClass(attribute, classToCheck)) {
            Set<ASTCDClass> classes =
                CDDiffUtil.getAllSuperclasses(
                        classToCheck, helper.getSrcCD().getCDDefinition().getCDClassesList());
            classes.remove(astcdClass);
            boolean isContained = false;
            for (ASTCDClass superOfSub : classes) {
              if (helper.isAttContainedInClass(attribute, superOfSub)) {
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
  public Set<Pair<ASTCDType, Set<ASTCDType>>> deletedInheritance() {
    Set<Pair<ASTCDType, Set<ASTCDType>>> diff = new HashSet<>();
    for (Pair<ASTCDType, List<ASTCDType>> struc : deletedInheritance) {
      Optional<ASTCDType> src = helper.findMatchedTypeSrc(struc.a);
      if (src.isPresent()
        && !helper.getNotInstClassesSrc().contains(src.get())
        && !helper.getNotInstClassesTgt().contains(struc.a)) {
        List<ASTCDType> superClasses = struc.b;//deleted superclasses from tgtCD
        Set<ASTCDType> currentDiff = new HashSet<>();
        for (ASTCDType superClass : superClasses) {
          if (!helper.getNotInstClassesTgt().contains(superClass)
            && isInheritanceDeleted(superClass, src.get())) {
            currentDiff.add(superClass);
          }
        }
        if (!currentDiff.isEmpty()) {
          diff.add(new Pair<>(helper.findMatchedTypeSrc(struc.a).get(), currentDiff));
        }
      }
    }
    return diff;
  }

  // CHECKED
  @Override
  public boolean isInheritanceDeleted(ASTCDType superClassTgt, ASTCDType subClassSrc) {
    if (superClassTgt instanceof ASTCDClass) {
      Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = helper.getAllAttrTgt((ASTCDClass) superClassTgt);
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.isAttContainedInClass(attribute, subClassSrc)) {
          Set<ASTCDType> astcdClassList =
            getAllSuper(subClassSrc, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (!helper.getNotInstClassesSrc().contains(type)) {
              if (helper.isAttContainedInClass(attribute, type)) {
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
    for (AssocStruct tgtStruct : helper.getTgtMap().get(superClassTgt)) {
      if (!areZeroAssocs(tgtStruct, tgtStruct)) {
        for (AssocStruct srcAssoc : helper.getSrcMap().get(subClassSrc)) {
          if (helper.sameAssociationTypeSrcTgt(srcAssoc, tgtStruct)) {
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
    for (AssocStruct otherStruct : helper.getAllOtherAssocsTgt(superClassTgt)) {
      boolean isContained1 = false;
      for (AssocStruct srcStruct : helper.getAllOtherAssocsSrc(subClassSrc)) {
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
  public Set<Pair<ASTCDType, Set<ASTCDType>>> addedInheritance() {
    Set<Pair<ASTCDType, Set<ASTCDType>>> diff = new HashSet<>();
    for (Pair<ASTCDType, List<ASTCDType>> struc : addedInheritance) {
      List<ASTCDType> superClasses = struc.b;
      Set<ASTCDType> currentDiff = new HashSet<>();
      for (ASTCDType superClass : superClasses) {
        if (!helper.getNotInstClassesSrc().contains(struc.a)
            && helper.findMatchedTypeTgt(struc.a).isPresent()
            && !helper.getNotInstClassesTgt().contains(helper.findMatchedTypeTgt(struc.a).get())
            && !helper.getNotInstClassesSrc().contains(superClass)
            && isInheritanceAdded(superClass, struc.a)) {
          currentDiff.add(superClass);
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
    Set<Pair<ASTCDType, Set<ASTCDType>>> added = addedInheritance();
    Set<Pair<ASTCDType, Set<ASTCDType>>> deleted = deletedInheritance();
    Set<InheritanceDiff> set = new HashSet<>();
    for (Pair<ASTCDType, Set<ASTCDType>> pair : added) {
      InheritanceDiff diff =
          new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedTypeTgt(pair.a).get()));
      diff.setNewSuperClasses(new ArrayList<>(pair.b));
      set.add(diff);
    }
    for (Pair<ASTCDType, Set<ASTCDType>> pair : deleted) {
      boolean holds = false;
      for (InheritanceDiff diff : set) {
        if (pair.a.equals(diff.getAstcdClasses().b)) {
          diff.setDeletedSuperClasses(new ArrayList<>(pair.b));
          holds = true;
          break;
        }
      }
      if (!holds) {
        InheritanceDiff diff =
            new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedTypeTgt(pair.a).get()));
        diff.setDeletedSuperClasses(new ArrayList<>(pair.b));
        set.add(diff);
      }
    }
    return set;
  }

  // CHECKED
  @Override
  public boolean isInheritanceAdded(ASTCDType astcdClass, ASTCDType subClass) {
    if (astcdClass instanceof ASTCDClass) {
      Pair<ASTCDType, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
      if (subClass instanceof ASTCDClass) {
        for (ASTCDAttribute attribute : allAtts.b) {
          boolean conditionSatisfied = false; // Track if the condition is satisfied
          if (!helper.getNotInstClassesSrc().contains(astcdClass)
            && !helper.isAttContainedInClassTgt(
            attribute, helper.findMatchedClass((ASTCDClass) subClass).get())) {
            Set<ASTCDType> astcdClassList =
              getAllSuper(
                helper.findMatchedClass((ASTCDClass) subClass).get(),
                (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
            for (ASTCDType type : astcdClassList) {
              if (type instanceof ASTCDClass
                && !helper.getNotInstClassesTgt().contains(type)
                && helper.isAttContainedInClassTgt(attribute, (ASTCDClass) type)) {
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
    }
    boolean isContained = false;
    Optional<ASTCDType> matched = helper.findMatchedTypeTgt(astcdClass);
    if (matched.isEmpty()){

    }

    for (AssocStruct newAssocs : helper.getSrcMap().get(astcdClass)) {
      for (AssocStruct srcStruct : helper.getTgtMap().get(helper.findMatchedTypeTgt(subClass).get())) {
        if (helper.sameAssociationTypeSrcTgt(newAssocs, srcStruct)) {
          isContained = true;
          break;
        }
      }
      if (!isContained) {
        return true;
      } else {
        isContained = false;
      }

    }
    for (AssocStruct newAssocs : helper.getAllOtherAssocsSrc(astcdClass)) {
      boolean isContained1 = false;
      for (AssocStruct oldAssocs : helper.getAllOtherAssocsTgt(helper.findMatchedTypeTgt(subClass).get())) {
        if (helper.sameAssociationTypeSrcTgt(newAssocs, oldAssocs)) {
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
  public List<ASTCDType> isAssocDeleted(ASTCDAssociation association, ASTCDType astcdClass) {
    ASTCDType isDeletedSrc = null;
    ASTCDType isDeletedTgt = null;
    Optional<AssocStruct> assocStruct = helper.getAssocStructByUnmodTgt(astcdClass, association);
    if (assocStruct.isPresent()) { // if assocStruc is null, then the association is deleted because of overlapping
      if (assocStruct.get().getSide().equals(ClassSide.Left)) {
        if (!(assocStruct.get().getAssociation().getRight().getCDCardinality().isMult()
            || assocStruct.get().getAssociation().getRight().getCDCardinality().isOpt())) {
          Optional<ASTCDType> matched = helper.findMatchedTypeSrc(astcdClass);
          Optional<ASTCDType> sub = helper.allSubclassesHaveIt(assocStruct.get(), astcdClass);
          if (!astcdClass.getModifier().isAbstract()
              && matched.isPresent()
              && !helper.getNotInstClassesTgt().contains(astcdClass)
              && !helper.getNotInstClassesSrc().contains(matched.get())
              && !helper.classHasAssociationTgtSrc(assocStruct.get(), matched.get())) {
            isDeletedSrc = matched.get();
          } else if (!helper.getNotInstClassesTgt().contains(astcdClass) && sub.isPresent()
              && !helper.getNotInstClassesSrc().contains(sub.get())
              && helper.findMatchedTypeSrc(sub.get()).isPresent()) {
            isDeletedSrc = helper.findMatchedTypeSrc(sub.get()).get();
          }
        }
        if (!(assocStruct.get().getAssociation().getLeft().getCDCardinality().isOpt()
            || assocStruct.get().getAssociation().getLeft().getCDCardinality().isMult())) {
          ASTCDType right = getConnectedTypes(assocStruct.get().getAssociation(), helper.getTgtCD()).b;
          Optional<ASTCDType> matched = helper.findMatchedTypeSrc(right);
          Optional<ASTCDType> sub = helper.allSubClassesAreTargetTgtSrc(assocStruct.get(), right);
          if (!right.getModifier().isAbstract()
              && matched.isPresent()
              && !helper.getNotInstClassesTgt().contains(right)
              && !helper.getNotInstClassesSrc().contains(matched.get())
              && !helper.classIsTargetTgtSrc(assocStruct.get(), matched.get())) {
            isDeletedTgt = matched.get();
          } else if (!helper.getNotInstClassesTgt().contains(right) && sub.isPresent()
              && !helper.getNotInstClassesSrc().contains(sub.get())
              && helper.findMatchedTypeSrc(sub.get()).isPresent()) {
            isDeletedTgt = helper.findMatchedTypeSrc(sub.get()).get();
          }
        }
      } else {
        if (!(assocStruct.get().getAssociation().getLeft().getCDCardinality().isMult()
            || assocStruct.get().getAssociation().getLeft().getCDCardinality().isOpt())) {
          Optional<ASTCDType> matched = helper.findMatchedTypeSrc(astcdClass);
          Optional<ASTCDType> sub = helper.allSubclassesHaveIt(assocStruct.get(), astcdClass);
          if (!astcdClass.getModifier().isAbstract()
              && matched.isPresent()
              && !helper.getNotInstClassesTgt().contains(astcdClass)
              && !helper.getNotInstClassesSrc().contains(matched.get())
              && !helper.classHasAssociationTgtSrc(assocStruct.get(), matched.get())) {
            isDeletedSrc = matched.get();
          } else if (!helper.getNotInstClassesTgt().contains(astcdClass) && sub.isPresent()
              && !helper.getNotInstClassesSrc().contains(sub.get())
              && helper.findMatchedTypeSrc(sub.get()).isPresent()) {
            isDeletedSrc = helper.findMatchedTypeSrc(sub.get()).get();
          }
        }
        if (!(assocStruct.get().getAssociation().getRight().getCDCardinality().isOpt()
            || assocStruct.get().getAssociation().getRight().getCDCardinality().isMult())) {
          ASTCDType left = getConnectedTypes(assocStruct.get().getAssociation(), helper.getTgtCD()).a;
          Optional<ASTCDType> matched = helper.findMatchedTypeSrc(left);
          Optional<ASTCDType> sub = helper.allSubClassesAreTargetTgtSrc(assocStruct.get(), left);
          if (!left.getModifier().isAbstract()
              && matched.isPresent()
              && !helper.getNotInstClassesTgt().contains(left)
              && !helper.getNotInstClassesSrc().contains(matched.get())
              && !helper.classIsTargetTgtSrc(assocStruct.get(), matched.get())) {
            isDeletedTgt = matched.get();
          } else if (!helper.getNotInstClassesTgt().contains(left) && sub.isPresent()
              && !helper.getNotInstClassesSrc().contains(sub.get())
              && helper.findMatchedTypeSrc(sub.get()).isPresent()) {
            isDeletedTgt = helper.findMatchedTypeSrc(sub.get()).get();
          }
        }
      }
    }
    List<ASTCDType> list = new ArrayList<>();
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
  public List<ASTCDType> isAssocAdded(ASTCDAssociation association) {
    ASTCDType isAddedSrc = null;
    ASTCDType isAddedTgt = null;
    ASTCDType classToUse;
    ASTCDType otherSide;
    Pair<ASTCDType, ASTCDType> pair =
        getConnectedTypes(association, helper.getSrcCD());
    Optional<AssocStruct> assocStruct = helper.getAssocStructByUnmod(pair.a, association);
    classToUse = pair.a;
    otherSide = pair.b;
    if (assocStruct.isEmpty()) {
      assocStruct = helper.getAssocStructByUnmod(pair.b, association);
      classToUse = pair.b;
      otherSide = pair.a;
    }
    if (assocStruct.isPresent()) {
      Optional<ASTCDType> matched = helper.findMatchedTypeTgt(classToUse);
      Optional<ASTCDType> sub = helper.allSubClassesHaveItTgt(assocStruct.get(), classToUse);
      if (!classToUse.getModifier().isAbstract()
          && matched.isPresent()
          && !helper.getNotInstClassesTgt().contains(matched.get())
          && !helper.getNotInstClassesSrc().contains(classToUse)
          && !helper.classHasAssociationSrcTgt(assocStruct.get(), matched.get())) {
        isAddedSrc = classToUse;
      } else if (!helper.getNotInstClassesSrc().contains(classToUse) && sub.isPresent()) {
        isAddedSrc = sub.get();
      }
      Optional<ASTCDType> matched2 = helper.findMatchedTypeTgt(otherSide);
      Optional<ASTCDType> sub2 = helper.allSubClassesAreTgtSrcTgt(assocStruct.get(), otherSide);
      if (!otherSide.getModifier().isAbstract()
          && matched2.isPresent()
          && !helper.getNotInstClassesTgt().contains(matched2.get())
          && !helper.getNotInstClassesSrc().contains(otherSide)
          && !helper.classIsTgtSrcTgt(assocStruct.get(), matched2.get())) {
        isAddedTgt = otherSide;
      } else if (!helper.getNotInstClassesSrc().contains(otherSide) && sub2.isPresent()) {
        isAddedTgt = sub2.get();
      }
    }
    List<ASTCDType> list = new ArrayList<>();
    if (isAddedSrc != null) {
      list.add(isAddedSrc);
    }
    if (isAddedTgt != null) {
      list.add(isAddedTgt);
    }
    return list;
  }

  @Override
  public void findOverlappingAssocs() {
    Set<ASTCDType> srcToDelete = new HashSet<>();
    Set<Pair<ASTCDType, ASTCDRole>> srcAssocsToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> srcAssocsToMerge = new HashSet<>();
    Set<DeleteStruct> srcAssocsToMergeWithDelete = new HashSet<>();
    Set<ASTCDType> tgtToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> tgtAssocsToMerge = new HashSet<>();
    Set<Pair<ASTCDType, ASTCDRole>> tgtAssocsToDelete = new HashSet<>();
    Set<DeleteStruct> tgtAssocsToMergeWithDelete = new HashSet<>();
    for (ASTCDType astcdClass : helper.getSrcMap().keySet()) {
      Set<Pair<AssocStruct, AssocStruct>> toCheck = new HashSet<>();
      OverlappingAssocsDirect pairs = helper.computeDirectForType(astcdClass, helper.getSrcMap(), helper.getSrcCD());
      toCheck.addAll(pairs.getDirectOverlappingAssocs());
      toCheck.addAll(pairs.getDirectAssocsNoRelation());
      for (Pair<AssocStruct, AssocStruct> pair : toCheck) {
        AssocStruct association = pair.a;
        AssocStruct superAssoc = pair.b;
        if ((helper.sameAssocStruct(association, superAssoc)
          || helper.sameAssocStructInReverse(association, superAssoc))
          && !helper.isAdded(
          association, superAssoc, astcdClass, srcAssocsToMergeWithDelete)) {
          srcAssocsToMergeWithDelete.add(
            new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
          && helper.inInheritanceRelation(association, superAssoc, helper.getSrcCD())) {
          srcAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
          && !helper.inInheritanceRelation(association, superAssoc, helper.getSrcCD())
          && !getConnectedTypes(association.getAssociation(), srcCD)
          .equals(getConnectedTypes(superAssoc.getAssociation(), srcCD))) {
          if (areZeroAssocs(association, superAssoc)) {
            srcAssocsToDelete.add(
              new Pair<>(astcdClass, getConflict(association, superAssoc)));
          } else {
            srcToDelete.add(astcdClass);
          }
        }
      }
    }

    for (ASTCDType astcdClass : helper.getTgtMap().keySet()) {
      Set<Pair<AssocStruct, AssocStruct>> toCheck = new HashSet<>();
      OverlappingAssocsDirect pairs = helper.computeDirectForType(astcdClass, helper.getTgtMap(), helper.getTgtCD());
      toCheck.addAll(pairs.getDirectOverlappingAssocs());
      toCheck.addAll(pairs.getDirectAssocsNoRelation());
      for (Pair<AssocStruct, AssocStruct> pair : toCheck){
        AssocStruct association = pair.a;
        AssocStruct superAssoc = pair.b;
        if (isInConflict(association, superAssoc)
          && helper.inInheritanceRelation(association, superAssoc, helper.getTgtCD())) {
          tgtAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
          && !helper.inInheritanceRelation(association, superAssoc, helper.getTgtCD())) {
          if (areZeroAssocs(association, superAssoc)) {
            tgtAssocsToDelete.add(
              new Pair<>(astcdClass, getConflict(association, superAssoc)));
          } else {
            tgtToDelete.add(astcdClass);
          }
        } else if (helper.sameAssocStruct(association, superAssoc)
          || helper.sameAssocStructInReverse(association, superAssoc)) {
          tgtAssocsToMergeWithDelete.add(
            new DeleteStruct(association, superAssoc, astcdClass));
        }
      }
    }
    for (ASTCDType astcdClass : srcToDelete) {
      helper.updateSrc(astcdClass);
      helper.getSrcMap().removeAll(astcdClass);
      helper.deleteOtherSideSrc(astcdClass);
      for (ASTCDType subClass : helper.getSrcSubMap().get(astcdClass)) {
        helper.updateSrc(subClass);
        helper.getSrcMap().removeAll(subClass);
        helper.deleteOtherSideSrc(subClass);
      }
    }
    for (DeleteStruct pair : srcAssocsToMergeWithDelete) {
      if (!helper.getNotInstClassesSrc().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruct pair : srcAssocsToMergeWithDelete) {
      if (!pair.getSuperAssoc().isSuperAssoc()
        && pair.getSuperAssoc().getDirection().equals(AssocDirection.BiDirectional)) {
        helper.deleteAssocOtherSideSrc(pair.getSuperAssoc());
        helper.deleteAssocsFromSubSrc(pair.getSuperAssoc(), getConnectedTypes(pair.getSuperAssoc().getAssociation(), srcCD).b);
        helper.deleteAssocsFromSubSrc(pair.getSuperAssoc(), getConnectedTypes(pair.getSuperAssoc().getAssociation(), srcCD).a);
      }
      helper.getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : srcAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (Pair<ASTCDType, ASTCDRole> pair : srcAssocsToDelete) {
      helper.deleteAssocsFromSrc(pair.a, pair.b);
    }
    for (ASTCDType astcdClass : tgtToDelete) {
      helper.updateTgt(astcdClass);
      helper.getTgtMap().removeAll(astcdClass);
      helper.deleteOtherSideTgt(astcdClass);
      for (ASTCDType subClass : helper.getTgtSubMap().get(astcdClass)) {
        helper.deleteOtherSideTgt(subClass);
        helper.getTgtMap().removeAll(subClass);
      }
    }
    for (DeleteStruct pair : tgtAssocsToMergeWithDelete) {
      if (!helper.getNotInstClassesTgt().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruct pair : tgtAssocsToMergeWithDelete) {
      if (!pair.getSuperAssoc().isSuperAssoc()
        && pair.getSuperAssoc().getDirection().equals(AssocDirection.BiDirectional)) {
        helper.deleteAssocOtherSideTgt(pair.getSuperAssoc());
        helper.deleteAssocFromSubTgt(pair.getSuperAssoc(), getConnectedTypes(pair.getSuperAssoc().getAssociation(), tgtCD).b);
        helper.deleteAssocFromSubTgt(pair.getSuperAssoc(), getConnectedTypes(pair.getSuperAssoc().getAssociation(), tgtCD).a);
      }
      helper.getTgtMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : tgtAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (Pair<ASTCDType, ASTCDRole> pair : tgtAssocsToDelete) {
      helper.deleteAssocsFromTgt(pair.a, pair.b);
    }
    helper.deleteCompositions();
    helper.reduceMaps();
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDAssociation, List<ASTCDType>>> addedAssocList() {
    List<Pair<ASTCDAssociation, List<ASTCDType>>> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      List<ASTCDType> list = isAssocAdded(association);
      if (!list.isEmpty()) {
        associationList.add(new Pair<>(association, list));
      }
    }
    return associationList;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDAssociation, List<ASTCDType>>> deletedAssocList() {
    List<Pair<ASTCDAssociation, List<ASTCDType>>> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(association, tgtCD);
      if (association.getCDAssocDir().isBidirectional()) {
        List<ASTCDType> astcdClass = isAssocDeleted(association, pair.a);
        List<ASTCDType> astcdClass1 = isAssocDeleted(association, pair.b);
        if (helper.findMatchedTypeSrc(pair.a).isPresent() && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
        if (helper.findMatchedTypeSrc(pair.a).isPresent() && !astcdClass1.isEmpty()) {
          list.add(new Pair<>(association, astcdClass1));
        }
      } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        List<ASTCDType> astcdClass = isAssocDeleted(association, pair.b);
        if (helper.findMatchedTypeSrc(pair.b).isPresent() && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
      } else {
        List<ASTCDType> astcdClass = isAssocDeleted(association, pair.a);
        if (helper.findMatchedTypeSrc(pair.a).isPresent() && !astcdClass.isEmpty()) {
          list.add(new Pair<>(association, astcdClass));
        }
      }
    }
    return list;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDType, ASTCDType>> addedClassList() {
    List<Pair<ASTCDType, ASTCDType>> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClassesSem) {
      ASTCDType result = isSupClass(astcdClass);
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
      } else if (!helper.getNotInstClassesSrc().contains(typeDiff.getSrcElem())) {
        if ((typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE)
            || typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER)
            ) && !typeDiff.changedAttribute().isEmpty()) {
          diff.setMemberDiff(typeDiff.changedAttribute());
          changed = true;
          List<Pair<ASTCDAttribute, ASTCDAttribute>> pairs = new ArrayList<>();
          for (Pair<ASTCDClass, ASTCDAttribute> attribute : typeDiff.changedAttribute()) {
            pairs.add(new Pair<>(attribute.b, typeDiff.getOldAttribute(attribute.b)));
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
        if (typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_CLASS_MODIFIER)) {
          Pair<Boolean, Boolean> changedStereotype = helper.stereotypeChange((ASTCDClass) typeDiff.getSrcElem(), (ASTCDClass) typeDiff.getTgtElem());
          if (changedStereotype.a) {
            diff.setChangedStereotype(typeDiff.isClassNeeded());
            changed = true;
          }
          if (changedStereotype.b) {
            diff.setChangedSingleton(true);
            changed = true;
          }
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
  public List<AssocDiffStruct> changedAssoc() {
    List<AssocDiffStruct> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs){
    }
    for (CDAssocDiff assocDiff : helper.getDiffs()) {
      Pair<AssocStruct, AssocStruct> matchedPairs =
          helper.getStructsForAssocDiff(assocDiff.getSrcElem(), assocDiff.getTgtElem(), assocDiff.isReversed());
      if (matchedPairs.a == null || matchedPairs.b == null) {
        continue;
      }
      Pair<ASTCDType, ASTCDType> pairDef =
          getConnectedTypes(assocDiff.getSrcElem(), srcCD);
      Pair<ASTCDType, ASTCDType> pair;
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
        AssocDiffStruct diff = new AssocDiffStruct();
        diff.setAssociation(assocDiff.getSrcElem());
        boolean changed = false;
        assocDiff.setStructs();
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
          List<Pair<ClassSide, ASTCDRole>> changedRoles = assocDiff.getRoleDiff().b;
          if (!changedRoles.isEmpty()) {
            diff.setChangedRoleNames(changedRoles);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
          if (assocDiff.isDirectionChanged()) {
            diff.setChangedDir(true);
            changed = true;
          }
        }

        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
          if (!helper.inheritanceTgt(matchedPairs.a, matchedPairs.b)){
            List<ASTCDType> added = isAssocAdded(matchedPairs.a.getAssociation());
            if (!added.isEmpty()){
              diff.setChangedTgt(added.get(0));
              changed = true;
            } else {
              Pair<ASTCDType, ASTCDType> connected = getConnectedTypes(matchedPairs.b.getAssociation(), helper.getTgtCD());
              List<ASTCDType> deleted = isAssocDeleted(matchedPairs.b.getAssociation(), connected.a);
              List<ASTCDType> deleted1 = isAssocDeleted(matchedPairs.b.getAssociation(), connected.b);
              if (!deleted.isEmpty()){
                diff.setChangedTgt(deleted.get(0));
                changed = true;
              } else if (!deleted1.isEmpty()){
                diff.setChangedTgt(deleted1.get(0));
                changed = true;
              }
            }
          } else {
            ASTCDType change = assocDiff.changedTgt();
            if (change != null) {
              diff.setChangedTgt(change);
              changed = true;
            }
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          ASTCDType changedSrc = assocDiff.changedSrc();
          ASTCDType changedTgt = assocDiff.changedTgt();
          if (changedSrc != null) {
            diff.setChangedSrc(changedSrc);
            changed = true;
          }
          if (changedTgt != null) {
            diff.setChangedTgt(changedTgt);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
          ASTCDType change = assocDiff.changedSrc();
          if (change != null) {
            diff.setChangedSrc(change);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY)) {
          List<Pair<ClassSide, Integer>> changedCard = assocDiff.getCardDiff().b;
          if (!changedCard.isEmpty()) {
            diff.setChangedCard(changedCard);
            changed = true;
          }
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
  public List<ASTCDType> srcExistsTgtNot() {
    List<ASTCDType> list = new ArrayList<>();
    for (ASTCDType astcdClass : helper.getNotInstClassesTgt()) {
      Optional<ASTCDType> matched = helper.findMatchedTypeSrc(astcdClass);
      if (matched.isPresent() && !helper.getNotInstClassesSrc().contains(matched.get())) {
        list.add(matched.get());
      }
    }
    return list;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDClass, List<AssocStruct>>> srcAssocExistsTgtNot() {
    List<Pair<ASTCDClass, List<AssocStruct>>> allAddedAssocs = new ArrayList<>();//because of merging
    for (ASTCDType astcdType : helper.getSrcMap().keySet()) {
      Optional<ASTCDType> matched = helper.findMatchedTypeTgt(astcdType);
      if (matched.isPresent()
        && !helper.getNotInstClassesTgt().contains(matched.get())) {
        List<AssocStruct> assocStructs = helper.getSrcMap().get(astcdType);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = helper.addedAssocsForClass(astcdType);
        copy.removeAll(added);
        List<Pair<ASTCDClass, AssocStruct>> addedAssocs = helper.srcAssocsExist(copy, matched.get());
        allAddedAssocs.addAll(helper.sortDiffs(addedAssocs));
      }
    }
    return allAddedAssocs;
  }

  // CHECKED
  @Override
  public List<Pair<ASTCDClass, List<AssocStruct>>> tgtAssocsExistsSrcNot() {
    List<Pair<ASTCDClass, List<AssocStruct>>> allDeletedAssocs = new ArrayList<>();//because of merging
    for (ASTCDType astcdType : helper.getTgtMap().keySet()){
      Optional<ASTCDType> matched = helper.findMatchedTypeSrc(astcdType);
      if (matched.isPresent()
        && !helper.getNotInstClassesSrc().contains(matched.get())){
        List<AssocStruct> assocStructs = helper.getTgtMap().get(astcdType);
        List<AssocStruct> copy = new ArrayList<>(assocStructs);
        List<AssocStruct> added = helper.deletedAssocsForClass(astcdType);
        copy.removeAll(added);
        List<Pair<ASTCDClass, AssocStruct>> deletedAssocs = helper.tgtAssocsExist(copy, matched.get());
        allDeletedAssocs.addAll(helper.sortDiffs(deletedAssocs));
      }
    }
    return new ArrayList<>(allDeletedAssocs);
  }

  // CHECKED
  @Override
  public List<AssocMatching> getAssocDiffs() {
    List<Pair<ASTCDClass, List<AssocStruct>>> srcAssocExistsTgtNot = srcAssocExistsTgtNot();
    List<Pair<ASTCDClass, List<AssocStruct>>> tgtAssocExistsSrcNot = tgtAssocsExistsSrcNot();
    List<AssocMatching> result = new ArrayList<>();
    Iterator<Pair<ASTCDClass, List<AssocStruct>>> iterator = tgtAssocExistsSrcNot.iterator();
    while (iterator.hasNext()) {
      Pair<ASTCDClass, List<AssocStruct>> tgt = iterator.next();
      Optional<Pair<ASTCDClass, List<AssocStruct>>> pair = helper.getPair(srcAssocExistsTgtNot, tgt.a);
      if (pair.isPresent()) {
        AssocMatching matching = new AssocMatching();
        matching.setClassToInstantiate(tgt.a);
        matching.setNotMatchedAssocsInSrc(pair.get().b);
        matching.setNotMatchedAssocsInTgt(tgt.b);
        result.add(matching);
        iterator.remove();
        srcAssocExistsTgtNot.remove(pair.get());
      }
    }
    for (Pair<ASTCDClass, List<AssocStruct>> pair : srcAssocExistsTgtNot) {
      AssocMatching matching = new AssocMatching();
      matching.setClassToInstantiate(pair.a);
      matching.setNotMatchedAssocsInSrc(pair.b);
      result.add(matching);
    }
    for (Pair<ASTCDClass, List<AssocStruct>> pair : tgtAssocExistsSrcNot) {
      AssocMatching matching = new AssocMatching();
      matching.setClassToInstantiate(pair.a);
      matching.setNotMatchedAssocsInTgt(pair.b);
      result.add(matching);
    }
    return result;
  }

  // CHECKED
  @Override
  public List<ASTCDType> hasDiffSuper() {
    List<ASTCDType> list = new ArrayList<>();
    for (Pair<ASTCDClass, ASTCDClass> pair : helper.getMatchedClasses()) {
      if (!helper.getNotInstClassesTgt().contains(pair.b)) {
        if (helper.hasDiffSuper(pair.a)) {
          list.add(pair.a);
        }
      }
    }
    for (Pair<ASTCDInterface, ASTCDInterface> pair : helper.getMatchedInterfaces()) {
      if (!helper.getNotInstClassesTgt().contains(pair.b)) {
        if (helper.hasDiffSuper(pair.a)) {
          list.add(pair.a);
        }
      }
    }
    return list;
  }

  public void addAllAddedClassesSem(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    boolean found = false;
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      for(ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()){
        if(srcClass.getName().equals(tgtClass.getName())){
          found = true;
          break;
        }
      }
      if(!found){
        addedClassesSem.add(srcClass);
      }
    }
    if (!addedClassesSem.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_CLASS)) {
      baseDiff.add(DiffTypes.ADDED_CLASS);
    }
  }

  public void addAllDeletedClassesSem(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    boolean found = false;
    for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
      for(ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()){
        if(srcClass.getName().equals(tgtClass.getName())){
          found = true;
          break;
        }
      }
      if(!found){
        deletedClassesSem.add(tgtClass);
      }
    }
    if (!deletedClassesSem.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_CLASS)) {
      baseDiff.add(DiffTypes.DELETED_CLASS);
    }
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
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD, srcCD, helper);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
    for(Pair<ASTCDEnum, ASTCDEnum> pair : matchedEnums){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, tgtCD, srcCD, helper);
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
   */
  public void addAllDeletedAssocs(ASTCDCompilationUnit tgtCD) {
    deletedAssocs.addAll(tgtCD.getCDDefinition().getCDAssociationsList());
    for(ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()){
      for(Pair<ASTCDAssociation,ASTCDAssociation> matchedAssoc : matchedAssocs){
        if(matchedAssoc.b.equals(tgtAssoc)){
          deletedAssocs.remove(tgtAssoc);
        }
      }
    }

    if (!deletedAssocs.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_ASSOCIATION)) {
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
  public void addAllAddedInheritance(ICD4CodeArtifactScope srcCDScope, ICD4CodeArtifactScope tgtCDScope, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    for (ASTCDClass srcClass : getSrcCD().getCDDefinition().getCDClassesList()) {
      List<ASTCDType> allSuperClassOfSrcClass = new ArrayList<>(getAllSuper(srcClass, srcCDScope));
      ASTCDType matchOfSrcClass;
      List<ASTCDType> allSuperClassOfTgtClass = new ArrayList<>();
      for (Map.Entry<ASTCDType, ASTCDType> entry : computedMatchingMapTypes.entrySet()) {
        if(entry.getKey().equals(srcClass)){
          matchOfSrcClass = entry.getValue();
          allSuperClassOfTgtClass.addAll(getAllSuper(matchOfSrcClass,tgtCDScope));
        }
      }

      for(ASTCDType srcSuper : getAllSuper(srcClass, srcCDScope)){
        for(ASTCDType tgtSuper : allSuperClassOfTgtClass){
          for(Pair<ASTCDClass,ASTCDClass> pair : matchedClasses){
            if(pair.a.equals(srcSuper) && pair.b.equals(tgtSuper)){
              allSuperClassOfSrcClass.remove(srcSuper);
            }
          }
        }
      }

      if(!allSuperClassOfSrcClass.isEmpty()){
        addedInheritance.add(new Pair<>(srcClass, allSuperClassOfSrcClass));
      }
      if (!addedInheritance.isEmpty() && !baseDiff.contains(DiffTypes.ADDED_INHERITANCE)) {
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
  public void addAllDeletedInheritance(ICD4CodeArtifactScope srcCDScope, ICD4CodeArtifactScope tgtCDScope, Map<ASTCDType, ASTCDType> computedMatchingMapTypes) {
    for (ASTCDClass tgtClass : getTgtCD().getCDDefinition().getCDClassesList()) {
      List<ASTCDType> allSuperClassOfTgtClass = new ArrayList<>(getAllSuper(tgtClass, tgtCDScope));
      List<ASTCDType> allSuperClassOfSrcClass = new ArrayList<>();
      for (Map.Entry<ASTCDType, ASTCDType> entry : computedMatchingMapTypes.entrySet()) {
        if(entry.getValue().equals(tgtClass)){
          allSuperClassOfSrcClass.addAll(getAllSuper(entry.getKey(),srcCDScope));
        }
      }

      for(ASTCDType tgtSuper : getAllSuper(tgtClass, srcCDScope)){
        for(ASTCDType srcSuper : allSuperClassOfSrcClass){
          for(Pair<ASTCDClass,ASTCDClass> pair : matchedClasses){
            if(pair.a.equals(srcSuper) && pair.b.equals(tgtSuper)){
              allSuperClassOfTgtClass.remove(tgtSuper);
            }
          }
        }
      }

      if(!allSuperClassOfTgtClass.isEmpty()){
        deletedInheritance.add(new Pair<>(tgtClass, allSuperClassOfTgtClass));
      }
      if (!deletedInheritance.isEmpty() && !baseDiff.contains(DiffTypes.DELETED_INHERITANCE)) {
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
    addAllAddedClassesSem(srcCD, tgtCD);
    addAllDeletedClassesSem(tgtCD, tgtCD);
    addAllAddedEnums(srcCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllDeletedEnums(tgtCD, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllAddedAssocs(
        srcCD,
        computeMatchingMapAssocs(srcCD.getCDDefinition().getCDAssociationsList(), srcCD, tgtCD));
    addAllDeletedAssocs(tgtCD);
    addAllAddedInheritance(srcCDScope, tgtCDScope, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
    addAllDeletedInheritance(srcCDScope, tgtCDScope, computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD));
  }
}
