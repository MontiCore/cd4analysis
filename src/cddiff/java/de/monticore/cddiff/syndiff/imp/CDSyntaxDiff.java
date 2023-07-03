package de.monticore.cddiff.syndiff.imp;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.CardinalityStruc;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDSyntaxDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.*;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

public class CDSyntaxDiff implements ICDSyntaxDiff {
  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit trgCD;
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

  private ArrayListMultimap<ASTCDClass, AssocStruct> srcMap = ArrayListMultimap.create();
  private ArrayListMultimap<ASTCDClass, AssocStruct> trgMap = ArrayListMultimap.create();

  @Override
  public ASTCDCompilationUnit getSrcCD() {
    return srcCD;
  }

  @Override
  public void setSrcCD(ASTCDCompilationUnit srcCD) {
    this.srcCD = srcCD;
  }

  @Override
  public ASTCDCompilationUnit getTrgCD() {
    return trgCD;
  }

  @Override
  public void setTrgCD(ASTCDCompilationUnit trgCD) {
    this.trgCD = trgCD;
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

  public ArrayListMultimap<ASTCDClass, AssocStruct> getSrcMap() {
    return srcMap;
  }

  public ArrayListMultimap<ASTCDClass, AssocStruct> getTrgMap() {
    return trgMap;
  }

  /**
   * Checks if each of the added classes refactors the old structure. The class must be abstarct,
   * its subclasses in the old CD need to have all of its attributes and it can't have new ones.
   */
  @Override
  public boolean isSuperclass(ASTCDClass astcdClass){
    List<ASTCDClass> subclassesToCheck = new ArrayList<>();
    if (!astcdClass.getModifier().isAbstract()){
      for (ASTCDClass classesToCheck : getSrcCD().getCDDefinition().getCDClassesList()){
        ASTMCObjectType newSuper =
          MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName()))
            .build();
        if (isNewSuper(newSuper, classesToCheck,
          (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())){
          subclassesToCheck.add(classesToCheck);
        }
      }
    }
    else {
      return false;
    }

    if (!astcdClass.getCDAttributeList().isEmpty()){
      for (ASTCDClass classToCheck : subclassesToCheck){
        ASTCDClass matchedClass = findMatchedClass(classToCheck);
        if (matchedClass != null){
          for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()){
            if (!matchedClass.getCDAttributeList().contains(attribute) || !isAttributInSuper(attribute, matchedClass,
              (ICD4CodeArtifactScope) getTrgCD().getEnclosingScope())){
              return false;
            }
          }
        }
      }
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
   * Get the whole inheritance hierarchy that @param astcdClass.
   * is part of - all direct and indirect superclasses.
   * @return a list of the superclasses.
   */
  @Override
  public List<ASTCDClass> getClassHierarchy(ASTCDClass astcdClass){
    return null;
    //implemented - not needed, it is part of other functions inCDDiffUtil
  }

  /**
   *
   * Check if a deleted @param astcdAssociation was needed in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      //List<ASTCDClass> superClassesLeft = CDHelper.getSuperClasses(this, pair.a);
      List<ASTCDClass> superClassesRight = CDHelper.getSuperClasses(this, pair.b);
      for (AssocStruct association : getSrcMap().get(pair.a)){
        if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Left
          && sameRoleNames(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(CDHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).b)){
          return false;
        } else if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Right
          && sameRoleNames(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(CDHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).a)){
          return false;
        }
      }
      //TODO: replace other cases
      //Done
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()){
      //leftSide
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesRight = CDHelper.getSuperClasses(this, pair.b);
      for (AssocStruct association : getSrcMap().get(pair.a)){
        if (association.getSide() == ClassSide.Left
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableRight()
          && sameRoleNames(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getSide() == ClassSide.Right //reversed assoc
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
          && sameRoleNames(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
      //rightSide
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesLeft = CDHelper.getSuperClasses(this, pair.a);
      for (AssocStruct association : getSrcMap().get(pair.b)){
        if (association.getDirection() == AssocDirection.LeftToRight
          && association.getSide() == ClassSide.Left
          && sameRoleNames(astcdAssociation, association.getAssociation())
          && superClassesLeft.contains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getDirection() == AssocDirection.RightToLeft
          && association.getSide() == ClassSide.Right
          && sameRoleNames(association.getAssociation(), astcdAssociation)
          && superClassesLeft.contains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    }
    return false;
  }

  public static boolean sameRoleNames(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          && assoc1.getRight().getCDCardinality().equals(assoc2.getLeft().getCDCardinality());
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
          && assoc1.getLeft().getCDCardinality().equals(assoc2.getRight().getCDCardinality());
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && assoc1.getRight().getCDCardinality().equals(assoc2.getLeft().getCDCardinality())
        && assoc1.getLeft().getCDCardinality().equals(assoc2.getRight().getCDCardinality());
  }

  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAlwaysNeededAssoc(ASTCDAssociation astcdAssociation) {
    //TODO: check again
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = CDHelper.getSuperClasses(this, matchedLeft);
        for (AssocStruct assocStruct : getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameRoleNames(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(CDHelper.getConnectedClasses(assocStruct.getAssociation(), getTrgCD()).b)){
            return false;
          }
          if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameRoleNames(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(CDHelper.getConnectedClasses(assocStruct.getAssociation(), getTrgCD()).a)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = CDHelper.getSuperClasses(this, matchedLeft);
        for (AssocStruct assocStruct : getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && sameRoleNames(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(CDHelper.getConnectedClasses(assocStruct.getAssociation(), getTrgCD()).b)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = CDHelper.getSuperClasses(this, matchedLeft);
        for (AssocStruct assocStruct : getTrgMap().get(matchedLeft)){
          if (assocStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
            && sameRoleNames(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(CDHelper.getConnectedClasses(assocStruct.getAssociation(), getTrgCD()).a)){
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without
   * attribute. Similar case for added ones.
   *
   * @param astcdEnum
   *///not needed - if a class can't be inst with an enum, then the asstribute has been deleted
  //we will get this info from CDTypeDiff with isDeleted
  @Override
  public List<ASTCDClass> getAttForEnum(ASTCDEnum astcdEnum){
    List<ASTCDClass> classesWithEnum = new ArrayList<>();
    for (ASTCDClass classToCheck : getSrcCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAttribute attribute : classToCheck.getCDAttributeList()){
        if (attribute.getMCType().printType().equals(astcdEnum.getName())){
          classesWithEnum.add(classToCheck);
        }
      }
    }
    return classesWithEnum;
  }

  //to be moved in CDTypeDiff with cd Parameter
  @Override
  public ASTCDType isClassNeeded(CDTypeDiff pair) {
    //TODO: check
    ASTCDClass srcCLass = (ASTCDClass) pair.getElem1();
    if (!srcCLass.getModifier().isAbstract()){
      return pair.getElem1();
    }
    else{
      //do we check if assocs make sense - assoc to abstract class
      Set<ASTCDClass> map = getSrcMap().keySet();
      map.remove((ASTCDClass) pair.getElem1());
      for (ASTCDClass astcdClass : map){
        for (AssocStruct mapPair : getSrcMap().get(astcdClass)){//Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>
          if (Objects.equals(mapPair.getDirection(), AssocDirection.LeftToRight)
            && CDHelper.getConnectedClasses(mapPair.getAssociation(), getSrcCD()).b.equals(pair.getElem1())
            && mapPair.getAssociation().getRight().getCDCardinality().isAtLeastOne()){
             //add to Diff List - class can be instantiated without the abstract class
            return astcdClass;
          } else if (Objects.equals(mapPair.getDirection(), AssocDirection.RightToLeft)
            && CDHelper.getConnectedClasses(mapPair.getAssociation(), getSrcCD()).a.equals(pair.getElem1())
            && mapPair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()) {
            //add to Diff List - class can be instantiated without the abstract class
            return astcdClass;
          } else if (Objects.equals(mapPair.getDirection(), AssocDirection.BiDirectional)) {
            if (Objects.equals(mapPair.getSide(), ClassSide.Left)
              && mapPair.getAssociation().getRight().getCDCardinality().isAtLeastOne()){
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
    //not implemented
    return null;
  }

  @Override
  public ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> findDuplicatedAssociations() {
    // need to add all superAssocs(CDAssocHelper?)
    ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> map = ArrayListMultimap.create();
    for (ASTCDAssociation astcdAssociation : getSrcCD().getCDDefinition().getCDAssociationsList()) {
      map.put(astcdAssociation, null);
      for (ASTCDAssociation astcdAssociation1 :
          getSrcCD().getCDDefinition().getCDAssociationsList()) {
        if (!astcdAssociation.equals(astcdAssociation1)
            && matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft())
            && matchRoleNames(astcdAssociation.getRight(), astcdAssociation1.getRight())
            && getSrcCD()
                .getEnclosingScope()
                .resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName())
                .equals(
                    getSrcCD()
                        .getEnclosingScope()
                        .resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))
            && getSrcCD()
                .getEnclosingScope()
                .resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName())
                .equals(
                    getSrcCD()
                        .getEnclosingScope()
                        .resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {
          map.put(astcdAssociation, astcdAssociation1);
          // assocs1 needs to be deleted if not from superclass
          // Can I change the ASTCdCompilationUnit?
        }
      }
    }
    return map;
  }

  private static AssocCardinality cardToEnum(ASTCDCardinality cardinality){
    if (cardinality.isOne()) {
      return AssocCardinality.One;
    } else if (cardinality.isOpt()) {
      return AssocCardinality.Optional;
    } else if (cardinality.isAtLeastOne()) {
      return AssocCardinality.AtLeastOne;
    } else {
      return AssocCardinality.Multiple;
    }
  }

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
    //TODO: check if class position is right
    //Seems right
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            //srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, astcdAssociation)));
            srcMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            //srcMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, astcdAssociation)));
            srcMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            //srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, astcdAssociation)));
            srcMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            //srcMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, astcdAssociation)));
            srcMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTrgCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getTrgCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(astcdAssociation, getSrcCD());
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            //trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, astcdAssociation)));
            trgMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            //trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, astcdAssociation)));
            trgMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            //trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, astcdAssociation)));
            trgMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            //trgMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, astcdAssociation)));
            trgMap.put(astcdClass, new AssocStruct(astcdAssociation, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());//falsch
      for (ASTCDType superClass : superClasses){//getAllSuperTypes CDDffUtils
        if (superClass instanceof ASTCDClass){
          for (ASTCDAssociation association : getSrcCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(association, getSrcCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName())
              && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              ASTCDAssocLeftSideBuilder leftSideBuilder = CD4CodeMill.cDAssocLeftSideBuilder()
                .setModifier(association.getLeft().getModifier())
                .setCDCardinality(association.getLeft().getCDCardinality())
                .setCDRole(association.getLeft().getCDRole())
                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
//              //subClass must be set on the left side - how
              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
                .setCDAssocType(association.getCDAssocType())
                .setModifier(association.getModifier())
                .setName(association.getName())
                .setLeft(leftSideBuilder.build())
                //.setRight()
                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, assocForSubClass)));
                srcMap.put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Left));
              }
              else {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, association)));
                srcMap.put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.LeftToRight, ClassSide.Left));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              ASTCDAssocLeftSideBuilder leftSideBuilder = CD4CodeMill.cDAssocLeftSideBuilder();
//                .setModifier(association.getLeft().getModifier())
//                .setCDCardinality(association.getLeft().getCDCardinality())
//                .setCDRole(association.getLeft().getCDRole())
//                .setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
//              //subClass must be set on the left side - how
              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
                .setCDAssocType(association.getCDAssocType())
                .setModifier(association.getModifier())
                .setName(association.getName())
                .setLeft(leftSideBuilder.build())
                //.setRight()
                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                //srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, association)));
                srcMap.put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Right));
              }
              else {
                //srcMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, association)));
                srcMap.put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.RightToLeft, ClassSide.Right));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTrgCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getTrgCD().getEnclosingScope());
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          for (ASTCDAssociation association : getTrgCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = CDHelper.getConnectedClasses(association, getSrcCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              if (association.getCDAssocDir().isBidirectional()) {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, association)));
                trgMap.put(astcdClass, new AssocStruct(association, AssocDirection.BiDirectional, ClassSide.Left));
              }
              else {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, association)));
                trgMap.put(astcdClass, new AssocStruct(association, AssocDirection.LeftToRight, ClassSide.Left));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              if (association.getCDAssocDir().isBidirectional()) {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, association)));
                trgMap.put(astcdClass, new AssocStruct(association, AssocDirection.BiDirectional, ClassSide.Right));
              }
              else {
                //trgMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, association)));
                trgMap.put(astcdClass, new AssocStruct(association, AssocDirection.RightToLeft, ClassSide.Right));
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
  public List<Object> findTypeDiff(CDTypeDiff typeDiff){
    //TODO: find a better return type
    List<Object> list = new ArrayList<>();
    for (DiffTypes types : typeDiff.getBaseDiffs()){
      switch (types){
        case CHANGED_ATTRIBUTE: if (typeDiff.changedAttribute(getSrcCD()) != null){ list.addAll(typeDiff.changedAttribute(getSrcCD())); }
        case STEREOTYPE_DIFFERENCE: if (isClassNeeded(typeDiff) != null){ list.add(isClassNeeded(typeDiff)); }
        case REMOVED_ATTRIBUTE: list.addAll(typeDiff.deletedAttributes(getSrcCD()));
        case ADDED_ATTRIBUTE: list.addAll(typeDiff.addedAttributes(getSrcCD()));
        case ADDED_CONSTANTS: list.add(typeDiff.newConstants());
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
  public List<Object> findAssocDiff(CDAssocDiff assocDiff){
    //TODO: find a better return type
    List<Object> list = new ArrayList<>();
    for (DiffTypes types : assocDiff.getBaseDiff()){
      switch (types){
        case CHANGED_ASSOCIATION_MULTIPLICITY: list.addAll(assocDiff.getCardDiff());
        case CHANGED_ASSOCIATION_DIRECTION: list.add(new Pair<>(assocDiff.getElem1(), assocDiff.getDirection(assocDiff.getElem1())));
        case CHANGED_ASSOCIATION_ROLE: list.addAll(assocDiff.getRoleDiff());
        case CHANGED_TARGET: list.add(assocDiff.getChangedTgtClass(getSrcCD()));
          //other cases?
      }
    }
    return list;
  }

  /**
   * Create a minimal set of associations and classes that are needed for deriving
   * an object diagram for a given class or association
   * @param astcdClass optional
   * @param astcdAssociation optional
   * @return minimal set of objects
   */
  public Set<Object> createObjectsForOD(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation){
    Set<Object> set = new HashSet<>();
    return (createChains(astcdClass, astcdAssociation, set));
  }

  public Set<Object> createChains(ASTCDClass astcdClass, ASTCDAssociation astcdAssociation, Set<Object> objectSet){
    if (astcdClass != null) {
      if (!objectSet.contains(astcdClass)) {
        objectSet.add(astcdClass);
        List<AssocStruct> list = getSrcMap().get(astcdClass);
        for (AssocStruct pair : list) { //Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair
          if (!objectSet.contains(pair.getAssociation())) {
            switch (pair.getSide()) {
              case Left:
                if (pair.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getRight().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(CDHelper.getConnectedClasses(pair.getAssociation(), getSrcCD()).b, null, objectSet));
                }
              case Right:
                if (pair.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                  || pair.getAssociation().getLeft().getCDCardinality().isOne()) {
                  objectSet.add(pair.getAssociation());
                  objectSet.addAll(createChains(CDHelper.getConnectedClasses(pair.getAssociation(), getSrcCD()).a, null, objectSet));
                }
            }
          }
        }
      }
    }
    else {
      if (!objectSet.contains(astcdAssociation)) {
        objectSet.addAll(createChains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a, null, objectSet));
        objectSet.addAll(createChains(CDHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b, null, objectSet));
      }
    }
    return objectSet;
  }

  public void overlappingAssocs(){
    //In each map we have for each class all associations that need to be considered (not only direct)
    //Each direct association we can compare to each superAssociation - I can add an attribute isSuperAssoc in AssocStruct so that it can be more efficient
    //We determine the case
    //1)If the associations can be merged, we change the direction and cardinality
    //2)If we have a conflict situation - we save the role name that causes the problem, because there can't be such association
    //After that we look at the cardinalities - if the cardinality can't be 0, then a class of that type can't exist
    //
    //We also need to compare between direct associations
    //If we have two direct associations with the same role name in srcDirection, but the classes aren't in an inheritance hierarchy
    for (ASTCDClass astcdClass : getSrcMap().keySet()) {
      for (AssocStruct association : getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(cardToEnum(cardinalities.getLeftCardinalities().a), cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(cardToEnum(cardinalities.getRightCardinalities().a), cardToEnum(cardinalities.getRightCardinalities().b));
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
                  deleteAssocs(astcdClass, getConflict(association, superAssoc));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  getSrcMap().removeAll(astcdClass);
                  //Do I need to give some output about the class?
                }
              }
            } else if (!association.equals(superAssoc)){
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationInReverse(association.getAssociation(), superAssoc.getAssociation())){
                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
                AssocCardinality cardinalityLeft = intersectCardinalities(cardToEnum(cardinalities.getLeftCardinalities().a), cardToEnum(cardinalities.getLeftCardinalities().b));
                AssocCardinality cardinalityRight = intersectCardinalities(cardToEnum(cardinalities.getRightCardinalities().a), cardToEnum(cardinalities.getRightCardinalities().b));
                association.getAssociation().setCDAssocDir(direction);
                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
                getSrcMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)){
                  deleteAssocs(astcdClass, getConflict(association, superAssoc));
                } else {
                  getSrcMap().removeAll(astcdClass);
                }
              }
            }
          }
        }
      }
    }
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
   * Check if the first cardinality is contained in the second cardinality
   * @param cardinality1 first cardinality
   * @param cardinality2 second cardinality
   * @return true if first cardinality is contained in the second one
   */
  //TODO: replace in all statements, where cardinalities are compared
  public boolean isContainedIn(AssocCardinality cardinality1, AssocCardinality cardinality2){
    if (cardinality1.equals(AssocCardinality.One)
      || cardinality2.equals(AssocCardinality.Multiple)){
      return true;
    } else if (cardinality1.equals(AssocCardinality.Optional)){
      return !cardinality2.equals(AssocCardinality.One)
        && !cardinality2.equals(AssocCardinality.AtLeastOne);
    } else if (cardinality1.equals(AssocCardinality.AtLeastOne)){
      return cardinality2.equals(AssocCardinality.AtLeastOne);
    } else{
      return false;
    }
  }

  public boolean inInheritanceRelation(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
      //do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
    } else if (association.getSide().equals(ClassSide.Right)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
    } else {
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getCDDefinition());
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

  public void deleteAssocs(ASTCDClass astcdClass, ASTCDRole role){
    for (AssocStruct assocStruct : getSrcMap().get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().equals(role)){
        getSrcMap().remove(astcdClass, assocStruct);
      } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().equals(role)){
        getSrcMap().remove(astcdClass, assocStruct);
      }
    }
  }
}
