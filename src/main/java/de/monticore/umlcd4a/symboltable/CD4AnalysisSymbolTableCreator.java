package de.monticore.umlcd4a.symboltable;/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTImportStatement;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a._ast.*;
import de.monticore.umlcd4a._visitor.CD4AnalysisVisitor;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public interface CD4AnalysisSymbolTableCreator extends CD4AnalysisVisitor, SymbolTableCreator {

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and returns the first scope
   * that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  default Scope createFromAST(ASTCD4AnalysisBase rootNode) {
    requireNonNull(rootNode);
    rootNode.accept(this);
    return getFirstCreatedScope();
  }

  @Override
  default void visit(final ASTCDCompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    setPackageName(Names.getQualifiedName(compilationUnit.getPackage()));

    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getImportStatements() != null) {
      for (ASTImportStatement imp : compilationUnit.getImportStatements()) {
        imports.add(new ImportStatement(Names.getQualifiedName(imp.getImportList()), imp.isStar()));
      }
    }

    final ArtifactScope scope = new ArtifactScope(Optional.empty(), getPackageName(), imports);
    putOnStackAndSetEnclosingIfExists(scope);
  }

  @Override
  default void endVisit(final ASTCDCompilationUnit compilationUnit) {
    removeCurrentScope();

    Log.debug("Finished build of symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    // TODO PN test this
    setEnclosingScopeOfNodes(compilationUnit);
  }

  @Override
  default void visit(final ASTCDDefinition astDefinition) {
    final String cdName = astDefinition.getName();

    setFullClassDiagramName(getPackageName().isEmpty() ? cdName : (getPackageName() + "." + cdName));

    final CDSymbol cdSymbol = new CDSymbol(cdName);
    defineInScopeAndLinkWithAst(cdSymbol, astDefinition);
  }

  @Override
  default void endVisit(final ASTCDDefinition astDefinition) {
    astDefinition.getCDAssociations().forEach(this::handleAssociation);
    removeCurrentScope();
  }

  @Override
  default void visit(final ASTCDClass astClass) {
    final CDTypeSymbol classSymbol = new CDTypeSymbol(astClass.getName());

    if (astClass.getSuperclass().isPresent()) {
      final CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(astClass
          .getSuperclass().get());
      classSymbol.setSuperClass(superClassSymbol);
    }

    final ASTModifier astModifier = astClass.getModifier().orElse(new ASTModifier.Builder().build());

    setModifiersOfType(classSymbol, astModifier);

    addInterfacesToType(classSymbol, astClass.getInterfaces());

    defineInScopeAndLinkWithAst(classSymbol, astClass);
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
      } else {
        // public is default
        typeSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        for (final ASTStereoValue stereoValue : astModifier.getStereotype().get().getValues()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
          final Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          typeSymbol.addStereotype(stereotype);
        }
      }
    }
  }

  default CDTypeSymbolReference createCDTypeSymbolFromReference(final ASTReferenceType astReferenceType) {
    // TODO PN replace by type converter
    CDTypeSymbolReference superSymbol = null;
    if (astReferenceType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSuperClass = (ASTSimpleReferenceType) astReferenceType;
      superSymbol = new CDTypeSymbolReference(Names.getQualifiedName(astSuperClass.getNames()),
          currentScope().get());
    }

    return superSymbol;
  }

  @Override
  default void visit(final ASTCDAttribute astAttribute) {
    final String typeName = TypesPrinter.printTypeWithoutTypeArguments(astAttribute.getType());

    // TODO PN type arguments are not set yet. For every argument a CDTypeSymbolReference must be created.

    final CDTypeSymbolReference typeReference = new CDTypeSymbolReference(typeName, currentScope().get());
    typeReference.setStringRepresentation(TypesPrinter.printType(astAttribute.getType()));


    final CDFieldSymbol fieldSymbol = new CDFieldSymbol(astAttribute.getName(), typeReference);

    if (astAttribute.getModifier().isPresent()) {
      final ASTModifier astModifier = astAttribute.getModifier().get();

      fieldSymbol.setDerived(astModifier.isDerived());
      fieldSymbol.setStatic(astModifier.isStatic());
      fieldSymbol.setFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        fieldSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        fieldSymbol.setPrivate();
      } else {
        // public is default
        fieldSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        for (final ASTStereoValue stereoValue : astModifier.getStereotype().get().getValues()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
          final Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          fieldSymbol.addStereotype(stereotype);
        }
      }
    }

    defineInScopeAndSetLinkBetweenSymbolAndAst(fieldSymbol, astAttribute);
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

    addInterfacesToType(interfaceSymbol, astInterface.getInterfaces());
    setModifiersOfType(interfaceSymbol, astInterface.getModifier().orElse(new ASTModifier.Builder().build()));

    // Interfaces are always abstract
    interfaceSymbol.setAbstract(true);


    defineInScopeAndLinkWithAst(interfaceSymbol, astInterface);
  }

  default void addInterfacesToType(final CDTypeSymbol typeSymbol, final ASTReferenceTypeList astInterfaces) {
    if (astInterfaces != null) {
      for (final ASTReferenceType superInterface : astInterfaces) {
        final CDTypeSymbolReference superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
        typeSymbol.addInterface(superInterfaceSymbol);
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

    if (astEnum.getCDEnumConstants() != null) {
      for (final ASTCDEnumConstant astConstant : astEnum.getCDEnumConstants()) {
        final CDFieldSymbol constantSymbol = new CDFieldSymbol(astConstant.getName(), enumSymbol);
        constantSymbol.setEnumConstant(true);
        // enum constants are implicitly public static final (Java Langspec 3rd Edition Chapter 8.9 Enums)
        constantSymbol.setStatic(true);
        constantSymbol.setFinal(true);

        enumSymbol.addField(constantSymbol);
      }
    }

    addInterfacesToType(enumSymbol, astEnum.getInterfaces());
    setModifiersOfType(enumSymbol, astEnum.getModifier().orElse(new ASTModifier.Builder().build()));

    defineInScopeAndLinkWithAst(enumSymbol, astEnum);
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

    defineInScopeAndSetLinkBetweenSymbolAndAst(methodSymbol, astMethod);

    putScopeOnStackAndSetEnclosingIfExists(methodSymbol);
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
      else if(astModifier.isProtected()) {
        methodSymbol.setProtected();
      }
      else {
        methodSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        addStereotypes(methodSymbol, astModifier.getStereotype().get());
      }
    }
  }

  default void addStereotypes(final CDMethodSymbol methodSymbol, final ASTStereotype astStereotype) {
    if (astStereotype != null) {
      for (final ASTStereoValue val : astStereotype.getValues()) {
        // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
        methodSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  default void setParametersOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getCDParameters() != null) {
      CDTypeSymbolReference paramTypeSymbol;

      for (ASTCDParameter astParameter : astMethod.getCDParameters()) {
        final String paramName = astParameter.getName();
        paramTypeSymbol = new CDTypeSymbolReference(
            TypesPrinter.printType(astParameter.getType()), currentScope().get());

        if (astParameter.isEllipsis()) {
          methodSymbol.setEllipsisParameterMethod(true);
          // ellipsis parameters are (like) arrays
          // TODO: Ist das so?
          // paramTypeSymbol = CDTypeEntryCreator.getInstance().create(paramTypeSymbol, 1);
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
    methodSymbol.setReturnType(returnSymbol);
  }

  default void setExceptionsOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getExceptions() != null) {
      for (final ASTQualifiedName exceptionName : astMethod.getExceptions()) {
        final CDTypeSymbol exception = new CDTypeSymbolReference(exceptionName.toString(),
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
    setLinkBetweenSymbolAndNode(s!= null? s : s2, cdAssoc);
  }

  // TODO PN discuss: We can have TWO symbols for the SAME association ast. So, no link ast->symbol possible?
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
        assocRight2LeftSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orElse(null)));
        assocRight2LeftSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orElse(null)));
        assocRight2LeftSymbol.setRole(cdAssoc.getLeftRole());

        if (cdAssoc.getLeftModifier().isPresent()) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().get().getStereotype().orElse(null));
        }

        if (cdAssoc.getRightQualifier().isPresent()) {
          final ASTCDQualifier qualifier = cdAssoc.getRightQualifier().get();
          if ((qualifier.getName().isPresent()) && (!qualifier.getName().get().isEmpty())) {
            assocRight2LeftSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType().isPresent()) {
            assocRight2LeftSymbol.setQualifier(Optional.of(TypesPrinter.printType(qualifier.getType().get())));
          }
        }
        assocRight2LeftSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isUnspecified());
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
        assocLeft2RightSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orElse(null)));
        assocLeft2RightSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orElse(null)));
        assocLeft2RightSymbol.setRole(cdAssoc.getRightRole());

        if (cdAssoc.getRightModifier().isPresent()) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().get().getStereotype().orElse(null));
        }

        if (cdAssoc.getLeftQualifier().isPresent()) {
          final ASTCDQualifier qualifier = cdAssoc.getLeftQualifier().get();
          if ((qualifier.getName().isPresent()) && (!qualifier.getName().get().isEmpty())) {
            assocLeft2RightSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType().isPresent()) {
            assocLeft2RightSymbol.setQualifier(Optional.of(TypesPrinter.printType(qualifier.getType().get())));
          }
        }
        assocLeft2RightSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isUnspecified());
      }
      return assocLeft2RightSymbol;
    }    
    return null;
  }

  default CDAssociationSymbol createAssociationSymbol(final ASTCDAssociation astAssoc,
      final ASTQualifiedName astSourceName, final ASTQualifiedName astTargetName) {
    final CDTypeSymbolReference sourceType = new CDTypeSymbolReference(Names.getQualifiedName(
        astSourceName.getParts()), currentScope().get());

    final CDTypeSymbolReference targetType = new CDTypeSymbolReference(Names.getQualifiedName(
        astTargetName.getParts()), currentScope().get());

    final CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);

    if (sourceType.existsReferencedSymbol()) {
      // TODO PN use association reference instead?
      sourceType.addAssociation(associationSymbol);
    }
    // the else case should be checked by a context conditions


    associationSymbol.setAssocName(astAssoc.getName());

    addStereotypes(associationSymbol, astAssoc.getStereotype().orElse(null));

    if ((astSourceName.getParts().size() > 1 && !sourceType.getName().equals(Names.getQualifiedName(astSourceName.getParts())))) {
      Log.error("0xU0270 Association referenced type " + astSourceName + " wasn't declared in the "
          + "class diagram " + getFullClassDiagramName() + ". Pos: " + astAssoc.get_SourcePositionStart());
      return null;
    }

    defineInScope(associationSymbol);
    associationSymbol.setAstNode(astAssoc);

    return associationSymbol;
  }

  default void addStereotypes(final CDAssociationSymbol associationSymbol,
      final ASTStereotype astStereotype) {
    if (astStereotype != null) {
      // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
      for (final ASTStereoValue val : astStereotype.getValues()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  void setPackageName(String name);
  String getPackageName();

  void setFullClassDiagramName(String name);
  String getFullClassDiagramName();

}
