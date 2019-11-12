/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.IScope;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.monticore.symboltable.ISymbol.sortSymbolsByPosition;

/**
 * This class helps in creating, editing, and managing the symboltable for the CD4Analysis language
 *
 * @author Alexander Roth
 */
//TODO: move this class to Cd4Analysis
public class CDSymbolTable {
  
  private final CD4AnalysisLanguage cd4AnalysisLang = new CD4AnalysisLanguage();

  private ICD4AnalysisScope cdScope;
  
  private CD4AnalysisGlobalScope globalScope;
  
  private CD4AnalysisArtifactScope artifactScope;
  
  public CDSymbolTable(ASTCDCompilationUnit ast, List<File> modelPaths) {
    checkNotNull(modelPaths);

    this.globalScope = createSymboltable(ast, modelPaths);
  }
  
  /**
   * Create a new symbol table from a given ASTCompilation unit
   *
   * @param ast
   * @param modelPaths
   * @return
   */
  private CD4AnalysisGlobalScope createSymboltable(ASTCDCompilationUnit ast,
      List<File> modelPaths) {
    
    ModelPath modelPath = new ModelPath(modelPaths.stream().map(mp -> Paths.get(mp.getAbsolutePath())).collect(Collectors.toList()));

    CD4AnalysisGlobalScope globalScope = new CD4AnalysisGlobalScope(modelPath, cd4AnalysisLang);

    CD4AnalysisSymbolTableCreatorDelegator stc = cd4AnalysisLang
            .getSymbolTableCreator(globalScope);
    
    this.artifactScope = (CD4AnalysisArtifactScope) stc.createFromAST(ast);
    this.cdScope = ast.getCDDefinition().getSpannedScope();

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
    return this.cdScope.<CDTypeSymbol> resolveCDType(className);
  }

  // TODO AR: I don't like this method ...
  public Optional<CDAssociationSymbol> resolveAssoc(String assocName) {
    return this.cdScope.resolveCDAssociation(assocName);
  }

  public List<CDFieldSymbol> getVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        !field.isIsDerived() && !field.isIsFinal() && !field.isIsStatic()).collect(Collectors.toList());

    if (cdType.get().isPresentSuperClass()) {
      visibleAttr.addAll(getVisibleAttributesInHierarchy(cdType.get()
          .getSuperClass().getName()));
    }

    return visibleAttr;
  }

  public List<CDFieldSymbol> getVisibleAttributes(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());

    return cdType.get().getFields().stream().filter(field ->
        !field.isIsDerived() && !field.isIsFinal() && !field.isIsStatic()).collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getDerivedAttributesInHierarchy(CDTypeSymbol symbol) {
    List<CDFieldSymbol> visibleAttr = symbol.getFields().stream().filter(field ->
        field.isIsDerived()).collect(Collectors.toList());

    if (symbol.isPresentSuperClass()) {
      visibleAttr.addAll(getDerivedAttributesInHierarchy(symbol.getSuperClass().getLoadedSymbol()));
    }

    return visibleAttr;
  }

  public List<CDFieldSymbol> getNonVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());

    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        field.isIsDerived() && field.isIsFinal() && field.isIsStatic() || field.isIsPrivate())
        .collect(Collectors.toList());

    if (cdType.get().isPresentSuperClass()) {
      visibleAttr.addAll(getNonVisibleAttributesInHierarchy(cdType.get()
          .getSuperClass().getName()));
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
      .map(CDTypeSymbol::getCdInterfaceList)
      .flatMap(Collection::stream)
      .flatMap(superInterface -> getSuperInterfacesRecursively(superInterface.getLoadedSymbol(), new ArrayList<>()).stream())
      .collect(Collectors.toList());
  }

  private List<CDTypeSymbol> getSuperClassesRecursively(CDTypeSymbol cdType,
                                                        final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);

    symbols.add(cdType);
    if (cdType.isPresentSuperClass()) {
      getSuperClassesRecursively(cdType.getSuperClass().getLoadedSymbol(), symbols);
    }

    return symbols;
  }

  private List<CDTypeSymbol> getSuperInterfacesRecursively(CDTypeSymbol cdType,
                                                           final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);

    symbols.add(cdType);
    cdType.getCdInterfaceList().forEach(i -> getSuperInterfacesRecursively(i.getLoadedSymbol(), symbols));

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
        if (sym.isIsClass()) {
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
    String currentScopeName = this.cdScope.getName();

    IScope parentScope = type.getSpannedScope()
            .getEnclosingScope();
    if (parentScope != null
            && currentScopeName.equals(parentScope.getName())) {
      return true;
    }
    return false;
  }

  public boolean isTypeDefinedInModel(String typeName) {
    Optional<CDTypeSymbol> type = this.cdScope.resolveCDType(typeName);
    if (type.isPresent()) {

      return isTypeDefinedInModel(type.get());
    }
    else {
      return false;
    }
  }

  private List<CDTypeSymbol> getSubclassesAndInterfaces(String name) {
    List<CDTypeSymbol> cdTypes = Lists.newArrayList();

    for (CDTypeSymbol cdType : this.cdScope.getLocalCDTypeSymbols()) {
      // check if the given class is a super class
      if (cdType.isPresentSuperClass()
          && cdType.getSuperClass().getName().equals(name)) {
        cdTypes.add(cdType);
      }

      // check if the given class is an interface
      if (!cdType.getCdInterfaceList().isEmpty()) {
        for (CDTypeSymbolLoader a : cdType.getCdInterfaceList()) {
          if (a.getName().equals(name)) {
            cdTypes.add(cdType);
          }
        }
      }
    }
    return cdTypes;
  }

  public List<CDAssociationSymbol> getAllAssociations() {
    return sortSymbolsByPosition(this.cdScope.getLocalCDAssociationSymbols());
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
    // TODO MB: Darf man einfach den vollqualifizierten Namen abspeichern?
    //    CommonSymbol type = new CDTypeSymbol(Names.getSimpleName(typeName));
    //    type.setPackageName(Names.getQualifier(typeName));
    CDTypeSymbol type = new CDTypeSymbol(typeName);
    this.globalScope.add(type);
  }
}
