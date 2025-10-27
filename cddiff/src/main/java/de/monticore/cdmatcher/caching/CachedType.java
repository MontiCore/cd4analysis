/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.HashSet;
import java.util.Set;

public class CachedType {

  private final ASTCDType cachedType;

  private final Set<ASTCDType> superTypes = new HashSet<>();
  private final Set<ASTCDType> directSuperTypes = new HashSet<>();

  private final Set<ASTCDType> subTypes = new HashSet<>();
  private final Set<ASTCDType> directSubTypes = new HashSet<>();

  private final Set<ASTCDAttribute> directAttributes = new HashSet<>();
  private final Set<ASTCDAttribute> attributes = new HashSet<>();

  private final Set<ASTCDAssociation> directAssociations = new HashSet<>();
  private final Set<ASTCDAssociation> associations = new HashSet<>();

  private final Set<ASTCDMethod> directMethods = new HashSet<>();
  private final Set<ASTCDMethod> methods = new HashSet<>();

  public CachedType(ASTCDType cachedType) {
    this.cachedType = cachedType;
  }

  public ASTCDType getCachedType() { return cachedType; }

  public Set<ASTCDType> getSuperTypes() { return superTypes; }

  public void addSuperType(ASTCDType superType) {
    superTypes.add(superType);
  }

  public Set<ASTCDType> getDirectSuperTypes() { return directSuperTypes; }

  public void addDirectSuperType(ASTCDType directSuperType) {
    directSuperTypes.add(directSuperType);
  }

  public Set<ASTCDType> getSubTypes() { return subTypes; }

  public void addSubType(ASTCDType subType) {
    subTypes.add(subType);
  }

  public Set<ASTCDType> getDirectSubTypes() { return directSubTypes; }

  public void addDirectSubType(ASTCDType directSubType) {
    directSubTypes.add(directSubType);
  }

  public Set<ASTCDAttribute> getDirectAttributes() { return directAttributes; }

  public void addDirectAttribute(ASTCDAttribute directAttribute) {
    directAttributes.add(directAttribute);
  }

  public Set<ASTCDAttribute> getAttributes() { return attributes; }

  public void addAttribute(ASTCDAttribute attribute) {
    attributes.add(attribute);
  }

  public Set<ASTCDAssociation> getDirectAssociations() { return directAssociations; }

  public void addDirectAssociation(ASTCDAssociation directAssociation) {
    directAssociations.add(directAssociation);
  }

  public Set<ASTCDAssociation> getAssociations() { return associations; }

  public void addAssociation(ASTCDAssociation association) {
    associations.add(association);
  }

  public Set<ASTCDMethod> getDirectMethods() {
    return directMethods;
  }

  public void addDirectMethod(ASTCDMethod directMethod) {
    directMethods.add(directMethod);
  }

  public Set<ASTCDMethod> getMethods() {
    return methods;
  }

  public void addMethod(ASTCDMethod method) {
    methods.add(method);
  }

}
