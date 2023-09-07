package de.monticore.cddiff.syndiff.imp;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syndiff.OD.ODBuilder;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.odbasis._ast.ASTODAttribute;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.*;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;

public class Syn2SemDiffHelper {

  private static Syn2SemDiffHelper instance;

  public static Syn2SemDiffHelper getInstance() {
    if (instance == null) {
      instance = new Syn2SemDiffHelper();
    }
    return instance;
  }
  public Syn2SemDiffHelper() {
  }

  private ODBuilder ODBuilder = new ODBuilder();
  private ArrayListMultimap<ASTCDClass, AssocStruct> srcMap = ArrayListMultimap.create();
  private ArrayListMultimap<ASTCDClass, AssocStruct> trgMap = ArrayListMultimap.create();

  private Set<ASTCDClass> notInstanClassesSrc = new HashSet<>();

  private Set<ASTCDClass> notInstanClassesTgt = new HashSet<>();

  private ASTCDCompilationUnit srcCD;

  private ASTCDCompilationUnit tgtCD;

  public List<AssocStruct> getOtherAssocFromSuper(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass classToCheck : srcMap.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : srcMap.get(classToCheck)) {
          if (assocStruct.getSide().equals(ClassSide.Left)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  public ASTCDClass findMatchedClass(ASTCDClass astcdClass){
    ASTCDClass matchedClass = null;
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if(pair.a.equals(astcdClass)){
        matchedClass = pair.b;
      }
    }
    return matchedClass;
  }

  public ASTCDClass findMatchedSrc(ASTCDClass astcdClass){
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if (pair.b.equals(astcdClass)){
        return pair.a;
      }
    }
    return null;
  }

  public List<Pair<ASTCDClass, ASTCDClass>> getMatchedClasses() {
    return matchedClasses;
  }

  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;

  public static AssocDirection getDirection(ASTCDAssociation association) {
    if (association.getCDAssocDir() == null) {
      return AssocDirection.Unspecified;
    }
    if (association.getCDAssocDir().isBidirectional()) {
      return AssocDirection.BiDirectional;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return AssocDirection.RightToLeft;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return AssocDirection.LeftToRight;
    }
    return null;
  }

  static void setBiDirRoleName(AssocStruct association, AssocStruct superAssoc){
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

  static void mergeAssocs(AssocStruct association, AssocStruct superAssoc){
    ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
    CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
    AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
    AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
    association.getAssociation().setCDAssocDir(direction);
    if (association.getSide().equals(ClassSide.Left)) {
      association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
      association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
    } else {
      association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
      association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
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
  public static boolean isInConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Right)){
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    return false;
  }

  public static boolean sameRoleNames(AssocStruct assocDown, AssocStruct assocUp){
    if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Left)){
      return assocDown.getAssociation().getLeft().getCDRole().getName().equals(assocUp.getAssociation().getLeft().getCDRole().getName());
    } else if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Right)) {
      return assocDown.getAssociation().getLeft().getCDRole().getName().equals(assocUp.getAssociation().getRight().getCDRole().getName());
    } else if (assocDown.getSide().equals(ClassSide.Right) && assocUp.getSide().equals(ClassSide.Left)){
      return assocDown.getAssociation().getRight().getCDRole().getName().equals(assocUp.getAssociation().getLeft().getCDRole().getName());
    } else {
      return assocDown.getAssociation().getRight().getCDRole().getName().equals(assocUp.getAssociation().getRight().getCDRole().getName());
    }
  }

  /**
   * Given the two associations, get the role name that causes the conflict
   * @param association base association
   * @param superAssociation association from superclass
   * @return role name
   */
  //TODO: same as isInConflict
  public static ASTCDRole getConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight())){
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft())) {
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())) {
      return srcAssoc.getLeft().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Right)
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft())) {
      return srcAssoc.getLeft().getCDRole();
    }

    return null;
  }

  /**
   * Merge the directions of two associations
   * @param association association from the class
   * @param superAssociation association from the class or superAssociation
   * @return merged direction in ASTCDAssocDir
   */
  public static ASTCDAssocDir mergeAssocDir(AssocStruct association, AssocStruct superAssociation){
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
  public static CardinalityStruc getCardinalities(AssocStruct association, AssocStruct superAssociation){
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
  public static ASTCDCardinality createCardinality(AssocCardinality assocCardinality){
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
  public static boolean areZeroAssocs(AssocStruct association, AssocStruct superAssociation){
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

  public boolean isAdded(AssocStruct assocStruct, AssocStruct assocStruct2, ASTCDClass astcdClass, Set<DeleteStruc> set){
    for (DeleteStruc deleteStruc : set){
      if (((deleteStruc.getAssociation().equals(assocStruct)
        && deleteStruc.getSuperAssoc().equals(assocStruct2))
        || ((deleteStruc.getAssociation().equals(assocStruct2) && deleteStruc.getSuperAssoc().equals(assocStruct))))
        && deleteStruc.getAstcdClass().equals(astcdClass)){
        return true;
      }
    }
    return false;
  }

  //Why did I add this?
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

  //add those to deleted/added inheritance - done
  public boolean isContainedInSuper(ASTCDAssociation association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct.getAssociation(), association)
        || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct.getAssociation(), association)){
        return true;
      }
    }
    return false;
  }

  public ASTCDClass allSubclassesHaveIt(ASTCDAssociation association, ASTCDClass astcdClass){
    for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)){
      boolean isContained = false;
      for (AssocStruct assocStruct : srcMap.get(subClass)){
        if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct.getAssociation(), association)
          || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct.getAssociation(), association)){
          isContained = true;
          break;
        }
      }
      if (!isContained){
        return subClass;
      }
    }
    return null;
  }

  public AssocStruct getAssocStrucForClassTgt(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : trgMap.get(astcdClass)){
      if (CDAssociationHelper.sameAssociation(assocStruct.getAssociation(), association)
        || CDAssociationHelper.sameAssociationInReverse(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  /**
   * Get the AssocStruc that has the same type
   * @param astcdClass class to search in
   * @param association association to match with
   * @return matched association, if found
   */
  public AssocStruct getAssocStrucForClass(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (sameAssociationType(assocStruct.getAssociation(), association)
        || sameAssociationTypeInReverse(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  public boolean srcAssocExistsTgtNot(ASTCDAssociation association, ASTCDAssociation association2) {
    boolean exists1 = false;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    if (association.getCDAssocDir().isBidirectional()) {
      AssocStruct assocStructLeft = getAssocStrucForClass(pair.a, association);
      AssocStruct assocStructRight = getAssocStrucForClass(pair.b, association);
      if (assocStructLeft != null && assocStructRight != null) {
        exists1 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (getAssocStrucForClass(pair.b, association) != null) {
        exists1 = true;
      }
    } else {
      if (getAssocStrucForClass(pair.a, association) != null) {
        exists1 = true;
      }
    }

    boolean exists2 = false;
    Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    if (association.getCDAssocDir().isBidirectional()) {
      AssocStruct assocStructLeft = getAssocStrucForClass(pair2.a, association);
      AssocStruct assocStructRight = getAssocStrucForClass(pair2.b, association);
      if (assocStructLeft != null && assocStructRight != null) {
        exists2 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (getAssocStrucForClass(pair2.b, association) != null) {
        exists2 = true;
      }
    } else {
      if (getAssocStrucForClass(pair2.a, association) != null) {
        exists2 = true;
      }
    }
    return exists1 && !exists2;
  }

  public boolean srcNotTgtExists(ASTCDAssociation association, ASTCDAssociation association2){
    boolean exists1 = false;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    if (association.getCDAssocDir().isBidirectional()) {
      AssocStruct assocStructLeft = getAssocStrucForClass(pair.a, association);
      AssocStruct assocStructRight = getAssocStrucForClass(pair.b, association);
      if (assocStructLeft != null && assocStructRight != null) {
        exists1 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (getAssocStrucForClass(pair.b, association) != null) {
        exists1 = true;
      }
    } else {
      if (getAssocStrucForClass(pair.a, association) != null) {
        exists1 = true;
      }
    }

    boolean exists2 = false;
    Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    if (association.getCDAssocDir().isBidirectional()) {
      AssocStruct assocStructLeft = getAssocStrucForClass(pair2.a, association);
      AssocStruct assocStructRight = getAssocStrucForClass(pair2.b, association);
      if (assocStructLeft != null && assocStructRight != null) {
        exists2 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (getAssocStrucForClass(pair2.b, association) != null) {
        exists2 = true;
      }
    } else {
      if (getAssocStrucForClass(pair2.a, association) != null) {
        exists2 = true;
      }
    }
    return !exists1 && exists2;
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
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
      //do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    } else if (association.getSide().equals(ClassSide.Right)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    } else {
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    }
  }

  /**
   * Get all attributes that need to be added from inheritance structure to an object of a given type
   * @param astcdClass class
   * @return Pair of the class and a list of attributes
   */
  public Pair<ASTCDClass, List<ASTCDAttribute>> getAllAttr(ASTCDClass astcdClass){
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<ASTCDType> classes = getAllSuper(astcdClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    for (ASTCDType classToCheck : classes){
      if (classToCheck instanceof ASTCDClass) {
        attributes.addAll(classToCheck.getCDAttributeList());
      }
    }
    return new Pair<>(astcdClass, attributes);
  }

  public ArrayListMultimap<ASTCDClass, AssocStruct> getSrcMap() {
    return srcMap;
  }

  public ArrayListMultimap<ASTCDClass, AssocStruct> getTrgMap() {
    return trgMap;
  }

  public ASTCDCompilationUnit getSrcCD() {
    return srcCD;
  }

  public void setSrcCD(ASTCDCompilationUnit srcCD) {
    this.srcCD = srcCD;
  }

  public ASTCDCompilationUnit getTgtCD() {
    return tgtCD;
  }

  public void setTgtCD(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  public Set<ASTCDClass> getNotInstanClassesSrc() {
    return notInstanClassesSrc;
  }

  public void setNotInstanClassesSrc(Set<ASTCDClass> notInstanClassesSrc) {
    this.notInstanClassesSrc = notInstanClassesSrc;
  }

  public Set<ASTCDClass> getNotInstanClassesTgt() {
    return notInstanClassesTgt;
  }

  public void setNotInstanClassesTgt(Set<ASTCDClass> notInstanClassesTgt) {
    this.notInstanClassesTgt = notInstanClassesTgt;
  }

  public void updateSrc(ASTCDClass astcdClass){
    notInstanClassesSrc.add(astcdClass);
  }

  public void updateTgt(ASTCDClass astcdClass){
    notInstanClassesTgt.add(astcdClass);
  }

  public static boolean isAttContainedInClass(ASTCDAttribute attribute, ASTCDClass astcdClass){
    for (ASTCDAttribute att : astcdClass.getCDAttributeList()){
      if ((att.getName().equals(attribute.getName())
        && att.getMCType().printType().equals(attribute.getMCType().printType()))){
        return true;
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
    //TODO: change to work with assocstructs
    if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
      && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        //&& assoc1.getRight().getCDCardinality().equals(assoc2.getLeft().getCDCardinality())
        && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
    }

    if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
      && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        //&& assoc1.getLeft().getCDCardinality().equals(assoc2.getRight().getCDCardinality())
        && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
    }

    return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
      && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
      && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()))
      && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
  }

  public static boolean sameAssociationType(AssocStruct assocStruct1, ASTCDAssociation association, ClassSide side){
    if (((assocStruct1.getSide().equals(ClassSide.Left) && side.equals(ClassSide.Left))
      || assocStruct1.getSide().equals(ClassSide.Right) && side.equals(ClassSide.Right))){
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), association.getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), association.getRight());
    } else if (((assocStruct1.getSide().equals(ClassSide.Left) && side.equals(ClassSide.Right))
      || assocStruct1.getSide().equals(ClassSide.Right) && side.equals(ClassSide.Left))){
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), association.getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), association.getLeft());
    }
    return false;
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
        && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
    }

    if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
      && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
    }

    return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
      && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
      && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()))
      && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
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
  public static boolean sameAssociationTypeWithClasses(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    if (assoc1.getLeftQualifiedName().getQName().equals(assoc2.getLeftQualifiedName().getQName())
      && assoc1
      .getRightQualifiedName()
      .getQName()
      .equals(assoc2.getRightQualifiedName().getQName())) {
      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          //&& assoc1.getRight().getCDCardinality().equals(assoc2.getLeft().getCDCardinality())
          && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
          //&& assoc1.getLeft().getCDCardinality().equals(assoc2.getRight().getCDCardinality())
          && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
    }
    return false;
  }

  /**
   * 'sameAssociationType' for reversed directions
   * @param assoc1 first association
   * @param assoc2 second association
   * @return true, if all conditions are fulfilled
   */
  public static boolean sameAssociationTypeInReverseWithClasses(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {

    if (assoc1.getLeftQualifiedName().getQName().equals(assoc2.getRightQualifiedName().getQName())
      && assoc1
      .getRightQualifiedName()
      .getQName()
      .equals(assoc2.getLeftQualifiedName().getQName())) {

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()));
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
        && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight())
          && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
        && matchRoleNames(assoc1.getLeft(), assoc2.getRight())
        && isContainedIn(cardToEnum(assoc1.getRight().getCDCardinality()), cardToEnum(assoc2.getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assoc1.getLeft().getCDCardinality()), cardToEnum(assoc2.getRight().getCDCardinality()));
    }
    return false;
  }

  /**
   * Given the following two cardinalities, find their intersection
   * @param cardinalityA first cardinality
   * @param cardinalityB second cardinality
   * @return intersection of the cardinalities
   */
  public static AssocCardinality intersectCardinalities(AssocCardinality cardinalityA, AssocCardinality cardinalityB) {
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
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation, getSrcCD());
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName("");
        if (!copyAssoc.getLeft().isPresentCDCardinality()){
          copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()){
          copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
        }
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getTgtCD().getCDDefinition().getCDAssociationsListForType(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation, getTgtCD());
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName("");
        if (!copyAssoc.getLeft().isPresentCDCardinality()){
          copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()){
          copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
        }
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right) );
          }
          else {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      //Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());//falsch
      Set<ASTCDType> superClasses = CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses){//getAllSuperTypes CDDffUtils
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getSrcCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association, getSrcCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName())
              && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"))).build());
              if (copyAssoc.getLeft().getCDRole().getName().equals(Character.toLowerCase(superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0)) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1))){
                char firstChar = superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0);
                String roleName = Character.toLowerCase(firstChar) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1);
                copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (copyAssoc.getCDAssocType().isComposition()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardOne());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"))).build());
              if (copyAssoc.getRight().getCDRole().getName().equals(Character.toLowerCase(superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0)) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1))){
                char firstChar = superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0);
                String roleName = Character.toLowerCase(firstChar) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1);
                copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (copyAssoc.getCDAssocType().isComposition()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardOne());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right, true));
              }
              else {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right, true));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getTgtCD().getCDDefinition().getCDAssociationsListForType(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association, getTgtCD());
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              //change left side from superClass to subClass
              ASTCDAssociation assocForSubClass = association.deepClone();
              assocForSubClass.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName().replace(".", "_"))).build());
              if (assocForSubClass.getLeft().getCDRole().getName().equals(Character.toLowerCase(superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0)) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1))){
                char firstChar = superC.getSymbol().getInternalQualifiedName().replace(".", "_").charAt(0);
                String roleName = Character.toLowerCase(firstChar) + superC.getSymbol().getInternalQualifiedName().replace(".", "_").substring(1);
                assocForSubClass.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              assocForSubClass.setName(" ");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (assocForSubClass.getCDAssocType().isComposition()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardOne());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              //change right side from superClass to subclass
              ASTCDAssociation assocForSubClass = association.deepClone();
              assocForSubClass.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
              if (assocForSubClass.getRight().getCDRole().getName().equals(Character.toLowerCase(superC.getName().charAt(0)) + superC.getName().substring(1))){
                char firstChar = superC.getName().charAt(0);
                String roleName = Character.toLowerCase(firstChar) + superC.getName().substring(1);
                assocForSubClass.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              assocForSubClass.setName(" ");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (assocForSubClass.getCDAssocType().isComposition()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardOne());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Right, true));
              }
              else {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.RightToLeft, ClassSide.Right, true));
              }
            }
          }
        }
      }
    }
    for(ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        for (ASTCDAttribute attribute1 : attributes){
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())){
            notInstanClassesSrc.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for(ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstanClassesTgt.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        if (sameRoleNameAndClass(attribute.getName(), astcdClass)){
          notInstanClassesSrc.add(astcdClass);
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        if (sameRoleNameAndClassTgt(attribute.getName(), astcdClass)){
          notInstanClassesTgt.add(astcdClass);
          break;
        }
      }
    }
  }

  private boolean sameRoleNameAndClass(String roleName, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (assocStruct.getAssociation().getRight().getCDRole().getName().equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName().equals(roleName)){
          return true;
        }
      }
      else {
        if (assocStruct.getAssociation().getLeft().getCDRole().getName().equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName().equals(roleName)){
          return true;
        }
      }
    }
    return false;
  }
  private boolean sameRoleNameAndClassTgt(String roleName, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : trgMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (assocStruct.getAssociation().getRight().getCDRole().getName().equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), tgtCD).b.getName().equals(roleName)){
          return true;
        }
      }
      else {
        if (assocStruct.getAssociation().getLeft().getCDRole().getName().equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), tgtCD).a.getName().equals(roleName)){
          return true;
        }
      }
    }
    return false;
  }

  public static Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> astcdClass =
      compilationUnit
        .getEnclosingScope()
        .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
      compilationUnit
        .getEnclosingScope()
        .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    if (astcdClass.isPresent() && astcdClass1.isPresent()
      && astcdClass.get().getAstNode() instanceof ASTCDClass
      && astcdClass1.get().getAstNode() instanceof ASTCDClass) {
      return new Pair<>(
        (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
    }
    return null;
  }

  /**
   * Compute the classes that extend a given class.
   *
   * @param compilationUnit diagram
   * @param astcdClass root class for spanned inheritance
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  public static List<ASTCDClass> getSpannedInheritance(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (childClass != astcdClass && (CDDiffUtil.getAllSuperTypes(childClass, compilationUnit.getCDDefinition())).contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    subclasses.remove(astcdClass);
    return subclasses;
  }

  public static List<ASTCDClass> getSuperClasses(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = new ArrayList<>();
    for (ASTCDType type : getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      if (type instanceof ASTCDClass){
        superClasses.add((ASTCDClass) type);
      }
    }
    return superClasses;
  }

  /**
   * Check if the first cardinality is contained in the second cardinality
   * @param cardinality1 first cardinality
   * @param cardinality2 second cardinality
   * @return true if first cardinality is contained in the second one
   */
  //TODO: replace in all statements, where cardinalities are compared
  public static boolean isContainedIn(AssocCardinality cardinality1, AssocCardinality cardinality2){
    if (cardinality1.equals(AssocCardinality.One)
      || cardinality2.equals(AssocCardinality.Multiple)){
      return true;
    } else if (cardinality1.equals(AssocCardinality.Optional)){
      return !cardinality2.equals(AssocCardinality.One);
    } else if (cardinality1.equals(AssocCardinality.AtLeastOne)){
      return cardinality2.equals(AssocCardinality.AtLeastOne);
    } else{
      return false;
    }
  }

  static AssocCardinality cardToEnum(ASTCDCardinality cardinality){
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

  public AssocStruct findMatchingAssocStructSrc(
    ASTCDAssociation association, ASTCDClass associatedClass) {
    Pair<ASTCDClass, ASTCDClass> associatedClasses = getConnectedClasses(association, getSrcCD());
    for (AssocStruct assocStruct : getSrcMap().get(associatedClass)) {
      Pair<ASTCDClass, ASTCDClass> structAssociatedClasses = getConnectedClasses(assocStruct.getUnmodifiedAssoc(), getSrcCD());
      if (associatedClasses.a.equals(structAssociatedClasses.a)
        && associatedClasses.b.equals(structAssociatedClasses.b)) {
        return assocStruct;
      }
    }
    return null;
  }

  public AssocStruct findMatchingAssocStructTgt(
    ASTCDAssociation association, ASTCDClass associatedClass) {
    Pair<ASTCDClass, ASTCDClass> associatedClasses = getConnectedClasses(association, getTgtCD());
    for (AssocStruct assocStruct : getTrgMap().get(associatedClass)) {
      Pair<ASTCDClass, ASTCDClass> structAssociatedClasses = getConnectedClasses(assocStruct.getUnmodifiedAssoc(), getTgtCD());
      if (associatedClasses.a.equals(structAssociatedClasses.a)
        && associatedClasses.b.equals(structAssociatedClasses.b)) {
        return assocStruct;
      }
    }
    return null;
  }

  public ASTCDClass minDiffWitness(ASTCDClass baseClass) {

    List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, baseClass);

    int lowestCount = Integer.MAX_VALUE;
    ASTCDClass subclassWithLowestCount = null;

    for (ASTCDClass subclass : subClasses) {
      assert subclass != null;
      if (!subclass.getModifier().isAbstract() && !notInstanClassesSrc.contains(subclass)) {
        int attributeCount = getAllAttr(baseClass).b.size();
        int associationCount = getAssociationCount(subclass);
        int otherAssocsCount = getOtherAssocFromSuper(subclass).size();
        int totalCount = attributeCount + associationCount + otherAssocsCount;

        if (totalCount < lowestCount) {
          lowestCount = totalCount;
          subclassWithLowestCount = subclass;
        }
      }
    }

    return subclassWithLowestCount;
  }

  private int getAssociationCount(ASTCDClass astcdClass) {
    int count = 0;
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
          && !getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getSymbol().getInternalQualifiedName())) {
          count++;
        }
      }
      else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
          && !getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getSymbol().getInternalQualifiedName())){
          count++;
        }
      }
    }
    return count;
  }

  public List<String> getSuperClasses(ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = getSuperClasses(srcCD, astcdClass);
    List<String> classes = new ArrayList<>();
    for (int i = superClasses.size() - 1; i >= 0; i--) {
      String className = superClasses.get(i).getSymbol().getInternalQualifiedName().replace(".", "_");
      classes.add(className);
    }
    return classes;
  }

  public List<ASTODAttribute> getAttributesOD(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      odAttributes.add(ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
    }
    return odAttributes;
  }

  public ASTCDClass getCDClass(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getName().equals(className)) {
        return astcdClass;
      }
    }
    return null;
  }
}
