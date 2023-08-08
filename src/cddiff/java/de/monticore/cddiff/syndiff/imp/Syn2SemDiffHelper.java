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
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.AssocCardinality;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;

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
  private ArrayListMultimap<ASTCDClass, AssocStruct> srcMap = ArrayListMultimap.create();
  private ArrayListMultimap<ASTCDClass, AssocStruct> trgMap = ArrayListMultimap.create();

  private Set<ASTCDClass> notInstanClassesSrc = new HashSet<>();

  private Set<ASTCDClass> notInstanClassesTgt = new HashSet<>();

  private ASTCDCompilationUnit srcCD;

  private ASTCDCompilationUnit tgtCD;

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
        && att.printType().equals(attribute.printType()))){
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
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
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
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
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
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
              if (copyAssoc.getLeft().getCDRole().getName().equals(Character.toLowerCase(superC.getName().charAt(0)) + superC.getName().substring(1))){
                char firstChar = astcdClass.getName().charAt(0);
                String roleName = Character.toLowerCase(firstChar) + astcdClass.getName().substring(1);
                copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              copyAssoc.setName("");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
              if (copyAssoc.getRight().getCDRole().getName().equals(Character.toLowerCase(superC.getName().charAt(0)) + superC.getName().substring(1))){
                char firstChar = astcdClass.getName().charAt(0);
                String roleName = Character.toLowerCase(firstChar) + astcdClass.getName().substring(1);
                copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              copyAssoc.setName("");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (association.getCDAssocDir().isBidirectional()) {
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
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              //change left side from superClass to subClass
              ASTCDAssociation assocForSubClass = association.deepClone();
              if (assocForSubClass.getLeft().getCDRole().getName().equals(Character.toLowerCase(superC.getName().charAt(0)) + superC.getName().substring(1))){
                char firstChar = astcdClass.getName().charAt(0);
                String roleName = Character.toLowerCase(firstChar) + astcdClass.getName().substring(1);
                assocForSubClass.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              assocForSubClass.setName("");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            } else if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociationBuilder builder = CD4CodeMill.cDAssociationBuilder();
              //change right side from superClass to subclass
              ASTCDAssociation assocForSubClass = association.deepClone();
              if (assocForSubClass.getRight().getCDRole().getName().equals(Character.toLowerCase(superC.getName().charAt(0)) + superC.getName().substring(1))){
                char firstChar = astcdClass.getName().charAt(0);
                String roleName = Character.toLowerCase(firstChar) + astcdClass.getName().substring(1);
                assocForSubClass.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(roleName).build());
              }
              assocForSubClass.setName("");
              //TODO: problem if I use the same datastructure for composition
              //maybe add !...isComposition()
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(new ASTCDCardMult());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(new ASTCDCardMult());
              }
              if (association.getCDAssocDir().isBidirectional()) {
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
    return new Pair<>(
      (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
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
      return !cardinality2.equals(AssocCardinality.One)
        && !cardinality2.equals(AssocCardinality.AtLeastOne);
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

  public ASTCDClass minDiffWitness(ASTCDClass astcdClass){
    assert srcCD != null;
    List<ASTCDClass> set = getSpannedInheritance(srcCD, astcdClass);

    ASTCDClass closestClass = null;
    int closestDepth = Integer.MAX_VALUE;

    for (ASTCDClass cdClass : set) {
      if (!cdClass.getModifier().isAbstract()
        && notInstanClassesSrc.contains(astcdClass)) {
        int depth = getDepthOfClass(cdClass, astcdClass);
        if (depth < closestDepth) {
          closestClass = cdClass;
          closestDepth = depth;
        }
      }
    }

    return closestClass;
  }

  public int getDepthOfClass(ASTCDClass classNode, ASTCDClass rootClass) {
    if (classNode == null || rootClass == null) {
      return -1; // or any other suitable value to indicate an invalid depth
    }

    if (classNode == rootClass) {
      return 0; // base case: classNode is the root class
    }

    int maxDepth = -1;
    for (ASTCDType directSuperClass : CDInheritanceHelper.getDirectSuperClasses(classNode, (ICD4CodeArtifactScope) srcCD.getEnclosingScope())) {
      if (directSuperClass instanceof ASTCDClass) {
        int depth = getDepthOfClass((ASTCDClass) directSuperClass, rootClass);
        if (depth >= 0 && depth > maxDepth) {
          maxDepth = depth;
        }
      }
    }

    return maxDepth >= 0 ? maxDepth + 1 : -1; // add 1 to the maximum depth found
  }
//  public int calculateClassDepth(ASTCDClass rootClass, ASTCDClass targetClass) {
//    // Check if the target class is the root class
//    if (rootClass.getName().equals(targetClass.getName())) {
//      return 0;
//    }
//
//    // Get the direct superclasses of the target class
//    Set<ASTCDClass> superClasses = CDDiffUtil.getAllSuperclasses(targetClass, helper.getSrcCD().getCDDefinition().getCDClassesList());
//
//    // If the target class has no superclasses, it is not in the hierarchy
//    if (superClasses.isEmpty()) {
//      return -1;
//    }
//
//    // Recursively calculate the depth for each direct superclass
//    List<Integer> depths = new ArrayList<>();
//    for (ASTCDClass superClass : superClasses) {
//      int depth = calculateClassDepth(rootClass, superClass);
//      if (depth >= 0) {
//        depths.add(depth + 1);
//      }
//    }
//
//    // Return the maximum depth from the direct superclasses
//    if (depths.isEmpty()) {
//      return -1;
//    } else {
//      return depths.stream().max(Integer::compare).get();
//    }
//  }
}
