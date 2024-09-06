package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.inc.association.CompAssocIncStrategy;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import java.util.*;
import java.util.stream.Collectors;

public class ConcretizationHelper {

  private final ASTCDCompilationUnit ccd;
  private final ASTCDCompilationUnit rcd;
  private final CompTypeIncStrategy compTypeIncStrategy;
  private final CompAssocIncStrategy compAssocIncStrategy;

  // Mappings to store results
  private final Map<String, String> typeMapping;
  private final Map<String, String> roleMapping;
  private final Map<String, ASTCDType> roleToTypeMapping;

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
        typeMapping.put(refType.getName(), conType.getName());

        // Get concrete associations incarnating the reference association and referencing conType
        Set<ASTCDAssociation> conAssocSet = getConcreteAssociationsForType(conType, refAssoc);

        for (ASTCDAssociation conAssoc : conAssocSet) {
          // Process role names and other side type mappings
          processAssociationSides(refAssoc, conAssoc, refType, conType);
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
      ASTCDType conType, ASTCDAssociation refAssoc) {
    return ccd.getCDDefinition().getCDAssociationsList().stream()
        .filter(
            conAssoc ->
                compAssocIncStrategy.isMatched(conAssoc, refAssoc)
                        && getTypeFromAssocSide(conAssoc.getLeft()).equals(conType)
                    || getTypeFromAssocSide(conAssoc.getRight()).equals(conType))
        .collect(Collectors.toSet());
  }

  // Process both sides of the association to map roles and other types
  private void processAssociationSides(
      ASTCDAssociation refAssoc, ASTCDAssociation conAssoc, ASTCDType refType, ASTCDType conType)
      throws CompletionException {
    // Get reference and concrete sides
    ASTCDAssocSide refAssocSide = getAssociationSideForType(refAssoc, refType);
    ASTCDAssocSide conAssocSide = getAssociationSideForType(conAssoc, conType);

    // Get the other side for both reference and concrete associations
    ASTCDAssocSide refOtherSide = getOtherSide(refAssoc, refAssocSide);
    ASTCDAssocSide conOtherSide = getOtherSide(conAssoc, conAssocSide);

    // Map roles and other side types
    mapRolesAndTypes(refAssocSide, conAssocSide, refOtherSide, conOtherSide, refType, conType);
  }

  // Helper to get the correct side of the association for a given type
  private ASTCDAssocSide getAssociationSideForType(ASTCDAssociation assoc, ASTCDType type)
      throws CompletionException {
    ASTCDType leftType = getAssocLeftType(ccd, assoc);
    return (compTypeIncStrategy.isMatched(leftType, type)) ? assoc.getLeft() : assoc.getRight();
  }

  // Helper to get the other side of the association
  private ASTCDAssocSide getOtherSide(ASTCDAssociation assoc, ASTCDAssocSide side) {
    return (side.equals(assoc.getLeft())) ? assoc.getRight() : assoc.getLeft();
  }

  // Helper to map roles and types between reference and concrete associations
  private void mapRolesAndTypes(
      ASTCDAssocSide refAssocSide,
      ASTCDAssocSide conAssocSide,
      ASTCDAssocSide refOtherSide,
      ASTCDAssocSide conOtherSide,
      ASTCDType refType,
      ASTCDType conType) {
    // Map roles between reference and concrete sides
    Optional<String> refRole = getRoleName(refAssocSide);
    Optional<String> conRole = getRoleName(conAssocSide);

    if (refRole.isPresent() && conRole.isPresent()) {
      roleMapping.put(
          refType.getName() + "." + refRole.get(), conType.getName() + "." + conRole.get());
    }

    // Map reference role to the other reference type
    ASTCDType refOtherType = getTypeFromAssocSide(refOtherSide);
    refRole.ifPresent(role -> roleToTypeMapping.put(refType.getName() + "." + role, refOtherType));

    // Map concrete role to the other concrete type
    ASTCDType conOtherType = getTypeFromAssocSide(conOtherSide);
    conRole.ifPresent(role -> roleToTypeMapping.put(conType.getName() + "." + role, conOtherType));
  }

  // Helper to get the role name of an association side
  private Optional<String> getRoleName(ASTCDAssocSide assocSide) {
    return assocSide.isPresentCDRole()
        ? Optional.of(assocSide.getCDRole().getName())
        : Optional.empty();
  }

  // Helper to get the type from an association side
  private ASTCDType getTypeFromAssocSide(ASTCDAssocSide side) {
    // how to get QName from side?
    return ccd.getEnclosingScope().resolveCDTypeDown(side.getName()).get().getAstNode();
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
}
