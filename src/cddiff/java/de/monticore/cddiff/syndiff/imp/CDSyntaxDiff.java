package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.CardinalityStruc;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDSyntaxDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.*;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

public class CDSyntaxDiff implements ICDSyntaxDiff {
  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit tgtCD;
  private List<CDTypeDiff> changedClasses;
  private List<CDAssocDiff> changedAssocs;
  private List<ASTCDClass> addedClasses;
  private List<ASTCDClass> deletedClasses;
  private List<ASTCDEnum> addedEnums;
  private List<ASTCDEnum> deletedEnums;
  private List<ASTCDAssociation> addedAssocs;
  private List<ASTCDAssociation> deletedAssocs;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  private List<DiffTypes> baseDiff;
  private MatchingStrategy<ASTCDAssociation> typeMatcher;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

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
  public List<CDTypeDiff> getChangedClasses() {
    return changedClasses;
  }

  @Override
  public void setChangedClasses(List<CDTypeDiff> changedCLasses) {
    this.changedClasses = changedCLasses;
  }

  @Override
  public List<CDTypeDiff> getChangedTypes() {
    return null;
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
  public List<ASTCDInterface> getAddedInterfaces() {
    return null;
  }

  @Override
  public List<ASTCDInterface> getDeletedInterfaces() {
    return null;
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
  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses) { this.matchedClasses = matchedClasses; }
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
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  /**
   * Checks if each of the added classes refactors the old structure. The class must be abstarct,
   * its subclasses in the old CD need to have all of its attributes and it can't have new ones.
   */
//  @Override
//  public boolean isSuperclass(ASTCDClass astcdClass){
//    List<ASTCDClass> subclassesToCheck = new ArrayList<>();
//    if (!astcdClass.getModifier().isAbstract()){
//      for (ASTCDClass classesToCheck : getSrcCD().getCDDefinition().getCDClassesList()){
//        ASTMCObjectType newSuper =
//          MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
//            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName()))
//            .build();
////        if (isNewSuper(newSuper, classesToCheck,
////          (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())){
////          subclassesToCheck.add(classesToCheck);
////        }
//      }
//    }
//    else {
//      return false;
//    }
//
//    if (!astcdClass.getCDAttributeList().isEmpty()){
//      for (ASTCDClass classToCheck : subclassesToCheck){
//        ASTCDClass matchedClass = findMatchedClass(classToCheck);
//        if (matchedClass != null){
//          for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()){
//            if (!matchedClass.getCDAttributeList().contains(attribute) || !isAttributInSuper(attribute, matchedClass,
//              (ICD4CodeArtifactScope) getTgtCD().getEnclosingScope())){
//              return false;
//            }
//          }
//        }
//      }
//    }
//    return true;
//  }

  public boolean isSupClass(ASTCDClass astcdClass){
    if (astcdClass.getModifier().isAbstract()){
      List<ASTCDClass> classesToCheck = Syn2SemDiffHelper.getSpannedInheritance(helper.getSrcCD(), astcdClass);
      List<ASTCDAttribute> attributes = astcdClass.getCDAttributeList();
      for (ASTCDClass classToCheck : classesToCheck){
        for (ASTCDAttribute attribute : attributes){
          if (Syn2SemDiffHelper.isAttContainedInClass(attribute, classToCheck)){

          } else {
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
              return false;
            }
          }
        }
      }
    } else {
      return false;
    }
    return true;
  }

  private ASTCDClass findMatchedClass(ASTCDClass astcdClass){
    ASTCDClass matchedClass = null;
    for (Pair<ASTCDClass, ASTCDClass> pair : getMatchedClasses()){
      if(pair.a.equals(astcdClass)){
        matchedClass = pair.b;
      }
    }
    return matchedClass;
  }

  /**
   *
   * Check if a deleted @param astcdAssociation was needed in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  //TODO: replace the boolean with the class
  //TODO: check if exists after overlapping Assocs
  //TODO: also for classes - maybe do this when creating the list with changes as if(){...}
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      //List<ASTCDClass> superClassesLeft = Syn2SemDiffHelper.getSuperClasses(this, pair.a);
      List<ASTCDClass> superClassesRight = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.b);
      for (AssocStruct association : helper.getSrcMap().get(pair.a)){
        if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Left
          && sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).b)){
          return false;
        } else if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Right
          && sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).a)){
          return false;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()){
      //leftSide
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesRight = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.b);
      for (AssocStruct association : helper.getSrcMap().get(pair.a)){
        if (association.getSide() == ClassSide.Left
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableRight()
          && sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getSide() == ClassSide.Right //reversed assoc
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
          && sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
      //rightSide
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesLeft = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.a);
      for (AssocStruct association : helper.getSrcMap().get(pair.b)){
        if (association.getDirection() == AssocDirection.LeftToRight
          && association.getSide() == ClassSide.Left
          && sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getDirection() == AssocDirection.RightToLeft
          && association.getSide() == ClassSide.Right
          && sameAssociationType(association.getAssociation(), astcdAssociation)
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This function is a less restrictive version of sameAssociation in CDAssociationHelper.
   * We check if the first association has the same navigation and role names.
   * The cardinalities of the first do not need to be same, but they have to be
   * 'contained' in the cardinalities of the second association
   * @param assoc1 first association
   * @param assoc2 second association
   * @return true, if all conditions are fulfilled
   */
  public static boolean sameAssociationType(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          //&& assoc1.getRight().getCDCardinality().equals(assoc2.getLeft().getCDCardinality())
          && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getRight().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getLeft().getCDCardinality()));
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
          //&& assoc1.getLeft().getCDCardinality().equals(assoc2.getRight().getCDCardinality())
          && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getLeft().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getRight().getCDCardinality()));
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getLeft().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getRight().getCDCardinality()))
        && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getRight().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getLeft().getCDCardinality()));
  }

  /**
   * 'sameAssociationType' for reversed directions
   * @param assoc1 first association
   * @param assoc2 second association
   * @return true, if all conditions are fulfilled
   */
  //TODO: add this to all places where sameAssociationType is used
  public static boolean sameAssociationTypeInReverse(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getRight().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getLeft().getCDCardinality()));
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
          && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getLeft().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getRight().getCDCardinality()));
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getRight().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getLeft().getCDCardinality()))
        && Syn2SemDiffHelper.isContainedIn(Syn2SemDiffHelper.cardToEnum(assoc1.getLeft().getCDCardinality()), Syn2SemDiffHelper.cardToEnum(assoc2.getRight().getCDCardinality()));
  }

  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAddedAssoc(ASTCDAssociation astcdAssociation) {
    //TODO: check again
    //List<ASTCDAssociation> list = typeMatcher.getMatchedElements(astcdAssociation);
    //this must replace first if()
    //so just check if the list isn't empty?
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
          if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedLeft)){
          if (assocStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
            && sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    return true;
  }

//  /**
//   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without
//   * attribute. Similar case for added ones.
//   *
//   * @param astcdEnum
//   *///not needed - if a class can't be inst with an enum, then the asstribute has been deleted
//  //we will get this info from CDTypeDiff with isDeleted
//  @Override
//  public List<ASTCDClass> getAttForEnum(ASTCDEnum astcdEnum){
//    List<ASTCDClass> classesWithEnum = new ArrayList<>();
//    for (ASTCDClass classToCheck : getSrcCD().getCDDefinition().getCDClassesList()){
//      for (ASTCDAttribute attribute : classToCheck.getCDAttributeList()){
//        if (attribute.getMCType().printType().equals(astcdEnum.getName())){
//          classesWithEnum.add(classToCheck);
//        }
//      }
//    }
//    return classesWithEnum;
//  }

//  @Override
//  public ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> findDuplicatedAssociations() {
//    // need to add all superAssocs(CDAssocHelper?)
//    ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> map = ArrayListMultimap.create();
//    for (ASTCDAssociation astcdAssociation : getSrcCD().getCDDefinition().getCDAssociationsList()) {
//      map.put(astcdAssociation, null);
//      for (ASTCDAssociation astcdAssociation1 :
//          getSrcCD().getCDDefinition().getCDAssociationsList()) {
//        if (!astcdAssociation.equals(astcdAssociation1)
//            && matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft())
//            && matchRoleNames(astcdAssociation.getRight(), astcdAssociation1.getRight())
//            && getSrcCD()
//                .getEnclosingScope()
//                .resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName())
//                .equals(
//                    getSrcCD()
//                        .getEnclosingScope()
//                        .resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))
//            && getSrcCD()
//                .getEnclosingScope()
//                .resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName())
//                .equals(
//                    getSrcCD()
//                        .getEnclosingScope()
//                        .resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {
//          map.put(astcdAssociation, astcdAssociation1);
//          // assocs1 needs to be deleted if not from superclass
//          // Can I change the ASTCdCompilationUnit?
//        }
//      }
//    }
//    return map;
//  }

  /**
   * Given the following two cardinalities, find their intersection
   * @param cardinalityA first cardinality
   * @param cardinalityB second cardinality
   * @return intersection of the cardinalities
   */
  private static AssocCardinality intersectCardinalities(AssocCardinality cardinalityA, AssocCardinality cardinalityB) {
    if (cardinalityA == null){
      return cardinalityB;
    }
    if (cardinalityA.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
    } else if (cardinalityA.equals(AssocCardinality.Optional)) {
      if (cardinalityB.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
      } else if (cardinalityB.equals(AssocCardinality.Multiple) || cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.Optional;
      } else if (cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.One;
      }
    } else if (cardinalityA.equals(AssocCardinality.Multiple)) {
      if (cardinalityB.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
      } else if (cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.Optional;
      } else if (cardinalityB.equals(AssocCardinality.Multiple)) {
        return AssocCardinality.Multiple;
      } else if (cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.AtLeastOne;
      }
    } else if (cardinalityA.equals(AssocCardinality.AtLeastOne)) {
      if (cardinalityB.equals(AssocCardinality.One) || cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.AtLeastOne;
      } else if (cardinalityB.equals(AssocCardinality.Multiple) || cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.AtLeastOne;
      }
    }
    return null;
  }

  /**
   * Compute what associations can be used from a class (associations that were from the class and superAssociations).
   * For each class and each possible association we save the direction and
   * also on which side the class is.
   * Two maps are created - srcMap (for srcCD) and trgMap (for trgCD).
   */
  public void setMaps(){
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getTgtCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            helper.getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {

            helper.getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {

            helper.getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            helper.getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());//falsch
      //Set<ASTCDType> superClasses = CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      for (ASTCDType superClass : superClasses){//getAllSuperTypes CDDffUtils
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getSrcCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName())
              && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());

//              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
//              //change left side from superClass to subClass
//              ASTCDAssocLeftSideBuilder leftSideBuilder = CD4CodeMill.cDAssocLeftSideBuilder()
//                .setModifier(association.getLeft().getModifier())
//                .setCDCardinality(association.getLeft().getCDCardinality())
//                .setCDRole(association.getLeft().getCDRole())
//                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
//
//              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
//                .setCDAssocType(association.getCDAssocType())
//                .setModifier(association.getModifier())
//                .setName(association.getName())
//                .setLeft(leftSideBuilder.build())
//                .setRight(association.getRight())
//                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
              }
              else {
                helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());

//              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
//              //change right side from superClass to subclass
//              ASTCDAssocRightSideBuilder rightSideBuilder = CD4CodeMill.cDAssocRightSideBuilder()
//                .setModifier(association.getRight().getModifier())
//                .setCDCardinality(association.getRight().getCDCardinality())
//                .setCDRole(association.getRight().getCDRole())
//                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
//
//              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
//                .setCDAssocType(association.getCDAssocType())
//                .setModifier(association.getModifier())
//                .setName(association.getName())
//                .setLeft(association.getLeft())
//                .setRight(rightSideBuilder.build())
//                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
              }
              else {
                helper.getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getTgtCD().getEnclosingScope());
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getTgtCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              //change left side from superClass to subClass
              ASTCDAssocLeftSideBuilder leftSideBuilder = CD4CodeMill.cDAssocLeftSideBuilder()
                .setModifier(association.getLeft().getModifier())
                .setCDCardinality(association.getLeft().getCDCardinality())
                .setCDRole(association.getLeft().getCDRole())
                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());

              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
                .setCDAssocType(association.getCDAssocType())
                .setModifier(association.getModifier())
                .setName(association.getName())
                .setLeft(leftSideBuilder.build())
                .setRight(association.getRight())
                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                helper.getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Left));
              }
              else {
                helper.getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.LeftToRight, ClassSide.Left));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              //change right side from superClass to subclass
              ASTCDAssocRightSideBuilder rightSideBuilder = CD4CodeMill.cDAssocRightSideBuilder()
                .setModifier(association.getRight().getModifier())
                .setCDCardinality(association.getRight().getCDCardinality())
                .setCDRole(association.getRight().getCDRole())
                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());

              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
                .setCDAssocType(association.getCDAssocType())
                .setModifier(association.getModifier())
                .setName(association.getName())
                .setLeft(association.getLeft())
                .setRight(rightSideBuilder.build())
                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                helper.getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Right));
              }
              else {
                helper.getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.RightToLeft, ClassSide.Right));
              }
            }
          }
        }
      }
    }
  }

  /**
   * Get the differences in a matched pair as a String
   * @param diff object of type CDAssocDiff or CDTypeDIff
   * @return differeces as a String
   */
  @Override
  public String findDiff(Object diff) {
    if (diff instanceof CDTypeDiff) {
      CDTypeDiff obj = (CDTypeDiff) diff;
      StringBuilder stringBuilder = new StringBuilder();
      for (DiffTypes type : obj.getBaseDiffs()) {
        switch (type) {
          case STEREOTYPE_DIFFERENCE:
            stringBuilder.append(obj.sterDiff());
          case CHANGED_ATTRIBUTE:
            stringBuilder.append(obj.attDiff());
        }
      }
      return stringBuilder.toString();
    } else {
      CDAssocDiff obj = (CDAssocDiff) diff;
      StringBuilder difference = new StringBuilder();
      for (DiffTypes type : obj.getBaseDiff()) {
        switch (type) {
          case CHANGED_ASSOCIATION_ROLE:
            difference.append(obj.roleDiff());
          case CHANGED_ASSOCIATION_DIRECTION:
            difference.append(obj.dirDiff());
          case CHANGED_ASSOCIATION_MULTIPLICITY:
            difference.append(obj.cardDiff());
        }
      }
      return difference.toString();
    }
  }

  /**
   * Find all differences (with additional information) in a pair of changed types
   *
   * @param typeDiff pair of new and old type
   * @return list of changes with information about it
   */
  public List<Pair<ASTCDClass, Object>> findTypeDiff(CDTypeDiff typeDiff){
    List<Pair<ASTCDClass, Object>> list = new ArrayList<>();
    for (DiffTypes types : typeDiff.getBaseDiffs()){
      switch (types){
        case CHANGED_ATTRIBUTE: if (typeDiff.changedAttribute(getSrcCD()) != null){ for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.changedAttribute(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));} }
        case STEREOTYPE_DIFFERENCE: if (typeDiff.isClassNeeded() != null){ list.add(new Pair<>((ASTCDClass) typeDiff.isClassNeeded(), null)); }
        case REMOVED_ATTRIBUTE: for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.deletedAttributes(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));}
        case ADDED_ATTRIBUTE: for (Pair<ASTCDClass, ASTCDAttribute> pair : typeDiff.addedAttributes(getSrcCD())){list.add(new Pair<>(pair.a, pair.b));}
        case ADDED_CONSTANTS: for (Pair<ASTCDClass, ASTCDEnumConstant> pair : typeDiff.newConstants()){list.add(new Pair<>(pair.a, pair.b));}
        //other cases?
      }
    }
    return list;
  }

  /**
   * Find all differences (with additional information) in a pair of changed associations
   *
   * @param assocDiff pair od new and old association
   * @return list of changes with information about it
   */
  public List<Pair<ASTCDAssociation, Object>> findAssocDiff(CDAssocDiff assocDiff){
    //with this I can use instanceOf when creating the info for the ODs
    List<Pair<ASTCDAssociation, Object>> list = new ArrayList<>();
    for (DiffTypes types : assocDiff.getBaseDiff()){
      switch (types){
        case CHANGED_ASSOCIATION_MULTIPLICITY: for (Pair<ASTCDAssociation, Pair<ClassSide, Integer>> pair : assocDiff.getCardDiff()){list.add(new Pair<>(pair.a, pair.b));}
        case CHANGED_ASSOCIATION_DIRECTION: list.add(new Pair<>(assocDiff.getSrcElem(), assocDiff.getDirection(assocDiff.getSrcElem())));
        case CHANGED_ASSOCIATION_ROLE: for (Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>> pair : assocDiff.getRoleDiff()){list.add(new Pair<>(pair.a, pair.b));}
        case CHANGED_TARGET: list.add(new Pair<>(assocDiff.getSrcElem(), assocDiff.getChangedTgtClass(getSrcCD())));
          //other cases?
      }
    }
    return list;
  }

  /**
   * Get all attributes that need to be added from inheritance structure to an object of a given type
   * @param astcdClass class
   * @return Pair of the class and a list of attributes
   */
  public Pair<ASTCDClass, List<ASTCDAttribute>> getAllAttr(ASTCDClass astcdClass){
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<ASTCDType> classes = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());
    for (ASTCDType classToCheck : classes){
      if (classToCheck instanceof ASTCDClass) {
        attributes.addAll(classToCheck.getCDAttributeList());
      }
    }
    return new Pair<>(astcdClass, attributes);
  }

  /**
   * Find all overlapping and all duplicated associations.
   *
   */
  //TODO: check cases and write a good description of the idea
  @Override
  public void findOverlappingAssocs(){
    //In each map we have for each class all associations that need to be considered (not only direct)
    //Each direct association we can compare to each superAssociation - I can add an attribute isSuperAssoc in AssocStruct so that it can be more efficient
    //We determine the case
    //1)If the associations can be merged, we change the direction and cardinality
    //2)If we have a conflict situation - we save the role name that causes the problem, because there can't be such association
    //After that we look at the cardinalities - if the cardinality can't be 0, then a class of that type can't exist
    //
    //We also need to compare between direct associations
    //If we have two direct associations with the same role name in srcDirection, but the classes aren't in an inheritance hierarchy
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)){
                  Log.error("Bad overlapping found");
                }

                if (!association.getDirection().equals(AssocDirection.BiDirectional)
                  && superAssoc.getDirection().equals(AssocDirection.BiDirectional)){
                  if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)){
                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
                  } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
                  } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)){
                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
                  } else {
                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
                  }
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged

                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
                association.getAssociation().setCDAssocDir(direction);
                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)){
                  //such association can't exist
                  //delete
                  deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.getSrcMap().removeAll(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)){
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationTypeInReverse(association.getAssociation(), superAssoc.getAssociation())){
                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
                association.getAssociation().setCDAssocDir(direction);
                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
                helper.getSrcMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)){
                  deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                } else {
                  helper.getSrcMap().removeAll(astcdClass);
                }
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : helper.getTrgMap().keySet()) {
      for (AssocStruct association : helper.getTrgMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getTrgMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)){
                  Log.error("Bad overlapping found");
                }

                if (!association.getDirection().equals(AssocDirection.BiDirectional)
                  && superAssoc.getDirection().equals(AssocDirection.BiDirectional)){
                  if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)){
                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
                  } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
                  } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)){
                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
                  } else {
                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
                  }
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
                association.getAssociation().setCDAssocDir(direction);
                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)){
                  //such association can't exist
                  //delete
                  deleteAssocsFromTgt(astcdClass, getConflict(association, superAssoc));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.getTrgMap().removeAll(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)){
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationTypeInReverse(association.getAssociation(), superAssoc.getAssociation())){
                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
                association.getAssociation().setCDAssocDir(direction);
                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
                helper.getTrgMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)){
                  deleteAssocsFromTgt(astcdClass, getConflict(association, superAssoc));
                } else {
                  helper.getTrgMap().removeAll(astcdClass);
                }
              }
            }
          }
        }
      }
    }
  }

  public boolean classExistsNew(ASTCDClass astcdClass){
    return helper.getSrcMap().containsKey(astcdClass);
  }

  public boolean classExistsOld(ASTCDClass astcdClass){
    return helper.getSrcMap().containsKey(astcdClass);
  }

  /**
   * Modified version of the function inConflict in CDAssociationHelper.
   * In the map, all association that can be created from a class
   * are saved in the values for this class (key).
   * Because of that we don't need to check if the source classes of both
   * associations are in an inheritance relation.
   * @param association association from the class
   * @param superAssociation superAssociation for that class
   * @return true, if the role names in target direction are the same
   */
  public boolean isInConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }

    return false;
  }

  public boolean sameRoleNames(AssocStruct assocDown, AssocStruct assocUp){
    if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Left)){
      return assocDown.getAssociation().getLeft().getCDRole().equals(assocUp.getAssociation().getLeft().getCDRole());
    } else if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Right)) {
      return assocDown.getAssociation().getLeft().getCDRole().equals(assocUp.getAssociation().getRight().getCDRole());
    } else if (assocDown.getSide().equals(ClassSide.Right) && assocUp.getSide().equals(ClassSide.Left)){
      return assocDown.getAssociation().getRight().getCDRole().equals(assocUp.getAssociation().getLeft().getCDRole());
    } else {
      return assocDown.getAssociation().getRight().getCDRole().equals(assocUp.getAssociation().getRight().getCDRole());
    }
  }

  /**
   * Given the two associations, get the role name that causes the conflict
   * @param association base association
   * @param superAssociation association from superclass
   * @return role name
   */
  public ASTCDRole getConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight())){
      return srcAssoc.getRight().getCDRole();
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft())){
      return srcAssoc.getLeft().getCDRole();
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft())){
      return srcAssoc.getRight().getCDRole();
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
      && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())){
      return srcAssoc.getLeft().getCDRole();
    }

    return null;
  }

  /**
   * Check if the target classes of the two associations are in an inheritance relation
   * @param association base association
   * @param superAssociation association from superclass
   * @return true, if they filfill the condition
   */
  //TODO: check inheritance in the other way
  //done
  public boolean inInheritanceRelation(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
      //do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
    } else if (association.getSide().equals(ClassSide.Right)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
    } else {
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
    }
  }

  /**
   * Merge the directions of two associations
   * @param association association from the class
   * @param superAssociation association from the class or superAssociation
   * @return merged direction in ASTCDAssocDir
   */
  public ASTCDAssocDir mergeAssocDir(AssocStruct association, AssocStruct superAssociation){
    if (association.getDirection().equals(AssocDirection.BiDirectional) || superAssociation.getDirection().equals(AssocDirection.BiDirectional)){
      return new ASTCDBiDir();
    } else if (association.getDirection().equals(AssocDirection.LeftToRight)) {
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)){
        return new ASTCDLeftToRightDir();
      }
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)){
        return new ASTCDBiDir();
      }
    } else if (association.getDirection().equals(AssocDirection.RightToLeft)){
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)){
        return new ASTCDRightToLeftDir();
      }
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)){
        return new ASTCDBiDir();
      }
    }
    return null;
  }

  /**
   * Group corresponding cardinalities
   * @param association association
   * @param superAssociation association
   * @return structure with two pairs of corresponding cardinalities
   */
  public CardinalityStruc getCardinalities(AssocStruct association, AssocStruct superAssociation){
    //I think that all cardinalities should be the other way around
    //Changed
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()),
        new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()),
        new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()),
        new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()));
    } else {
      return new CardinalityStruc(new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()),
        new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()));
    }
  }

  /**
   * Transform the internal cardinality to orginal
   * @param assocCardinality cardinality to transform
   * @return cardinality with type ASTCDCardinality
   */
  public ASTCDCardinality createCardinality(AssocCardinality assocCardinality){
    if (assocCardinality.equals(AssocCardinality.One)){
      return new ASTCDCardOne();
    } else if (assocCardinality.equals(AssocCardinality.Optional)) {
      return new ASTCDCardOpt();
    } else if (assocCardinality.equals(AssocCardinality.AtLeastOne)) {
      return new ASTCDCardAtLeastOne();
    } else {
      return new ASTCDCardMult();
    }
  }

  /**
   * Check if the associations allow 0 objects from target class
   * @param association association
   * @param superAssociation association
   * @return true if the condition is fulfilled
   */
  public boolean areZeroAssocs(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return (association.getAssociation().getRight().getCDCardinality().isMult() || association.getAssociation().getRight().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getRight().getCDCardinality().isMult() || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return (association.getAssociation().getRight().getCDCardinality().isMult() || association.getAssociation().getRight().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult() || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return (association.getAssociation().getLeft().getCDCardinality().isMult() || association.getAssociation().getLeft().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getRight().getCDCardinality().isMult() || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else {
      return (association.getAssociation().getLeft().getCDCardinality().isMult() || association.getAssociation().getLeft().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult() || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    }
  }

  /**
   * Delete associations from srcMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromSrc(ASTCDClass astcdClass, ASTCDRole role){
    for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().equals(role)){
        helper.getSrcMap().remove(astcdClass, assocStruct);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().equals(role)){
        helper.getSrcMap().remove(astcdClass, assocStruct);
      }
    }
  }

  /**
   * Delete associations from trgMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromTgt(ASTCDClass astcdClass, ASTCDRole role){
    for (AssocStruct assocStruct : helper.getTrgMap().get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().equals(role)){
        helper.getTrgMap().remove(astcdClass, assocStruct);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().equals(role)){
        helper.getTrgMap().remove(astcdClass, assocStruct);
      }
    }
  }

  public List<ASTCDAssociation> addedAssocList(){
    List<ASTCDAssociation> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs){
      if (isAddedAssoc(association)){
        associationList.add(association);
      }
    }
    return associationList;
  }

  public List<ASTCDClass> addedClassList(){
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClasses){
      if (isSupClass(astcdClass)){
        classList.add(astcdClass);
      }
    }
    return classList;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAssocList(){
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    return pairList;
  }



  public List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributeList(){
    return null;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributeList(){
    return null;
  }

  public List<Pair<ASTCDAttribute, ASTCDEnumConstant>> addedConstantsList(){
    return null;
  }

  public List changedAttributeList() { return null; }

  public List changedRoleNameList() { return null; }

  public List changedCardinalityList() { return null; }

  public List changedTargetList() { return null;}

  public List changedDirectionList() { return null; }

  public List changedVisibilityList() { return null; }

  public List overlappingAssocList() { return null; }
  //TODO: to for all diffs
  //TODO: after overlappingAssocs there might be associations to classes that cannot be instantiated - function
}
