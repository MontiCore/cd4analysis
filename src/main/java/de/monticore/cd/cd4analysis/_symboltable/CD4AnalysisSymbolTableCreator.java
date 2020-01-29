/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.symboltable.modifiers.BasicAccessModifier.*;

public class CD4AnalysisSymbolTableCreator extends CD4AnalysisSymbolTableCreatorTOP {

  public CD4AnalysisSymbolTableCreator(Deque<? extends ICD4AnalysisScope> scopeStack) {
    super(scopeStack);
  }

  public CD4AnalysisSymbolTableCreator(final ICD4AnalysisScope scopeStack) {
    super(scopeStack);
  }

  @Override
  public CD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    CD4AnalysisArtifactScope artifactScope = new CD4AnalysisArtifactScope(Optional.empty(),
        Names.getQualifiedName(rootNode.getPackageList()), new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void visit(final ASTCDCompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    // we must create CDSymbol here to add imports to the symbol
    final String cdName = compilationUnit.getCDDefinition().getName();
    final CDDefinitionSymbol cdSymbol = new CDDefinitionSymbol(cdName);

    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getMCImportStatementList() != null) {
      for (ASTMCImportStatement imp : compilationUnit.getMCImportStatementList()) {
        String qualifiedImport = imp.getQName();
        cdSymbol.addImport(qualifiedImport);
        imports.add(new ImportStatement(qualifiedImport, imp.isStar()));
      }
    }

    getCurrentScope().get().setAstNode(compilationUnit);

    // note that the cdsymbol scope is removed in the endVisit of
    // ASTCDDefinition
    addToScopeAndLinkWithNode(cdSymbol, compilationUnit.getCDDefinition());
  }

  @Override
  public void endVisit(final ASTCDCompilationUnit compilationUnit) {
    removeCurrentScope();

    Log.debug("Finished build of symboltable for CD: "
            + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

  }

  @Override
  public void visit(final ASTCDDefinition astDefinition) {
    // Do nothing: see visit(ASTCDCompilationUnit)
  }

  @Override
  public void endVisit(final ASTCDDefinition astDefinition) {
    astDefinition.getSymbol().getAssociations().forEach(this::addAssocToTarget);
    removeCurrentScope();
  }

  // TODO think about external Stereotypes...
  Collection<String> getExternals(ASTCDType type) {
    Collection<String> externals = new HashSet<>();
    final ASTModifier astModifier = type.isPresentModifier() ? type.getModifier() :
        CD4AnalysisMill.modifierBuilder().build();

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
  public void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    symbol.setIsClass(true);

    Collection<String> externals = getExternals(ast);
    if (ast.isPresentSuperclass()) {
      ASTMCObjectType superC = ast.getSuperclass();
      if (!externals.contains((new AstPrinter()).printType(superC))) {
        final CDTypeSymbolLoader superClassSymbol = createCDTypeSymbolFromReference(superC);
        symbol.setSuperClass(superClassSymbol);
      }
    }

    final ASTModifier astModifier = ast.isPresentModifier() ? ast.getModifier() :
        CD4AnalysisMill.modifierBuilder().build();

    setModifiersOfType(symbol, astModifier);
    if (ast.isPresentStereotype()) {
      setStereotype(symbol, ast.getStereotype());
    }

    addInterfacesToType(symbol, ast.getInterfaceList(), externals);
  }

  public void setStereotype(CDTypeSymbol typeSymbol, ASTCDStereotype stereotype) {
    for (final ASTCDStereoValue stereoValue : stereotype.getValueList()) {
      final Stereotype s = new Stereotype(stereoValue.getName(),
          stereoValue.isPresentValue() ? stereoValue.getValue() : "");
      typeSymbol.addStereotype(s);
    }

  }

  public void setModifiersOfType(final CDTypeSymbol typeSymbol, final ASTModifier astModifier) {
    if (astModifier != null) {
      typeSymbol.setIsAbstract(astModifier.isAbstract());
      typeSymbol.setIsFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        typeSymbol.setAccessModifier(PROTECTED);
        typeSymbol.setIsProtected(true);
      }
      else if (astModifier.isPrivate()) {
        typeSymbol.setAccessModifier(PRIVATE);
        typeSymbol.setIsPrivate(true);
      }
      else {
        // public is default
        typeSymbol.setIsPublic(true);
        typeSymbol.setAccessModifier(PUBLIC);
      }

      if (astModifier.isPresentStereotype()) {
        for (final ASTCDStereoValue stereoValue : astModifier.getStereotype().getValueList()) {
          final Stereotype stereotype = new Stereotype(stereoValue.getName(),
              stereoValue.isPresentValue() ? stereoValue.getValue() : "");
          typeSymbol.addStereotype(stereotype);
        }
      }
    }
  }

  CDTypeSymbolLoader createCDTypeSymbolFromReference(final ASTMCObjectType astmcObjectType) {
    return new CDTypeSymbolLoader(
        astmcObjectType.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())),
        getCurrentScope().get());
  }

  @Override
  public void initialize_CDAttribute(CDFieldSymbol fieldSymbol, ASTCDAttribute astAttribute) {
    ASTMCType astType = astAttribute.getMCType();
    final String typeName;
    if (astType instanceof ASTMCGenericType) {
      typeName = ((ASTMCGenericType) astType).printWithoutTypeArguments();
    }
    else if (astType instanceof ASTMCArrayType) {
      typeName = ((ASTMCArrayType) astType).printTypeWithoutBrackets();
    }
    else {
      typeName = astAttribute.getMCType()
          .printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()));
    }

    // TODO PN type arguments are not set yet. For every argument a
    // CDTypeSymbolReference must be created.

    final CDTypeSymbolLoader typeReference = new CDTypeSymbolLoader(typeName, getCurrentScope()
        .get());
    fieldSymbol.setType(typeReference);
    addTypeArgumentsToTypeSymbol(typeReference, astType);

    if (astAttribute.isPresentModifier()) {
      final ASTModifier astModifier = astAttribute.getModifier();

      fieldSymbol.setIsDerived(astModifier.isDerived());
      fieldSymbol.setIsStatic(astModifier.isStatic());
      fieldSymbol.setIsFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        fieldSymbol.setIsProtected(true);
      }
      else if (astModifier.isPrivate()) {
        fieldSymbol.setIsPrivate(true);
      }
      else {
        // public is default
        fieldSymbol.setIsPublic(true);
      }

      if (astModifier.isPresentStereotype()) {
        for (final ASTCDStereoValue stereoValue : astModifier.getStereotype().getValueList()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
          // geschrieben)
          final Stereotype stereotype = new Stereotype(stereoValue.getName(),
              stereoValue.getName());
          fieldSymbol.addStereotype(stereotype);
        }
      }
    }
  }

  public void addTypeArgumentsToTypeSymbol(CDTypeSymbolLoader typeReference, ASTMCType astType) {
    if (astType instanceof ASTMCGenericType) {
      ASTMCGenericType astmcGenericType = (ASTMCGenericType) astType;
      if (astmcGenericType.getMCTypeArgumentList().isEmpty()) {
        return;
      }
      List<CDTypeSymbolLoader> actualTypeArguments = new ArrayList<>();
      for (ASTMCTypeArgument astTypeArgument : astmcGenericType.getMCTypeArgumentList()) {
        if (astTypeArgument instanceof ASTMCBasicTypeArgument) {
          // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
          ASTMCBasicTypeArgument astmcBasicTypeArgument = (ASTMCBasicTypeArgument) astTypeArgument;
          if (astmcBasicTypeArgument.getMCQualifiedType() != null) {
            ASTMCQualifiedType astTypeNoBound = astmcBasicTypeArgument.getMCQualifiedType();
            CDTypeSymbolLoader typeArgumentSymbolReference = new CDTypeSymbolLoader(
                astTypeNoBound.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())),
                getCurrentScope().get());
            // TODO PN, GV: add dimension?
            // TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoound)
            addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);
            actualTypeArguments.add(typeArgumentSymbolReference);
          }
          else {
            Log.error("0xU0401 Unknown type argument " + astTypeArgument + " of type "
                + typeReference);
          }
        }
        else if (astTypeArgument instanceof ASTMCPrimitiveTypeArgument) {
          ASTMCPrimitiveTypeArgument astmcPrimitiveTypeArgument = (ASTMCPrimitiveTypeArgument) astTypeArgument;
          if (astmcPrimitiveTypeArgument.getMCPrimitiveType() != null) {
            ASTMCType astTypeNoBound = astmcPrimitiveTypeArgument.getMCPrimitiveType();
            CDTypeSymbolLoader typeArgumentSymbolReference = new CDTypeSymbolLoader(
                astTypeNoBound.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())),
                getCurrentScope().get());
            addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);
            actualTypeArguments.add(typeArgumentSymbolReference);
          }

        }
        else {
          Log.error("0xU0401 Unknown type argument " + astTypeArgument + " of type "
              + typeReference);
        }
        typeReference.setActualTypeArguments(actualTypeArguments);
      }
    }
  }

  @Override
  public void initialize_CDInterface(CDTypeSymbol interfaceSymbol, ASTCDInterface astInterface) {
    interfaceSymbol.setIsInterface(true);
    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);

    Collection<String> externals = getExternals(astInterface);
    addInterfacesToType(interfaceSymbol, astInterface.getInterfaceList(), externals);
    setModifiersOfType(interfaceSymbol,
        astInterface.isPresentModifier() ?
            astInterface.getModifier() :
            CD4AnalysisMill.modifierBuilder().build());
    if (astInterface.isPresentStereotype()) {
      setStereotype(interfaceSymbol, astInterface.getStereotype());
    }

    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);
  }

  public void addInterfacesToType(final CDTypeSymbol typeSymbol,
      final List<ASTMCObjectType> astInterfaces, Collection<String> externals) {
    if (astInterfaces != null) {
      for (final ASTMCObjectType superInterface : astInterfaces) {
        if (!externals.contains((new AstPrinter()).printType(superInterface))) {
          final CDTypeSymbolLoader superInterfaceSymbol = createCDTypeSymbolFromReference(
              superInterface);
          typeSymbol.getCdInterfaceList().add(superInterfaceSymbol);
        }
      }
    }
  }

  @Override
  public void initialize_CDEnum(CDTypeSymbol enumSymbol, ASTCDEnum astEnum) {
    enumSymbol.setIsEnum(true);

    if (astEnum.getCDEnumConstantList() != null) {
      for (final ASTCDEnumConstant astConstant : astEnum.getCDEnumConstantList()) {
        final CDTypeSymbolLoader enumReference = new CDTypeSymbolLoader(enumSymbol.getName(),
            enumSymbol.getSpannedScope());

        final CDFieldSymbol constantSymbol = new CDFieldSymbol(astConstant.getName(),
            enumReference);
        constantSymbol.setEnumConstant(true);
        // enum constants are implicitly public static final (Java Langspec 3rd
        // Edition Chapter 8.9 Enums)
        constantSymbol.setIsStatic(true);
        constantSymbol.setIsFinal(true);

        // TODO eigene visit-Methode
        // enumSymbol.addField(constantSymbol);
      }
    }
    Collection<String> externals = getExternals(astEnum);
    addInterfacesToType(enumSymbol, astEnum.getInterfaceList(), externals);
    setModifiersOfType(enumSymbol, astEnum.isPresentModifier() ?
        astEnum.getModifier() :
        CD4AnalysisMill.modifierBuilder().build());
  }

  @Override
  public void endVisit(final ASTCDEnum astEnum) {
    final CDTypeSymbol enumSymbol = astEnum.getSymbol();

    for (CDFieldSymbol constSymbol : enumSymbol.getEnumConstants()) {
      final CDTypeSymbolLoader enumReference = new CDTypeSymbolLoader(enumSymbol.getName(),
          enumSymbol.getSpannedScope());
      constSymbol.setType(enumReference);
    }
    removeCurrentScope();
  }

  @Override
  public void initialize_CDMethod(CDMethOrConstrSymbol methodSymbol, ASTCDMethod astMethod) {
    setModifiersOfMethod(methodSymbol, astMethod.getModifier());
    setReturnTypeOfMethod(methodSymbol, astMethod);
    setExceptionsOfMethod(methodSymbol, astMethod);
    setDefiningTypeOfMethod(methodSymbol);
    if (!astMethod.getCDParameterList().isEmpty()) {
      if (astMethod.getCDParameter(astMethod.getCDParameterList().size() - 1).isEllipsis()) {
        methodSymbol.setIsEllipsis(true);
      }
    }
  }

  public void setModifiersOfMethod(final CDMethOrConstrSymbol methodSymbol,
      final ASTModifier astModifier) {
    if (astModifier != null) {
      methodSymbol.setIsAbstract(astModifier.isAbstract());
      methodSymbol.setIsStatic(astModifier.isStatic());
      methodSymbol.setIsFinal(astModifier.isFinal());

      if (astModifier.isPrivate()) {
        methodSymbol.setIsPrivate(true);
      }
      else if (astModifier.isProtected()) {
        methodSymbol.setIsProtected(true);
      }
      else {
        methodSymbol.setIsPublic(true);
      }

      if (astModifier.isPresentStereotype()) {
        addStereotypes(methodSymbol, astModifier.getStereotype());
      }
    }
  }

  public void addStereotypes(final CDMethOrConstrSymbol methodSymbol,
      final ASTCDStereotype astStereotype) {
    if (astStereotype != null) {
      for (final ASTCDStereoValue val : astStereotype.getValueList()) {
        // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
        // geschrieben)
        methodSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  @Override
  protected void initialize_CDParameter(
      de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol symbol,
      de.monticore.cd.cd4analysis._ast.ASTCDParameter ast) {
    CDTypeSymbolLoader paramTypeSymbol = new CDTypeSymbolLoader(
        (new AstPrinter()).printType(ast.getMCType()), getCurrentScope().get());

    addTypeArgumentsToTypeSymbol(paramTypeSymbol, ast.getMCType());

    symbol.setType(paramTypeSymbol);
    symbol.setIsParameter(true);
    // Parameters are always private
    symbol.setIsPrivate(true);
  }

  @Override
  protected void initialize_CDEnumConstant(
      de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol symbol,
      de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant ast) {
    symbol.setEnumConstant(true);
    // enum constants are implicitly public static final (Java Langspec 3rd
    // Edition Chapter 8.9 Enums)
    symbol.setIsStatic(true);
    symbol.setIsFinal(true);
  }

  public void setReturnTypeOfMethod(final CDMethOrConstrSymbol methodSymbol,
      ASTCDMethod astMethod) {
    // TODO PN use ASTTypesConverter
    final CDTypeSymbolLoader returnSymbol = new CDTypeSymbolLoader(
        (new AstPrinter()).printType(astMethod.getMCReturnType()),
        getCurrentScope().get());//TODO CollectionTypesPrinter
    if (astMethod.getMCReturnType().isPresentMCType()) {
      addTypeArgumentsToTypeSymbol(returnSymbol, astMethod.getMCReturnType().getMCType());
    }
    methodSymbol.setReturnType(returnSymbol);
  }

  public void setExceptionsOfMethod(final CDMethOrConstrSymbol methodSymbol,
      final ASTCDMethod astMethod) {
    if (astMethod.getExceptionList() != null) {
      for (final ASTMCQualifiedName exceptionName : astMethod.getExceptionList()) {
        final CDTypeSymbolLoader exception = new CDTypeSymbolLoader(exceptionName.toString(),
            getCurrentScope().get());
        methodSymbol.getExceptionList().add(exception);
      }
    }
  }

  public void setDefiningTypeOfMethod(final CDMethOrConstrSymbol methodSymbol) {
    if (currentSymbol().isPresent()) {
      if (currentSymbol().get() instanceof CDTypeSymbol) {
        final CDTypeSymbol definingType = (CDTypeSymbol) currentSymbol().get();
        methodSymbol.setDefiningType(definingType);

        if (definingType.isIsInterface()) {
          methodSymbol.setIsAbstract(true);
        }
      }
    }
  }

  @Override public void endVisit(ASTCDAssociation node) {
    Optional<String> assocName = node.isPresentName() ?
        Optional.of(node.getName()) :
        Optional.empty();
    if (!getCurrentScope().isPresent()) {
      Log.error("0xTODO Current scope must not be null!");
    }
    CDTypeSymbol rightRef = getCurrentScope().get()
        .resolveCDType(node.getRightReferenceName().getQName()).orElse(null);
    if (null == rightRef) {
      Log.error("0xTODO CDType referenced from the right side of association '" + node
          + "' could not be found!");
    }

    CDTypeSymbol leftRef = getCurrentScope().get()
        .resolveCDType(node.getLeftReferenceName().getQName()).orElse(null);
    if (null == leftRef) {
      Log.error("0xTODO CDType referenced from the left side of association '" + node
          + "' could not be found!");
    }

    String leftRoleName = getRoleName(node.getLeftReferenceName(), assocName,
        getRoleNameOpt(node, false));
    boolean isLeftNavigable =
        node.isLeftToRight() || node.isBidirectional() || node.isUnspecified();
    CDTypeSymbolLoader rightReference = new CDTypeSymbolLoader(
        node.getRightReferenceName().getQName(),
        rightRef.getEnclosingScope());
    RoleSymbol leftRoleSymbol = CD4AnalysisSymTabMill.roleSymbolBuilder().setName(leftRoleName)
        .setNavigable(isLeftNavigable)
        .setAssociationTarget(rightReference).build();
    leftRef.getSpannedScope().add(leftRoleSymbol);

    String rightRoleName = getRoleName(node.getRightReferenceName(), assocName,
        getRoleNameOpt(node, true));
    boolean isRightNavigable =
        node.isRightToLeft() || node.isBidirectional() || node.isUnspecified();
    CDTypeSymbolLoader leftReference = new CDTypeSymbolLoader(
        node.getLeftReferenceName().getQName(),
        leftRef.getEnclosingScope());
    RoleSymbol rightRoleSymbol = CD4AnalysisSymTabMill.roleSymbolBuilder().setName(rightRoleName)
        .setNavigable(isRightNavigable)
        .setAssociationTarget(leftReference).build();
    rightRef.getSpannedScope().add(rightRoleSymbol);
  }

  /**
   * TODO
   *
   * @param node
   * @param isLeftRole
   * @return
   */
  protected Optional<String> getRoleNameOpt(ASTCDAssociation node, boolean isLeftRole) {
    if (isLeftRole && node.isPresentLeftRole()) {
      return Optional.of(node.getLeftRole().getName());
    }
    if (!isLeftRole && node.isPresentRightRole()) {
      return Optional.of(node.getRightRole().getName());
    }
    return Optional.empty();
  }

  /**
   * TODO
   *
   * @param targetName
   * @param associationName
   * @param roleName
   * @return
   */
  protected String getRoleName(ASTMCQualifiedName targetName, Optional<String> associationName,
      Optional<String> roleName) {
    if (roleName.isPresent()) {
      return roleName.get();
    }
    if (associationName.isPresent()) {
      return associationName.get();
    }
    else {
      String qualifier = Names.getQualifier(targetName.getQName());
      String name = StringTransformations.uncapitalize(Names.getSimpleName(targetName.getQName()));
      return qualifier + "." + name;
    }
  }

  @Override
  public void visit(ASTCDAssociation cdAssoc) {
    CDAssociationSymbol s = handleLeftToRightAssociation(cdAssoc);
    CDAssociationSymbol s2 = handleRightToLeftAssociation(cdAssoc);
    // TODO PN <- RH remove quick fix see #1627 maybe merge symbols?
    ICD4AnalysisScope scope = createScope(false);
    putOnStack(scope);
    cdAssoc.setSpannedScope(scope);
    if (s != null) {
      s.setSpannedScope(scope);
      setLinkBetweenSymbolAndNode(s, cdAssoc);
    }
    else {
      s2.setSpannedScope(scope);
      setLinkBetweenSymbolAndNode(s2, cdAssoc);
    }
  }

  // TODO PN discuss: We can have TWO symbols for the SAME association ast. So,
  // no link ast->symbol possible?
  // TODO PN <- RH remove returns, its only a quick fix see #1627
  CDAssociationSymbol handleRightToLeftAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isRightToLeft() || cdAssoc.isBidirectional() || cdAssoc.isUnspecified()) {
      final CDAssociationSymbol assocRight2LeftSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getRightReferenceName(), cdAssoc.getLeftReferenceName());
      // complete association properties
      if (assocRight2LeftSymbol != null) {
        assocRight2LeftSymbol.setDerived(cdAssoc.isDerived());
        if (cdAssoc.isComposition()) {
          assocRight2LeftSymbol.setRelationship(Relationship.PART);
        }
        assocRight2LeftSymbol.setTargetCardinality(
            Cardinality.convertCardinality(cdAssoc.isPresentLeftCardinality() ? cdAssoc
                .getLeftCardinality() : null));
        assocRight2LeftSymbol.setSourceCardinality(
            Cardinality.convertCardinality(cdAssoc.isPresentRightCardinality() ? cdAssoc
                .getRightCardinality() : null));
        if (cdAssoc.isPresentLeftRole()) {
          assocRight2LeftSymbol.setTargetRole(cdAssoc.getLeftRole().getName());
        }
        if (cdAssoc.isPresentRightRole()) {
          assocRight2LeftSymbol.setSourceRole(cdAssoc.getRightRole().getName());
        }

        if (cdAssoc.isPresentLeftModifier()) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().isPresentStereotype() ?
              cdAssoc.getLeftModifier().getStereotype() : null);
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
  CDAssociationSymbol handleLeftToRightAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isLeftToRight() || cdAssoc.isBidirectional() || cdAssoc.isUnspecified()) {
      final CDAssociationSymbol assocLeft2RightSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getLeftReferenceName(), cdAssoc.getRightReferenceName());

      if (assocLeft2RightSymbol != null) {
        assocLeft2RightSymbol.setDerived(cdAssoc.isDerived());
        if (cdAssoc.isComposition()) {
          assocLeft2RightSymbol.setRelationship(Relationship.COMPOSITE);
        }
        assocLeft2RightSymbol.setTargetCardinality(
            Cardinality.convertCardinality(cdAssoc.isPresentRightCardinality() ? cdAssoc
                .getRightCardinality() : null));
        assocLeft2RightSymbol.setSourceCardinality(
            Cardinality.convertCardinality(cdAssoc.isPresentLeftCardinality() ? cdAssoc
                .getLeftCardinality() : null));
        if (cdAssoc.isPresentLeftRole()) {
          assocLeft2RightSymbol.setSourceRole(cdAssoc.getLeftRole().getName());
        }
        if (cdAssoc.isPresentRightRole()) {
          assocLeft2RightSymbol.setTargetRole(cdAssoc.getRightRole().getName());
        }

        if (cdAssoc.isPresentRightModifier()) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().isPresentStereotype() ?
              cdAssoc.getRightModifier().getStereotype() : null);
        }

        assocLeft2RightSymbol
            .setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isUnspecified());

        cdAssoc.setLeftToRightSymbol(assocLeft2RightSymbol);
      }
      return assocLeft2RightSymbol;
    }
    return null;
  }

  @Override
  protected CDQualifierSymbol create_CDQualifier(ASTCDQualifier ast) {
    CDQualifierSymbol s;
    if (ast.isPresentMCType()) {
      s = new CDQualifierSymbol(new AstPrinter().printType(ast.getMCType()));
      s.setTypeQualifier(true);
    }
    else {
      s = new CDQualifierSymbol(ast.getName());
      s.setNameQualifier(true);
    }
    return s;
  }

  CDAssociationSymbol createAssociationSymbol(final ASTCDAssociation astAssoc,
      final ASTMCQualifiedName astSourceName, final ASTMCQualifiedName astTargetName) {
    final CDTypeSymbolLoader sourceType = new CDTypeSymbolLoader(Names.getQualifiedName(
        astSourceName.getPartList()), getCurrentScope().get());

    final CDTypeSymbolLoader targetType = new CDTypeSymbolLoader(Names.getQualifiedName(
        astTargetName.getPartList()), getCurrentScope().get());

    final CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);

    if (astAssoc.isPresentName()) {
      associationSymbol.setAssocName(astAssoc.getName());
    }

    addStereotypes(associationSymbol,
        astAssoc.isPresentStereotype() ? astAssoc.getStereotype() : null);

    if ((astSourceName.getPartList().size() > 1 && !sourceType.getName().equals(
        Names.getQualifiedName(astSourceName.getPartList())))) {
      Log.error("0xU0270 Association referenced type " + astSourceName + " wasn't declared in the "
          + "class diagram " + ". Pos: "
          + astAssoc.get_SourcePositionStart());
      return null;
    }

    addToScope(associationSymbol);
    associationSymbol.setAstNode(astAssoc);

    return associationSymbol;
  }

  public void addStereotypes(final CDAssociationSymbol associationSymbol,
      final ASTCDStereotype astStereotype) {
    if (astStereotype != null) {
      // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail
      // geschrieben)
      for (final ASTCDStereoValue val : astStereotype.getValueList()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  protected void addAssocToTarget(CDAssociationSymbol assocSymbol) {
    if (assocSymbol.getTargetType().loadSymbol().isPresent()) {
      assocSymbol.getTargetType().getLoadedSymbol().addSpecAssociation(assocSymbol);
    }
    if (assocSymbol.getSourceType().loadSymbol().isPresent()) {
      // TODO PN use association reference instead?
      // TODO PN should we really invoke methods of the symbol definition during the symbol table creation?
      assocSymbol.getSourceType().getLoadedSymbol().getSpannedScope().add(assocSymbol);
    }

  }

  public final ICD4AnalysisScope currentScope() {
    return (this.scopeStack.peekLast());
  }

  public final Optional<? extends IScopeSpanningSymbol> currentSymbol() {
    return currentScope().isPresentSpanningSymbol() ?
        Optional.of(currentScope().getSpanningSymbol()) :
        Optional.empty();
  }

}
