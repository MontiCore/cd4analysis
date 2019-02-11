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

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CDTypeSymbol extends CommonJTypeSymbol<CDTypeSymbol, CDFieldSymbol, CDMethodSymbol, CDTypeSymbolReference> {
  
  public static final CDTypeSymbolKind KIND = new CDTypeSymbolKind();
  
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  private final List<CDAssociationSymbol> associations = new ArrayList<>();
  private final List<CDAssociationSymbol> specAssociations = new ArrayList<>();
  
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

  public void addSpecAssociation(final CDAssociationSymbol assoc) {
    specAssociations.add(assoc);
  }

  public List<CDAssociationSymbol> getSpecAssociations() {
    return ImmutableList.copyOf(specAssociations);
  }

  public List<CDAssociationSymbol> getInheritedSpecAssociations() {
    final List<CDAssociationSymbol> allNames = new ArrayList<>();
    for (CDTypeSymbol superType : getSuperTypes()) {
      allNames.addAll(superType.getAllSpecAssociations());
    }
    return ImmutableList.copyOf(allNames);
  }

  /**
   * Get all associations including inherited
   *
   * @return
   */
  public List<CDAssociationSymbol> getAllSpecAssociations() {
    final List<CDAssociationSymbol> allNames = new ArrayList<>();
    allNames.addAll(getInheritedSpecAssociations());
    allNames.addAll(getSpecAssociations());
    return ImmutableList.copyOf(allNames);
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

  public boolean isSameOrSuperType(CDTypeSymbol type) {
    if (this.getFullName().equals(type.getFullName())) {
      return true;
    }
    return getSuperTypesTransitive().stream().anyMatch(t -> t.getFullName().equals(type.getFullName()));
  }

  public List<CDTypeSymbolReference> getSuperTypesTransitive() {
    return getSuperTypesTransitive(new CDTypeSymbolReference(this.getName(), this.getEnclosingScope()));
  }

  protected List<CDTypeSymbolReference> getSuperTypesTransitive(CDTypeSymbolReference startType) {
    List<CDTypeSymbolReference> superTypes = new ArrayList();
    if (startType.getSuperClass().isPresent()) {
      CDTypeSymbolReference s = startType.getSuperClass().get();
      superTypes.add(s);
      superTypes.addAll(getSuperTypesTransitive(new CDTypeSymbolReference(s.getName(),
              s.getEnclosingScope())));
    }

    for (CDTypeSymbolReference i : startType.getInterfaces()) {
      superTypes.add(i);
      superTypes.addAll(getSuperTypesTransitive(new CDTypeSymbolReference(i.getName(),
              i.getEnclosingScope())));
    }
    return superTypes;
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
    for (Stereotype stereotype : getStereotypes()) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.empty();
  }

  public boolean containsStereotype(String name, String value) {
    for (Stereotype stereotype : getStereotypes()) {
      if (stereotype.compare(name, value)) {
        return true;
      }
    }
    return false;
  }

  protected List<Stereotype> addStereoTypes(List<Stereotype> stereotypes, CDTypeSymbol type) {
    for (Stereotype s : type.getStereotypes()) {
      if (!stereotypes.stream().anyMatch(x -> x.getName().equals(s.getName()))) {
        stereotypes.add(s);
      }
    }
    type.getSuperTypes().stream().forEach(t -> addStereoTypes(stereotypes, t));
    return stereotypes;
  }
  
  public List<Stereotype> getAllStereotypes() {
   return addStereoTypes(new ArrayList<>(), this);
  }
  
  public Optional<Stereotype> getAllStereotype(String name) {
    for (Stereotype stereotype : getAllStereotypes()) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.empty();
  }
  
  public boolean containsAllStereotype(String name, String value) {
    for (Stereotype stereotype : getAllStereotypes()) {
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

  public Collection<CDMethodSymbol> getAllVisibleMethodsOfSuperTypes() {
    final Set<CDMethodSymbol> allSuperTypeMethods = new LinkedHashSet<>();
    final List<CDMethodSymbol> methods = getMethods();

    for (CDTypeSymbol superType : getSuperTypes()) {
      for (CDMethodSymbol superMethod : superType.getMethods()) {
        if (methods.stream().noneMatch(cdFieldSymbol -> cdFieldSymbol.getName().equals(superMethod.getName()))) {
          allSuperTypeMethods.add(superMethod);
        }
      }

      allSuperTypeMethods.addAll(superType.getAllVisibleMethodsOfSuperTypes());
    }

    // filter-out all private fields
    final Set<CDMethodSymbol> allVisibleSuperTypeMethods = allSuperTypeMethods.stream().
            filter(field -> !field.isPrivate())
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return ImmutableSet.copyOf(allVisibleSuperTypeMethods);
  }

  /**
   * Gets all visible methods including inherited.
   *
   * @return visible methods including inherited methods.
   */
  public Collection<CDMethodSymbol> getAllVisibleMethods() {
    final Set<CDMethodSymbol> allMethods = new LinkedHashSet<>();
    allMethods.addAll(getMethods());
    // filter-out all fields with same name
    for (CDMethodSymbol inheritedMethod: getAllVisibleMethodsOfSuperTypes()) {
      if (getMethods().stream().noneMatch(cdMethodSymbol -> cdMethodSymbol.getName().equals(inheritedMethod.getName()))) {
        allMethods.add(inheritedMethod);
      }
    }
    // filter-out all private fields
    final Set<CDMethodSymbol> allVisibleMethods = allMethods.stream().
            filter(method -> !method.isPrivate())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    return ImmutableSet.copyOf(allVisibleMethods);
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

  private List<CDTypeSymbol> getAllSuperTypes() {
    ArrayList allSuperTypes = new ArrayList();
    allSuperTypes.addAll(this.getSuperTypes());

    if(this.getSuperClass().isPresent()) {
      allSuperTypes.addAll(this.getSuperClass().get().getReferencedSymbol().getAllSuperTypes());
    }

    return allSuperTypes;
  }
}
