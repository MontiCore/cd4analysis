package de.monticore.umlcd4a.symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.se_rwth.commons.Names;

public class CDTypeSymbol extends CommonJTypeSymbol<CDTypeSymbol, CDFieldSymbol, CDMethodSymbol> {
  
  public static final CDTypeSymbolKind KIND = new CDTypeSymbolKind();
  
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  private final List<CDAssociationSymbol> associations = new ArrayList<>();
  
  private String stringRepresentation = "";
  
  public CDTypeSymbol(final String name) {
    super(name, KIND, CDFieldSymbol.KIND, CDMethodSymbol.KIND);
  }
  
  public String getExtendedName() {
    return "CD type " + getName();
  }
  
  public void addAssociation(final CDAssociationSymbol assoc) {
    associations.add(assoc);
  }
  
  public List<CDAssociationSymbol> getAssociations() {
    return ImmutableList.copyOf(associations);
  }
  
  public List<CDFieldSymbol> getEnumConstants() {
    final List<CDFieldSymbol> enums = getFields().stream()
        .filter(CDFieldSymbol::isEnumConstant)
        .collect(Collectors.toList());
    return ImmutableList.copyOf(enums);
  }
  
  public List<Stereotype> getStereotypes() {
    return stereotypes;
  }
  
  public Optional<Stereotype> getStereotype(String name) {
    for (Stereotype stereotype : this.stereotypes) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.empty();
  }
  
  public boolean containsStereotype(String name, String value) {
    for (Stereotype stereotype : this.stereotypes) {
      if (stereotype.compare(name, value)) {
        return true;
      }
    }
    return false;
  }
  
  public void addStereotype(Stereotype stereotype) {
    this.stereotypes.add(stereotype);
  }
  
  public String getModelName() {
    return Names.getQualifier(getFullName());
  }
  
  public Collection<CDFieldSymbol> getAllVisibleFieldsOfSuperTypes() {
    final Set<CDFieldSymbol> allSuperTypeFields = new LinkedHashSet<>();
    
    for (CDTypeSymbol superType : getSuperTypes()) {
      allSuperTypeFields.addAll(superType.getFields());
      allSuperTypeFields.addAll(superType.getAllVisibleFieldsOfSuperTypes());
    }
    
    // filter-out all private fields
    final Set<CDFieldSymbol> allVisibleSuperTypeFields = allSuperTypeFields.stream().
        filter(field -> !field.isPrivate())
        .collect(Collectors.toCollection(LinkedHashSet::new));
    
    return ImmutableSet.copyOf(allVisibleSuperTypeFields);
  }
  
  /**
   * Gets all visible fields including inherited.
   * 
   * @return visible fields including inherited fields.
   */
  public Collection<CDFieldSymbol> getAllVisibleFields() {
    final Set<CDFieldSymbol> allFields = new LinkedHashSet<>();
    allFields.addAll(getAllVisibleFieldsOfSuperTypes());
    allFields.addAll(getFields());
    // filter-out all private fields
    final Set<CDFieldSymbol> allVisibleFields = allFields.stream().
        filter(field -> !field.isPrivate())
        .collect(Collectors.toCollection(LinkedHashSet::new));
    return ImmutableSet.copyOf(allVisibleFields);
  }
  
  public List<CDAssociationSymbol> getInheritedAssociations() {
    final List<CDAssociationSymbol> allNames = new ArrayList<>();
    for (CDTypeSymbol superType : getSuperTypes()) {
      allNames.addAll(superType.getAllAssociations());
    }
    return ImmutableList.copyOf(allNames);
  }
  
  /**
   * Get all associations including inherited
   * 
   * @return
   */
  public List<CDAssociationSymbol> getAllAssociations() {
    final List<CDAssociationSymbol> allNames = new ArrayList<>();
    allNames.addAll(getInheritedAssociations());
    allNames.addAll(getAssociations());
    return ImmutableList.copyOf(allNames);
  }
  
  public boolean hasSuperType(final String superTypeName) {
    requireNonNull(superTypeName);
    checkArgument(!superTypeName.isEmpty());
    
    // Every type is a super type of itself.
    if (superTypeName.equals(getName())) {
      return true;
    }
    
    for (final CDTypeSymbol superType : getSuperTypes()) {
      if (superType.hasSuperType(superTypeName)) {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean hasSuperTypeByFullName(final String superTypeFullName) {
    requireNonNull(superTypeFullName);
    checkArgument(!superTypeFullName.isEmpty());
    
    // Every type is a super type of itself.
    if (superTypeFullName.equals(getFullName())) {
      return true;
    }
    
    for (final CDTypeSymbol superType : getSuperTypes()) {
      if (superType.hasSuperTypeByFullName(superTypeFullName)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Sets the string representation of this type reference. This can include the
   * type parameters, e.g., <code>List&lt;E&gt;</pre></code>
   * 
   * @param stringRepresentation
   */
  public void setStringRepresentation(final String stringRepresentation) {
    this.stringRepresentation = nullToEmpty(stringRepresentation);
  }
  
  public String getStringRepresentation() {
    if (stringRepresentation.isEmpty()) {
      return getName();
    }
    return stringRepresentation;
  }

}
