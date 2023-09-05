package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getDirectSuperClasses;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getSpannedInheritance;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDSyntaxDiff;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.*;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociation;
import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociationInReverse;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class CDSyntaxDiff extends CDDiffHelper implements ICDSyntaxDiff {
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
  private List<Pair<ASTCDClass, List<ASTCDClass>>> addedInheritance;
  private List<Pair<ASTCDClass, List<ASTCDClass>>> deletedInheritance;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  private List<DiffTypes> baseDiff;
  NameTypeMatcher nameTypeMatch;
  StructureTypeMatcher structureTypeMatch;
  SuperTypeMatcher superTypeMatch;
  NameAssocMatcher nameAssocMatch;
  SrcTgtAssocMatcher associationSrcTgtMatch;
  List<MatchingStrategy<ASTCDType>> typeMatchers;
  List<MatchingStrategy<ASTCDAssociation>> assocMatchers;
  ICD4CodeArtifactScope scopeSrcCD, scopeTgtCD;
  //Print
  protected StringBuilder outputSrc,outputTgt, outputAdded, outputDeleted, outputChanged, outputDiff;
  //Print end

  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  public Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
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
    nameTypeMatch = new NameTypeMatcher(tgtCD);
    structureTypeMatch = new StructureTypeMatcher(tgtCD);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    nameAssocMatch = new NameAssocMatcher(tgtCD);
    associationSrcTgtMatch = new SrcTgtAssocMatcher(superTypeMatch, srcCD, tgtCD);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    //typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
    assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatch);
    scopeSrcCD = (ICD4CodeArtifactScope) srcCD.getEnclosingScope();
    scopeTgtCD = (ICD4CodeArtifactScope) tgtCD.getEnclosingScope();

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(srcCD);
    new CD4CodeDirectCompositionTrafo().transform(tgtCD);

    loadAllLists(srcCD, tgtCD, scopeSrcCD, scopeTgtCD, typeMatchers, assocMatchers);
    helper.setMatchedClasses(matchedClasses);
    setStrings(scopeSrcCD, scopeTgtCD);
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

  public List<Pair<ASTCDClass, List<ASTCDClass>>> getAddedInheritance() {
    return addedInheritance;
  }
  public void setAddedInheritance(List<Pair<ASTCDClass, List<ASTCDClass>>> addedInheritance) {
    this.addedInheritance = addedInheritance;
  }
  public List<Pair<ASTCDClass, List<ASTCDClass>>> getDeletedInheritance() {
    return deletedInheritance;
  }
  public void setDeletedInheritance(List<Pair<ASTCDClass, List<ASTCDClass>>> deletedInheritance) {
    this.deletedInheritance = deletedInheritance;
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
  public boolean isSupClass(ASTCDClass astcdClass){
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

  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> deletedClasses(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDClass>> struc : deletedInheritance){
      List<ASTCDClass> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDClass superClass : superClasses){
        if (isClassDeleted(struc.a, struc.a)){
          currentDiff.add(superClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  public boolean isClassDeleted(ASTCDClass astcdClass, ASTCDClass subClass){
    //check if a deleted class brings a semantic difference
    //check if all subclasses have the attributes from this class from tgt tgtCD
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr( astcdClass);
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
    for (AssocStruct assocStruct : getHelper().getTrgMap().get(astcdClass)){
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getSrcMap().get(subClass)) {
          if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())
            || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
      }
      List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, subClass);
      for (ASTCDClass sub : subClasses) {
        if (helper.findMatchedClass(sub) != null) {
          for (AssocStruct assocStruct1 : getHelper().getSrcMap().get(sub)) {
            if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())
              || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())) {
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
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - done (only outgoing are saved to the values of a key)
    return false;
  }

  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> addedInheritance(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDClass>> struc : addedInheritance){
      List<ASTCDClass> subclasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDClass subClass : subclasses){
        if (isInheritanceAdded(struc.a, subClass)){
          currentDiff.add(subClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  public Set<InheritanceDiff> mergeInheritanceDiffs(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> added = addedInheritance();
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> deleted = deletedClasses();
    Set<InheritanceDiff> set = new HashSet<>();
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : added){
      InheritanceDiff diff = new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedClass(pair.a)));
      diff.setNewDirectSuper(new ArrayList<>(pair.b));
      set.add(diff);
    }
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : deleted){
      boolean holds = true;
      for (InheritanceDiff diff : set){
        if (pair.a.equals(diff.getAstcdClasses().a)){
          diff.setOldDirectSuper(new ArrayList<>(pair.b));
          holds = false;
          break;
        }
      }
      if (!holds){
        InheritanceDiff diff = new InheritanceDiff(new Pair<>(pair.a, helper.findMatchedClass(pair.a)));
        diff.setOldDirectSuper(new ArrayList<>(pair.b));
        set.add(diff);
      }
    }
    return set;
  }

  public boolean isInheritanceAdded(ASTCDClass astcdClass, ASTCDClass subClass) {
    //reversed case
    //check if new attributes existed in the given subclass - use function from CDTypeDiff
    //check if the associations also existed(are subtypes of the associations) in the tgtMap - same subfunction from isClassDeleted
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesTgt().contains(astcdClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, astcdClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
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
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - only outgoing are saved in the map
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getSrcMap().get(astcdClass)) {
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getTrgMap().get(subClass)) {
          if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())
            || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
        ASTCDClass matchedClass = helper.findMatchedClass(subClass);
        List<ASTCDClass> subClasses = getSpannedInheritance(tgtCD, matchedClass);
        for (ASTCDClass sub : subClasses) {
          if (helper.findMatchedClass(sub) != null) {
            for (AssocStruct assocStruct1 : getHelper().getTrgMap().get(sub)) {
              if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())
                || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())) {
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
      }
    }
    return false;
  }

  public ASTCDClass isAssocDeleted(ASTCDAssociation association, ASTCDClass astcdClass) {
    AssocStruct assocStruct = findDeletedStruc(association, astcdClass);
    if (assocStruct != null) {//if assocStruc is null, then the association is deleted because of overlapping
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())) {
          if (!astcdClass.getModifier().isAbstract() && !helper.isContainedInSuper(association, astcdClass) && helper.findMatchedSrc(astcdClass) != null) {
            return helper.findMatchedSrc(astcdClass);
          } else {
            return helper.allSubclassesHaveIt(association, astcdClass);
          }
        }
      } else {
        if (!(assocStruct.getAssociation().getRight().getCDCardinality().isMult()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())) {
          if (!astcdClass.getModifier().isAbstract() && !helper.isContainedInSuper(association, astcdClass) && helper.findMatchedSrc(astcdClass) != null) {
            return helper.findMatchedSrc(astcdClass);
          } else {
            return helper.allSubclassesHaveIt(association, astcdClass);
          }
        }
      }
    }
    return null;
  }

  private AssocStruct findDeletedStruc(ASTCDAssociation association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : helper.getTrgMap().get(astcdClass)){
      if (CDAssociationHelper.sameAssociation(assocStruct.getAssociation(), association) || CDAssociationHelper.sameAssociationInReverse(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  public boolean getSTADiff(ASTCDClass astcdClass){
    ASTCDClass oldClass = helper.findMatchedClass(astcdClass);
    List<ASTCDClass> oldCLasses = getSuperClasses(tgtCD, oldClass);
    List<ASTCDClass> newClasses = getSuperClasses(srcCD, astcdClass);
    if (oldCLasses.size() != newClasses.size()){
      return false;
    }
    boolean different = false;
    for (ASTCDClass class1 : oldCLasses){
      for (ASTCDClass class2 : newClasses){
        if (class1.getSymbol().getInternalQualifiedName().equals(class2.getSymbol().getInternalQualifiedName())){
          different = true;
          break;
        }
      }
      if (different){
        break;
      }
    }
    for (ASTCDClass class1 : newClasses){
      for (ASTCDClass class2 : oldCLasses){
        if (class1.getSymbol().getInternalQualifiedName().equals(class2.getSymbol().getInternalQualifiedName())){
          different = true;
          break;
        }
      }
    }
    return different;
  }


  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAddedAssoc(ASTCDAssociation astcdAssociation) {
    //List<ASTCDAssociation> list = typeMatcher.getMatchedElements(astcdAssociation);
    //this must replace first if()
    //so just check if the list isn't empty?
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
    if (pair.a.getModifier().isAbstract() && getSpannedInheritance(helper.getSrcCD(), pair.a).isEmpty()){
      return false;
    }
    if (pair.b.getModifier().isAbstract() && getSpannedInheritance(helper.getSrcCD(), pair.b).isEmpty()){
      return false;
    }
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      ASTCDClass matchedRight = helper.findMatchedClass(pair.b);
      ASTCDClass matchedLeft = helper.findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
          if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      ASTCDClass matchedRight = helper.findMatchedClass(pair.b);
      ASTCDClass matchedLeft = helper.findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      ASTCDClass matchedRight = helper.findMatchedClass(pair.b);
      ASTCDClass matchedLeft = helper.findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedLeft)){
          if (assocStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    return true;
  }

  public boolean isAssocAdded(ASTCDAssociation association){
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
    ASTCDClass matchedRight = helper.findMatchedClass(pair.b);
    ASTCDClass matchedLeft = helper.findMatchedClass(pair.a);
    if (matchedLeft != null && matchedRight != null) {
      AssocStruct matchedAssocStruc = helper.getAssocStrucForClass(matchedLeft, association);
      assert matchedAssocStruc != null;
      if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)) {
          if (Syn2SemDiffHelper.sameAssociationType(assocStruct.getAssociation(), association)
            && (helper.inInheritanceRelation(matchedAssocStruc, assocStruct) || sameTgt(matchedAssocStruc, assocStruct))) {
            return false;
          }
          else if (helper.allSubclassesHaveIt(association, matchedRight) != null){
            return false;
          }
        }
      }
      if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedLeft)) {
          if ((Syn2SemDiffHelper.sameAssociationType(assocStruct.getAssociation(), association)
            && (helper.inInheritanceRelation(matchedAssocStruc, assocStruct) || sameTgt(matchedAssocStruc, assocStruct)))) {
            return false;
          }
          else if (helper.allSubclassesHaveIt(association, matchedLeft) != null){
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Get the differences in a matched pair as a String
   * @param diff object of type CDAssocDiff or CDTypeDIff
   * @return differences as a String
   */
  @Override
  public String findDiff(Object diff) {
    if (diff instanceof CDTypeDiff) {
      CDTypeDiff obj = (CDTypeDiff) diff;
      StringBuilder stringBuilder = new StringBuilder();
      for (DiffTypes type : obj.getBaseDiff()) {
        switch (type) {
          case STEREOTYPE_DIFFERENCE:
            stringBuilder.append(obj.sterDiff());
          case CHANGED_ATTRIBUTE_TYPE:
            stringBuilder.append(obj.attDiff());
          case CHANGED_ATTRIBUTE_MODIFIER:
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
            //ADD RIGHT MULTIPLICITY - Done
          case CHANGED_ASSOCIATION_LEFT_MULTIPLICITY :
          case CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY:
            difference.append(obj.cardDiff());
        }
      }
      return difference.toString();
    }
  }

  //TODO: checkduplicated assocs and merge with delete
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
                if (!sameRoleNames(association, superAssoc)) {
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
                System.out.println("in inheritance relation");
                srcAssocsToMerge.add(new Pair<>(association, superAssoc));
              }
              else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)
                && !getConnectedClasses(association.getAssociation(), srcCD).equals(getConnectedClasses(superAssoc.getAssociation(), srcCD))){
                System.out.println("not in inheritance relation");
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
                if (!sameRoleNames(association, superAssoc)) {
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
      for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)) {
        helper.getSrcMap().removeAll(subClass);
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
      for (ASTCDClass subClass : getSpannedInheritance(tgtCD, astcdClass)) {
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

  public void deleteCompositions() {
    for (ASTCDAssociation association : helper.getSrcCD().getCDDefinition().getCDAssociationsList()) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
      if (association.getCDAssocDir().isDefinitiveNavigableRight() && association.getCDAssocType().isComposition()) {
        AssocStruct assocStruct = getAssocStrucForBaseAssoc(pair.a, association);
        if (assocStruct != null && helper.getNotInstanClassesSrc().contains(pair.a)) {
          helper.updateSrc(pair.b);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getSrcCD(), pair.b)) {
            helper.getSrcMap().removeAll(subClass);
          }
        }
      }
      if (association.getCDAssocDir().isDefinitiveNavigableLeft() && association.getCDAssocType().isComposition()) {
        AssocStruct assocStruct = getAssocStrucForBaseAssoc(pair.b, association);
        if (assocStruct != null && helper.getNotInstanClassesSrc().contains(pair.b)) {
          helper.updateSrc(pair.a);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getSrcCD(), pair.a)) {
            helper.getSrcMap().removeAll(subClass);
          }
        }
      }
    }

    for (ASTCDAssociation association : helper.getTgtCD().getCDDefinition().getCDAssociationsList()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getTgtCD());
      if (association.getCDAssocDir().isDefinitiveNavigableRight() && association.getCDAssocType().isComposition()){
        AssocStruct assocStruct = getAssocStrucForBaseTgt(pair.a, association);
        if (assocStruct != null && helper.getNotInstanClassesTgt().contains(pair.a)){
          helper.updateTgt(pair.b);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getTgtCD(), pair.b)) {
            helper.getSrcMap().removeAll(subClass);
          }
        }
      }
      if (association.getCDAssocDir().isDefinitiveNavigableLeft() && association.getCDAssocType().isComposition()){
        AssocStruct assocStruct = getAssocStrucForBaseTgt(pair.b, association);
        if (assocStruct != null && helper.getNotInstanClassesTgt().contains(pair.b)){
          helper.updateTgt(pair.a);
          for (ASTCDClass subClass : getSpannedInheritance(helper.getTgtCD(), pair.a)) {
            helper.getSrcMap().removeAll(subClass);
          }
        }
      }
    }
  }

  private AssocStruct getAssocStrucForBaseAssoc(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
      if (sameAssociation(assocStruct.getUnmodifiedAssoc(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  private AssocStruct getAssocStrucForBaseTgt(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : helper.getTrgMap().get(astcdClass)){
      if (sameAssociation(assocStruct.getUnmodifiedAssoc(), association)){
        return assocStruct;
      }
    }
    return null;
  }

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
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
    }
  }

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
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
    }
  }

  public Pair<ASTCDClass, Set<ASTCDAttribute>> newAttributes(InheritanceDiff inheritanceDiff) {
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getNewDirectSuper()){
      boolean isContained = false;
      for (ASTCDClass astcdClass1 : inheritanceDiff.getOldDirectSuper()){
        if (astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_").equals(astcdClass1.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
          isContained = true;
          break;
        }
      }
      if (!isContained){
        classes.add(astcdClass);
      }
    }
    Set<ASTCDAttribute> attributes = new HashSet<>();
    for (ASTCDClass astcdClass : classes) {
      for (ASTCDAttribute attribute : getHelper().getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getHelper().getAllAttr(inheritanceDiff.getAstcdClasses().b).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            isContained = true;
            break;
          }
        }
        if (!isContained) {
          attributes.add(attribute);
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, attributes);
  }

  public Pair<ASTCDClass, Set<ASTCDAttribute>> deletedAttributes(InheritanceDiff inheritanceDiff){
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getOldDirectSuper()){
      boolean isContained = false;
      for (ASTCDClass astcdClass1 : inheritanceDiff.getNewDirectSuper()){
        if (astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_").equals(astcdClass1.getSymbol().getInternalQualifiedName().replace(".", "_"))) {
          isContained = true;
          break;
        }
      }
      if (!isContained){
        classes.add(astcdClass);
      }
    }
    Set<ASTCDAttribute> attributes = new HashSet<>();
    for (ASTCDClass astcdClass : classes) {
      for (ASTCDAttribute attribute : getHelper().getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getHelper().getAllAttr(inheritanceDiff.getAstcdClasses().a).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            isContained = true;
          }
        }
        if (!isContained) {
          attributes.add(attribute);
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, attributes);
  }

  //composition - association.getCDType.isComposition()

  public boolean sameTgt(AssocStruct assocStruct, AssocStruct assocStruct2){
    if (assocStruct.getSide().equals(ClassSide.Left)){
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().replace(".", "_")
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getSymbol().getInternalQualifiedName().replace(".", "_"));
      } else {
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().replace(".", "_")
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).a.getSymbol().getInternalQualifiedName().replace(".", "_"));
      }
    }
    else {
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getSymbol().getInternalQualifiedName().replace(".", "_")
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getSymbol().getInternalQualifiedName().replace(".", "_"));
      } else {
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).a.getName());
      }
    }
  }
  public List<ASTCDAssociation> addedAssocList(){
    List<ASTCDAssociation> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs){
      Pair<ASTCDClass, ASTCDClass> assocClasses = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      if ((helper.findMatchingAssocStructSrc(association, assocClasses.a) != null
        || helper.findMatchingAssocStructSrc(association, assocClasses.b) != null)
        && isAddedAssoc(association)){
        associationList.add(association);
      }
    }
    return associationList;
  }

  public List<Pair<ASTCDAssociation, ASTCDClass>> deletedAssocList() {
    List<Pair<ASTCDAssociation, ASTCDClass>> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, tgtCD);
      if (association.getCDAssocDir().isBidirectional()){
        ASTCDClass astcdClass = isAssocDeleted(association, pair.a);
        ASTCDClass astcdClass1 = isAssocDeleted(association, pair.b);
        if (astcdClass != null){
          list.add(new Pair<>(association, pair.a));
        }
        else if (astcdClass1 != null){
          list.add(new Pair<>(association, pair.b));
        }
      }
      else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        ASTCDClass astcdClass = isAssocDeleted(association, pair.b);
        if (astcdClass != null){
          list.add(new Pair<>(association, pair.b));
        }
      }
      else {
        ASTCDClass astcdClass = isAssocDeleted(association, pair.a);
        if (astcdClass != null){
          list.add(new Pair<>(association, pair.a));
        }
      }
    }
    return list;
  }

  public List<ASTCDClass> addedClassList(){
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClasses){
      if (!helper.getNotInstanClassesSrc().contains(astcdClass)
        && isSupClass(astcdClass)){
        classList.add(astcdClass);
      }
    }
    return classList;
  }

  //TODO: src exist and target not
  //src not and tgt exists
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

  public List<AssocDiffStruc> changedAssoc() {
    List<AssocDiffStruc> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pairDef = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
      Pair<ASTCDClass, ASTCDClass> pair;
      if (pairDef.a.getModifier().isAbstract() || pairDef.b.getModifier().isAbstract()) {
        pair = getClassesForAssoc(pairDef);
      } else {
        pair = pairDef;
      }
      if (pair != null && !helper.getNotInstanClassesSrc().contains(pair.a) && !helper.getNotInstanClassesSrc().contains(pair.b)) {
        AssocDiffStruc diff = new AssocDiffStruc();
        diff.setAssociation(assocDiff.getSrcElem());
        boolean changed = false;
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedRoleNames(assocDiff.getRoleDiff().b);
            changed = true;
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
          if (srcAndTgtExist(assocDiff)
            && assocDiff.isDirectionChanged()) {
            diff.setChangedDir(true);
            changed = true;
          }
        }

        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_TARGET)) {
          if (srcAndTgtExist(assocDiff)) {
            ASTCDClass change = assocDiff.changedTgt();
            if (change != null) {
              diff.setChangedTgt(change);
              changed = true;
            }
          }
        }
//        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_SOURCE)) {
//          if (srcAndTgtExist(assocDiff)) {
//            ASTCDClass change = assocDiff.changedSrc();
//            if (change != null) {
//              diff.setChangedSrc(change);
//              changed = true;
//            }
//          }
//        }
        // NOT LEFT MULTIPLICITY, DO ALSO FOR RIGHT MULTIPLICITY
        //Done
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedCard(assocDiff.getCardDiff().b);
            changed = true;
          }
        }
        if (changed){
          list.add(diff);
        }
      }
    }
    return list;
  }

  private Pair<ASTCDClass, ASTCDClass> getClassesForAssoc(Pair<ASTCDClass, ASTCDClass> pair){
    ASTCDClass left = null;
    ASTCDClass right = null;
    if (pair.a.getModifier().isAbstract()){
      left = helper.minDiffWitness(pair.a);
    }
    if (pair.b.getModifier().isAbstract()){
      right = helper.minDiffWitness(pair.b);
    }
    if (left != null && right != null){
      return new Pair<>(left, right);
    }
    return null;
  }

  public List<ASTCDClass> srcExistsTgtNot(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstanClassesTgt()){
      if (!helper.getNotInstanClassesSrc().contains(astcdClass)){
        list.add(astcdClass);
      }
    }
    return list;
  }

  //function that checks for each ASTCDCLass if the superclasses in the new and in the old diagram are the same
  public List<ASTCDClass> getSTADiff(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getSrcCD().getCDDefinition().getCDClassesList()){
      ASTCDClass matchedClass = helper.findMatchedClass(astcdClass);
      if (matchedClass != null){
        if (!getSTADiff(astcdClass)){
          list.add(astcdClass);
        }
      }
    }
    return list;
  }

  //TODO: CDSyntax2SemDiff4ASTODHelper

  /*--------------------------------------------------------------------*/

  public void addAllMatchedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDType>> typeMatchers) {
    List<ASTCDClass> tgtClasses = tgtCD.getCDDefinition().getCDClassesList();
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDClass tgtClass : tgtClasses) {
        for (MatchingStrategy<ASTCDType> typeMatcher : typeMatchers) {
          if (typeMatcher.isMatched(srcClass, tgtClass)) {
            matchedClasses.add(new Pair<>(srcClass, tgtClass));
            tgtClasses.remove(tgtClass);
            break;
          }
        }
        break;
      }
    }
  }

  public void addAllMatchedInterfaces(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDType>> typeMatchers) {
    List<ASTCDInterface> tgtInterfaces = tgtCD.getCDDefinition().getCDInterfacesList();
    for (ASTCDInterface srcInterface : srcCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDInterface tgtInterface : tgtInterfaces) {
        for (MatchingStrategy<ASTCDType> typeMatcher : typeMatchers) {
          if (typeMatcher.isMatched(srcInterface, tgtInterface)) {
            matchedInterfaces.add(new Pair<>(srcInterface, tgtInterface));
            tgtInterfaces.remove(tgtInterface);
            break;
          }
        }
        break;
      }
    }
  }

  public void addAllMatchedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDAssociation>> assocMatchers) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        for (MatchingStrategy<ASTCDAssociation> assocMatcher : assocMatchers) {
          if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
            matchedAssocs.add(new Pair<>(srcAssoc, tgtAssoc));
          }
        }
      }
    }
  }

  public void addAllMatchedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDType>> typeMatchers) {
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
        for (MatchingStrategy<ASTCDType> typeMatcher : typeMatchers) {
          if (typeMatcher.isMatched(srcEnum, tgtEnum)) {
            matchedEnums.add(new Pair<>(srcEnum, tgtEnum));
          }
        }
      }
    }
  }

  public void addAllchangedTypes(ICD4CodeArtifactScope scopeSrcCD, ICD4CodeArtifactScope scopeTgtCD) {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, scopeSrcCD, scopeTgtCD);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedTypes.add(typeDiff);
        baseDiff.addAll(typeDiff.getBaseDiff());
      }
    }
    for(Pair<ASTCDEnum, ASTCDEnum> pair : matchedEnums){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, scopeSrcCD, scopeTgtCD);
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

  public void addAllAddedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      boolean notFound = true;
      for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
        if (srcClass.getName().equals(tgtClass.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedClasses.add(srcClass);
        if(!baseDiff.contains(DiffTypes.ADDED_CLASS)){
          baseDiff.add(DiffTypes.ADDED_CLASS);
        }
      }
    }
  }

  public void addAllDeletedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
      boolean notFound = true;
      for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
        if (srcClass.getName().equals(tgtClass.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedClasses.add(tgtClass);
        if(!baseDiff.contains(DiffTypes.REMOVED_CLASS)){
          baseDiff.add(DiffTypes.REMOVED_CLASS);
        }
      }
    }
  }

  public void addAllAddedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      boolean notFound = true;
      for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.getName().equals(tgtEnum.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedEnums.add(srcEnum);
        if(!baseDiff.contains(DiffTypes.ADDED_ENUM)){
          baseDiff.add(DiffTypes.ADDED_ENUM);
        }
      }
    }
  }

  public void addAllDeletedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
      boolean notFound = true;
      for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.getName().equals(tgtEnum.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedEnums.add(tgtEnum);
        if(!baseDiff.contains(DiffTypes.REMOVED_ENUM)){
          baseDiff.add(DiffTypes.REMOVED_ENUM);
        }
      }
    }
  }

  public void addAllAddedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDAssociation>> assocMatchers) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        for (MatchingStrategy<ASTCDAssociation> assocMatcher : assocMatchers) {
          if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
            notFound = false;
            break;
          }
        }
        if(!notFound) {
          break;
        }
      }
      if (notFound) {
        addedAssocs.add(srcAssoc);
        if(!baseDiff.contains(DiffTypes.ADDED_ASSOCIATION)){
          baseDiff.add(DiffTypes.ADDED_ASSOCIATION);
        }
      }
    }
  }

  public void addAllDeletedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, List<MatchingStrategy<ASTCDAssociation>> assocMatchers) {
    for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
        for (MatchingStrategy<ASTCDAssociation> assocMatcher : assocMatchers) {
          if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
            notFound = false;
            break;
          }
        }
        if(!notFound) {
          break;
        }
      }
      if (notFound) {
        deletedAssocs.add(tgtAssoc);
        if(!baseDiff.contains(DiffTypes.REMOVED_ASSOCIATION)){
          baseDiff.add(DiffTypes.REMOVED_ASSOCIATION);
        }
      }
    }
  }

  public void addAllAddedInheritance(ICD4CodeArtifactScope srcCDScope,
                                     ICD4CodeArtifactScope tgtCDScope,
                                     List<MatchingStrategy<ASTCDType>> typeMatchers) {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      boolean found = false;
      List<ASTCDClass> addedClasses = new ArrayList<>();
      Set<ASTCDType> srcDirectSuperClasses = getDirectSuperClasses(pair.a, srcCDScope);
      Set<ASTCDType> tgtDirectSuperClasses = getDirectSuperClasses(pair.b, tgtCDScope);
      for(ASTCDType srcSuperType : srcDirectSuperClasses){
        for(ASTCDType tgtSuperType : tgtDirectSuperClasses){
          for(MatchingStrategy<ASTCDType> typeMatcher : typeMatchers){
            if(typeMatcher.isMatched(srcSuperType, tgtSuperType)){
              found = true;
              break;
            }
          }
        }
        if(!found){
          addedClasses.add((ASTCDClass) srcSuperType);
          if(!baseDiff.contains(DiffTypes.ADDED_INHERITANCE)){
            baseDiff.add(DiffTypes.ADDED_INHERITANCE);
          }
        }
      }
      addedInheritance.add(new Pair<>(pair.a, addedClasses));
    }
  }

  public void addAllDeletedInheritance(ICD4CodeArtifactScope srcCDScope,
                                       ICD4CodeArtifactScope tgtCDScope,
                                       List<MatchingStrategy<ASTCDType>> typeMatchers) {
    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      boolean found = false;
      List<ASTCDClass> deletedClasses = new ArrayList<>();
      Set<ASTCDType> srcDirectSuperClasses = getDirectSuperClasses(pair.a, srcCDScope);
      Set<ASTCDType> tgtDirectSuperClasses = getDirectSuperClasses(pair.b, tgtCDScope);
      for(ASTCDType tgtSuperType : tgtDirectSuperClasses){
        for(ASTCDType srcSuperType : srcDirectSuperClasses){
          for(MatchingStrategy<ASTCDType> typeMatcher : typeMatchers){
            if(typeMatcher.isMatched(srcSuperType, tgtSuperType)){
              found = true;
              break;
            }
          }
        }
        if(!found){
          deletedClasses.add((ASTCDClass) tgtSuperType);
          if(!baseDiff.contains(DiffTypes.REMOVED_INHERITANCE)){
            baseDiff.add(DiffTypes.REMOVED_INHERITANCE);
          }
        }
      }
      deletedInheritance.add(new Pair<>(pair.b, deletedClasses));
    }
  }


  private void loadAllLists(ASTCDCompilationUnit srcCD,
                            ASTCDCompilationUnit tgtCD,
                            ICD4CodeArtifactScope srcCDScope,
                            ICD4CodeArtifactScope tgtCDScope,
                            List<MatchingStrategy<ASTCDType>> typeMatchers,
                            List<MatchingStrategy<ASTCDAssociation>> assocMatchers) {
    addAllMatchedClasses(srcCD, tgtCD, typeMatchers);
    addAllMatchedInterfaces(srcCD, tgtCD, typeMatchers);
    addAllMatchedAssocs(srcCD, tgtCD, assocMatchers);
    addAllMatchedEnums(srcCD, tgtCD, typeMatchers);
    addAllchangedTypes(srcCDScope, tgtCDScope);
    addAllChangedAssocs();
    addAllAddedClasses(srcCD, tgtCD);
    addAllDeletedClasses(srcCD, tgtCD);
    addAllAddedEnums(srcCD, tgtCD);
    addAllDeletedEnums(srcCD, tgtCD);
    addAllAddedAssocs(srcCD, tgtCD, assocMatchers);
    addAllDeletedAssocs(srcCD, tgtCD, assocMatchers);
    addAllAddedInheritance(srcCDScope, tgtCDScope, typeMatchers);
    addAllDeletedInheritance(srcCDScope, tgtCDScope, typeMatchers);
  }

  private void setStrings(ICD4CodeArtifactScope scopeSrcCD, ICD4CodeArtifactScope scopeTgtCD) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder initialPrintAdd = new StringBuilder();
    StringBuilder initialPrintDelete = new StringBuilder();
    StringBuilder initialPrintChange = new StringBuilder();
    List<Pair<Integer, String>> onlySrcCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyTgtCDSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyAddedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyDeletedSort = new ArrayList<>();
    List<Pair<Integer, String>> onlyChangedSort = new ArrayList<>();

    initialPrintAdd
      .append(System.lineSeparator())
      .append("The following elements were added to ")
      .append(srcCD.getCDDefinition().getName())
      .append(" while comparing it to ")
      .append(tgtCD.getCDDefinition().getName())
      .append(":");
    initialPrintDelete
      .append(System.lineSeparator())
      .append("The following elements were removed while comparing ")
      .append(srcCD.getCDDefinition().getName())
      .append(" to ")
      .append(tgtCD.getCDDefinition().getName())
      .append(":");
    initialPrintChange
      .append(System.lineSeparator())
      .append("The following diffs were found while comparing ")
      .append(srcCD.getCDDefinition().getName())
      .append(" to ")
      .append(tgtCD.getCDDefinition().getName())
      .append(":");

    for(CDTypeDiff x : changedTypes) {
      if(x.getBaseDiff().contains(DiffTypes.ADDED_ATTRIBUTE) || x.getBaseDiff().contains(DiffTypes.ADDED_CONSTANT)) {
        String tmp = x.printIfAddedAttr();
        onlyAddedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
      }
      if(x.getBaseDiff().contains(DiffTypes.REMOVED_ATTRIBUTE) || x.getBaseDiff().contains(DiffTypes.DELETED_CONSTANT)) {
        String tmp = x.printIfRemovedAttr();
        onlyDeletedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), tmp));
      }
      if(!x.getBaseDiff().isEmpty()) {
        onlySrcCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printSrcCD()));
        onlyTgtCDSort.add(new Pair<>(x.getTgtElem().get_SourcePositionStart().getLine(), x.printTgtCD()));
        onlyChangedSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printChangedType()));
      }
    }

    for(CDAssocDiff x : changedAssocs) {
      if(!x.getBaseDiff().isEmpty()) {
        onlySrcCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
        onlyTgtCDSort.add(new Pair<>(x.getSrcElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
        onlyChangedSort.add(new Pair<>(x.getTgtElem().get_SourcePositionStart().getLine(), x.printDiffAssoc()));
      }
    }

    if (!addedClasses.isEmpty()) {
      for (ASTCDClass x : addedClasses) {
        CDTypeDiff diff = new CDTypeDiff(x, x, scopeSrcCD, scopeTgtCD);
        String tmp = diff.printAddedType() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedClasses.isEmpty()) {
      for (ASTCDClass x : deletedClasses) {
        CDTypeDiff diff = new CDTypeDiff(x, x, scopeSrcCD, scopeTgtCD);
        String tmp = diff.printRemovedType() + RESET;
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedEnums.isEmpty()) {
      for (ASTCDEnum x : addedEnums) {
        CDTypeDiff diff = new CDTypeDiff(x, x, scopeSrcCD, scopeTgtCD);
        String tmp = diff.printAddedType() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedEnums.isEmpty()) {
      for (ASTCDEnum x : deletedEnums) {
        CDTypeDiff diff = new CDTypeDiff(x, x, scopeSrcCD, scopeTgtCD);
        String tmp = diff.printRemovedType() + RESET;
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedAssocs.isEmpty()) {
      for (ASTCDAssociation x : addedAssocs) {
        CDAssocDiff diff = new CDAssocDiff(x, x, srcCD, srcCD);
        String tmp = diff.printAddedAssoc() + RESET;
        onlySrcCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyAddedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedAssocs.isEmpty()) {
      for (ASTCDAssociation x : deletedAssocs) {
        CDAssocDiff diff = new CDAssocDiff(x, x, tgtCD, tgtCD);
        String tmp = diff.printDeletedAssoc() + RESET;
        onlyTgtCDSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyDeletedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
        onlyChangedSort.add(new Pair<>(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    //--print src
    onlySrcCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlySrc = new StringBuilder();
    outputOnlySrc.append("classdiagram ").append(srcCD.getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlySrcCDSort) {
      outputOnlySrc.append(System.lineSeparator()).append(x.b);
    }
    outputOnlySrc.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    this.outputSrc = outputOnlySrc;

    //--print tgt
    onlyTgtCDSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outputOnlyTgt = new StringBuilder();
    outputOnlyTgt.append("classdiagram ").append(tgtCD.getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlyTgtCDSort) {
      outputOnlyTgt.append(System.lineSeparator()).append(x.b);
    }
    outputOnlyTgt.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    this.outputTgt = outputOnlyTgt;

    //--print added
    onlyAddedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyAdded = new StringBuilder();
    outPutOnlyAdded.append(initialPrintAdd);
    for (Pair<Integer, String> x : onlyAddedSort) {
      outPutOnlyAdded.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputAdded = outPutOnlyAdded;

    //--print deleted
    onlyDeletedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyDeleted = new StringBuilder();
    outPutOnlyDeleted.append(initialPrintDelete);
    for (Pair<Integer, String> x : onlyDeletedSort) {
      outPutOnlyDeleted.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputDeleted = outPutOnlyDeleted;

    //--print diff
    onlyChangedSort.sort(Comparator.comparing(p -> +p.a));
    StringBuilder outPutOnlyChanged = new StringBuilder();
    outPutOnlyChanged.append(initialPrintChange);
    for (Pair<Integer, String> x : onlyChangedSort) {
      outPutOnlyChanged.append(System.lineSeparator()).append(System.lineSeparator()).append(x.b);
    }
    this.outputChanged = outPutOnlyChanged;
  }

  //--print src
  public String printSrcCD () { return outputSrc.toString(); }
  //--print tgt
  public String printTgtCD() { return outputTgt.toString(); }
  //--print added
  public String printOnlyAdded() { return outputAdded.toString(); }
  //--print deleted
  public String printOnlyDeleted() { return outputDeleted.toString(); }
  //--print diff
  public String printDiff() { return outputChanged.toString(); }
  //--print changed
  //public String printOnlyChanged() { return outputChanged.toString(); }
}
