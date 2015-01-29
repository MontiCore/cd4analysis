package cd4analysis.symboltable;/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import cd4analysis.symboltable.references.CDTypeSymbolReference;
import com.google.common.base.Optional;
import de.cd4analysis._ast.*;
import de.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.SymbolTableCreationVisitor;
import de.monticore.types._ast.ASTImportStatement;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.helper.NameHelper;

import java.util.ArrayList;
import java.util.List;

import static mc.helper.NameHelper.dotSeparatedStringFromList;

public interface CD4AnalysisSymbolTableCreationVisitor extends CD4AnalysisVisitor, SymbolTableCreationVisitor {

  @Override
  public default void visit(final ASTCDCompilationUnit compilationUnit) {
    Log.info("Building Symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    setPackageName(dotSeparatedStringFromList(compilationUnit.getPackage()));

    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getImportStatements() != null) {
      for (ASTImportStatement imp : compilationUnit.getImportStatements()) {
        imports.add(new ImportStatement(dotSeparatedStringFromList(imp.getImportList()), imp.isStar()));
      }
    }

    final ArtifactScope scope = new ArtifactScope(Optional.absent(), getPackageName(), imports);
    getSymbolTableCreator().putOnStackAndSetEnclosingIfExists(scope);
  }

  @Override
  public default void endVisit(final ASTCDCompilationUnit compilationUnit) {
    getSymbolTableCreator().removeCurrentScope();

    Log.info("Finished build of symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());
  }

  @Override
  public default void visit(final ASTCDDefinition astDefinition) {
    final String cdName = astDefinition.getName();

    setFullClassDiagramName(getPackageName().isEmpty() ? cdName : (getPackageName() + "." + cdName));

    final MutableScope cdScope = new CommonScope(true);
    cdScope.setName(cdName);
    getSymbolTableCreator().putOnStackAndSetEnclosingIfExists(cdScope);
  }

  @Override
  public default void endVisit(final ASTCDDefinition astDefinition) {
    astDefinition.getCDAssociations().forEach(this::handleAssociation);
    getSymbolTableCreator().removeCurrentScope();
  }

  @Override
  public default void visit(final ASTCDClass astClass) {
    final CDTypeSymbol typeSymbol = new CDTypeSymbol(astClass.getName());

    if (astClass.getSuperclass().isPresent()) {
      final CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(astClass
          .getSuperclass().get());
      typeSymbol.setSuperClass(superClassSymbol);
    }

    final ASTModifier astModifier = astClass.getModifier().or(new ASTModifier.Builder().build());

    setModifiersOfType(typeSymbol, astModifier);

    addInterfacesToType(typeSymbol, astClass.getInterfaces());

    getSymbolTableCreator().defineInScope(typeSymbol);
    getSymbolTableCreator().putScopeOnStackAndSetEnclosingIfExists(typeSymbol);
  }

  public default void setModifiersOfType(final CDTypeSymbol typeSymbol, final ASTModifier astModifier) {
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

  public default CDTypeSymbolReference createCDTypeSymbolFromReference(final ASTReferenceType astReferenceType) {
    // TODO PN replace by type converter
    CDTypeSymbolReference superSymbol = null;
    if (astReferenceType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSuperClass = (ASTSimpleReferenceType) astReferenceType;
      superSymbol = new CDTypeSymbolReference(Names.getQualifiedName(astSuperClass.getName()),
          getSymbolTableCreator().currentScope().get());
    }

    return superSymbol;
  }

  @Override
  public default void visit(final ASTCDAttribute astAttribute) {
    final String typeName = "TODO_Type"; // TODO PN use TypePrinter for astAttribute.getType() instead
    final CDTypeSymbolReference typeReference = new CDTypeSymbolReference(typeName,
        getSymbolTableCreator().currentScope().get());

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

    getSymbolTableCreator().defineInScope(fieldSymbol);
  }

  @Override
  public default void endVisit(final ASTCDClass astClass) {
    getSymbolTableCreator().removeCurrentScope();
  }

  @Override
  public default void visit(final ASTCDInterface astInterface) {
    final CDTypeSymbol typeSymbol = new CDTypeSymbol(astInterface.getName());
    typeSymbol.setInterface(true);
    // Interfaces are always abstract
    typeSymbol.setAbstract(true);

    addInterfacesToType(typeSymbol, astInterface.getInterfaces());
    setModifiersOfType(typeSymbol, astInterface.getModifier().or(new ASTModifier.Builder().build()));

    // Interfaces are always abstract
    typeSymbol.setAbstract(true);


    getSymbolTableCreator().defineInScope(typeSymbol);
    getSymbolTableCreator().putScopeOnStackAndSetEnclosingIfExists(typeSymbol);
  }

  public default void addInterfacesToType(final CDTypeSymbol typeSymbol, final ASTReferenceTypeList astInterfaces) {
    if (astInterfaces != null) {
      for (final ASTReferenceType superInterface : astInterfaces) {
        final CDTypeSymbolReference superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
        typeSymbol.addInterface(superInterfaceSymbol);
      }
    }
  }

  @Override
  public default void endVisit(final ASTCDInterface astInterface) {
    getSymbolTableCreator().removeCurrentScope();
  }

  @Override
  public default void visit(final ASTCDEnum astEnum) {
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
    setModifiersOfType(enumSymbol, astEnum.getModifier().or(new ASTModifier.Builder().build()));

    getSymbolTableCreator().defineInScope(enumSymbol);
    getSymbolTableCreator().putScopeOnStackAndSetEnclosingIfExists(enumSymbol);
  }

  @Override
  public default void endVisit(final ASTCDEnum astEnum) {
    getSymbolTableCreator().removeCurrentScope();
  }

  @Override
  public default void visit(final ASTCDMethod astMethod) {
    final CDMethodSymbol methodSymbol = new CDMethodSymbol(astMethod.getName());

    setModifiersOfMethod(methodSymbol, astMethod.getModifier());
    setParametersOfMethod(methodSymbol, astMethod);
    setReturnTypeOfMethod(methodSymbol);
    setExceptionsOfMethod(methodSymbol, astMethod);
    setDefiningTypeOfMethod(methodSymbol);

    getSymbolTableCreator().defineInScope(methodSymbol);
    getSymbolTableCreator().putScopeOnStackAndSetEnclosingIfExists(methodSymbol);
  }

  @Override
  public default void endVisit(final ASTCDMethod astMethod) {
    getSymbolTableCreator().removeCurrentScope();
  }

  public default void setModifiersOfMethod(final CDMethodSymbol methodSymbol, final ASTModifier astModifier) {
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

  public default void addStereotypes(final CDMethodSymbol methodSymbol, final ASTStereotype astStereotype) {
    if (astStereotype != null) {
      for (final ASTStereoValue val : astStereotype.getValues()) {
        // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
        methodSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  public default void setParametersOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getCDParameters() != null) {
      CDTypeSymbolReference paramTypeSymbol;

      for (ASTCDParameter astParameter : astMethod.getCDParameters()) {
        final String paramName = astParameter.getName();
        // TODO PN use ASTTypesConverter
        //paramTypeSymbol = ASTTypesConverter.astTypeToTypeEntry(CDTypeEntryCreator.getInstance(), // astParameter.getType());
        paramTypeSymbol = new CDTypeSymbolReference("TODO_TYPE", getSymbolTableCreator().currentScope().get());

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

  public default void setReturnTypeOfMethod(final CDMethodSymbol methodSymbol) {
    // TODO PN use ASTTypesConverter
    final CDTypeSymbolReference returnSymbol = new CDTypeSymbolReference("TODO_RETURN_TYPE",
        getSymbolTableCreator().currentScope().get());
    /*ASTTypesConverter
    .astReturnTypeToTypeEntry
        (CDTypeEntryCreator.getInstance(), cdMethod.getReturnType());
    if (returnSymbol == null) {
      delegator.addErrorToCurrentResource("Return type couldn't be converted: " + ASTTypesConverter.astReturnTypeToString(cdMethod.getReturnType()));
    }
    else {*/
    methodSymbol.setReturnType(returnSymbol);
    //}
  }

  public default void setExceptionsOfMethod(final CDMethodSymbol methodSymbol, final ASTCDMethod astMethod) {
    if (astMethod.getExceptions() != null) {
      for (final ASTQualifiedName exceptionName : astMethod.getExceptions()) {
        final CDTypeSymbol exception = new CDTypeSymbolReference(exceptionName.toString(),
            getSymbolTableCreator().currentScope().get());
        methodSymbol.addException(exception);
      }
    }
  }

  public default void setDefiningTypeOfMethod(final CDMethodSymbol methodSymbol) {
    if (getSymbolTableCreator().currentSymbol().isPresent()) {
      if (getSymbolTableCreator().currentSymbol().get() instanceof CDTypeSymbol) {
        final CDTypeSymbol definingType = (CDTypeSymbol) getSymbolTableCreator().currentSymbol().get();
        methodSymbol.setDefiningType(definingType);

        if (definingType.isInterface()) {
          methodSymbol.setAbstract(true);
        }
      }
    }
  }

  public default void handleAssociation(final ASTCDAssociation cdAssoc) {
    handleLeftToRightAssociation(cdAssoc);
    handleRightToLeftAssociation(cdAssoc);
  }

  public default void handleRightToLeftAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isRightToLeft() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      final CDAssociationSymbol assocRight2LeftSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getRightReferenceName(), cdAssoc.getLeftReferenceName());
      // complete association properties
      if (assocRight2LeftSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocRight2LeftSymbol.setRelationship(Relationship.PART);
        }
        assocRight2LeftSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orNull()));
        assocRight2LeftSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orNull()));
        assocRight2LeftSymbol.setRole(cdAssoc.getLeftRole().orNull());

        if (cdAssoc.getLeftModifier().isPresent()) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().get().getStereotype().orNull());
        }

        if (cdAssoc.getRightQualifier().isPresent()) {
          final ASTCDQualifier qualifier = cdAssoc.getRightQualifier().get();
          if ((qualifier.getName() != null) && (qualifier.getName().equals(""))) {
            assocRight2LeftSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType() != null) {
            // TODO PN get type
            //            assocRight2LeftSymbol.setQualifier(qualifier.printType());
            assocRight2LeftSymbol.setQualifier("TODO_QUALIFIER_TYPE");
          }
        }
        assocRight2LeftSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isSimple());
      }
    }
  }

  public default void handleLeftToRightAssociation(final ASTCDAssociation cdAssoc) {
    if (cdAssoc.isLeftToRight() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      final CDAssociationSymbol assocLeft2RightSymbol = createAssociationSymbol(cdAssoc, cdAssoc
              .getLeftReferenceName(),
          cdAssoc.getRightReferenceName());

      if (assocLeft2RightSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocLeft2RightSymbol.setRelationship(Relationship.COMPOSITE);
        }
        assocLeft2RightSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orNull()));
        assocLeft2RightSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orNull()));
        assocLeft2RightSymbol.setRole(cdAssoc.getRightRole().orNull());
        if (cdAssoc.getRightModifier().isPresent()) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().get().getStereotype().orNull());
        }

        if (cdAssoc.getLeftQualifier().isPresent()) {
          final ASTCDQualifier qualifier = cdAssoc.getLeftQualifier().get();
          if ((qualifier.getName() != null) && (!qualifier.getName().equals(""))) {
            assocLeft2RightSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType() != null) {
            // TODO PN get type
            //            assocLeft2RightSymbol.setQualifier(qualifier.printType());
            assocLeft2RightSymbol.setQualifier("TODO_QUALIFIER_TYPE");
          }
        }
        assocLeft2RightSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isSimple());
      }
    }
  }

  public default CDAssociationSymbol createAssociationSymbol(final ASTCDAssociation astAssoc,
      final ASTQualifiedName astSourceName, final ASTQualifiedName astTargetName) {
    final CDTypeSymbolReference sourceType = new CDTypeSymbolReference(Names.getQualifiedName(
        astSourceName.getParts()), getSymbolTableCreator().currentScope().get());

    final CDTypeSymbolReference targetType = new CDTypeSymbolReference(Names.getQualifiedName(
        astTargetName.getParts()), getSymbolTableCreator().currentScope().get());

    final CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);

    associationSymbol.setAssocName(astAssoc.getName().orNull());

    addStereotypes(associationSymbol, astAssoc.getStereotype().orNull());

    if ((astSourceName.getParts().size() > 1 && !sourceType.getName().equals(NameHelper.dotSeparatedStringFromList(astSourceName.getParts())))) {
      Log.error("0xU0270 Association referenced type " + astSourceName + " wasn't declared in the "
          + "class diagram " + getFullClassDiagramName() + ". Pos: " + astAssoc.get_SourcePositionStart());
      return null;
    }

    getSymbolTableCreator().defineInScope(associationSymbol);

    return associationSymbol;
  }

  public default void addStereotypes(final CDAssociationSymbol associationSymbol,
      final ASTStereotype astStereotype) {
    if (astStereotype != null) {
      // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
      for (final ASTStereoValue val : astStereotype.getValues()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  public void setPackageName(String name);
  public String getPackageName();

  public void setFullClassDiagramName(String name);
  public String getFullClassDiagramName();


}
