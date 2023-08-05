package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.*;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.AssocStruct;
import de.monticore.cddiff.syndiff.CardinalityStruc;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDSyntaxDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;

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
  private List<InheritanceDiff> addedInheritance;
  private List<InheritanceDiff> deletedInheritance;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  // needed baseDiff?????
  private List<DiffTypes> baseDiff;
  protected MatchingStrategy<ASTCDType> typeMatcher;
  protected MatchingStrategy<ASTCDAssociation> assocMatcher;

  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  public Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  private ArrayListMultimap<ASTCDClass, AssocStruct> srcMap = ArrayListMultimap.create();
  private ArrayListMultimap<ASTCDClass, AssocStruct> trgMap = ArrayListMultimap.create();

 public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
   helper.setSrcCD(srcCD);
   helper.setTgtCD(tgtCD);

   CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    String hello = "Hello";
    System.out.println(hello);

    new CD4CodeDirectCompositionTrafo().transform(srcCD);
    new CD4CodeDirectCompositionTrafo().transform(tgtCD);

    final String GREEN = "\u001B[32m";
    final String RED = "\u001B[31m";
    final String YELLOW = "\u001B[33m";
    final String RESET = "\u001B[0m";


    StringBuilder initial = new StringBuilder();

    initial
      .append(System.lineSeparator())
      .append("Printing the differences between the (new) CD")
      .append(srcCD.getCDDefinition().getName())
      .append(" and the (old) CD ")
      .append(tgtCD.getCDDefinition().getName())
      .append(" is created")
      .append(System.lineSeparator())
      .append(System.lineSeparator());

//    for (CDTypeDiff x : changedClasses) {
//      System.out.println(x);
//    }

    StringBuilder outPutAll = new StringBuilder();
    outPutAll.append(initial);
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

  public List<InheritanceDiff> getAddedInheritance() {
    return addedInheritance;
  }
  public void setAddedInheritance(List<InheritanceDiff> addedInheritance) {
    this.addedInheritance = addedInheritance;
  }
  public List<InheritanceDiff> getDeletedInheritance() {
    return deletedInheritance;
  }
  public void setDeletedInheritance(List<InheritanceDiff> deletedInheritance) {
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

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  /**
   * Checks if each of the added classes refactors the old structure. The class must be abstract,
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
//            if (!matchedClass.getCDAttributeList().contains(attribute) || !isAttributeInSuper(attribute, matchedClass,
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

  public Set<Pair<ASTCDClass, ASTCDClass>> deletedClasses(){
    Set<Pair<ASTCDClass, ASTCDClass>> diff = new HashSet<>();
    for (InheritanceDiff struc : deletedInheritance){
      List<ASTCDClass> subclasses = struc.getOldDirectSuper();
      for (ASTCDClass subClass : subclasses){
        if (isClassDeleted(struc.getAstcdClasses().a, subClass)){
          diff.add(new Pair<>(struc.getAstcdClasses().a, subClass));
        }
      }
    }
    return diff;
  }

  //TODO
  public boolean isClassDeleted(ASTCDClass astcdClass, ASTCDClass subClass){
    //check if a deleted class brings a semantic difference
    //check if all subclasses have the attributes from this class from tgt tgtCD
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getAllAttr(astcdClass);
    ASTCDClass matchedClass = findMatchingClassSrc(subClass);
    if (matchedClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesSrc().contains(matchedClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, matchedClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(matchedClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
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
    for (AssocStruct assocStruct : getHelper().getSrcMap().get(astcdClass)){
      for (AssocStruct baseAssoc : getHelper().getSrcMap().get(subClass)){
        if (Syn2SemDiffHelper.sameAssociationType(baseAssoc.getAssociation(), assocStruct.getAssociation())
          || Syn2SemDiffHelper.sameAssociationTypeInReverse(baseAssoc.getAssociation(), assocStruct.getAssociation())){
          isContained = true;
        }
      }
      if (!isContained){
        return true;
      } else {
        isContained = false;
      }
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them
    return false;
  }

  public ASTCDClass findMatchingClassSrc(ASTCDClass astcdClass){
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if (pair.b.equals(astcdClass)){
        return pair.a;
      }
    }
    return null;
  }

  public Pair<ASTCDAssociation, List<ASTCDClass>> deletedAssoc(ASTCDAssociation astcdAssociation){
    List<ASTCDClass> classes = new ArrayList<>();
    if (astcdAssociation.getLeft().isPresentCDCardinality()){
      if (!(astcdAssociation.getLeft().getCDCardinality().isOpt()
        || astcdAssociation.getLeft().getCDCardinality().isMult())){
        ASTCDClass astcdClass = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, tgtCD).a;
        ASTCDClass matched = isSecondElementInPair(astcdClass);
        if (matched != null){
          classes.add(matched);
        }
      }
    }
    if (astcdAssociation.getRight().isPresentCDCardinality()){
      if (!(astcdAssociation.getRight().getCDCardinality().isMult()
        || astcdAssociation.getRight().getCDCardinality().isOpt())){
        ASTCDClass astcdClass = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, tgtCD).b;
        ASTCDClass matched = isSecondElementInPair(astcdClass);
        if (matched != null){
          classes.add(matched);
        }
      }
    }
    if (!classes.isEmpty()){
      return new Pair<>(astcdAssociation, classes);
    }
    return null;
  }

  public ASTCDClass isSecondElementInPair(ASTCDClass astClass) {
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      if (pair.b.equals(astClass)) {
        return pair.a;
      }
    }
    return null;
  }


  /**
   *
   * Check if a deleted @param astcdAssociation was needed in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  //false idea
  //if old was bidirectional, check if both sides allowed 0. If this is the case - we have no semDiff
  //if this isn't the case, we have a semDiff for the side(s) that doesn't allow 0
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      //List<ASTCDClass> superClassesLeft = Syn2SemDiffHelper.getSuperClasses(this, pair.a);
      List<ASTCDClass> superClassesRight = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.b);
      for (AssocStruct association : helper.getSrcMap().get(pair.a)){
        if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Left
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).b)){
          return false;
        } else if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Right
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
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
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getSide() == ClassSide.Right //reversed assoc
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
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
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getDirection() == AssocDirection.RightToLeft
          && association.getSide() == ClassSide.Right
          && Syn2SemDiffHelper.sameAssociationType(association.getAssociation(), astcdAssociation)
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAddedAssoc(ASTCDAssociation astcdAssociation) {
    //TODO: ask Tsveti
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
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
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
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
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

//  /**
//   * Deleted Enum-classes always bring a semantic difference - a class can be instantiated without
//   * attribute. Similar case for added ones.
//   *
//   * @param astcdEnum
//   *///not needed - if a class can't be inst with an enum, then the attribute has been deleted
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
   * Get the differences in a matched pair as a String
   * @param diff object of type CDAssocDiff or CDTypeDIff
   * @return differences as a String
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
            //TODO: ADD RIGHT MULTIPLICITY
          case CHANGED_ASSOCIATION_LEFT_MULTIPLICITY:
            difference.append(obj.cardDiff());
        }
      }
      return difference.toString();
    }
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
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }

//                if (!association.getDirection().equals(AssocDirection.BiDirectional)
//                  && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
//                  if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  }
//                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  //save a list with assocs to delete
                  //deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                  //iterator.remove();
                  //save a list with classes to delete
                  //helper.getSrcMap().removeAll(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || Syn2SemDiffHelper.sameAssociationTypeInReverse(association.getAssociation(), superAssoc.getAssociation())) {
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
//                helper.getSrcMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  //deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //superAssocIterator.remove();
                } else {
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                  //iterator.remove();
                  // superAssocIterator.remove();
                  //helper.getSrcMap().removeAll(astcdClass);
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
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
//                if (!sameRoleNames(association, superAssoc)) {
//                  Log.error("Bad overlapping found");
//                }

//                if (!association.getDirection().equals(AssocDirection.BiDirectional)
//                  && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
//                  if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  }
//                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  //deleteAssocsFromTgt(astcdClass, getConflict(association, superAssoc));
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //superAssocIterator.remove();
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                  //iterator.remove();
                  //superAssocIterator.remove();
                  //helper.getTrgMap().removeAll(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || Syn2SemDiffHelper.sameAssociationTypeInReverse(association.getAssociation(), superAssoc.getAssociation())) {
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
//                helper.getTrgMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  //deleteAssocsFromTgt(astcdClass, getConflict(association, superAssoc));
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //superAssocIterator.remove();
                } else {
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                  //iterator.remove();
                  //superAssocIterator.remove();
                  // helper.getTrgMap().removeAll(astcdClass);
                }
              }
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass : srcToDelete){
      helper.getSrcMap().removeAll(astcdClass);
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : srcAssocsToDelete){
      deleteAssocsFromSrc(pair.a, pair.b);
    }
    for (Pair<AssocStruct, AssocStruct> pair : srcAssocsToMerge){
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete){
      setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
      mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete){
      helper.getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }

    for (ASTCDClass astcdClass : srcToDelete){
      helper.getTrgMap().removeAll(astcdClass);
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : tgtAssocsToDelete){
      deleteAssocsFromTgt(pair.a, pair.b);
    }
    for (Pair<AssocStruct, AssocStruct> pair : tgtAssocsToMerge){
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete){
      setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
      mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete){
      helper.getTrgMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
  }

  public void doSmt(ASTCDClass astcdClass){
    Set<ASTCDClass> srcToDelete = new HashSet<>();
    Set<Pair<ASTCDClass, ASTCDRole>> srcAssocsToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> srcAssocsToMerge = new HashSet<>();
    Set<DeleteStruc> srcAssocsToMergeWithDelete = new HashSet<>();
    for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
      if (!association.isSuperAssoc()) {
        for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
          if (!association.equals(superAssoc)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }

//                if (!association.getDirection().equals(AssocDirection.BiDirectional)
//                  && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
//                  if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
//                    association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)) {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
//                  } else {
//                    association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
//                  }
//                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                srcAssocsToMerge.add(new Pair<>(association, superAssoc));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  //save a list with assocs to delete
                  //deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                  //iterator.remove();
                  //save a list with classes to delete
                  //helper.getSrcMap().removeAll(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || Syn2SemDiffHelper.sameAssociationTypeInReverse(association.getAssociation(), superAssoc.getAssociation())) {
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
//                ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
//                CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
//                AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
//                AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
//                association.getAssociation().setCDAssocDir(direction);
//                association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
//                association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
//                helper.getSrcMap().remove(astcdClass, superAssoc);
              } else if (isInConflict(association, superAssoc) && !inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  //deleteAssocsFromSrc(astcdClass, getConflict(association, superAssoc));
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //iterator.remove();
                  //superAssocIterator.remove();
                } else {
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                  //iterator.remove();
                  // superAssocIterator.remove();
                  //helper.getSrcMap().removeAll(astcdClass);
                }
              }
            }
          }
        }
      }
    }
  }

  private void setBiDirRoleName(AssocStruct association, AssocStruct superAssoc){
    if (!association.getDirection().equals(AssocDirection.BiDirectional)
      && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
      if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)) {
        association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
        association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)) {
        association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else {
        association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      }
    }
  }
  private AssocStruct mergeAssocs(AssocStruct association, AssocStruct superAssoc){
    ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
    CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
    AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
    AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
    association.getAssociation().setCDAssocDir(direction);
    association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
    association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
    return association;
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
      return assocDown.getAssociation().getLeft().getCDRole().toString().equals(assocUp.getAssociation().getLeft().getCDRole().toString());
    } else if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Right)) {
      return assocDown.getAssociation().getLeft().getCDRole().toString().equals(assocUp.getAssociation().getRight().getCDRole().toString());
    } else if (assocDown.getSide().equals(ClassSide.Right) && assocUp.getSide().equals(ClassSide.Left)){
      return assocDown.getAssociation().getRight().getCDRole().toString().equals(assocUp.getAssociation().getLeft().getCDRole().toString());
    } else {
      return assocDown.getAssociation().getRight().getCDRole().toString().equals(assocUp.getAssociation().getRight().getCDRole().toString());
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
   * @return true, if they fulfill the condition
   */
  public boolean inInheritanceRelation(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());
      //do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());
    } else if (association.getSide().equals(ClassSide.Right)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());
    } else {
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());
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
   * Transform the internal cardinality to original
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

  public List<Pair<ASTCDClass, Set<ASTCDAttribute>>> allNewAttributes(){
    List<Pair<ASTCDClass, Set<ASTCDAttribute>>> list = new ArrayList<>();
    for (InheritanceDiff inheritanceDiff : addedInheritance){
      Pair<ASTCDClass, Set<ASTCDAttribute>> pair = newAttributes(inheritanceDiff);
      if (!helper.getNotInstanClassesSrc().contains(inheritanceDiff.getAstcdClasses().a)
        && !pair.b.isEmpty()){
        list.add(pair);
      }
    }
    return mergeSets(list);
  }

  public static List<Pair<ASTCDClass, Set<ASTCDAttribute>>> mergeSets(List<Pair<ASTCDClass, Set<ASTCDAttribute>>> list) {
    Map<ASTCDClass, Set<ASTCDAttribute>> classMap = new HashMap<>();

    for (Pair<ASTCDClass, Set<ASTCDAttribute>> pair : list) {
      ASTCDClass cdClass = pair.a;
      Set<ASTCDAttribute> attributeSet = pair.b;

      // Check if the class already exists in the map
      if (classMap.containsKey(cdClass)) {
        Set<ASTCDAttribute> mergedSet = classMap.get(cdClass);
        mergedSet.addAll(attributeSet);
      } else {
        // Add the class and its attribute set to the map
        classMap.put(cdClass, new HashSet<>(attributeSet));
      }
    }

    List<Pair<ASTCDClass, Set<ASTCDAttribute>>> mergedList = new ArrayList<>();
    for (Map.Entry<ASTCDClass, Set<ASTCDAttribute>> entry : classMap.entrySet()) {
      ASTCDClass cdClass = entry.getKey();
      Set<ASTCDAttribute> attributeSet = entry.getValue();
      mergedList.add(new Pair<>(cdClass, attributeSet));
    }

    return mergedList;
  }
  public Pair<ASTCDClass, Set<ASTCDAttribute>> newAttributes(InheritanceDiff inheritanceDiff) {
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getNewDirectSuper()){
      boolean isContained = false;
      for (ASTCDClass astcdClass1 : inheritanceDiff.getOldDirectSuper()){
        if (astcdClass.getName().equals(astcdClass1.getName())) {
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
      for (ASTCDAttribute attribute : getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getAllAttr(inheritanceDiff.getAstcdClasses().b).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.printType().equals(attribute1.printType())) {
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
        if (astcdClass.getName().equals(astcdClass1.getName())) {
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
      for (ASTCDAttribute attribute : getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getAllAttr(inheritanceDiff.getAstcdClasses().a).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.printType().equals(attribute1.printType())) {
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

  public Pair<ASTCDClass, Set<ASTCDAssociation>> newAssocs(InheritanceDiff inheritanceDiff){
    Set<ASTCDAssociation> associations = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getNewDirectSuper()){
      for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
        boolean isContained = false;
        for (AssocStruct assocStruct1 : helper.getTrgMap().get(inheritanceDiff.getAstcdClasses().a)){
          Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD);
          Pair<ASTCDClass, ASTCDClass> pair1 = Syn2SemDiffHelper.getConnectedClasses(assocStruct1.getAssociation(), tgtCD);
          if (sameTgt(assocStruct, assocStruct1)
            && (sameAssociation(assocStruct.getAssociation(), assocStruct1.getAssociation())
            || sameAssociation(assocStruct.getAssociation(), assocStruct1.getAssociation()))){
            isContained = true;
          }
        }
        if (!isContained){
          associations.add(assocStruct.getAssociation());
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, associations);
  }

  //composition - association.getCDType.isComposition()

  public boolean sameTgt(AssocStruct assocStruct, AssocStruct assocStruct2){
    if (assocStruct.getSide().equals(ClassSide.Left)){
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getName());
      } else {
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).a.getName());
      }
    }
    else {
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getName());
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

  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAssocList(){
    List<Pair<ASTCDClass, ASTCDAttribute>> pairList = new ArrayList<>();
    return pairList;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributeList() {
    List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses) {
      if (typeDiff.getSrcElem() instanceof ASTCDClass
        && !helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())
        && typeDiff.getBaseDiffs().contains(DiffTypes.ADDED_ATTRIBUTE)) {
        list.addAll(typeDiff.addedAttributes(tgtCD));
      }
    }
    return list;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributeList(){
    List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses) {
      if (typeDiff.getSrcElem() instanceof ASTCDClass
        && !helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())
        && typeDiff.getBaseDiffs().contains(DiffTypes.REMOVED_ATTRIBUTE)) {
        list.addAll(typeDiff.deletedAttributes(srcCD));
      }
    }
    return list;
  }

  public List<EnumStruc> addedConstantsList(){
    List<EnumStruc> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses){
      if (typeDiff.getSrcElem() instanceof ASTCDEnum){
        ASTCDClass astcdClass = null;
        ASTCDAttribute attribute = null;
        for (ASTCDEnumConstant enumConstant : typeDiff.getAddedConstants()){
          list.add(new EnumStruc(astcdClass, attribute, enumConstant));
        }
      }
    }
    return list;
  }

  public List<Pair<ASTCDClass, ASTCDAttribute>> changedAttributeList() {
    List<Pair<ASTCDClass, ASTCDAttribute>> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses) {
      if (typeDiff.getSrcElem() instanceof ASTCDClass
        && !helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())
        && typeDiff.getBaseDiffs().contains(DiffTypes.CHANGED_ATTRIBUTE)) {
        list.addAll(typeDiff.deletedAttributes(srcCD));
      }
    }
    return list;
  }

  // check if somewhere the comparison should be with unmatchedAssoc
  //if no match is found for src, we don't look anymore at this
  //if no match in tgt is found, we just add this assoc to diff
  //TODO: add check if the classes can be instantiated to added/deleted attributes, also for super?
  public boolean srcAndTgtExist(CDAssocDiff assocDiff){
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
    if (assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.a) != null
      || assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.b) != null){
      Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getTgtElem(), tgtCD);
      if ((assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.a) != null
        || assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.b) != null)){
        return true;
      }
    }
    return false;
  }

  public List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> changedRoleNameList() {
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs){
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
        if (srcAndTgtExist(assocDiff)) {
          //list.addAll(assocDiff.getRoleDiff());
        }
      }
    }
    return list;
  }

//  public List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> changedCardinalityList() {
//    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
//    for (CDAssocDiff assocDiff : changedAssocs){
//      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_MULTIPLICITY)) {
//        if (srcAndTgtExist(assocDiff)) {
//          list.addAll(assocDiff.getCardDiff());
//        }
//      }
//    }
//    return list;
//  }

  public List<Pair<ASTCDAssociation, ASTCDClass>> changedTargetList() {
    List<Pair<ASTCDAssociation, ASTCDClass>> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs){
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_TARGET)) {
        if (srcAndTgtExist(assocDiff)) {
          list.add(assocDiff.getChangedTgtClass());
        }
      }
    }
    return list;
  }

  public List<ASTCDAssociation> changedDirectionList() {
    List<ASTCDAssociation> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs){
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
        if (srcAndTgtExist(assocDiff)
          && assocDiff.isDirectionChanged()) {
          Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
          AssocStruct association = assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.a);
          AssocStruct assocStruct = assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.b);
          if (association != null){
            list.add(association.getAssociation());
          } else if (assocStruct != null){
            list.add(assocStruct.getAssociation());
          }
        }
      }
    }
    return list;
  }

  public List<AssocDiffStruc> changedAssoc(){
    List<AssocDiffStruc> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs) {
      AssocDiffStruc diff = new AssocDiffStruc();
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
        if (srcAndTgtExist(assocDiff)) {
          diff.setChangedRoleNames(assocDiff.getRoleDiff().b);
        }
      }
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
        if (srcAndTgtExist(assocDiff)
          && assocDiff.isDirectionChanged()) {
          diff.setChangedDir(true);
        }
      }

      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_TARGET)) {
        if (srcAndTgtExist(assocDiff)) {
          diff.setChangedTgt(assocDiff.getChangedTgtClass().b);
        }
      }
      // TODO: NOT LEFT MULTIPLICITY, DO ALSO FOR RIGHT MULTIPLICITY
      if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_LEFT_MULTIPLICITY)) {
        if (srcAndTgtExist(assocDiff)) {
          diff.setChangedCard(assocDiff.getCardDiff().b);
        }
      }
    }
    return list;
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

//  public List changedVisibilityList() { return null; }

//  public List overlappingAssocList() { return null; }
  //after overlappingAssocs there might be associations to classes that cannot be instantiated - function
  //check if exists after overlapping Assocs
  //TODO: also for classes - maybe do this when creating the list with changes as if(){...}
  //TODO: CDSyntax2SemDiff4ASTODHelper

  public void addAllChangedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDType srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDType tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
        CDTypeDiff diffClass = new CDTypeDiff(srcClass, tgtClass);
        // TODO Easier way to call all the functions from the class to fill the list baseDiff!!!!!
        diffClass.addAllChangedMembers(srcClass, tgtClass);
        diffClass.addAllAddedAttributes(srcClass, tgtClass);
        diffClass.addAllDeletedAttributes(srcClass, tgtClass);
        // for (ASTCDEnum srcEnum : srcClass.getCDDefinition().getCDEnumsList()) {
        //  for (ASTCDEnum tgtEnum : tgtClass.getCDDefinition().getCDEnumsList()) {
        //    diffClass.addAllAddedConstants(srcClass, tgtClass);
        //  }
        // }
        // for (ASTCDEnum srcEnum : srcClass.getCDDefinition().getCDEnumsList()) {
        //  for (ASTCDEnum tgtEnum : tgtClass.getCDDefinition().getCDEnumsList()) {
        //    diffClass.addAllDeletedConstants(srcClass,tgtClass);
        //  }
        // }
        if (!diffClass.getBaseDiffs().isEmpty()) {
          changedClasses.add(diffClass);
        }
      }
    }
  }

  public void addAllChangedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        // TODO: boolean
        CDAssocDiff diffAssoc = new CDAssocDiff(srcAssoc, tgtAssoc, false);
        if (!diffAssoc.getBaseDiff().isEmpty()) {
          changedAssocs.add(diffAssoc);
        }
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
      }
    }
  }

  public void addAllAddedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        if (srcAssoc.getName().equals(tgtAssoc.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedAssocs.add(srcAssoc);
      }
    }
  }

  public void addAllDeletedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
        if (srcAssoc.getName().equals(tgtAssoc.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedAssocs.add(tgtAssoc);
      }
    }
  }

  public void addAllMatchedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
        if (typeMatcher.isMatched(srcClass, tgtClass)) {
          matchedClasses.add(new Pair(srcClass, tgtClass));
        }
      }
    }
  }

  public void addAllMatchedInterfaces(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDInterface srcInterface : srcCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDInterface tgtInterface : tgtCD.getCDDefinition().getCDInterfacesList()) {
        if (typeMatcher.isMatched(srcInterface, tgtInterface)) {
          matchedInterfaces.add(new Pair(srcInterface, tgtInterface));
        }
      }
    }
  }

  public void addAllMatchedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    // 1. Phase - für SemDiff
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
          matchedAssocs.add(new Pair(srcAssoc, tgtAssoc));
        }
      }
    }

    // 2. Phase - added/deleted Assocs matching of Types
    for(ASTCDAssociation assoc1 : addedAssocs){
      for(ASTCDAssociation assoc2 : deletedAssocs){
        Optional<CDTypeSymbol> typeSymbol1L = srcCD.getEnclosingScope().resolveCDTypeDown(assoc1.getLeftQualifiedName().getQName());
        Optional<CDTypeSymbol> typeSymbol1R = srcCD.getEnclosingScope().resolveCDTypeDown(assoc1.getRightQualifiedName().getQName());
        Optional<CDTypeSymbol> typeSymbol2L = srcCD.getEnclosingScope().resolveCDTypeDown(assoc2.getLeftQualifiedName().getQName());
        Optional<CDTypeSymbol> typeSymbol2R = tgtCD.getEnclosingScope().resolveCDTypeDown(assoc1.getRightQualifiedName().getQName());
        ASTCDType srcTypeL = typeSymbol1L.get().getAstNode();
        ASTCDType srcTypeR = typeSymbol1R.get().getAstNode();
        ASTCDType tgtTypeL = typeSymbol2L.get().getAstNode();
        ASTCDType tgtTypeR = typeSymbol2R.get().getAstNode();
        if((typeMatcher.isMatched(srcTypeL,tgtTypeL) && typeMatcher.isMatched(srcTypeR,tgtTypeR))
        || (typeMatcher.isMatched(srcTypeL,tgtTypeR) && typeMatcher.isMatched(srcTypeR,tgtTypeL))) {
          matchedAssocs.add(new Pair(assoc1, assoc2));
        }
      }
    }
  }

  public void addAllMatchedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.deepEquals(tgtEnum)) {
          matchedEnums.add(new Pair(srcEnum, tgtEnum));
        }
      }
    }
  }
}
