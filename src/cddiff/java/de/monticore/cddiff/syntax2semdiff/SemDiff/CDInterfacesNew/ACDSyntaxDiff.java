package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces.ICDSyntaxDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;

import java.util.List;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;

public abstract class ACDSyntaxDiff implements ICDSyntaxDiff {
  private ASTCDCompilationUnit newCD;
  private ASTCDCompilationUnit oldCD;

  public ASTCDCompilationUnit getNewCD() {
    return newCD;
  }

  public void setNewCD(ASTCDCompilationUnit newCD) {
    this.newCD = newCD;
  }

  public ASTCDCompilationUnit getOldCD() {
    return oldCD;
  }

  public void setOldCD(ASTCDCompilationUnit oldCD) {
    this.oldCD = oldCD;
  }

  private List<ACDTypeDiff> changedClasses;
  private List<ACDAssocDiff> changedAssocs;
  private List<ASTCDClass> addedClasses;
  private List<ASTCDClass> deletedClasses;
  private List<ASTCDEnum> addedEnums;
  private List<ASTCDEnum> deletedEnums;
  private List<ASTCDAssociation> addedAssocs;
  private List<ASTCDAssociation> deletedAssocs;
  private List<ACDTypeDiff> matchedClasses;
  private List<ACDAssocDiff> matchedAssocs;

  private List<DiffTypes> baseDiff;

  public List<ACDTypeDiff> getChangedClasses() {
    return changedClasses;
  }

  public void setChangedClasses(List<ACDTypeDiff> changedCLasses) {
    this.changedClasses = changedCLasses;
  }

  @Override
  public List<ACDAssocDiff> getChangedAssocs() {
    return changedAssocs;
  }

  public void setChangedAssocs(List<ACDAssocDiff> changedAssocs) {
    this.changedAssocs = changedAssocs;
  }

  @Override
  public List<ASTCDClass> getAddedClasses() {
    return addedClasses;
  }

  public void setAddedClasses(List<ASTCDClass> addedClasses) {
    this.addedClasses = addedClasses;
  }

  @Override
  public List<ASTCDClass> getDeletedClasses() {
    return deletedClasses;
  }

  public void setDeletedClasses(List<ASTCDClass> deletedClasses) {
    this.deletedClasses = deletedClasses;
  }

  @Override
  public List<ASTCDEnum> getAddedEnums() {
    return addedEnums;
  }

  public void setAddedEnums(List<ASTCDEnum> addedEnums) {
    this.addedEnums = addedEnums;
  }

  @Override
  public List<ASTCDEnum> getDeletedEnums() {
    return deletedEnums;
  }

  public void setDeletedEnums(List<ASTCDEnum> deletedEnums) {
    this.deletedEnums = deletedEnums;
  }

  @Override
  public List<ASTCDAssociation> getAddedAssocs() {
    return addedAssocs;
  }

  public void setAddedAssocs(List<ASTCDAssociation> addedAssocs) {
    this.addedAssocs = addedAssocs;
  }

  @Override
  public List<ASTCDAssociation> getDeletedAssocs() {
    return deletedAssocs;
  }

  public void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs) {
    this.deletedAssocs = deletedAssocs;
  }

  @Override
  public List<ACDTypeDiff> getMatchedClasses() {
    return matchedClasses;
  }

  public void setMatchedClasses(List<ACDTypeDiff> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  @Override
  public List<ACDAssocDiff> getMatchedAssocs() {
    return matchedAssocs;
  }

  public void setMatchedAssocs(List<ACDAssocDiff> matchedAssocs) {
    this.matchedAssocs = matchedAssocs;
  }

  /**
   *
   * Checks if each of the added classes refactors the old structure.
   * The class must be abstarct, its subclasses in the old CD need to have all of its attributes
   * and it can't have new ones.
   */
  abstract void isSuperclass();

  /**
   *
   * Get the whole inheritance hierarchy that @param astcdClass.
   * is part of - all direct and indirect superclasses.
   * @return a list of the superclasses.
   */
  public abstract List<ASTCDClass> getClassHierarchy(ASTCDClass astcdClass);

  /**
   *
   * Check if a deleted @param astcdAssociation was need in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  public abstract boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation);

  /**
   *
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   * @return true if a class instantiate another one by @param association.
   */
  public abstract boolean isAlwaysNeededAssoc(ASTCDAssociation astcdAssociation);

  /**
   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without attribute.
   * Similar case for added ones.
   * @param astcdEnum
   */
  public abstract List<ASTCDClass> getAttForEnum(ASTCDEnum astcdEnum);

  /**
   * Compute the classes that extend a given class.
   * @param astcdClass
   * @return list of extending classes.
   * This function is similar to getClassHierarchy().
   */
  public abstract List<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass);

  @Override
  public ArrayListMultimap mergeAssociations() {
    //need to add all superAssocs(CDAssocHelper?)
    ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> map = ArrayListMultimap.create();
    for (ASTCDAssociation astcdAssociation : getNewCD().getCDDefinition().getCDAssociationsList()) {
      map.put(astcdAssociation, null);
      for (ASTCDAssociation astcdAssociation1 : getNewCD().getCDDefinition().getCDAssociationsList()) {
        if (!astcdAssociation.equals(astcdAssociation1) && matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft()) && matchRoleNames(astcdAssociation
          .getRight(), astcdAssociation1.getRight()) &&
          getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))
          && getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {
          map.put(astcdAssociation, astcdAssociation1);
          //assocs1 needs to be deleted if not from superclass
          //Can I change the ASTCdCompilationUnit?
        }
      }
    }
    return map;
  }

  @Override
  public ArrayListMultimap findOverlappingAssocs() {
    //need to add all superAssocs(CDAssocHelper?)
    ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> map = ArrayListMultimap.create();
    for (ASTCDAssociation astcdAssociation : getNewCD().getCDDefinition().getCDAssociationsList()) {
      map.put(astcdAssociation, null);
      for (ASTCDAssociation astcdAssociation1 : getNewCD().getCDDefinition().getCDAssociationsList()) {
        if (!astcdAssociation.equals(astcdAssociation1) && matchRoleNames(astcdAssociation.getRight(), astcdAssociation1.getRight()) &&
          getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))
          && getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(getNewCD().getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {
          map.put(astcdAssociation, astcdAssociation1);
          //assocs1 needs to be deleted if not from superclass
          //Can I change the ASTCdCompilationUnit?
        }
      }
    }
    return map;
  }

  @Override
  public String getTypeOfDiff(Object diff) {
    if (diff instanceof ACDTypeDiff){
      ACDTypeDiff obj = (ACDTypeDiff) diff;
      return obj.getBaseDiffs().toString();
    }
    else{
      ACDAssocDiff obj = (ACDAssocDiff) diff;
      StringBuilder difference = new StringBuilder(new String());
      for (DiffTypes type : obj.getBaseDiff()){
        switch (type){
          case CHANGED_ASSOCIATION_ROLE: difference.append(roleDiff(obj.getElem1(), obj.getElem2()));
          case CHANGED_ASSOCIATION_DIRECTION: difference.append(dirDiff(obj.getElem1(), obj.getElem2()));
          case CHANGED_ASSOCIATION_MULTIPLICITY: difference.append(carDiff(obj.getElem1(), obj.getElem2()));
        }
      }
    }
    return null;
  }

  private String roleDiff(ASTCDAssociation src, ASTCDAssociation trg){
    StringBuilder diff = new StringBuilder(new String());
    if (!src.getLeftQualifiedName().getQName().equals(trg.getLeftQualifiedName().getQName())){
      diff.append("\nLeft role changed from " +trg.getLeftQualifiedName().getQName()+" to "+src.getLeftQualifiedName().getQName());
    }
    if (!src.getRightQualifiedName().getQName().equals(trg.getRightQualifiedName().getQName())){
      diff.append("\nLeft role changed from " +trg.getLeftQualifiedName().getQName()+" to "+src.getLeftQualifiedName().getQName());
    }
    return diff.toString();
  }
  public static String dirDiff(ASTCDAssociation src, ASTCDAssociation trg) {
    boolean oldSourceNavigatable = trg.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean oldTargetNavigatable = trg.getCDAssocDir().isDefinitiveNavigableRight();
    boolean newSourceNavigatable = src.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean newTargetNavigatable = src.getCDAssocDir().isDefinitiveNavigableRight();
    boolean oldBidirectional = trg.getCDAssocDir().isBidirectional();
    boolean newBidirectional = src.getCDAssocDir().isBidirectional();

    if (oldSourceNavigatable == newSourceNavigatable && oldTargetNavigatable == newTargetNavigatable && oldBidirectional == newBidirectional) {
      return "Navigation direction remains the same";
    } else if ((oldSourceNavigatable == newTargetNavigatable && oldTargetNavigatable == newSourceNavigatable) || (oldBidirectional && !newBidirectional)) {
      return "Navigation direction is reversed";
    } else {
      StringBuilder directionChanges = new StringBuilder();

      if (oldSourceNavigatable != newSourceNavigatable) {
        directionChanges.append("Source direction changed to ").append(newSourceNavigatable ? "navigatable" : "non-navigatable").append(" ");
      }

      if (oldTargetNavigatable != newTargetNavigatable) {
        directionChanges.append("Target direction changed to ").append(newTargetNavigatable ? "navigatable" : "non-navigatable").append(" ");
      }

      if (oldBidirectional != newBidirectional) {
        directionChanges.append("Bidirectional flag changed to ").append(newBidirectional ? "true" : "false").append(" ");
      }

      return "Navigation direction changed: " + directionChanges.toString().trim();
    }
  }
  private String carDiff(ASTCDAssociation src, ASTCDAssociation trg){
    if (trg.getLeft().isPresentCDCardinality()){}

    return null;
  }
}
