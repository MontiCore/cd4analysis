package de.monticore.cddiff.syndiff.imp;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.ICDSyntaxDiff;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isAttributInSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isNewSuper;

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
    //implemented
  }

  /**
   *
   * Check if a deleted @param astcdAssociation was need in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
    ASTCDCardinality leftCar;
    ASTCDCardinality rightCar;
    if (astcdAssociation.getLeft().isPresentCDCardinality()){
      Optional<ASTNatLiteral> literal = Optional.ofNullable(astcdAssociation.getLeft().getCDCardinality().toCardinality().getLowerBoundLit());
      if (!(literal.isPresent()) || astcdAssociation.getLeft().getCDCardinality().toCardinality().getLowerBound() == 0){
        //add to Diff List
        ASTCDClass astcdClass = getConnectedClasses(astcdAssociation).a;
      }
    }
    if (astcdAssociation.getLeft().isPresentCDCardinality()){
      Optional<ASTNatLiteral> literal = Optional.ofNullable(astcdAssociation.getRight().getCDCardinality().toCardinality().getLowerBoundLit());
      if (literal.isPresent() || astcdAssociation.getRight().getCDCardinality().toCardinality().getLowerBound() == 0){
        //add to Diff List
        ASTCDClass astcdClass = getConnectedClasses(astcdAssociation).b;
      }
    }
    return false;
    //not implemented
  }

  /**
   *
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   *
   * @return true if a class instantiate another one by @param association.
   */
  @Override
  public boolean isAlwaysNeededAssoc(ASTCDAssociation astcdAssociation) {
    return false;
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
    return null;
  }

  /**
   * Compute the classes that extend a given class.
   *
   * @param astcdClass
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  @Override
  public List<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass){
    return null;
    //was impelemented in deleted commits
  }

  @Override
  public void isClassNeeded() {
    //not implemented
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
}
