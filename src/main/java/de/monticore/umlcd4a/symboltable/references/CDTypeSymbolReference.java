/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable.references;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.Stereotype;

public class CDTypeSymbolReference extends CDTypeSymbol implements JTypeReference<CDTypeSymbol> {
  
  private final CommonJTypeReference<CDTypeSymbol> typeReference;
  
  public CDTypeSymbolReference(String name, Scope definingScopeOfReference) {
    super(name);
    
    typeReference = new CommonJTypeReference<>(name, CDTypeSymbol.KIND, definingScopeOfReference);
  }
  
  @Override
  public CDTypeSymbol getReferencedSymbol() {
    return typeReference.getReferencedSymbol();
  }
  
  @Override
  public boolean existsReferencedSymbol() {
    return typeReference.existsReferencedSymbol();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return typeReference.isReferencedSymbolLoaded();
  }

  @Override
  public List<ActualTypeArgument> getActualTypeArguments() {
    return typeReference.getActualTypeArguments();
  }

  @Override
  public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
    typeReference.setActualTypeArguments(actualTypeArguments);
  }

  @Override
  public void setDimension(int dimension) {
    typeReference.setDimension(dimension);
  }

  @Override
  public int getDimension() {
    return typeReference.getDimension();
  }

  @Override
  public String getName() {
    if (isReferencedSymbolLoaded()) {
      return getReferencedSymbol().getName();
    }

    return super.getName();
  }

  @Override
  public Scope getSpannedScope() {
    return getReferencedSymbol().getSpannedScope();
  }
  
  @Override
  public void setEnclosingScope(MutableScope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }
  
  @Override
  public Scope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }
    
  @Override
  public String getModelName() {
    return getReferencedSymbol().getModelName();
  }
  
  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public List<CDAssociationSymbol> getInheritedAssociations() {
    return getReferencedSymbol().getInheritedAssociations();
  }
  
  @Override
  public List<CDAssociationSymbol> getAllAssociations() {
    return getReferencedSymbol().getAllAssociations();
  }
  
  @Override
  public Collection<CDFieldSymbol> getAllVisibleFields() {
    return getReferencedSymbol().getAllVisibleFields();
  }
  
  @Override
  public String getPackageName() {
    return getReferencedSymbol().getPackageName();
  }
  
  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }
  
  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }
  
  @Override
  public void addStereotype(Stereotype stereotype) {
    getReferencedSymbol().addStereotype(stereotype);
  }
  
  @Override
  public boolean containsStereotype(String name, String value) {
    return getReferencedSymbol().containsStereotype(name, value);
  }
  
  @Override
  public Optional<Stereotype> getStereotype(String name) {
    return getReferencedSymbol().getStereotype(name);
  }
  
  @Override
  public Optional<CDTypeSymbolReference> getSuperClass() {
    return getReferencedSymbol().getSuperClass();
  }
  
  @Override
  public void setSuperClass(CDTypeSymbolReference superClass) {
    getReferencedSymbol().setSuperClass(superClass);
  }
  
  @Override
  public List<CDTypeSymbolReference> getInterfaces() {
    return getReferencedSymbol().getInterfaces();
  }
  
  @Override
  public void addInterface(CDTypeSymbolReference superInterface) {
    getReferencedSymbol().addInterface(superInterface);
  }
  
  @Override
  public List<CDTypeSymbolReference> getSuperTypes() {
    return getReferencedSymbol().getSuperTypes();
  }
  
  @Override
  public List<CDTypeSymbol> getFormalTypeParameters() {
    return getReferencedSymbol().getFormalTypeParameters();
  }
  
  @Override
  public String getExtendedName() {
    return getReferencedSymbol().getExtendedName();
  }
  
  @Override
  public void addField(CDFieldSymbol field) {
    getReferencedSymbol().addField(field);
  }
  
  @Override
  public List<CDFieldSymbol> getFields() {
    return getReferencedSymbol().getFields();
  }
  
  @Override
  public Optional<CDFieldSymbol> getField(String fieldName) {
    return getReferencedSymbol().getField(fieldName);
  }
  
  @Override
  public void addMethod(CDMethodSymbol method) {
    getReferencedSymbol().addMethod(method);
  }
  
  @Override
  public List<CDMethodSymbol> getMethods() {
    return getReferencedSymbol().getMethods();
  }
  
  @Override
  public Optional<CDMethodSymbol> getMethod(String methodName) {
    return getReferencedSymbol().getMethod(methodName);
  }
  
  @Override
  public void addConstructor(CDMethodSymbol constructor) {
    getReferencedSymbol().addConstructor(constructor);
  }
  
  @Override
  public List<CDMethodSymbol> getConstructors() {
    return getReferencedSymbol().getConstructors();
  }
  
  @Override
  public void addAssociation(CDAssociationSymbol assoc) {
    getReferencedSymbol().addAssociation(assoc);
  }

  @Override
  public void addSpecAssociation(final CDAssociationSymbol assoc) {
    getReferencedSymbol().addSpecAssociation(assoc);
  }
  
  @Override
  public List<CDAssociationSymbol> getAssociations() {
    return getReferencedSymbol().getAssociations();
  }
  
  @Override
  public List<CDFieldSymbol> getEnumConstants() {
    return getReferencedSymbol().getEnumConstants();
  }
  
  @Override
  public void setAbstract(boolean isAbstract) {
    getReferencedSymbol().setAbstract(isAbstract);
  }
  
  @Override
  public boolean isAbstract() {
    return getReferencedSymbol().isAbstract();
  }
  
  @Override
  public void setFinal(boolean isFinal) {
    getReferencedSymbol().setFinal(isFinal);
  }
  
  @Override
  public boolean isFinal() {
    return getReferencedSymbol().isFinal();
  }
  
  @Override
  public void setInterface(boolean isInterface) {
    getReferencedSymbol().setInterface(isInterface);
  }
  
  @Override
  public boolean isInterface() {
    return getReferencedSymbol().isInterface();
  }
  
  @Override
  public void setEnum(boolean isEnum) {
    getReferencedSymbol().setEnum(isEnum);
  }
  
  @Override
  public boolean isEnum() {
    return getReferencedSymbol().isEnum();
  }
  
  @Override
  public boolean isClass() {
    return getReferencedSymbol().isClass();
  }
  
  @Override
  public List<Stereotype> getStereotypes() {
    return getReferencedSymbol().getStereotypes();
  }
  
  @Override
  public boolean isGeneric() {
    return getReferencedSymbol().isGeneric();
  }
  
  @Override
  public void setPrivate() {
    getReferencedSymbol().setPrivate();
  }
  
  @Override
  public void setProtected() {
    getReferencedSymbol().setProtected();
  }
  
  @Override
  public void setPublic() {
    getReferencedSymbol().setPublic();
  }
  
  @Override
  public boolean isPrivate() {
    return getReferencedSymbol().isPrivate();
  }
  
  @Override
  public boolean isProtected() {
    return getReferencedSymbol().isProtected();
  }
  
  @Override
  public boolean isPublic() {
    return getReferencedSymbol().isPublic();
  }
  
  @Override
  public Collection<CDFieldSymbol> getAllVisibleFieldsOfSuperTypes() {
    return getReferencedSymbol().getAllVisibleFieldsOfSuperTypes();
  }
  
  @Override
  public boolean hasSuperType(String superTypeName) {
    return getReferencedSymbol().hasSuperType(superTypeName);
  }
  
  @Override
  public boolean hasSuperTypeByFullName(String superTypeFullName) {
    return getReferencedSymbol().hasSuperTypeByFullName(superTypeFullName);
  }
  
  @Override
  public void addFormalTypeParameter(CDTypeSymbol formalTypeParameter) {
    getReferencedSymbol().addFormalTypeParameter(formalTypeParameter);
  }
  
}
