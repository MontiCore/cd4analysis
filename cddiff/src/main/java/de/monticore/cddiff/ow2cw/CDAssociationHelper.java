/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTOP;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class CDAssociationHelper {

  /**
   * Collect associations in srcAST that are strict super-associations of an associations in
   * targetAST
   */
  public static Set<ASTCDAssociation> collectStrictSuperAssociations(
      ASTCDCompilationUnit srcAST, ASTCDCompilationUnit targetAST) {

    Set<ASTCDAssociation> strictSuperAssociations = collectSuperAssociations(srcAST, targetAST);
    ICD4CodeArtifactScope targetScope = (ICD4CodeArtifactScope) targetAST.getEnclosingScope();
    for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
      strictSuperAssociations.removeIf(
          srcAssoc ->
              isSuperAssociation(targetAssoc, srcAssoc, targetScope)
                  || isSuperAssociationInReverse(targetAssoc, srcAssoc, targetScope));
    }
    return strictSuperAssociations;
  }

  /**
   * Collect all associations in srcAST that are super-associations of an associations in targetAST
   */
  public static Set<ASTCDAssociation> collectSuperAssociations(
      ASTCDCompilationUnit srcAST, ASTCDCompilationUnit targetAST) {

    if (targetAST.getEnclosingScope() instanceof ICD4CodeArtifactScope) {
      ICD4CodeArtifactScope targetScope = (ICD4CodeArtifactScope) targetAST.getEnclosingScope();
      Set<ASTCDAssociation> superAssociations = new HashSet<>();
      for (ASTCDAssociation srcAssoc : srcAST.getCDDefinition().getCDAssociationsList()) {
        for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
          if (isSuperAssociation(srcAssoc, targetAssoc, targetScope)
              || isSuperAssociationInReverse(srcAssoc, targetAssoc, targetScope)) {
            superAssociations.add(srcAssoc);
          }
        }
      }
      return superAssociations;
    }
    Log.error("0xCDD18: Enclosing scope of CD4Code-artifact was not an ICD4CodeArtifactScope!");
    return null;
  }

  /** Collect all associations in srcAST that are in conflict with associations in targetAST */
  public static Set<ASTCDAssociation> collectConflictingAssociations(
      ASTCDCompilationUnit srcAST, ASTCDCompilationUnit targetAST) {

    if (targetAST.getEnclosingScope() instanceof ICD4CodeArtifactScope) {
      ICD4CodeArtifactScope targetScope = (ICD4CodeArtifactScope) targetAST.getEnclosingScope();
      Set<ASTCDAssociation> conflicts = new HashSet<>();
      for (ASTCDAssociation srcAssoc : srcAST.getCDDefinition().getCDAssociationsList()) {
        for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
          if (inConflict(srcAssoc, targetAssoc, targetScope)) {
            conflicts.add(srcAssoc);
          }
        }
      }
      return conflicts;
    }
    Log.error("0xCDD18: Enclosing scope of CD4Code-artifact was not an ICD4CodeArtifactScope!");
    return null;
  }

  /**
   * An association srcAssoc is in conflict with another association targetAssoc if their
   * source-classes and target-role-names match in navigable direction, unless srcAssoc is a
   * super-association of targetAssoc
   */
  public static boolean inConflict(
      ASTCDAssociation srcAssoc, ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && (CDInheritanceHelper.isSuperOf(srcLeft, targetLeft, scope)
            || CDInheritanceHelper.isSuperOf(targetLeft, srcLeft, scope))
        && (!(CDInheritanceHelper.isSuperOf(srcRight, targetRight, scope)
            || CDInheritanceHelper.isSuperOf(targetRight, srcRight, scope)))) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && (CDInheritanceHelper.isSuperOf(srcRight, targetRight, scope)
            || CDInheritanceHelper.isSuperOf(targetRight, srcRight, scope))
        && !(CDInheritanceHelper.isSuperOf(srcLeft, targetLeft, scope)
            || CDInheritanceHelper.isSuperOf(targetLeft, srcLeft, scope))) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && (CDInheritanceHelper.isSuperOf(srcLeft, targetRight, scope)
            || CDInheritanceHelper.isSuperOf(targetRight, srcLeft, scope))
        && !(CDInheritanceHelper.isSuperOf(srcRight, targetLeft, scope)
            || CDInheritanceHelper.isSuperOf(targetLeft, srcRight, scope))) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && (CDInheritanceHelper.isSuperOf(srcRight, targetLeft, scope)
            || CDInheritanceHelper.isSuperOf(targetLeft, srcRight, scope))
        && !(CDInheritanceHelper.isSuperOf(srcLeft, targetRight, scope)
            || CDInheritanceHelper.isSuperOf(targetRight, srcLeft, scope))) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }

    return false;
  }

  /**
   * An association srcAssoc is a super-association of another association targetAssoc iff
   * srcClass.targetRoleName = srcClass.targetRoleName, srcAssoc.srcClass != targetAssoc.srcClass,
   * srcAssoc.srcClass is superclass of targetAssoc.srcClass, and srcAssoc.targetClass is superclass
   * of targetAssoc.targetClass in navigable direction.
   */
  public static boolean isSuperAssociation(
      ASTCDAssociation srcAssoc, ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && !targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return false;
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && !targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      return false;
    }

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (!CDInheritanceHelper.isSuperOf(srcLeft, targetLeft, scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(srcRight, targetRight, scope)) {
      return false;
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        || !targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        || !targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft())
        && matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
  }

  public static boolean isSuperAssociationInReverse(
      ASTCDAssociation srcAssoc, ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        && (!targetAssoc.getCDAssocDir().isDefinitiveNavigableRight())) {
      return false;
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        && !targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return false;
    }

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (!CDInheritanceHelper.isSuperOf(srcLeft, targetRight, scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(srcRight, targetLeft, scope)) {
      return false;
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
        || !targetAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
        || !targetAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }

    return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())
        && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
  }

  /**
   * check if assoc1 and assoc2 are the same associations, i.e. reference and role-names match in
   * navigable direction
   */
  public static boolean sameAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {

    if (assoc1.getLeftQualifiedName().getQName().equals(assoc2.getLeftQualifiedName().getQName())
        && assoc1
            .getRightQualifiedName()
            .getQName()
            .equals(assoc2.getRightQualifiedName().getQName())) {

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
          && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getRight());
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
          && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getLeft());
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getRight())
          && matchRoleNames(assoc1.getLeft(), assoc2.getLeft());
    }

    return false;
  }

  /**
   * check if assoc1 and assoc2 are the same associations, i.e. reference and role-names match in
   * navigable direction
   */
  public static boolean sameAssociationInReverse(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {

    if (assoc1.getLeftQualifiedName().getQName().equals(assoc2.getRightQualifiedName().getQName())
        && assoc1
            .getRightQualifiedName()
            .getQName()
            .equals(assoc2.getLeftQualifiedName().getQName())) {

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableLeft()
          && !assoc2.getCDAssocDir().isDefinitiveNavigableRight()) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft());
      }

      if (!assoc1.getCDAssocDir().isDefinitiveNavigableRight()
          && !assoc2.getCDAssocDir().isDefinitiveNavigableLeft()) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight());
      }

      return matchRoleNames(assoc1.getRight(), assoc2.getLeft())
          && matchRoleNames(assoc1.getLeft(), assoc2.getRight());
    }

    return false;
  }

  public static boolean matchRoleNames(ASTCDAssocSide side1, ASTCDAssocSide side2) {
    return CDDiffUtil.inferRole(side1).equals(CDDiffUtil.inferRole(side2));
  }

  public static ASTCDType getCDTypeSymbol(ASTCDAssocSide assoc) {
    Optional<TypeSymbol> typeSymbol = Optional.empty();
    // Depending on the symbol table completion a single type for both cds may exist, in which case the type can be resolved by "name"
    // or one type for each cd, in which case the full name "packageName.name" must be used.
    if(assoc.isPresentSymbol()) {
      typeSymbol = getCD4CodeArtifactScope(assoc.getEnclosingScope())
        .resolveType(assoc.getSymbol().getType().getTypeInfo().getName());
      if(typeSymbol.isEmpty()) {
        typeSymbol = getCD4CodeArtifactScope(assoc.getEnclosingScope())
          .resolveType(assoc.getSymbol().getType().getTypeInfo().getFullName());
      }
    }
    else {
      typeSymbol = getCD4CodeArtifactScope(assoc.getEnclosingScope())
        .resolveType(assoc.getMCQualifiedType().getMCQualifiedName().getQName());
    }
    if (typeSymbol.isPresent() && typeSymbol.get().isPresentAstNode()
        && typeSymbol.get().getAstNode() instanceof ASTCDType) {
      return (ASTCDType) typeSymbol.get().getAstNode();
    }
    return null;
  }

  public static Set<ASTCDAssociation> getDirectAssociations(ASTCDType type, ASTCDCompilationUnit cD) {
    return cD.getCDDefinition().getCDAssociationsList()
      .stream()
      .filter((assoc) -> typeHasAssociation(type, assoc))
      .collect(Collectors.toSet());
  }

  private static ICD4CodeArtifactScope getCD4CodeArtifactScope(ICDBasisScope scope) {
    if (scope instanceof ICD4CodeArtifactScope) {
      return (ICD4CodeArtifactScope) scope;
    } else if (scope == null) {
      Log.error("0xCDD20: ACDType was not contained in a CD4CodeArtifactScope.");
      return null;

    }
    else{
      return getCD4CodeArtifactScope(scope.getEnclosingScope());
    }
  }

  private static boolean typeHasAssociation(ASTCDType type, ASTCDAssociation assoc) {
    if(!type.isPresentSymbol()){
      return false;
    }
    return assoc.getLeftQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName()) && (assoc.getCDAssocDir().isDefinitiveNavigableRight())
        || assoc.getRightQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName()) && (assoc.getCDAssocDir().isDefinitiveNavigableLeft());
  }
}
