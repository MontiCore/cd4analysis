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
import static com.google.common.base.Preconditions.checkNotNull;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;

/**
 * This class helps in creating, editing, and managing the symboltable for the CD4Analysis language
 *
 * @author Alexander Roth
 */
//TODO: move this class to Cd4Analysis
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CDSymbolTable {
  
  private final CD4AnalysisLanguage cd4AnalysisLang = new CD4AnalysisLanguage();
  
  private final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
  
  private MutableScope cdScope;
  
  private GlobalScope globalScope;
  
  private ArtifactScope artifactScope;
  
  public CDSymbolTable(ASTCDCompilationUnit ast, List<File> modelPaths) {
    checkNotNull(modelPaths);
    
    resolvingConfiguration.addDefaultFilters(cd4AnalysisLang.getResolvingFilters());
    
    this.globalScope = createSymboltable(ast, modelPaths);
    this.artifactScope = (ArtifactScope) this.globalScope.getSubScopes()
        .iterator().next();
    this.cdScope = artifactScope.getSubScopes().iterator().next();
  }
  
  /**
   * Create a new symbol table from a given ASTCompilation unit
   *
   * @param ast
   * @param modelPaths
   * @return
   */
  private GlobalScope createSymboltable(ASTCDCompilationUnit ast,
      List<File> modelPaths) {
    
    ModelPath modelPath = new ModelPath(modelPaths.stream().map(mp -> Paths.get(mp.getAbsolutePath())).collect(Collectors.toList()));
    
    GlobalScope globalScope = new GlobalScope(modelPath, cd4AnalysisLang, resolvingConfiguration);
    
    Optional<CD4AnalysisSymbolTableCreator> stc = cd4AnalysisLang
        .getSymbolTableCreator(resolvingConfiguration, globalScope);
    
    if (stc.isPresent()) {
      stc.get().createFromAST(ast);
    }
    
    return globalScope;
  }
  
  /**
   * Retrieve the CDTypeSymbol from a class diagram scope, i.e., address Symbols without
   * qualification. For instance, given classdiagram D { class A; }. To resolve the class A simply
   * call resolve("A") instead of resolve("D.A")
   *
   * @param className the name of the class
   * @return a list of all visible attributes
   */
  public Optional<CDTypeSymbol> resolve(String className) {
    return this.cdScope.<CDTypeSymbol> resolve(className, CDTypeSymbol.KIND);
  }
  
  // TODO AR: I don't like this method ...
  public Optional<CDAssociationSymbol> resolveAssoc(String assocName) {
    return this.cdScope.<CDAssociationSymbol> resolve(assocName,
        CDAssociationSymbol.KIND);
  }
  
  public List<CDFieldSymbol> getVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        !field.isDerived() && !field.isFinal() && !field.isStatic()).collect(Collectors.toList());
    
    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getVisibleAttributesInHierarchy(cdType.get()
          .getSuperClass().get().getName()));
    }
    
    return visibleAttr;
  }
  
  public List<CDFieldSymbol> getVisibleAttributes(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    
    return cdType.get().getFields().stream().filter(field ->
        !field.isDerived() && !field.isFinal() && !field.isStatic()).collect(Collectors.toList());
  }
  
  public List<CDFieldSymbol> getDerivedAttributesInHierarchy(CDTypeSymbol symbol) {
    List<CDFieldSymbol> visibleAttr = symbol.getFields().stream().filter(field ->
        field.isDerived()).collect(Collectors.toList());
    
    if (symbol.getSuperClass().isPresent()) {
      visibleAttr.addAll(getDerivedAttributesInHierarchy(symbol.getSuperClass().get()));
    }
    
    return visibleAttr;
  }
  
  public List<CDFieldSymbol> getNonVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    
    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        field.isDerived() && field.isFinal() && field.isStatic() || field.isPrivate())
        .collect(Collectors.toList());
    
    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getNonVisibleAttributesInHierarchy(cdType.get()
          .getSuperClass().get().getName()));
    }
    
    return visibleAttr;
  }
  
  public List<CDTypeSymbol> getAllSuperTypes(String className) {
	List<CDTypeSymbol> superTypes = getAllSuperClasses(className);
	superTypes.addAll(getAllSuperInterfaces(className));
	return superTypes;
  }
  
  public List<CDTypeSymbol> getAllSuperClasses(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    return getSuperClassesRecursively(cdType.get(), Lists.newArrayList());
  }
  
  public List<CDTypeSymbol> getAllSuperInterfaces(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    return getAllSuperClasses(className).stream()
      .map(CDTypeSymbol::getInterfaces)
      .flatMap(Collection::stream)
      .flatMap(superInterface -> getSuperInterfacesRecursively(superInterface, new ArrayList<>()).stream())
      .collect(Collectors.toList());
  }
  
  private List<CDTypeSymbol> getSuperClassesRecursively(CDTypeSymbol cdType,
      final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);
    
    symbols.add(cdType);
    if (cdType.getSuperClass().isPresent()) {
      getSuperClassesRecursively(cdType.getSuperClass().get(), symbols);
    }
    
    return symbols;
  }
  
  private List<CDTypeSymbol> getSuperInterfacesRecursively(CDTypeSymbol cdType,
      final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);
    
    symbols.add(cdType);
    cdType.getInterfaces().forEach(i -> getSuperInterfacesRecursively(i, symbols));
    
    return symbols;
  }
  
  /**
   * Recursively get all subclasses. This means if the name of a class is passed all subclasses are
   * returned. If the name of an interface is passed, then all sub interfaces and implementing
   * classes are returned.
   *
   * @param name name of a class or interface
   * @return list of subclasses & subinterfaces
   */
  public List<CDTypeSymbol> getSubclasses(String name) {
    return getSubsRecusively(name, Lists.newArrayList(), false);
  }
  
  public List<CDTypeSymbol> getImplementingSubclasses(String name) {
    return getSubsRecusively(name, Lists.newArrayList(), true);
  }
  
  private List<CDTypeSymbol> getSubsRecusively(String name,
      final List<CDTypeSymbol> lst, boolean implementingClassOnly) {
    
    // get all sub classes and interfaces
    final List<CDTypeSymbol> concreteSubs = getSubclassesAndInterfaces(name);
    
    if (concreteSubs.size() == 0) {
      return Lists.newArrayList();
    }
    
    for (CDTypeSymbol sym : concreteSubs) {
      // check if only the first occurrance of a concrete class is wanted
      if (implementingClassOnly) {
        if (sym.isClass()) {
          lst.add(sym);
        }
        else {
          getSubsRecusively(sym.getName(), lst, implementingClassOnly);
        }
      }
      // return sub classes and interfaces
      else {
        lst.add(sym);
        getSubsRecusively(sym.getName(), lst, implementingClassOnly);
      }
    }
    
    return lst;
  }
  
  public List<CDAssociationSymbol> getSuperClassAssociationsInHierarchy(String className) {
    List<CDTypeSymbol> types = getAllSuperClasses(className);
    List<CDAssociationSymbol> allAssociations = Lists.newArrayList();
    
    for (CDTypeSymbol type : types) {
      for (CDAssociationSymbol symbol : type.getAssociations()) {
        if (!allAssociations.contains(symbol)) {
          allAssociations.add(symbol);
        }
      }
    }
    
    return allAssociations;
  }
  
  public List<CDAssociationSymbol> getSuperInterfacesAssocsiationsInHierarchy(String className) {
    List<CDTypeSymbol> types = getAllSuperInterfaces(className);
    List<CDAssociationSymbol> allAssociations = Lists.newArrayList();
    
    for (CDTypeSymbol type : types) {
      for (CDAssociationSymbol symbol : type.getAssociations()) {
        if (!allAssociations.contains(symbol)) {
          allAssociations.add(symbol);
        }
      }
    }
    
    return allAssociations;
  }

  public boolean isTypeDefinedInModel(CDTypeSymbol type) {
    String currentScopeName = this.cdScope.getName().get();
    if (type instanceof CDTypeSymbolReference) {
      CDTypeSymbolReference ca = (CDTypeSymbolReference) type;
      if (ca.existsReferencedSymbol()) {
        Optional<? extends Scope> parentScope = type.getSpannedScope()
            .getEnclosingScope();

        if (parentScope.isPresent()
            && currentScopeName.equals(parentScope.get().getName())) {
          return true;
        }
      }
    } else {
      Optional<? extends Scope> parentScope = type.getSpannedScope()
          .getEnclosingScope();
      if (parentScope.isPresent()
          && currentScopeName.equals(parentScope.get().getName())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isTypeDefinedInModel(String typeName) {
    Optional<CDTypeSymbol> type = this.cdScope.resolve(typeName, CDTypeSymbol.KIND);
    if (type.isPresent()) {
      
      return isTypeDefinedInModel(type.get());
    }
    else {
      return false;
    }
  }
  
  private List<CDTypeSymbol> getSubclassesAndInterfaces(String name) {
    List<CDTypeSymbol> cdTypes = Lists.newArrayList();
    
    for (Symbol symbol : this.cdScope.resolveLocally(CDTypeSymbol.KIND)) {
      CDTypeSymbol cdType = (CDTypeSymbol) symbol;
      
      // check if the given class is a super class
      if (cdType.getSuperClass().isPresent()
          && cdType.getSuperClass().get().getName().equals(name)) {
        cdTypes.add(cdType);
      }
      
      // check if the given class is an interface
      if (!cdType.getInterfaces().isEmpty()) {
        for (CDTypeSymbol a : cdType.getInterfaces()) {
          if (a.getName().equals(name)) {
            cdTypes.add(cdType);
          }
        }
      }
    }
    return cdTypes;
  }
  
  public List<CDAssociationSymbol> getAllAssociations() {
    return sortSymbolsByPosition(this.cdScope.resolveLocally(CDAssociationSymbol.KIND));
  }
  
  public List<CDAssociationSymbol> getAllAssociationsForClass(String className) {
    return getAllSuperTypes(className).stream()
        .map(CDTypeSymbol::getAssociations)
        .flatMap(Collection::stream)
        .distinct()
        .collect(Collectors.toList());
  }
  
  public List<CDAssociationSymbol> getInheritedAssociations(ASTCDClass clazz) {
    List<CDAssociationSymbol> localAssociations = getLocalAssociations(clazz);
    return getAllAssociationsForClass(clazz.getName()).stream()
        .filter(association -> !localAssociations.contains(association))
        .collect(Collectors.toList());
  }
  
  public List<CDAssociationSymbol> getLocalAssociations(ASTCDClass clazz) {
    return resolve(clazz.getName())
        .map(CDTypeSymbol::getAssociations)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .collect(Collectors.toList());
  }
  
  public List<CDFieldSymbol> getInheritedAttributes(ASTCDClass clazz) {
    List<CDFieldSymbol> localAttributes = getVisibleAttributes(clazz.getName());
    List<CDFieldSymbol> inheritedAttributes = getVisibleAttributesInHierarchy(clazz
        .getName());
    inheritedAttributes.removeAll(localAttributes);
    return inheritedAttributes;
  }

  /**
   * Adds the symbol for the type with the given qualified or simple name to the global scope
   * @param typeName - type's name
   */
  public void defineType(String typeName) {
    CommonSymbol type = new CDTypeSymbol(Names.getSimpleName(typeName));
    type.setPackageName(Names.getQualifier(typeName));
    this.globalScope.add(type);
  }
}
