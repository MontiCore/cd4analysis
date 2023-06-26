package de.monticore.cddiff.syndiff.imp;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSideBuilder;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationBuilder;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
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

  private ArrayListMultimap<ASTCDClass, Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> srcMap = ArrayListMultimap.create();
  private ArrayListMultimap<ASTCDClass, Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> trgMap = ArrayListMultimap.create();

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
          CD4CodeMill.scopesGenitorDelegator().createFromAST(getSrcCD()))){
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
              CD4CodeMill.scopesGenitorDelegator().createFromAST(getTrgCD()))){
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
        matchedClass = pair.b;;
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
   * Check if a deleted @param astcdAssociation was need in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
//    ASTCDCardinality leftCar;
//    ASTCDCardinality rightCar;
//    if (astcdAssociation.getLeft().isPresentCDCardinality()){
//      Optional<ASTNatLiteral> literal = Optional.ofNullable(astcdAssociation.getLeft().getCDCardinality().toCardinality().getLowerBoundLit());
//      if (!(literal.isPresent()) || astcdAssociation.getLeft().getCDCardinality().toCardinality().getLowerBound() == 0){
//        //add to Diff List
//        ASTCDClass astcdClass = getConnectedClasses(astcdAssociation).a;
//      }
//    }
//    if (astcdAssociation.getLeft().isPresentCDCardinality()){
//      Optional<ASTNatLiteral> literal = Optional.ofNullable(astcdAssociation.getRight().getCDCardinality().toCardinality().getLowerBoundLit());
//      if (!literal.isPresent() || astcdAssociation.getRight().getCDCardinality().toCardinality().getLowerBound() == 0){
//        //add to Diff List
//        ASTCDClass astcdClass = getConnectedClasses(astcdAssociation).b;
//      }
//    }
//    return false;
//    //not implemented
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
      List<ASTCDClass> superClassesLeft = getSuperClasses(pair.a);
      List<ASTCDClass> superClassesRight = getSuperClasses(pair.b);
      //leftSide
      int i = 0;
      for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> association : getSrcMap().get(pair.a)){
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Left
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getRight().getCDRole())
          && superClassesRight.contains(getConnectedClasses(astcdAssociation).b)
          ){
          i++;
        }
        if (i == 0){
          return false;
        }
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Right
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getRight().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && superClassesRight.contains(getConnectedClasses(astcdAssociation).a)){
          i++;
        }
        if (i == 2){
          return true;
        }
      }
      //rightSide
      for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> association : getSrcMap().get(pair.b)){
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Left
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getRight().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && superClassesLeft.contains(getConnectedClasses(astcdAssociation).a)){
          return true;
        }
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Right
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getRight().getCDRole())
          && superClassesLeft.contains(getConnectedClasses(astcdAssociation).b)){
          return true;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()){
      //leftSide
      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
      List<ASTCDClass> superClassesRight = getSuperClasses(pair.b);
      for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> association : getSrcMap().get(pair.a)){
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Left
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getRight().getCDRole())
          && superClassesRight.contains(getConnectedClasses(astcdAssociation).b)
        ){
          return true;
        }
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Right
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getRight().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && superClassesRight.contains(getConnectedClasses(astcdAssociation).a)){
          return true;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
      //rightSide
      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
      List<ASTCDClass> superClassesLeft = getSuperClasses(pair.a);
      for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> association : getSrcMap().get(pair.b)){
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Left
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getRight().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && superClassesLeft.contains(getConnectedClasses(astcdAssociation).a)){
          return true;
        }
        if (association.a == AssocDirection.BiDirectional
          && association.b.a == ClassSide.Right
          && astcdAssociation.getLeft().getCDCardinality().equals(association.b.b.getLeft().getCDCardinality())
          && astcdAssociation.getRight().getCDCardinality().equals(association.b.b.getRight().getCDCardinality())
          && astcdAssociation.getLeft().getCDRole().equals(association.b.b.getLeft().getCDRole())
          && astcdAssociation.getRight().getCDRole().equals(association.b.b.getRight().getCDRole())
          && superClassesLeft.contains(getConnectedClasses(astcdAssociation).b)){
          return true;
        }
      }
    }
    return false;
  }

  public List<ASTCDClass> getSuperClasses(ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = new ArrayList<>();
    for (ASTCDType type : getAllSuper(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(getSrcCD()))){
      if (type instanceof ASTCDClass){
        superClasses.add((ASTCDClass) type);
      }
    }
    return superClasses;
  }

  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAlwaysNeededAssoc(ASTCDAssociation astcdAssociation) {
    Map<ASTCDClass, Boolean> map = new HashMap<>();
    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      ASTCDClass classToCheck = getConnectedClasses(astcdAssociation).b;
      for (ASTCDClass astcdClass : getSpannedInheritance(classToCheck)){
        map.put(astcdClass, false);
        ASTCDClass matchedClass = findMatchedClass(astcdClass);
        if (matchedClass != null){
          List<Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> pairList = getTrgMap().get(matchedClass);
          for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair : pairList){
            if(sameAssociation(pair.b.b, astcdAssociation) || sameAssociationInReverse(pair.b.b, astcdAssociation)){
              map.remove(astcdClass);
              map.put(astcdClass, true);
            }
          }
        }
      }
    }

    Map<ASTCDClass, Boolean> map2 = new HashMap<>();
    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()){
      ASTCDClass classToCheck = getConnectedClasses(astcdAssociation).a;
      for (ASTCDClass astcdClass : getSpannedInheritance(classToCheck)){
        map2.put(astcdClass, false);
        ASTCDClass matchedClass = findMatchedClass(astcdClass);
        if (matchedClass != null){
          List<Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> pairList = getTrgMap().get(matchedClass);
          for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair : pairList){
            if(sameAssociation(pair.b.b, astcdAssociation) || sameAssociationInReverse(pair.b.b, astcdAssociation)){
              map2.remove(astcdClass);
              map2.put(astcdClass, true);
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass : map.keySet()){
      if (!map.get(astcdClass)){
        //add to diff list
        return false;
      }
    }

    for (ASTCDClass astcdClass : map2.keySet()){
      if (!map2.get(astcdClass)){
        //add to diff list
        return false;
      }
    }
    return true;
    //not needed - isNotNeededAssoc does the same
  }

  /**
   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without
   * attribute. Similar case for added ones.
   *
   * @param astcdEnum
   */
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

  /**
   * Compute the classes that extend a given class.
   *
   * @param astcdClass
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  @Override
  public List<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass){
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : getSrcCD().getCDDefinition().getCDClassesList()) {
      if ((getAllSuper(childClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(getSrcCD()))).contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    return subclasses;
  }

  @Override
  public ASTCDType isClassNeeded(CDTypeDiff pair) {
    ASTCDClass srcCLass = (ASTCDClass) pair.getElem1();
    if (!srcCLass.getModifier().isAbstract()){
      return pair.getElem1();
    }
    else{
      //do we check if assocs make sense - assoc to abstract class
      Set<ASTCDClass> map = getSrcMap().keySet();
      map.remove((ASTCDClass) pair.getElem1());
      for (ASTCDClass astcdClass : map){
        for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> mapPair : getSrcMap().get(astcdClass)){
          if (Objects.equals(mapPair.a, AssocDirection.LeftToRight) && getConnectedClasses(mapPair.b.b).b.equals(pair.getElem1()) && mapPair.b.b.getRight().getCDCardinality().isAtLeastOne()){
             //add to Diff List - class can be instantiated without the abstract class
            return astcdClass;
          } else if (Objects.equals(mapPair.a, AssocDirection.RightToLeft) && getConnectedClasses(mapPair.b.b).a.equals(pair.getElem1()) && mapPair.b.b.getLeft().getCDCardinality().isAtLeastOne()) {
            //add to Diff List - class can be instantiated without the abstract class
            return astcdClass;
          } else if (Objects.equals(mapPair.a, AssocDirection.BiDirectional)) {
            if (Objects.equals(mapPair.b.a, ClassSide.Left) && mapPair.b.b.getRight().getCDCardinality().isAtLeastOne()){
              //add to Diff List - class can be instantiated without the abstract class
              return astcdClass;
            } else if (mapPair.b.b.getLeft().getCDCardinality().isAtLeastOne()) {
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

//  public ArrayListMultimap<ASTCDAssociation, List<ASTCDAssociation>> findDuplicatedAssocs(){
//    ArrayListMultimap<ASTCDClass, Pair<String, Pair<String, ASTCDAssociation>>> map = getSrcMap();
//    ArrayListMultimap<ASTCDAssociation, List<ASTCDAssociation>> dupAssocMap = ArrayListMultimap.create();
//    for (ASTCDClass astcdClass : map.keySet()) {
//      for (Pair<String, Pair<String, ASTCDAssociation>> association1 : map.get(astcdClass)) {
//        for (Pair<String, Pair<String, ASTCDAssociation>> association2 : map.get(astcdClass)) {
//          if (association1 != association2) {
//            if (getConnectedClasses(association1.b.b).b.equals(getConnectedClasses(association2.b.b).b)
//              && matchRoleNames(association1.b.b.getLeft(), association2.b.b.getLeft())
//              && matchRoleNames(association1.b.b.getRight(), association2.b.b.getRight())) {
//              //add to Diff List - class can be instantiated without the abstract class
//            } else if (getConnectedClasses(association1.b.b).a.equals(getConnectedClasses(association2.b.b).a)
//              && matchRoleNames(association1.b.b.getLeft(), association2.b.b.getLeft())
//              && matchRoleNames(association1.b.b.getRight(), association2.b.b.getRight())) {
//              //add to Diff List - class can be instantiated without the abstract class
//            } else if (Objects.equals(mapPair.a, "<->")) {
//              if (Objects.equals(mapPair.b.a, "left") && mapPair.b.b.getRight().getCDCardinality().isAtLeastOne()) {
//                //add to Diff List - class can be instantiated without the abstract class
//                return astcdClass;
//              } else if (mapPair.b.b.getLeft().getCDCardinality().isAtLeastOne()) {
//                //add to Diff List - class can be instantiated without the abstract class
//                return astcdClass;
//              }
//            }
//          }
//        }
//      }
//    }
//  }

  public ArrayListMultimap<ASTCDAssociation, Pair<Boolean, ASTCDAssociation>> findDuplicatedAssocs() {
    ArrayListMultimap<ASTCDAssociation, Pair<Boolean, ASTCDAssociation>> dupAssocList = ArrayListMultimap.create();
    for (ASTCDAssociation association1 : getSrcCD().getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation association2 : getSrcCD().getCDDefinition().getCDAssociationsList()) {
        if (association1 != association2) {
          if (sameAssociation(association1, association2)
            && !dupAssocList.get(association2).contains(new Pair<>(true, association1))) {
            dupAssocList.put(association1, new Pair<>(true, association2));
          } else if (sameAssociationInReverse(association1, association2)
            && !dupAssocList.get(association2).contains(new Pair<>(false, association1))) {
            dupAssocList.put(association1, new Pair<>(false, association1));
          }
        }
      }
    }
    List<Pair<ASTCDAssociation, Pair<AssocCardinality, AssocCardinality>>> result = new ArrayList<>();
    for (ASTCDAssociation association : dupAssocList.keys()){
      AssocCardinality intersectionLeft = null;
      AssocCardinality intersectionRight = null;
      for (Pair<Boolean, ASTCDAssociation> pair : dupAssocList.get(association)){
        if (pair.a) {
          intersectionLeft = intersectCardinalities(intersectionLeft, cardToEnum(pair.b.getLeft().getCDCardinality()));
          intersectionRight = intersectCardinalities(intersectionRight, cardToEnum(pair.b.getRight().getCDCardinality()));
        }
        else {
          intersectionLeft = intersectCardinalities(intersectionLeft, cardToEnum(pair.b.getRight().getCDCardinality()));
          intersectionRight = intersectCardinalities(intersectionRight, cardToEnum(pair.b.getLeft().getCDCardinality()));
        }
      }
      result.add(new Pair<>(association, new Pair<>(intersectionLeft, intersectionRight)));
    }
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()) {
      for (ASTCDAssociation association : getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass)) {

      }
    }
    return dupAssocList;
  }

  /**
   * Compute all duplicated associations in srcCD.
   * For each class we first search for direct duplicated associations and after that for
   * duplicated associations with superClasses.
   * If such are found, the cardinalities and directions of the associations in
   * the srcMap are changed.
   */
  public void findDupAssocs(){
    ArrayListMultimap<ASTCDClass, ASTCDClass> multimap = ArrayListMultimap.create();
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superList = getAllSuper(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(getSrcCD()));
      for (ASTCDType astcdType : superList){
        if (astcdType instanceof ASTCDClass){
          multimap.put(astcdClass, (ASTCDClass) astcdType);
        }
      }
    }
    ArrayListMultimap<ASTCDAssociation, Pair<Boolean, ASTCDAssociation>> dupAssocList = ArrayListMultimap.create();
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()) {
      List<ASTCDAssociation> associationList = getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass);
      for (ASTCDAssociation association1 : associationList) {
        for (ASTCDAssociation association2 : associationList){
          if (association1 != association2) {
            if (sameAssociation(association1, association2)
              && !dupAssocList.get(association2).contains(new Pair<>(true, association1))) {
              dupAssocList.put(association1, new Pair<>(true, association2));
              associationList.remove(association2);
            } else if (sameAssociationInReverse(association1, association2)
              && !dupAssocList.get(association2).contains(new Pair<>(false, association1))) {
              dupAssocList.put(association1, new Pair<>(false, association1));
              associationList.remove(association2);
            }
          }
        }
      }
    }
    List<Pair<ASTCDAssociation, Pair<AssocCardinality, AssocCardinality>>> result = new ArrayList<>();
    for (ASTCDAssociation association : dupAssocList.keys()){
      AssocCardinality intersectionLeft = null;
      AssocCardinality intersectionRight = null;
      for (Pair<Boolean, ASTCDAssociation> pair : dupAssocList.get(association)){
        if (pair.a) {
          intersectionLeft = intersectCardinalities(intersectionLeft, cardToEnum(pair.b.getLeft().getCDCardinality()));
          intersectionRight = intersectCardinalities(intersectionRight, cardToEnum(pair.b.getRight().getCDCardinality()));
        }
        else {
          intersectionLeft = intersectCardinalities(intersectionLeft, cardToEnum(pair.b.getRight().getCDCardinality()));
          intersectionRight = intersectCardinalities(intersectionRight, cardToEnum(pair.b.getLeft().getCDCardinality()));
        }
      }
      result.add(new Pair<>(association, new Pair<>(intersectionLeft, intersectionRight)));
    }

    for (ASTCDAssociation association : dupAssocList.keySet()){
      ASTCDAssociation newAssoc = new ASTCDAssociation();
      if (findPair(result, association).isPresent()){
        //set cardinalities
        //set direction
      }
      Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association);
      getSrcMap().put(pair.a, new Pair<>(null, new Pair<>(null, newAssoc)));
      getSrcMap().put(pair.b, new Pair<>(null, new Pair<>(null, newAssoc)));
      for (Pair<Boolean, ASTCDAssociation> astcdAssociation : dupAssocList.get(association)){
        Optional<Pair<String, Pair<String, ASTCDAssociation>>> foundPair = findPair1(getSrcMap().get(pair.a), astcdAssociation.b);
        Optional<Pair<String, Pair<String, ASTCDAssociation>>> foundPair1 = findPair1(getSrcMap().get(pair.b), astcdAssociation.b);
        getSrcMap().remove(pair.a, foundPair.get());
        getSrcMap().remove(pair.b, foundPair1.get());
      }
    }
    //superAccos are saved in the map - need to make a check if the trgs of superAssoc and assoc are in the same inheritance
    for (ASTCDClass astcdClass : getSrcMap().keySet()){
      for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> association : getSrcMap().get(astcdClass)){
        for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> superAssoc : getSrcMap().get(astcdClass)){
          if (!getConnectedClasses(superAssoc.b.b).a.equals(astcdClass) && !getConnectedClasses(superAssoc.b.b).b.equals(astcdClass)){
            //check if target of superAssoc is in inheritance(can I use inConflict from Max?)
            //if true change cardinality and direction of association
            //if false - found error (superAssoc can't be added)
          }
        }
      }
    }
  }

  public static Optional<Pair<String, Pair<String, ASTCDAssociation>>> findPair1(
    List<Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> list, ASTCDAssociation association){
    for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair : list){
      if (pair.b.b.equals(association)){
        Optional.of(pair);
      }
    }
    return Optional.empty();
  }

  public static Optional<Pair<ASTCDAssociation, Pair<AssocCardinality, AssocCardinality>>> findPair(
    List<Pair<ASTCDAssociation, Pair<AssocCardinality, AssocCardinality>>> list, ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, Pair<AssocCardinality, AssocCardinality>> pair : list) {
      if (pair.a.equals(association)) {
        return Optional.of(pair);
      }
    }
    return Optional.empty();
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
   * @param cardinalityA
   * @param cardinalityB
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

  @Override
  public ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>>
      findOverlappingAssocs() {
    ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> mapLeftToRight =
        ArrayListMultimap.create();
    ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> mapRightToLeft =
        ArrayListMultimap.create();
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()) {
      mapLeftToRight.put(astcdClass, null);
      mapRightToLeft.put(astcdClass, null);
      List<ASTCDAssociation> assocsToCheck = new ArrayList<>();
      assocsToCheck.addAll(getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass));
      // need to add all superAssocs(CDAssocHelper?)
      for (ASTCDAssociation astcdAssociation :
          getSrcCD().getCDDefinition().getCDAssociationsListForType(astcdClass)) {
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
        if (pair.a
            .getSymbol()
            .getInternalQualifiedName()
            .equals(astcdClass.getSymbol().getInternalQualifiedName())) {
          for (ASTCDAssociation astcdAssociation1 : assocsToCheck) {
            // what to do when the class is at both ends
            if (!astcdAssociation.equals(astcdAssociation1)
                && matchRoleNames(astcdAssociation.getRight(), astcdAssociation1.getRight())
                && getSrcCD()
                    .getEnclosingScope()
                    .resolveDiagramDown(astcdAssociation.getRightQualifiedName().getQName())
                    .equals(
                        getSrcCD()
                            .getEnclosingScope()
                            .resolveDiagramDown(
                                astcdAssociation1.getRightQualifiedName().getQName()))) {
              mapLeftToRight.put(astcdClass, new Pair<>(astcdAssociation, astcdAssociation1));
              // assocs1 needs to be deleted if not from superclass
              // Can I change the ASTCdCompilationUnit?
            }
          }
        }
        if (pair.b
            .getSymbol()
            .getInternalQualifiedName()
            .equals(astcdClass.getSymbol().getInternalQualifiedName())) {
          for (ASTCDAssociation astcdAssociation1 : assocsToCheck) {
            if (!astcdAssociation.equals(astcdAssociation1)
                && matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft())
                && getSrcCD()
                    .getEnclosingScope()
                    .resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName())
                    .equals(
                        getSrcCD()
                            .getEnclosingScope()
                            .resolveDiagramDown(
                                astcdAssociation1.getLeftQualifiedName().getQName()))) {
              mapRightToLeft.put(astcdClass, new Pair<>(astcdAssociation, astcdAssociation1));
              // assocs1 needs to be deleted if not from superclass
              // Can I change the ASTCdCompilationUnit?
            }
          }
        }
      }
    }
    ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> checkedForType1 = getType1Conf(mapLeftToRight, mapRightToLeft);
    return null;
  }

  @Override
  public Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association) {
    Optional<CDTypeSymbol> astcdClass =
        getSrcCD()
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
        getSrcCD()
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    return new Pair<ASTCDClass, ASTCDClass>(
        (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
  }

  /**
   * We check each given pair of association if it fulfills the conditions for conflictType1. For
   * each pair we know that they have the same role in the trgDirection. The function checks if
   * there is an inheritance relation between the trgClasses that we get to via the assocs. If there
   * is no inheritance between them, we have a conflict.
   *
   * @param map1 LeftToRight
   * @param map2 RightToLeft
   * @return map with pairs of assocs that have a conflict of this type.
   */
  private ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> getType1Conf(
      ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> map1,
      ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> map2) {
    ArrayListMultimap<ASTCDClass, Pair<ASTCDAssociation, ASTCDAssociation>> foundConflicts =
        ArrayListMultimap.create();
    for (ASTCDClass astcdClass : map1.keySet()) {
      for (Pair<ASTCDAssociation, ASTCDAssociation> pair : map1.get(astcdClass)) {
        ASTCDClass rightClass1 = getConnectedClasses(pair.a).b;
        ASTCDClass rightCLass2 = getConnectedClasses(pair.b).b;
        boolean isSubclass =
            CDDiffUtil.getAllSuperclasses(
                    rightClass1, getSrcCD().getCDDefinition().getCDClassesList())
                .contains(rightCLass2);
        boolean isSubclassReverse =
            CDDiffUtil.getAllSuperclasses(
                    rightCLass2, getSrcCD().getCDDefinition().getCDClassesList())
                .contains(rightClass1);
        if (!(isSubclass || isSubclassReverse)) {
          // foundConflict
          foundConflicts.put(astcdClass, pair);
        }
      }
    }
    for (ASTCDClass astcdClass : map2.keys()) {
      for (Pair<ASTCDAssociation, ASTCDAssociation> pair : map1.get(astcdClass)) {
        ASTCDClass leftClass1 = getConnectedClasses(pair.a).a;
        ASTCDClass leftCLass2 = getConnectedClasses(pair.b).a;
        boolean isSubclass =
            CDDiffUtil.getAllSuperclasses(
                    leftClass1, getSrcCD().getCDDefinition().getCDClassesList())
                .contains(leftCLass2);
        boolean isSubclassReverse =
            CDDiffUtil.getAllSuperclasses(
                    leftCLass2, getSrcCD().getCDDefinition().getCDClassesList())
                .contains(leftClass1);
        if (!(isSubclass || isSubclassReverse)) {
          // foundConflict
          foundConflicts.put(astcdClass, pair);
        }
      }
    }
    return foundConflicts;
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
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, astcdAssociation)));
          }
          else {
            srcMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, astcdAssociation)));
          }
        } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, astcdAssociation)));
          }
          else {
            srcMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, astcdAssociation)));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTrgCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getTrgCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, astcdAssociation)));
          }
          else {
            trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, astcdAssociation)));
          }
        } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, astcdAssociation)));
          }
          else {
            trgMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, astcdAssociation)));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(getSrcCD()));
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          for (ASTCDAssociation association : getSrcCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association);
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              ASTCDAssocLeftSideBuilder leftSideBuilder = CD4CodeMill.cDAssocLeftSideBuilder().setMCQualifiedType(association.getLeft().getMCQualifiedType())
                .setModifier(association.getLeft().getModifier())
                .setCDCardinality(association.getLeft().getCDCardinality())
                .setCDRole(association.getLeft().getCDRole());
//              //subClass must be set on the left side - how
              ASTCDAssociation assocForSubClass = builder.setCDAssocDir(association.getCDAssocDir())
                .setCDAssocType(association.getCDAssocType())
                .setModifier(association.getModifier())
                .setName(association.getName())
                .setLeft(leftSideBuilder.build())
                //.setRight()
                .build();
              if (association.getCDAssocDir().isBidirectional()) {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, assocForSubClass)));
              }
              else {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, association)));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              if (association.getCDAssocDir().isBidirectional()) {
                srcMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, association)));
              }
              else {
                srcMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, association)));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTrgCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = getAllSuper(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(getTrgCD()));
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          for (ASTCDAssociation association : getTrgCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association);
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              if (association.getCDAssocDir().isBidirectional()) {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Left, association)));
              }
              else {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.LeftToRight, new Pair<>(ClassSide.Left, association)));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              if (association.getCDAssocDir().isBidirectional()) {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.BiDirectional, new Pair<>(ClassSide.Right, association)));
              }
              else {
                trgMap.put(astcdClass, new Pair<>(AssocDirection.RightToLeft, new Pair<>(ClassSide.Right, association)));
              }
            }
          }
        }
      }
    }

//    Set<ASTCDAssociation> srcAssocs = collectSuperAssociations(getSrcCD(), getSrcCD());
//    if (srcAssocs != null){
//      for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
//        for (ASTCDAssociation astcdAssociation : srcAssocs){
//          Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
//          if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())
//          || (pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())){
//            srcMap.put(astcdClass, astcdAssociation);
//          }
//        }
//      }
//    }
//
//    Set<ASTCDAssociation> trgAssocs = collectSuperAssociations(getSrcCD(), getSrcCD());
//    if (trgAssocs != null){
//      for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
//        for (ASTCDAssociation astcdAssociation : trgAssocs){
//          Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation);
//          if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())
//            || (pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())){
//            trgMap.put(astcdClass, astcdAssociation);
//          }
//        }
//      }
//    }
  }

  public ArrayListMultimap<ASTCDClass, Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> getSrcMap() {
    return srcMap;
  }

  public ArrayListMultimap<ASTCDClass, Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> getTrgMap() {
    return trgMap;
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
    }
    return null;
  }

  /**
   * Find all differences (with additional information) in a pair of changed types
   * @param typeDiff
   */
  public void findTypeDiff(CDTypeDiff typeDiff){
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
  }

  /**
   * Find all differences (with additional information) in a pair of changed associations
   * @param assocDiff
   */
  public void findAssocDiff(CDAssocDiff assocDiff){
    List<Object> list = new ArrayList<>();
    for (DiffTypes types : assocDiff.getBaseDiff()){
      switch (types){
        case CHANGED_ASSOCIATION_MULTIPLICITY: list.addAll(assocDiff.getCardDiff());
        case CHANGED_ASSOCIATION_DIRECTION: list.add(new Pair<>(assocDiff.getElem1(), assocDiff.getDirection(assocDiff.getElem1())));
        case CHANGED_ASSOCIATION_ROLE: list.addAll(assocDiff.getRoleDiff());
          //other cases?
      }
    }
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
        List<Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>>> list = getSrcMap().get(astcdClass);
        for (Pair<AssocDirection, Pair<ClassSide, ASTCDAssociation>> pair : list) {
          if (!objectSet.contains(pair.b.b)) {
            switch (pair.b.a) {
              case Left:
                if (pair.b.b.getRight().getCDCardinality().isAtLeastOne()) {
                  objectSet.add(pair.b.b);
                  objectSet.addAll(createChains(getConnectedClasses(pair.b.b).b, null, objectSet));
                }
              case Right:
                if (pair.b.b.getLeft().getCDCardinality().isAtLeastOne()) {
                  objectSet.add(pair.b.b);
                  objectSet.addAll(createChains(getConnectedClasses(pair.b.b).a, null, objectSet));
                }
            }
          }
        }
      }
    }
    else {
      if (!objectSet.contains(astcdAssociation)) {
        objectSet.addAll(createChains(getConnectedClasses(astcdAssociation).a, null, objectSet));
        objectSet.addAll(createChains(getConnectedClasses(astcdAssociation).b, null, objectSet));
      }
    }
    return objectSet;
  }
}
