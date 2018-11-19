package de.monticore.umlcd4a.symboltable;/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import de.monticore.symboltable.*;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * TODO Use JTypeSymbolsHelper when MC version >= 4.4.1-SNAPSHOT
 */
public interface CD4AnalysisSymbolTableCreator extends CD4AnalysisVisitor, SymbolTableCreator {
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  default Scope createFromAST(ASTCD4AnalysisNode rootNode) {
    requireNonNull(rootNode);
    rootNode.accept(this);
    return getFirstCreatedScope();
  }
  
  @Override
  default void visit(final ASTCDCompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());
    
    setPackageName(Names.getQualifiedName(compilationUnit.getPackageList()));
    
    // we must create CDSymbol here to add imports to the symbol
    final String cdName = compilationUnit.getCDDefinition().getName();
    final CDSymbol cdSymbol = new CDSymbol(cdName);
    
    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getImportStatementList() != null) {
      for (ASTImportStatement imp : compilationUnit.getImportStatementList()) {
        String qualifiedImport = Names.getQualifiedName(imp.getImportList());
        cdSymbol.addImport(qualifiedImport);
        imports.add(new ImportStatement(qualifiedImport, imp.isStar()));
      }
    }
    
    final ArtifactScope scope = new ArtifactScope(Optional.ofNullable(getEnclosingScope()), getPackageName(), imports);
    compilationUnit.setEnclosingScope(getEnclosingScope());
    putOnStack(scope);
    scope.setAstNode(compilationUnit);
    compilationUnit.setSpannedScope(scope);
    
    // note that the cdsymbol scope is removed in the endVisit of
    // ASTCDDefinition
    addToScopeAndLinkWithNode(cdSymbol, compilationUnit.getCDDefinition());
  }
  
  @Override
  default void endVisit(final ASTCDCompilationUnit compilationUnit) {
    removeCurrentScope();
    
    Log.debug("Finished build of symboltable for CD: "
        + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());
    
    setEnclosingScopeOfNodes(compilationUnit);
  }
  
  @Override
  default void visit(final ASTCDDefinition astDefinition) {
    final String cdName = astDefinition.getName();
    
    setFullClassDiagramName(getPackageName().isEmpty() ? cdName : (getPackageName() + "." + cdName));
  }
  
  @Override
  default void endVisit(final ASTCDDefinition astDefinition) {
    astDefinition.getCDAssociationList().forEach(this::handleAssociation);
    removeCurrentScope();
  }
  
  // TODO think about external Stereotypes...
  default Collection<String> getExternals(ASTCDType type) {
    Collection<String> externals = new HashSet<>();
    final ASTModifier astModifier = type.getModifierOpt()
        .orElse(CD4AnalysisMill.modifierBuilder().build());
    
    if (astModifier.isPresentStereotype()) {
      for (ASTCDStereoValue stereo : astModifier.getStereotype().getValueList()) {
        if ("externalType".equals(stereo.getName()) && stereo.isPresentValue()) {
          externals.add(stereo.getValue());
        }
      }
    }
    
    return externals;
  }
  
  @Override
  default void visit(final ASTCDClass astClass) {
    final CDTypeSymbol classSymbol = new CDTypeSymbol(astClass.getName());
    
    Collection<String> externals = getExternals(astClass);
    if (astClass.isPresentSuperclass()) {
      ASTReferenceType superC = astClass.getSuperclass();
      if (!externals.contains(TypesPrinter.printType(superC))) {
        final CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(superC);
        classSymbol.setSuperClass(superClassSymbol);
      }
    }
    
    final ASTModifier astModifier = astClass.getModifierOpt()
        .orElse(CD4AnalysisMill.modifierBuilder().build());
    
    setModifiersOfType(classSymbol, astModifier);
    setStereotype(classSymbol, astClass.getStereotypeOpt());
    
    addInterfacesToType(classSymbol, astClass.getInterfaceList(), externals);
    
    addToScopeAndLinkWithNode(classSymbol, astClass);
  }

  default void setStereotype(CDTypeSymbol typeSymbol, Optional<ASTCDStereotype> stereotype) {
    if (stereotype.isPresent()) {
      for (final ASTCDStereoValue stereoValue : stereotype.get().getValueList()) {
        final Stereotype s = new Stereotype(stereoValue.getName(), stereoValue.getValueOpt().orElse(""));
        typeSymbol.addStereotype(s);
      }
    }
  }

  default void setModifiersOfType(final CDTypeSymbol typeSymbol, final ASTModifier astModifier) {
    if (astModifier != null) {
      typeSymbol.setAbstract(astModifier.isAbstract());
      typeSymbol.setFinal(astModifier.isFinal());
      
      if (astModifier.isProtected()) {
        typeSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        typeSymbol.setPrivate();
      }
      else {
        // public is default
        typeSymbol.setPublic();
      }
      
      if (astModifier.isPresentStereotype()) {
        for (final ASTCDStereoValue stereoValue : astModifier.getStereotype().getValueList()) {
          final Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getValueOpt().orElse(""));
          typeSymbol.addStereotype(stereotype);
        }
      }
    }
  }
  
  default CDTypeSymbolReference createCDTypeSymbolFromReference(
      final ASTReferenceType astReferenceType) {
    // TODO PN replace by type converter
    CDTypeSymbolReference superSymbol = null;
    if (astReferenceType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSuperClass = (ASTSimpleReferenceType) astReferenceType;
      superSymbol = new CDTypeSymbolReference(Names.getQualifiedName(astSuperClass.getNameList()),
          currentScope().get());
    }
    
    return superSymbol;
  }
  
  @Override
  default void visit(final ASTCDAttribute astAttribute) {
    final String typeName = TypesPrinter.printTypeWithoutTypeArguments(astAttribute.getType());
    
    // TODO PN type arguments are not set yet. For every argument a
    // CDTypeSymbolReference must be created.
    
    final CDTypeSymbolReference typeReference = new CDTypeSymbolReference(typeName, currentScope()
        .get());
    ASTType astType = astAttribute.getType();
    typeReference.setStringRepresentation(TypesPrinter.printType(astType));
    typeReference.setAstNode(astType);
    
    addTypeArgumentsToTypeSymbol(typeReference, astType);

    final CDFieldSymbol fieldSymbol = new CDFieldSymbol(astAttribute.getName(), typeReference);
    
    if (astAttribute.isPresentModifier()) {
      final ASTModifier astModifier = astAttribute.getModifier();
      
      fieldSymbol.setDerived(astModifier.isDerived());
      fieldSymbol.setStatic(astModifier.isStatic());
      fieldSymbol.setFinal(astModifier.isFinal());
      
      if (astModifier.isProtected()) {
        fieldSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        fieldSymbol.setPrivate();
      }
      else {
        // public is default
        fieldSymbol.setPublic();
      }
      
      if (astModifier.isPresentStereotype()) {
        for (final ASTCDStereoValue stereoValue : astModifier.getStereotype().getValueList()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
          // geschrieben)
          final Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          fieldSymbol.addStereotype(stereotype);
        }
      }
    }
    
    addToScopeAndLinkWithNode(fieldSymbol, astAttribute);
  }
  
  /**
   * TODO: Write me!
   * @param typeReference
   * @param astType
   */
  default void addTypeArgumentsToTypeSymbol(CDTypeSymbolReference typeReference, ASTReturnType astType) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.isPresentTypeArguments()) {
        return;
      }
      List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
      for (ASTTypeArgument astTypeArgument : astSimpleReferenceType.getTypeArguments()
          .getTypeArgumentList()) {
        if (astTypeArgument instanceof ASTWildcardType) {
          ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
          
          // Three cases can occur here: lower bound, upper bound, no bound
          if (astWildcardType.isPresentLowerBound() || astWildcardType.isPresentUpperBound())
          {
            // We have a bound.
            // Examples: Set<? extends Number>, Set<? super Integer>
            
            // new bound
            boolean lowerBound = astWildcardType.isPresentLowerBound();
            ASTType typeBound = lowerBound
                ? astWildcardType.getLowerBound()
                : astWildcardType
                    .getUpperBound();
            int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
            // TODO PN, GV: add dimension?
            CDTypeSymbolReference typeBoundSymbolReference = new CDTypeSymbolReference(
                TypesPrinter.printTypeWithoutTypeArguments(typeBound),
                currentScope().get());
            typeBoundSymbolReference.setStringRepresentation(TypesPrinter.printWildcardType(astWildcardType));
            typeBoundSymbolReference.setAstNode(astTypeArgument);
            ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
                typeBoundSymbolReference);
            
            // init bound
            addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound);
            
            actualTypeArguments.add(actualTypeArgument);
          }
          else {
            // No bound. Example: Set<?>
            actualTypeArguments.add(new ActualTypeArgument(false, false, new CDTypeSymbolReference(
                "?", currentScope().get())));
          }
        }
        else if (astTypeArgument instanceof ASTType) {
          // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
          ASTType astTypeNoBound = (ASTType) astTypeArgument;
          CDTypeSymbolReference typeArgumentSymbolReference = new CDTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArguments(astTypeNoBound), currentScope().get());
          // TODO PN, GV: add dimension?
          // TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound)
          
          typeArgumentSymbolReference.setStringRepresentation(TypesPrinter.printType(astTypeNoBound));
          typeArgumentSymbolReference.setAstNode(astTypeArgument);          
          addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);
          
          actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
        }
        else {
          Log.error("0xU0401 Unknown type argument " + astTypeArgument + " of type "
              + typeReference);
        }
        typeReference.setActualTypeArguments(actualTypeArguments);
      }
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypeList()) {
        // TODO
        /*ASTComplexReferenceType represents types like class or interface types
         * which always have ASTSimpleReferenceType as qualification. For
         * example: a.b.c<Arg>.d.e<Arg> */
      }
    }
    
  }

  @Override
  default void endVisit(final ASTCDClass astClass) {
    removeCurrentScope();
  }
  
  @Override
  default void visit(final ASTCDInterface astInterface) {
    final CDTypeSymbol interfaceSymbol = new CDTypeSymbol(astInterface.getName());
    interfaceSymbol.setInterface(true);
    // Interfaces are always abstract
    interfaceSymbol.setAbstract(true);
    
    Collection<String> externals = getExternals(astInterface);
    addInterfacesToType(interfaceSymbol, astInterface.getInterfaceList(), externals);
    setModifiersOfType(interfaceSymbol,
        astInterface.getModifierOpt().orElse(CD4AnalysisMill.modifierBuilder().build()));
    setStereotype(interfaceSymbol, astInterface.getStereotypeOpt());

    // Interfaces are always abstract
    interfaceSymbol.setAbstract(true);
    
    addToScopeAndLinkWithNode(interfaceSymbol, astInterface);
  }
  
  default void addInterfacesToType(final CDTypeSymbol typeSymbol,
      final List<ASTReferenceType> astInterfaces, Collection<String> externals) {
    if (astInterfaces != null) {
      for (final ASTReferenceType superInterface : astInterfaces) {
        if (!externals.contains(TypesPrinter.printType(superInterface))) {
          final CDTypeSymbolReference superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
          typeSymbol.addInterface(superInterfaceSymbol);
        }
      }
    }
  }
  
  @Override
  default void endVisit(final ASTCDInterface astInterface) {
    removeCurrentScope();
  }
  
  @Override
  default void visit(final ASTCDEnum astEnum) {
    final CDTypeSymbol enumSymbol = new CDTypeSymbol(astEnum.getName());
    enumSymbol.setEnum(true);
    
    if (astEnum.getCDEnumConstantList() != null) {
      for (final ASTCDEnumConstant astConstant : astEnum.getCDEnumConstantList()) {
        final CDTypeSymbolReference enumReference = new CDTypeSymbolReference(enumSymbol.getName(), enumSymbol.getSpannedScope());

        final CDFieldSymbol constantSymbol = new CDFieldSymbol(astConstant.getName(), enumReference);
        constantSymbol.setEnumConstant(true);
        // enum constants are implicitly public static final (Java Langspec 3rd
        // Edition Chapter 8.9 Enums)
        constantSymbol.setStatic(true);
        constantSymbol.setFinal(true);
        
        enumSymbol.addField(constantSymbol);
      }
    }
    Collection<String> externals = getExternals(astEnum);
    addInterfacesToType(enumSymbol, astEnum.getInterfaceList(), externals);
    setModifiersOfType(enumSymbol, astEnum.getModifierOpt().orElse(CD4AnalysisMill.modifierBuilder().build()));
    
    addToScopeAndLinkWithNode(enumSymbol, astEnum);
  }
  
  @Override
  default void endVisit(final ASTCDEnum astEnum) {
    removeCurrentScope();
  }
  
  @Override
  default void visit(final ASTCDMethod astMethod) {
    final CDMethodSymbol methodSymbol = new CDMethodSymbol(astMethod.getName());
    
    setModifiersOfMethod(methodSymbol, astMethod.getModifier());
    setParametersOfMethod(methodSymbol, astMethod);
    setReturnTypeOfMethod(methodSymbol, astMethod);
    setExceptionsOfMethod(methodSymbol, astMethod);
    setDefiningTypeOfMethod(methodSymbol);
    
    addToScopeAndLinkWithNode(methodSymbol, astMethod);
  }
  
  @Override
  default void endVisit(final ASTCDMethod astMethod) {
    removeCurrentScope();
  }
  
  default void setModifiersOfMethod(final CDMethodSymbol methodSymbol, final ASTModifier astModifier) {
    if (astModifier != null) {
      methodSymbol.setAbstract(astModifier.isAbstract());
      methodSymbol.setStatic(astModifier.isStatic());
      methodSymbol.setFinal(astModifier.isFinal());
      
      if (astModifier.isPrivate()) {
        methodSymbol.setPrivate();
      }
      else if (astModifier.isProtected()) {
        methodSymbol.setProtected();
      }
      else {
        methodSymbol.setPublic();
      }
      
      if (astModifier.isPresentStereotype()) {
        addStereotypes(methodSymbol, astModifier.getStereotype());
      }
    }
  }
  
  default void addStereotypes(final CDMethodSymbol methodSymbol, final ASTCDStereotype astStereotype) {
    if (astStereotype != null) {
      for (final ASTCDStereoValue val : astStereotype.getValueList()) {
        // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
        // geschrieben)
        methodSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }
  
  default void setParametersOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getCDParameterList() != null) {
      CDTypeSymbolReference paramTypeSymbol;
      
      for (ASTCDParameter astParameter : astMethod.getCDParameterList()) {
        final String paramName = astParameter.getName();
        paramTypeSymbol = new CDTypeSymbolReference(
            TypesPrinter.printType(astParameter.getType()), currentScope().get());
        
        addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType());
        
        if (astParameter.isEllipsis()) {
          methodSymbol.setEllipsisParameterMethod(true);
          // ellipsis parameters are (like) arrays
          // TODO: Ist das so?
          // paramTypeSymbol =
          // CDTypeEntryCreator.getInstance().create(paramTypeSymbol, 1);
        }
        
        final CDFieldSymbol parameterSymbol = new CDFieldSymbol(paramName, paramTypeSymbol);
        parameterSymbol.setParameter(true);
        // Parameters are always private
        parameterSymbol.setPrivate();
        
        methodSymbol.addParameter(parameterSymbol);
      }
    }
  }
  
  default void setReturnTypeOfMethod(final CDMethodSymbol methodSymbol, ASTCDMethod astMethod) {
    // TODO PN use ASTTypesConverter
    final CDTypeSymbolReference returnSymbol = new CDTypeSymbolReference(
        TypesPrinter.printReturnType(astMethod.getReturnType()), currentScope().get());
    addTypeArgumentsToTypeSymbol(returnSymbol, astMethod.getReturnType());
    methodSymbol.setReturnType(returnSymbol);
  }
  
  default void setExceptionsOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getExceptionList() != null) {
      for (final ASTQualifiedName exceptionName : astMethod.getExceptionList()) {
        final CDTypeSymbolReference exception = new CDTypeSymbolReference(exceptionName.toString(),
            currentScope().get());
        methodSymbol.addException(exception);
      }
    }
  }
  
  default void setDefiningTypeOfMethod(final CDMethodSymbol methodSymbol) {
    if (currentSymbol().isPresent()) {
      if (currentSymbol().get() instanceof CDTypeSymbol) {
        final CDTypeSymbol definingType = (CDTypeSymbol) currentSymbol().get();
        methodSymbol.setDefiningType(definingType);
        
        if (definingType.isInterface()) {
          methodSymbol.setAbstract(true);
        }
      }
    }
  }
  
  default void handleAssociation(final ASTCDAssociation cdAssoc) {
    CDAssociationSymbol s = handleLeftToRightAssociation(cdAssoc);
    CDAssociationSymbol s2 = handleRightToLeftAssociation(cdAssoc);
    // TODO PN <- RH remove quick fix see #1627 maybe merge symbols?
    setLinkBetweenSymbolAndNode(s != null ? s : s2, cdAssoc);
  }
  
  // TODO PN discuss: We can have TWO symbols for the SAME association ast. So,
  // no link ast->symbol possible?
  // TODO PN <- RH remove returns, its only a quick fix see #1627
  default CDAssociationSymbol handleRightToLeftAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isRightToLeft() || cdAssoc.isBidirectional() || cdAssoc.isUnspecified()) {
      final CDAssociationSymbol assocRight2LeftSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getRightReferenceName(), cdAssoc.getLeftReferenceName());
      // complete association properties
      if (assocRight2LeftSymbol != null) {
        assocRight2LeftSymbol.setDerived(cdAssoc.isDerived());
        if (cdAssoc.isComposition()) {
          assocRight2LeftSymbol.setRelationship(Relationship.PART);
        }
        assocRight2LeftSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc
            .getLeftCardinalityOpt().orElse(null)));
        assocRight2LeftSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc
            .getRightCardinalityOpt().orElse(null)));
        assocRight2LeftSymbol.setTargetRole(cdAssoc.getLeftRoleOpt());
        assocRight2LeftSymbol.setSourceRole(cdAssoc.getRightRoleOpt());

        if (cdAssoc.isPresentLeftModifier()) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().getStereotypeOpt()
              .orElse(null));
        }
        
        if (cdAssoc.isPresentRightQualifier()) {
          handleCDQualifier(assocRight2LeftSymbol, cdAssoc.getRightQualifier());
        }
        
        assocRight2LeftSymbol
            .setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isUnspecified());

        cdAssoc.setRightToLeftSymbol(assocRight2LeftSymbol);
      }
      return assocRight2LeftSymbol;
    }
    return null;
  }
  
  // TODO PN <- RH remove returns, its only a quick fix see #1627
  default CDAssociationSymbol handleLeftToRightAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isLeftToRight() || cdAssoc.isBidirectional() || cdAssoc.isUnspecified()) {
      final CDAssociationSymbol assocLeft2RightSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getLeftReferenceName(), cdAssoc.getRightReferenceName());
      
      if (assocLeft2RightSymbol != null) {
        assocLeft2RightSymbol.setDerived(cdAssoc.isDerived());
        if (cdAssoc.isComposition()) {
          assocLeft2RightSymbol.setRelationship(Relationship.COMPOSITE);
        }
        assocLeft2RightSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc
            .getRightCardinalityOpt().orElse(null)));
        assocLeft2RightSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc
            .getLeftCardinalityOpt().orElse(null)));
        assocLeft2RightSymbol.setSourceRole(cdAssoc.getLeftRoleOpt());
        assocLeft2RightSymbol.setTargetRole(cdAssoc.getRightRoleOpt());

        if (cdAssoc.isPresentRightModifier()) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().getStereotypeOpt()
              .orElse(null));
        }
        
        if (cdAssoc.isPresentLeftQualifier()) {
          handleCDQualifier(assocLeft2RightSymbol, cdAssoc.getLeftQualifier());
        }
        
        assocLeft2RightSymbol
            .setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isUnspecified());

        cdAssoc.setLeftToRightSymbol(assocLeft2RightSymbol);
      }
      return assocLeft2RightSymbol;
    }
    return null;
  }
  
  /**
   * Creates {@link CDQualifierSymbol} for the given qualifier
   * 
   * @param assocSymbol the association symbol who's qualifier will be set
   * @param qualifier the ast qualifier to create the symbol for
   */
  default void handleCDQualifier(final CDAssociationSymbol assocSymbol,
      final ASTCDQualifier qualifier) {
    if (qualifier.isPresentName()) {
      CDQualifierSymbol s = new CDQualifierSymbol(qualifier.getName());
      setLinkBetweenSymbolAndNode(s, qualifier);
      assocSymbol.setQualifier(Optional.of(s));
    }
    else if (qualifier.isPresentType()) {
      CDQualifierSymbol s = new CDQualifierSymbol(qualifier.getType());
      setLinkBetweenSymbolAndNode(s, qualifier);
      assocSymbol.setQualifier(Optional.of(s));
    }
  }
  
  default CDAssociationSymbol createAssociationSymbol(final ASTCDAssociation astAssoc,
      final ASTQualifiedName astSourceName, final ASTQualifiedName astTargetName) {
    final CDTypeSymbolReference sourceType = new CDTypeSymbolReference(Names.getQualifiedName(
        astSourceName.getPartList()), currentScope().get());
    
    final CDTypeSymbolReference targetType = new CDTypeSymbolReference(Names.getQualifiedName(
        astTargetName.getPartList()), currentScope().get());
    
    final CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);
    
    if (sourceType.existsReferencedSymbol()) {
      // TODO PN use association reference instead?
      // TODO PN should we really invoke methods of the symbol definition during the symbol table creation?
      sourceType.addAssociation(associationSymbol);
    } // the else case should be checked by a context conditions

    if (targetType.existsReferencedSymbol()) {
      targetType.addSpecAssociation(associationSymbol);
    }

    
    associationSymbol.setAssocName(astAssoc.getNameOpt());
    
    addStereotypes(associationSymbol, astAssoc.getStereotypeOpt().orElse(null));
    
    if ((astSourceName.getPartList().size() > 1 && !sourceType.getName().equals(
        Names.getQualifiedName(astSourceName.getPartList())))) {
      Log.error("0xU0270 Association referenced type " + astSourceName + " wasn't declared in the "
          + "class diagram " + getFullClassDiagramName() + ". Pos: "
          + astAssoc.get_SourcePositionStart());
      return null;
    }

    addToScope(associationSymbol);
    associationSymbol.setAstNode(astAssoc);
    
    return associationSymbol;
  }
  
  default void addStereotypes(final CDAssociationSymbol associationSymbol,
      final ASTCDStereotype astStereotype) {
    if (astStereotype != null) {
      // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
      // geschrieben)
      for (final ASTCDStereoValue val : astStereotype.getValueList()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }
  
  void setPackageName(String name);
  
  String getPackageName();
  
  void setFullClassDiagramName(String name);
  
  String getFullClassDiagramName();

  MutableScope getEnclosingScope();
  
}
