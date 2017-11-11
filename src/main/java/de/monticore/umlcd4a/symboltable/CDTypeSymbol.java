/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;

public class CDTypeSymbol extends CommonJTypeSymbol<CDTypeSymbol, CDFieldSymbol, CDMethodSymbol, CDTypeSymbolReference> {
  
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

  /**
   * Tries to get an association by name.
   *
   * @param name name of the association.
   */
  public Optional<CDAssociationSymbol> getAssociation(String name) {
    return this.getAssociations().stream()
            .filter(a -> a.getName().equals(name))
            .findFirst();
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
    final List<CDFieldSymbol> fields = getFields();

    for (CDTypeSymbol superType : getSuperTypes()) {
      for (CDFieldSymbol superField : superType.getFields()) {
        if (fields.stream().noneMatch(cdFieldSymbol -> cdFieldSymbol.getName().equals(superField.getName()))) {
          allSuperTypeFields.add(superField);
        }
      }

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
    allFields.addAll(getFields());
    // filter-out all fields with same name
    for (CDFieldSymbol inheritedField: getAllVisibleFieldsOfSuperTypes()) {
      if (getFields().stream().noneMatch(cdFieldSymbol -> cdFieldSymbol.getName().equals(inheritedField.getName()))) {
        allFields.add(inheritedField);
      }   
    }
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

  /**
   * Gets all visible fields, all visible associations and all methods including inherited.
   *
   * @return visible elements including inherited fields.
   */
  public Scope getAllKindElements() {
    MutableScope scope = new CommonScope();
    addAssociationRoleNamesToScope(scope);
    getAllVisibleFields().forEach(scope::add);
    getAllAssociations().forEach(scope::add);
    getMethods().forEach(scope::add);
    getEnclosingScope().getResolvingFilters().forEach(scope::addResolver);
    return scope;
  }

  /**
   * Determines whether this is a superType of type
   */
  public boolean isSameOrSuperType(CDTypeSymbol type) {
    if(this.getFullName().equals(type.getFullName())){
      return true;
    }
    for(CDTypeSymbol superType : type.getSuperTypes()) {
      if(superType.getFullName().equals(this.getFullName())){
        return true;
      }
    }
    return false;
  }

  /**
   * [*] Auction (auctions) -> (bidder) Person [*];
   * If this is Auction then bidder is added as role name
   * If this is Person then auctions is added as role name
   * Purpose: Auction.bidder and Person.auctions can be resolved
   *
   * [*] Auction -> Person [*];
   * If the role name is missing, then the the lowercase type name is used as role name
   * If this is Auction then person is added as role name
   * If this is Person then auction is added as role name
   */
  protected void addAssociationRoleNamesToScope(MutableScope scope) {
    for (CDAssociationSymbol assoc : getAllAssociations()) {
      if(assoc.getSourceType().isSameOrSuperType(this) &&
              (assoc.getTargetRole().isPresent() ||
                      // role name can not be derived for self reflevtive associations
                      !assoc.getTargetType().getFullName().equals(assoc.getSourceType().getFullName()))) {

        String roleName = assoc.getTargetRole().orElse(firstLetterToLowerCase(assoc.getTargetType().getName()));
        CDAssociationSymbol newAssoc = flatCopyOfAssoc(assoc);
        newAssoc.setAssocName(Optional.of(roleName));
        scope.add(newAssoc);
      }
      else if(assoc.getTargetType().isSameOrSuperType(this) &&
              (assoc.getSourceRole().isPresent() ||
                      // role name can not be derived for self reflevtive associations
                      !assoc.getTargetType().getFullName().equals(assoc.getSourceType().getFullName()))) {

        String roleName = assoc.getSourceRole().orElse(firstLetterToLowerCase(assoc.getSourceType().getName()));
        CDAssociationSymbol newAssoc = flatCopyOfAssoc(assoc);
        newAssoc.setAssocName(Optional.of(roleName));
        scope.add(newAssoc);

      }
    }
  }

  private String firstLetterToLowerCase(String name) {
    return Character.toLowerCase(name.charAt(0)) +
            (name.length() > 1 ? name.substring(1) : "");
  }

  private CDAssociationSymbol flatCopyOfAssoc(CDAssociationSymbol assoc) {
    CDAssociationSymbol newAssoc = new CDAssociationSymbol(assoc.getSourceType(), assoc.getTargetType());
    newAssoc.setSourceCardinality(assoc.getSourceCardinality());
    newAssoc.setTargetCardinality(assoc.getTargetCardinality());
    newAssoc.setQualifier(assoc.getQualifier());
    newAssoc.setSourceRole(assoc.getSourceRole());
    newAssoc.setTargetRole(assoc.getTargetRole());
    newAssoc.setBidirectional(assoc.isBidirectional());
    newAssoc.setDerived(assoc.isDerived());
    newAssoc.setAssocName(assoc.getAssocName());
    newAssoc.setRelationship(assoc.getRelationship());
    assoc.getStereotypes().forEach(newAssoc::addStereotype);

    return newAssoc;
  }
}
