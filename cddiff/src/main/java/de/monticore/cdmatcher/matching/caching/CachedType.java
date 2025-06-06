package de.monticore.cdmatcher.matching.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.HashSet;
import java.util.Set;

public class CachedType {

  private final ASTCDType cachedType;

  private Set<ASTCDType> superTypes = new HashSet<>();
  private Set<ASTCDType> directSuperTypes = new HashSet<>();
  private Set<ASTCDType> directSubTypes = new HashSet<>();

  private Set<ASTCDAttribute> directAttributes = new HashSet<>();
  private Set<ASTCDAttribute> attributes = new HashSet<>();

  private Set<ASTCDAssociation> directAssociations = new HashSet<>();
  private Set<ASTCDAssociation> associations = new HashSet<>();

  public CachedType(ASTCDType cachedType) {
    this.cachedType = cachedType;
  }

  public ASTCDType getCachedType() {
    return cachedType;
  }

  public Set<ASTCDType> getSuperTypes() {
    return superTypes;
  }

  public void addSuperType(ASTCDType superType) {
    superTypes.add(superType);
  }

  public Set<ASTCDType> getDirectSuperTypes() {
    return directSuperTypes;
  }

  public void addDirectSuperType(ASTCDType directSuperType) {
    directSuperTypes.add(directSuperType);
  }

  public Set<ASTCDType> getDirectSubTypes() {
    return directSubTypes;
  }

  public void addDirectSubType(ASTCDType directSubType) {
    directSubTypes.add(directSubType);
  }

  public Set<ASTCDAttribute> getDirectAttributes() {
    return directAttributes;
  }

  public void addDirectAttribute(ASTCDAttribute directAttribute) {
    directAttributes.add(directAttribute);
  }

  public Set<ASTCDAttribute> getAttributes() {
    return attributes;
  }

  public void addAttribute(ASTCDAttribute attribute) {
    attributes.add(attribute);
  }

  public Set<ASTCDAssociation> getDirectAssociations() {
    return directAssociations;
  }

  public void addDirectAssociation(ASTCDAssociation directAssociation) {
    directAssociations.add(directAssociation);
  }

  public Set<ASTCDAssociation> getAssociations() {
    return associations;
  }

  public void addAssociation(ASTCDAssociation association) {
    associations.add(association);
  }


}
