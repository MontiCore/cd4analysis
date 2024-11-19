package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.inc.association.CompAssocIncStrategy;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cddiff.CDDiffUtil;
import java.util.*;
import java.util.stream.Collectors;

public class ConcretizationHelper {

  private final ASTCDCompilationUnit ccd;
  private final ASTCDCompilationUnit rcd;
  private final CompTypeIncStrategy compTypeIncStrategy;
  private final CompAssocIncStrategy compAssocIncStrategy;

  // Mappings to store results
  public Map<CDTypeSymbol, Set<CDTypeSymbol>> typeMapping;
  public Map<CDRoleSymbol, Set<CDRoleSymbol>> roleMapping;
  public Map<CDRoleSymbol, Set<CDTypeSymbol>> roleToTypeMapping;

  // Constructor
  public ConcretizationHelper(
      ASTCDCompilationUnit ccd,
      ASTCDCompilationUnit rcd,
      CompTypeIncStrategy compTypeIncStrategy,
      CompAssocIncStrategy compAssocIncStrategy) {
    this.ccd = ccd;
    this.rcd = rcd;
    this.compTypeIncStrategy = compTypeIncStrategy;
    this.compAssocIncStrategy = compAssocIncStrategy;
    this.typeMapping = new HashMap<>();
    this.roleMapping = new HashMap<>();
    this.roleToTypeMapping = new HashMap<>();
  }

  // Main function to map reference roles to concrete roles
  public void mapReferenceToConcreteRoles() throws CompletionException {
    CDDiffUtil.refreshSymbolTable(ccd);
    for (ASTCDType refType : getCDTypes(rcd)) {
      // Process associations for each reference type
      processAssociationsForType(refType);
    }
  }

  private void processAssociationsForType(ASTCDType refType) throws CompletionException {
    for (ASTCDAssociation refAssoc : getAssociationsReferencingType(refType, rcd)) {
      // Get all concrete types that incarnate refType
      Set<ASTCDType> concreteTypes = getConcreteTypesForReferenceType(refType);

      // Process each concrete type that incarnates refType
      for (ASTCDType conType : concreteTypes) {
        // Map reference type to concrete type
        typeMapping
            .computeIfAbsent(refType.getSymbol(), k -> new HashSet<>())
            .add(conType.getSymbol());

        // Get concrete associations incarnating the reference association and referencing conType
        Set<ASTCDAssociation> conAssocSet = getConcreteAssociationsForType(conType, refAssoc);

        for (ASTCDAssociation conAssoc : conAssocSet) {
          // Process left and right side of associations separately
          processAssociationSides(
              refAssoc.getLeft(), conAssoc.getLeft(), refAssoc.getRight(), conAssoc.getRight());
          processAssociationSides(
              refAssoc.getRight(), conAssoc.getRight(), refAssoc.getLeft(), conAssoc.getLeft());
        }
      }
    }
  }

  // Helper to get all associations in the reference diagram that reference a given type
  private Set<ASTCDAssociation> getAssociationsReferencingType(
      ASTCDType refType, ASTCDCompilationUnit cd) {
    return cd.getCDDefinition().getCDAssociationsList().stream()
        .filter(
            refAssoc -> {
              try {
                return isAssociationReferencingType(refAssoc, refType);
              } catch (CompletionException e) {
                throw new RuntimeException(e);
              }
            })
        .collect(Collectors.toSet());
  }

  // Check if an association references a type on either side
  private boolean isAssociationReferencingType(ASTCDAssociation assoc, ASTCDType type)
      throws CompletionException {
    ASTCDType assocLeftType = getAssocLeftType(rcd, assoc);
    ASTCDType assocRightType = getAssocRightType(rcd, assoc);
    return type.equals(assocLeftType) || type.equals(assocRightType);
  }

  // Helper to get all concrete types that incarnate the reference type
  private Set<ASTCDType> getConcreteTypesForReferenceType(ASTCDType refType) {
    return getCDTypes(ccd).stream()
        .filter(conType -> compTypeIncStrategy.isMatched(conType, refType))
        .collect(Collectors.toSet());
  }

  // Helper to get all concrete associations for a given concrete type and reference association
  private Set<ASTCDAssociation> getConcreteAssociationsForType(
      ASTCDType conType, ASTCDAssociation refAssoc) throws CompletionException {
    Set<ASTCDAssociation> set = new HashSet<>();
    for (ASTCDAssociation assoc : ccd.getCDDefinition().getCDAssociationsList()) {
      if (compAssocIncStrategy.isMatched(assoc, refAssoc)
          && (getTypeFromAssocSide(assoc.getLeft()).equals(conType)
              || getTypeFromAssocSide(assoc.getRight()).equals(conType))) {
        set.add(assoc);
      }
    }
    return set;
  }

  // Process both sides of the association to map roles and other types
  private void processAssociationSides(
      ASTCDAssocSide refAssocSide,
      ASTCDAssocSide conAssocSide,
      ASTCDAssocSide refOtherSide,
      ASTCDAssocSide conOtherSide)
      throws CompletionException {

    // Get reference and concrete role names for the current side
    Optional<CDRoleSymbol> refRoleOpt = getRoleSymbol(refAssocSide);
    Optional<CDRoleSymbol> conRoleOpt = getRoleSymbol(conAssocSide);

    // Add reference role to concrete role mapping
    if (refRoleOpt.isPresent() && conRoleOpt.isPresent()) {
      CDRoleSymbol refRole = refRoleOpt.get();
      CDRoleSymbol conRole = conRoleOpt.get();
      addToMapping(roleMapping, refRole, conRole);
    }

    // Map the reference role to the other reference type (for roleToTypeMapping)
    ASTCDType refOtherType = getTypeFromAssocSide(refOtherSide);
    refRoleOpt.ifPresent(role -> addToMapping(roleToTypeMapping, role, refOtherType.getSymbol()));

    // Map the concrete role to the other concrete type (for roleToTypeMapping)
    ASTCDType conOtherType = getTypeFromAssocSide(conOtherSide);
    conRoleOpt.ifPresent(role -> addToMapping(roleToTypeMapping, role, conOtherType.getSymbol()));
  }

  private void addToMapping(
      Map<CDRoleSymbol, Set<CDTypeSymbol>> map, CDRoleSymbol key, CDTypeSymbol value) {
    // If the key doesn't exist, create a new set for it
    map.computeIfAbsent(key, k -> new HashSet<>()).add(value);
  }

  private void addToMapping(
      Map<CDRoleSymbol, Set<CDRoleSymbol>> map, CDRoleSymbol key, CDRoleSymbol value) {
    // If the key doesn't exist, create a new set for it
    map.computeIfAbsent(key, k -> new HashSet<>()).add(value);
  }

  // Helper to get the role name of an association side
  private Optional<CDRoleSymbol> getRoleSymbol(ASTCDAssocSide assocSide) {
    return assocSide.isPresentCDRole()
        ? Optional.of(assocSide.getCDRole().getSymbol())
        : Optional.empty();
  }

  // Helper to get the type from an association side
  private ASTCDType getTypeFromAssocSide(ASTCDAssocSide side) throws CompletionException {
    // how to get QName from side?
    Optional<CDTypeSymbol> typeSymbol =
        ccd.getEnclosingScope()
            .resolveCDTypeDown(side.getMCQualifiedType().getMCQualifiedName().getQName());

    if (typeSymbol.isPresent()) {
      return typeSymbol.get().getAstNode();
    } else {
      throw new CompletionException("There was a problem getting a type from an association");
    }
  }

  protected Set<ASTCDType> getCDTypes(ASTCDCompilationUnit cd) {
    Set<ASTCDType> cdTypes = new HashSet<>();
    cdTypes.addAll(cd.getCDDefinition().getCDClassesList());
    cdTypes.addAll(cd.getCDDefinition().getCDInterfacesList());
    cdTypes.addAll(cd.getCDDefinition().getCDEnumsList());
    return cdTypes;
  }

  ASTCDType getAssocLeftType(ASTCDCompilationUnit cd, ASTCDAssociation assoc)
      throws CompletionException {
    Optional<CDTypeSymbol> typeSymbol =
        cd.getEnclosingScope().resolveCDTypeDown(assoc.getLeftQualifiedName().getQName());

    if (typeSymbol.isPresent()) {
      return typeSymbol.get().getAstNode();
    } else {
      throw new CompletionException("There was a problem getting a type from an association");
    }
  }

  ASTCDType getAssocRightType(ASTCDCompilationUnit cd, ASTCDAssociation assoc)
      throws CompletionException {
    Optional<CDTypeSymbol> typeSymbol =
        cd.getEnclosingScope().resolveCDTypeDown(assoc.getRightQualifiedName().getQName());

    if (typeSymbol.isPresent()) {
      return typeSymbol.get().getAstNode();
    } else {
      throw new CompletionException("There was a problem getting a type from an association");
    }
  }

  ASTCDType getAssocTypeByQName(ASTCDCompilationUnit cd, String QName) throws CompletionException {
    Optional<CDTypeSymbol> typeSymbol = cd.getEnclosingScope().resolveCDTypeDown(QName);

    if (typeSymbol.isPresent()) {
      return typeSymbol.get().getAstNode();
    } else {
      throw new CompletionException("There was a problem getting a type from an association");
    }
  }

  public static void reorderElements(ASTCDDefinition cdDefinition) {
    List<ASTCDClass> classList = new ArrayList<>(cdDefinition.getCDClassesList());
    List<ASTCDAssociation> assocList = new ArrayList<>(cdDefinition.getCDAssociationsList());
    cdDefinition.getCDElementList().clear();
    cdDefinition.addAllCDElements(classList);
    cdDefinition.addAllCDElements(assocList);
  }
}
