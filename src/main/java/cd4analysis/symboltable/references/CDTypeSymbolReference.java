/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable.references;

import cd4analysis.symboltable.CDAssociationSymbol;
import cd4analysis.symboltable.CDAttributeSymbol;
import cd4analysis.symboltable.CDMethodSymbol;
import cd4analysis.symboltable.CDTypeSymbol;
import cd4analysis.symboltable.Stereotype;
import com.google.common.base.Optional;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeManipulationApi;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.TypeReference;
import de.monticore.symboltable.references.TypeReferenceImpl;
import de.monticore.symboltable.types.ActualTypeArgument;

import java.util.List;

public class CDTypeSymbolReference extends CDTypeSymbol implements TypeReference<CDTypeSymbol> {

  private final TypeReference<CDTypeSymbol> typeReference;

  public CDTypeSymbolReference(String name, Scope definingScopeOfReference) {
    super(name);

    typeReference = new TypeReferenceImpl<>(name, CDTypeSymbol.KIND, definingScopeOfReference);
  }

  @Override
  public CDTypeSymbol getReferencedSymbol() {
    return typeReference.getReferencedSymbol();
  }

  @Override
  public Scope getSpannedScope() {
    return getReferencedSymbol().getSpannedScope();
  }

  @Override
  public void setDefinedInScope(ScopeManipulationApi scope) {
    getReferencedSymbol().setDefinedInScope(scope);
  }

  @Override
  public String getModelName() {
    return getReferencedSymbol().getModelName();
  }

  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
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
  public Optional<CDTypeSymbol> getSuperClass() {
    return getReferencedSymbol().getSuperClass();
  }

  @Override
  public void setSuperClass(CDTypeSymbol superClass) {
    getReferencedSymbol().setSuperClass(superClass);
  }

  @Override
  public List<CDTypeSymbol> getInterfaces() {
    return getReferencedSymbol().getInterfaces();
  }

  @Override
  public void addInterface(CDTypeSymbol superInterface) {
    getReferencedSymbol().addInterface(superInterface);
  }

  @Override
  public List<CDTypeSymbol> getSuperTypes() {
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
  public void addField(CDAttributeSymbol field) {
    getReferencedSymbol().addField(field);
  }

  @Override
  public List<CDAttributeSymbol> getAttribute() {
    return getReferencedSymbol().getAttribute();
  }

  @Override
  public Optional<CDAttributeSymbol> getField(String fieldName) {
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
  public List<CDAssociationSymbol> getAssociations() {
    return getReferencedSymbol().getAssociations();
  }

  @Override
  public List<CDAttributeSymbol> getEnumConstants() {
    return getReferencedSymbol().getEnumConstants();
  }

  @Override
  public List<? extends Symbol> getChildren() {
    return getReferencedSymbol().getChildren();
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
  public int getDimension() {
    return getReferencedSymbol().getDimension();
  }

  @Override
  public void addFormalTypeParameter(CDTypeSymbol formalTypeParameter) {
    getReferencedSymbol().addFormalTypeParameter(formalTypeParameter);
  }

  @Override
  public List<ActualTypeArgument> getActualTypeArguments() {
    return typeReference.getActualTypeArguments();
  }

  @Override
  public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
    typeReference.setActualTypeArguments(actualTypeArguments);
  }

}
